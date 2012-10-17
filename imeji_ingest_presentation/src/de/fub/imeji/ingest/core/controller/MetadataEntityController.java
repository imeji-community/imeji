/**
 * 
 */
package de.fub.imeji.ingest.core.controller;


import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Element;

import de.fub.imeji.ingest.core.beans.metadata.MetaDataEntity;
import de.fub.imeji.ingest.core.beans.metadata.MetadataProfile;


/**
 * @author hnguyen
 *
 */
public class MetadataEntityController {
	
	/**
	 * 
	 * @param sourceEntity
	 * @param toMappedEntities
	 * @return
	 */
	public static ArrayList<MetaDataEntity> getMappedEntities(ArrayList<MetaDataEntity> sourceEntity, ArrayList<MetaDataEntity> toMappedEntities) {
		int es = sourceEntity.size();
		
		ArrayList<MetaDataEntity> mdEntities = new ArrayList<MetaDataEntity>(es);
		
		for (int i = 0; i < es; i++) {
			MetadataProfile mdps =  sourceEntity.get(i).getMetaDataProfile();
			MetadataProfile mdpt =  toMappedEntities.get(i).getMetaDataProfile();
			
			Hashtable<String, Element> metadatass = mdps.getMetaDatas();
			Hashtable<String, Element> metadatast = mdpt.getMetaDatas();
			
			Enumeration<String> mdKeys = metadatass.keys();
			while(mdKeys.hasMoreElements()) {
				String mdKey = mdKeys.nextElement();			
				
				Element elements = metadatass.get(mdKey);
				
				List<Attribute> attributes = elements.getAttributes();
				
				String keyt = "";
				
				for (Attribute attribute : attributes) {
					if(attribute.getName().equalsIgnoreCase(MetaDataEntity.LABEL)) {
						keyt = attribute.getValue();
					}
				}
				
				if(keyt.isEmpty()) {
					continue;
				}
				
				Element elementt = getElement(keyt,metadatast);

				
				if(elementt == null) {
					continue;
				}

				if(elementt.getChildren().isEmpty()) {
					elements.setText(elementt.getValue());
				} else {
					
					if(!dfsSetElement(elements,elementt.getChildren())) {
						continue;
					}
				}
				
		    }
			
			MetaDataEntity me = new MetaDataEntity(mdps);
			mdEntities.add(me);
		}
		
		
		return mdEntities;
	}

	/**
	 * 
	 * @param elems
	 * @param children
	 * @return
	 */
	private static boolean dfsSetElement(Element elems, List<Element> children) {
		List<Attribute> attributes = elems.getAttributes();
		
		String keyt = "";
		
		for (Attribute attribute : attributes) {
			if(attribute.getName().equalsIgnoreCase(MetaDataEntity.LABEL)) {
				keyt = attribute.getValue();
			}
		}
		
		if(keyt.isEmpty()) {
			return false;
		}
		
		for (Element element : children) {
			if(element.getChildren().isEmpty()) {
				List<Attribute> attrs = element.getAttributes();
				
				for (Attribute attribute : attrs) {
					if(attribute.getName().equalsIgnoreCase(keyt)) {
						elems.setText(attribute.getValue());
						return true;
					}
				}
			} else {
				return dfsSetElement(elems,element.getChildren());
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @param key
	 * @param elements
	 * @return
	 */
	private static Element getElement(String key, Hashtable<String, Element> elements) {
		Enumeration<String> mdKeys = elements.keys();
		Element elem = null;
		while(mdKeys.hasMoreElements()) {
			String mdKey = mdKeys.nextElement();			

			if(elements.get(mdKey).getChildren().isEmpty()) {
				if(elements.get(mdKey).getName().equalsIgnoreCase(key))
					return elements.get(mdKey);
			} else {
				elem = getElement(key,elements.get(mdKey).getChildren());
			}	
		}
		return elem;
		
	}
	
	/**
	 * 
	 * @param key
	 * @param elements
	 * @return
	 */
	private static Element getElement(String key, List<Element> elements) {
		for (Element element : elements) {
			if(element.getChildren().isEmpty()) {
				if(element.getName().equalsIgnoreCase(key))
					return element;
			} else {
				getElement(key,element.getChildren());
			}
		}
		return null;
	}

}
