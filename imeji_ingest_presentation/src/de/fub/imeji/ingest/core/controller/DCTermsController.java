/**
 * 
 */
package de.fub.imeji.ingest.core.controller;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;

import de.fub.imeji.ingest.core.beans.metadata.MetaDataEntity;
import de.fub.imeji.ingest.core.beans.metadata.MetadataProfile;
import de.fub.imeji.ingest.core.beans.metadata.terms.DCTerms;
import de.fub.imeji.ingest.core.beans.metadata.terms.RDFTerms;
import de.fub.imeji.ingest.core.beans.metadata.terms.Terms;
import de.fub.imeji.ingest.core.helper.sorter.MetadataProfileSorter;
import de.fub.imeji.ingest.core.zuse.beans.ZuseDCTermsBean;
import de.fub.imeji.ingest.core.zuse.metadata.terms.ZuseDCTerms;
import de.fub.imeji.ingest.core.zuse.profiles.ZuseDCTermsProfile;


/**
 * @author hnguyen
 *
 */
public class DCTermsController {
	private ArrayList<ArrayList<ZuseDCTermsBean>> dctermsBeanList;
	
	/**
	 * 
	 */
	public DCTermsController() {
		this.setDctermsProfile(new ArrayList<ArrayList<ZuseDCTermsBean>>());
	}
	
	public static void build() {
		
	}
	
	/**
	 * 
	 * @param dctermsBean
	 */
	public void add(int i, ZuseDCTermsBean dctermsBean) {
		this.dctermsBeanList.get(i).add(dctermsBean);
		
	}
	
	/**
	 * 
	 * @param labelTree
	 * @param dcterms
	 */
	public void add(int i, ArrayList<String> labelTree, ZuseDCTerms dcterms) {
		this.dctermsBeanList.get(i).add(new ZuseDCTermsBean(labelTree, dcterms));
	}


	/**
	 * 
	 * @param dctermsProfiles
	 */
	public void setDctermsProfile(ArrayList<ArrayList<ZuseDCTermsBean>> dctermsProfiles) {
		this.dctermsBeanList = dctermsProfiles;
	}

	/**
	 * @return the dctermsProfile
	 */
	public ArrayList<ArrayList<ZuseDCTermsBean>> getDctermsProfiles() {
		if (this.dctermsBeanList == null) {
			throw new NullPointerException("DCTerms not available!");
		}
		return dctermsBeanList;
	}
	
//	public void exportProfileToXmlFile(File file, String projectName, String profileName,
//			ArrayList<ArrayList<DCTermsBean>> dctermsProfiles) throws Exception {
//		
//		this.setDctermsProfile(dctermsProfiles);
//		
//		MetadataProfile metadataProfile = new MetadataProfile(profileName);
//		
//		for (ArrayList<DCTermsBean> dctermsBeans : dctermsProfiles) {
//			
//			for (DCTermsBean dcTermsBean : dctermsBeans) {
//				
//				Terms terms = dcTermsBean.getDcterms();
//				
//				Element elem = new Element(terms.getTermLabel(),terms.getTermNamespace());
//				
//				elem.getAttributes().clear();
//				for (Attribute attribute : terms.getAttributes()) {
//					elem.getAttributes().add(attribute.clone());
//				}
//				
//				String toMappedMetadataName = dcTermsBean.getLabelTree().get(dcTermsBean.getLabelTree().size()-1);
//				
//				Attribute tagAttribute = new Attribute(MetaDataEntity.LABEL, toMappedMetadataName, Namespace.NO_NAMESPACE);
//				elem.getAttributes().add(tagAttribute);
//	
//				metadataProfile.getMetaDatas().put(terms.getTermLabel()+toMappedMetadataName, elem);
//	
//			}
//		}
//		ArrayList<Namespace> namespaces = new ArrayList<Namespace>();
//		
//		
//		namespaces.add(Terms.RDF_NAMESPACE);
//		namespaces.add(Terms.XSI_NAMESPACE);
//		namespaces.add(DCTerms.DCTERMS_NAMESPACE);
//		
//		MetadataProfileController.exportMetadataProfileToXmlFile(file, projectName, metadataProfile, namespaces);
//		
//	}
	
	public void exportProfileToXmlFileWithoutRedundancy(File file, String projectName, String profileName,
			ArrayList<ArrayList<ZuseDCTermsBean>> dctermsProfiles, ArrayList<MetadataProfile> metadataProfiles) throws Exception {
		
		this.setDctermsProfile(dctermsProfiles);
		
		ArrayList<MetadataProfile> dcTermsMetadataProfiles = new ArrayList<MetadataProfile>();
		
		
		for (ArrayList<ZuseDCTermsBean> dctermsBeans : dctermsProfiles) {
			MetadataProfile dcTermsMetadataProfile = new MetadataProfile(profileName);
			
			for (ZuseDCTermsBean dcTermsBean : dctermsBeans) {
				
				Terms terms = dcTermsBean.getDcterms();
				
				Element elem = new Element(terms.getTermLabel(),terms.getTermNamespace());
				
				elem.getAttributes().clear();
				for (Attribute attribute : terms.getAttributes()) {
					elem.getAttributes().add(attribute.clone());
				}
				
				String toMappedMetadataName = dcTermsBean.getLabelTree().get(dcTermsBean.getLabelTree().size()-1);
				
				Attribute tagAttribute = new Attribute(MetaDataEntity.LABEL, toMappedMetadataName, Namespace.NO_NAMESPACE);
				elem.getAttributes().add(tagAttribute);
	
				dcTermsMetadataProfile.getMetaDatas().put(terms.getTermLabel()+toMappedMetadataName, elem);
	
			}
			
			dcTermsMetadataProfiles.add(dcTermsMetadataProfile);
		}
		ArrayList<Namespace> namespaces = new ArrayList<Namespace>();

		namespaces.add(RDFTerms.RDF_NAMESPACE);
		namespaces.add(Terms.XSI_NAMESPACE);
		namespaces.add(DCTerms.DCTERMS_NAMESPACE);	
		
		ArrayList<MetadataProfile> mappedProfiles = getMappedDCTermsProfiles(dcTermsMetadataProfiles,metadataProfiles);
		
		MetadataProfileController.exportMetadataProfileToXmlFile(file, mappedProfiles, projectName, namespaces);
	}
	
	public void exportProfileToXmlFileWithoutRedundancy(FileOutputStream fileOutputStream, String projectName, String profileName,
			ArrayList<ArrayList<ZuseDCTermsBean>> dctermsProfiles, ArrayList<MetadataProfile> metadataProfiles) throws Exception {
		
		this.setDctermsProfile(dctermsProfiles);
		
		ArrayList<MetadataProfile> dcTermsMetadataProfiles = new ArrayList<MetadataProfile>();
		
		
		for (ArrayList<ZuseDCTermsBean> dctermsBeans : dctermsProfiles) {
			MetadataProfile dcTermsMetadataProfile = new MetadataProfile(profileName);
			
			for (ZuseDCTermsBean dcTermsBean : dctermsBeans) {
				
				Terms terms = dcTermsBean.getDcterms();
				
				Element elem = new Element(terms.getTermLabel(),terms.getTermNamespace());
				
				elem.getAttributes().clear();
				for (Attribute attribute : terms.getAttributes()) {
					elem.getAttributes().add(attribute.clone());
				}
				
				String toMappedMetadataName = dcTermsBean.getLabelTree().get(dcTermsBean.getLabelTree().size()-1);
				
				Attribute tagAttribute = new Attribute(MetaDataEntity.LABEL, toMappedMetadataName, Namespace.NO_NAMESPACE);
				elem.getAttributes().add(tagAttribute);
	
				dcTermsMetadataProfile.getMetaDatas().put(terms.getTermLabel()+toMappedMetadataName, elem);
	
			}
			
			dcTermsMetadataProfiles.add(dcTermsMetadataProfile);
		}
		ArrayList<Namespace> namespaces = new ArrayList<Namespace>();

		namespaces.add(RDFTerms.RDF_NAMESPACE);
		namespaces.add(Terms.XSI_NAMESPACE);
		namespaces.add(DCTerms.DCTERMS_NAMESPACE);
		
		
		
		ArrayList<MetadataProfile> mappedProfiles = getMappedDCTermsProfiles(dcTermsMetadataProfiles,metadataProfiles);
		
		
		ZuseDCTermsBean dctermb = new ZuseDCTermsBean(new ArrayList<String>(Arrays.asList(ZuseDCTermsProfile.METADATA_DATEINAME)), ZuseDCTerms.DESCRIPTION(0, false, "de"));
		MetadataProfileController.addAZuseSpecificMetadataTo(mappedProfiles,dctermb);

		MetadataProfileController.exportMetadataProfileToXmlFile(fileOutputStream, mappedProfiles, projectName, namespaces);
	}
	
	private ArrayList<MetadataProfile> getMappedDCTermsProfiles(ArrayList<MetadataProfile> dcTermsMetadataProfiles, ArrayList<MetadataProfile> metadataProfiles) {
		
		int profileSize = metadataProfiles.size();
		
		ArrayList<MetadataProfile> mappedProfiles = new ArrayList<MetadataProfile>(profileSize);
	
		for (int i = 0; i < profileSize; i++) {
			MetadataProfile mdp = dcTermsMetadataProfiles.get(i);
			MetadataProfile mdptm = metadataProfiles.get(i);
			
			Hashtable<String, Element> metadaTable = mdp.getMetaDatas();
			Hashtable<String, Element> metadaTableTM = mdptm.getMetaDatas();
			
			Enumeration<String> mdKeys = metadaTable.keys();
			
			String labelName = "";
			
			while(mdKeys.hasMoreElements()) {
				String mdKey = mdKeys.nextElement();
				Element element = metadaTable.get(mdKey);
			
				List<Attribute> attributes = element.getAttributes();
				
				labelName = "";
				
				for (Attribute attribute : attributes) {
					if(attribute.getName().equalsIgnoreCase(MetaDataEntity.LABEL)) {
						labelName = attribute.getValue();
					}
				}
				
				if(labelName.isEmpty()) {
					continue;
				}
				
				Element elementTM = metadaTableTM.get(element.getName()+labelName);
				
				if(elementTM == null) {
					dfsSetElement(labelName,element,metadaTableTM);
				} else {
					if(elementTM.getChildren().isEmpty()) {
						element.setText(elementTM.getValue());
					}
				}
				
			}
			mappedProfiles.add(mdp.clone());
		}
		
		
		return mappedProfiles;
	}

	private void dfsSetElement(String label, Element elementSource, Hashtable<String, Element> metadaTable) {
		Enumeration<String> mdKeys = metadaTable.keys();
		while(mdKeys.hasMoreElements()) {
			String mdKey = mdKeys.nextElement();			
			Element elem = metadaTable.get(mdKey);
			if(elem.getName().equalsIgnoreCase(label)) {
				elementSource.setText(elem.getValue());
				break;
			} else if(!elem.getChildren().isEmpty()) {
				dfsSetElement(label,elementSource,elem.getChildren());
			}
		}
	}
	
	private void dfsSetElement(String label, Element elementSource, List<Element> elems) {
		
		for (Element element : elems) {
			if(element.getName().equalsIgnoreCase(label)) {
				elementSource.setText(element.getValue());
			} else if(!element.getChildren().isEmpty()) {
				dfsSetElement(label,elementSource,element.getChildren());
			}
		}
		
		
	}

//	public void exportProfileToXmlFileWithoutRedundancy(File file, String projectName, String profileName,
//			ArrayList<ArrayList<DCTermsBean>> dctermsProfiles, ProjectObject projectObject) throws Exception {
//		
//		this.setDctermsProfile(dctermsProfiles);
//		
//		MetadataProfile metadataProfile = new MetadataProfile(profileName);
//		for (ArrayList<DCTermsBean> dctermsBeans : dctermsProfiles) {
//			for (DCTermsBean dcTermsBean : dctermsBeans) {
//				
//				Terms terms = dcTermsBean.getDcterms();
//				
//				Element elem = new Element(terms.getTermLabel(),terms.getTermNamespace());
//				
//				elem.getAttributes().clear();
//				for (Attribute attribute : terms.getAttributes()) {
//					elem.getAttributes().add(attribute.clone());
//				}
//				
//				String toMappedMetadataName = dcTermsBean.getLabelTree().get(dcTermsBean.getLabelTree().size()-1);
//				
//				Attribute tagAttribute = new Attribute(MetaDataEntity.LABEL, toMappedMetadataName, Namespace.NO_NAMESPACE);
//				elem.getAttributes().add(tagAttribute);
//	
//				metadataProfile.getMetaDatas().put(terms.getTermLabel()+toMappedMetadataName, elem);
//	
//			}
//		}
//		ArrayList<Namespace> namespaces = new ArrayList<Namespace>();
//
//		namespaces.add(Terms.RDF_NAMESPACE);
//		namespaces.add(Terms.XSI_NAMESPACE);
//		namespaces.add(DCTerms.DCTERMS_NAMESPACE);
//
//		namespaces.add(ZuseDCTerms.DCTERMS_CREATED_NAMESPACE);
//		namespaces.add(ZuseDCTerms.DCTERMS_CREATOR_NAMESPACE);
//		namespaces.add(ZuseDCTerms.DCTERMS_TYPE_NAMESPACE);
//		namespaces.add(ZuseDCTerms.DCTERMS_IDENTIFIER_NAMESPACE);
//		namespaces.add(ZuseDCTerms.DCTERMS_ALTERNATIVE_NAMESPACE);
//		namespaces.add(ZuseDCTerms.DCTERMS_TITLE_NAMESPACE);
//		namespaces.add(ZuseDCTerms.DCTERMS_SUBJECT_NAMESPACE);
//		namespaces.add(ZuseDCTerms.DCTERMS_FORMAT_NAMESPACE);
//		namespaces.add(ZuseDCTerms.DCTERMS_MEDIUM_NAMESPACE);
//		namespaces.add(ZuseDCTerms.DCTERMS_DESCRIPTION_NAMESPACE);
//		namespaces.add(ZuseDCTerms.DCTERMS_EXTENT_NAMESPACE);
//		namespaces.add(ZuseDCTerms.DCTERMS_HASVERSION_NAMESPACE);
//		namespaces.add(ZuseDCTerms.DCTERMS_HASFORMAT_NAMESPACE);
//		namespaces.add(ZuseDCTerms.DCTERMS_CONTRIBUTOR_NAMESPACE);
//		namespaces.add(ZuseDCTerms.DCTERMS_SPATIAL_NAMESPACE);
//		namespaces.add(ZuseDCTerms.DCTERMS_SOURCE_NAMESPACE);
//		
//		int entitySize = projectObject.getMetaDataEntities().size();
//		
//		ArrayList<MetaDataEntity> mdEntities = new ArrayList<MetaDataEntity>(projectObject.getMetaDataEntities().size());
//		
//		for (int i = 0; i < entitySize; i++) {
//			MetaDataEntity mde = new MetaDataEntity(metadataProfile.clone());		
//			mdEntities.add(mde);
//		}
//		
//		ArrayList<MetaDataEntity> mappedMdEntities = (ArrayList<MetaDataEntity>) MetadataEntityController.getMappedEntities(mdEntities, projectObject.getMetaDataEntities());
//		
//		
//		MetadataProfileController.exportMetadataProfileToXmlFile(file, projectName, mappedMdEntities, namespaces);
//		
//	}

	public void exportProfileToXmlFileWithoutRedundancySortedLabel(
			FileOutputStream fileOutputStream, String projectName,
			String profileName,
			ArrayList<ArrayList<ZuseDCTermsBean>> dctermsProfiles,
			ArrayList<MetadataProfile> metadataProfiles, String label) throws JDOMException, IOException {
		
		this.setDctermsProfile(dctermsProfiles);
		
		ArrayList<MetadataProfile> dcTermsMetadataProfiles = new ArrayList<MetadataProfile>();
		
		
		for (ArrayList<ZuseDCTermsBean> dctermsBeans : dctermsProfiles) {
			MetadataProfile dcTermsMetadataProfile = new MetadataProfile(profileName);
			
			for (ZuseDCTermsBean dcTermsBean : dctermsBeans) {
				
				Terms terms = dcTermsBean.getDcterms();
				
				Element elem = new Element(terms.getTermLabel(),terms.getTermNamespace());
				
				elem.getAttributes().clear();
				for (Attribute attribute : terms.getAttributes()) {
					elem.getAttributes().add(attribute.clone());
				}
				
				String toMappedMetadataName = dcTermsBean.getLabelTree().get(dcTermsBean.getLabelTree().size()-1);
				
				Attribute tagAttribute = new Attribute(MetaDataEntity.LABEL, toMappedMetadataName, Namespace.NO_NAMESPACE);
				elem.getAttributes().add(tagAttribute);
	
				dcTermsMetadataProfile.getMetaDatas().put(terms.getTermLabel()+toMappedMetadataName, elem);
	
			}
			
			dcTermsMetadataProfiles.add(dcTermsMetadataProfile);
		}
		ArrayList<Namespace> namespaces = new ArrayList<Namespace>();

		namespaces.add(RDFTerms.RDF_NAMESPACE);
		namespaces.add(Terms.XSI_NAMESPACE);
		namespaces.add(DCTerms.DCTERMS_NAMESPACE);
		
		
		
		ArrayList<MetadataProfile> mappedProfiles = getMappedDCTermsProfiles(dcTermsMetadataProfiles,metadataProfiles);
		
		
		ZuseDCTermsBean dctermb = new ZuseDCTermsBean(new ArrayList<String>(Arrays.asList(ZuseDCTermsProfile.METADATA_DATEINAME)), ZuseDCTerms.DESCRIPTION(0, false, "de"));
		MetadataProfileController.addAZuseSpecificMetadataTo(mappedProfiles,dctermb);
		
		MetadataProfileSorter comparator = new MetadataProfileSorter(label);
		
		java.util.Collections.sort(mappedProfiles,comparator);

		MetadataProfileController.exportMetadataProfileToXmlFile(fileOutputStream, mappedProfiles, projectName, namespaces);
		
	}
}
