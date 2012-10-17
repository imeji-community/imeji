package de.fub.imeji.ingest.core.controller;


import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.Namespace;

import de.fub.imeji.ingest.core.beans.metadata.MetaDataEntity;
import de.fub.imeji.ingest.core.beans.metadata.MetadataProfile;
import de.fub.imeji.ingest.core.beans.metadata.terms.Terms;


/**
 * 
 * @author hnguyen
 *
 */
public class MetadataController {

	private ArrayList<MetaDataEntity> metaDataEntities;
	
	/**
	 * 
	 * @param metaDataEntities
	 */
	public MetadataController(ArrayList<MetaDataEntity> metaDataEntities) {
		this.setMetaDataEntities(metaDataEntities);
	}

	/**
	 * @param metaDataEntities the metaDataEntities to set
	 */
	public void setMetaDataEntities(ArrayList<MetaDataEntity> metaDataEntities) {
		this.metaDataEntities = metaDataEntities;
	}

	/**
	 * @return the metaDataEntities
	 */
	public ArrayList<MetaDataEntity> getMetaDataEntities() {
		if(this.metaDataEntities == null) {
			throw new NullPointerException("No meta data entries available!");
		}
		return metaDataEntities;
	}
	
	/**
	 * 
	 * @param toMappedMetadataNames
	 * @param terms
	 */
	public void convertAndAddAllMetadataToDCTermMetadata(ArrayList<String> toMappedMetadataNames, Terms terms) {
		ArrayList<MetaDataEntity> mdes = this.getMetaDataEntities();
		for (MetaDataEntity metaDataEntity : mdes) {
			this.convertAndAddAMetadataEntityToDCTermMetadata(metaDataEntity, toMappedMetadataNames, terms);
		}
	}
	
	/**
	 * 
	 * @param metaDataEntity
	 * @param toMappedMetadataNames
	 * @param terms
	 */
	public void convertAndAddAMetadataEntityToDCTermMetadata(MetaDataEntity metaDataEntity, ArrayList<String> toMappedMetadataNames, Terms terms) {
		MetadataProfile mdp = metaDataEntity.getMetaDataProfile();
		Hashtable<String, Element> metadatas = mdp.getMetaDatas();
		
		Element metadata = null;
		String toMappedMetadataName = null;
		
		if(toMappedMetadataNames.size() > 1) {
			Iterator<String> it = toMappedMetadataNames.iterator();
			
			while(it.hasNext()) {
				toMappedMetadataName = it.next();
				if(metadata == null) {
					metadata = metadatas.get(toMappedMetadataName);
				} else {
					metadata = getRealChildElement(toMappedMetadataName,metadata);
				}
			}
			
		} else {
			toMappedMetadataName = toMappedMetadataNames.get(0);
			metadata = metadatas.get(toMappedMetadataName);
		}
			
		
		if(metadata == null)
			throw new NullPointerException("Specified meta data not available");

		Element elem = new Element(terms.getTermLabel(),terms.getTermNamespace());//(Element) metadata.clone();
		
		elem.setContent(metadata.cloneContent());
		
//		if(!elem.getValue().isEmpty()) {
//			String val = toMappedMetadataName+": "+elem.getValue();
//			elem.getContent().clear();
//			elem.addContent(val);
//		}
		
		elem.getAttributes().clear();
		for (Attribute attribute : terms.getAttributes()) {
			elem.getAttributes().add(attribute.clone());
		}
		
		Attribute tagAttribute = new Attribute("label", toMappedMetadataName, Namespace.NO_NAMESPACE);
		elem.getAttributes().add(tagAttribute);
		
		
		metadatas.put(terms.getTermLabel()+toMappedMetadataName, elem);
		
	}

	/**
	 * 
	 * @param toMappedMetadataName
	 * @param metadata
	 * @return
	 */
	private Element getRealChildElement(String toMappedMetadataName,
			Element metadata) {
		
		if(metadata.getName().equalsIgnoreCase(toMappedMetadataName))
			return metadata;
		
		List<Element> kids = metadata.getChildren();
		
		if(kids == null || kids.isEmpty()) {
			return null;
		}
		
		for (Element element : kids) {
			if(element.getName().equalsIgnoreCase(toMappedMetadataName)) {
				return element;
			}
			
			if(!element.getChildren().isEmpty()) {
				this.getRealChildElement(toMappedMetadataName,element);
			}
		}
		
		return null;
	}
	
}
