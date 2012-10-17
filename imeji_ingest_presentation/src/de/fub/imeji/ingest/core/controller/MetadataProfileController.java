/**
 * 
 */
package de.fub.imeji.ingest.core.controller;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;

import de.fub.imeji.ingest.core.beans.metadata.MetaDataEntity;
import de.fub.imeji.ingest.core.beans.metadata.MetadataProfile;
import de.fub.imeji.ingest.core.beans.metadata.ProfileObject;
import de.fub.imeji.ingest.core.beans.metadata.terms.Terms;
import de.fub.imeji.ingest.core.xml.XmlHandler;
import de.fub.imeji.ingest.core.zuse.beans.ZuseDCTermsBean;
import de.fub.imeji.ingest.core.zuse.profiles.ZuseDCTermsProfile;


/**
 * @author hnguyen
 *
 */
public class MetadataProfileController {

	private ProfileObject projectObject;
	private MetadataController metadataController;
	private DCTermsController dctermsController;
	
	/**
	 * 
	 * @param fileName
	 * @throws JDOMException
	 * @throws IOException
	 */
	public MetadataProfileController(String fileName) throws JDOMException, IOException {
		this.projectObject = this.retrieveProjectObject(fileName);
		this.init();
	}
	
	/**
	 * 
	 * @param file
	 * @throws JDOMException
	 * @throws IOException
	 */
	public MetadataProfileController(File file) throws JDOMException, IOException {
		this.projectObject = this.retrieveProjectObject(file);
		this.init();
	}
	
	public MetadataProfileController(FileInputStream fileInputStream) throws JDOMException, IOException {
		this.projectObject = this.retrieveProjectObject(fileInputStream);
		this.init();
	}
	
	public MetadataProfileController(File sourceFinProfile, File sourceFileToMap) {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private void init() {
		this.metadataController = new MetadataController(projectObject.getMetaDataEntities());
		this.dctermsController = new DCTermsController();
	}
	
	/**
	 * 
	 * @return
	 * @throws Exception 
	 */
	public ProfileObject getProjectObject() throws Exception {
		if(this.projectObject == null) {
			throw new Exception("Project object not initilized!");
		}
		return projectObject;
	}

	/**
	 * 
	 * @param projectObejct
	 */
	public void setProjectObject(ProfileObject projectObject) {
		this.projectObject = projectObject;
	}

	/**
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException 
	 * @throws JDOMException 
	 */
	public ProfileObject retrieveProjectObject(String fileName) throws JDOMException, IOException {
		this.projectObject = XmlHandler.getProjectObject(fileName); 
		return this.projectObject;
	}
	
	/**
	 * 
	 * @param file
	 * @return
	 * @throws JDOMException
	 * @throws IOException
	 */
	public ProfileObject retrieveProjectObject(File file) throws JDOMException, IOException {
		this.projectObject = XmlHandler.getProjectObject(file); 
		return this.projectObject;
	}
	
	public ProfileObject retrieveProjectObject(FileInputStream fileInputStream) throws JDOMException, IOException {
		this.projectObject = XmlHandler.getProjectObject(fileInputStream); 
		return this.projectObject;
	}

	/**
	 * 
	 * @param fileName
	 * @throws Exception
	 */
	public void exportToXmlFile(String fileName) throws Exception {
		XmlHandler.createMetadataXmlFile(fileName, this.getProjectObject());
	}
	
	/**
	 * 
	 * @param file
	 * @throws Exception
	 */
	public void exportToXmlFile(File file) throws Exception {
		XmlHandler.createMetadataXmlFile(file, this.getProjectObject());
	}
	
	/**
	 * 
	 * @param fileName
	 * @param projectName
	 * @param metadataProfile
	 * @throws JDOMException
	 * @throws IOException
	 */
	public void exportMetadataProfileToXmlFile(String fileName, String projectName, MetadataProfile metadataProfile, ArrayList<Namespace> namespaces) throws JDOMException, IOException {
		XmlHandler.createMetadataProfileXmlFile(fileName, projectName, metadataProfile, namespaces);
	}
	
	/**
	 * 
	 * @param file
	 * @param projectName
	 * @param metadataProfile
	 * @throws JDOMException
	 * @throws IOException
	 */
	public static void exportMetadataProfileToXmlFile(File file, String projectName, MetadataProfile metadataProfile, ArrayList<Namespace> namespaces) throws JDOMException, IOException {
		XmlHandler.createMetadataProfileXmlFile(file, projectName, metadataProfile, namespaces);
	}
	
	/**
	 * 
	 * @param file
	 * @param metadataProfiles
	 * @param projectName
	 * @param namespaces
	 * @throws JDOMException
	 * @throws IOException
	 */
	public static void exportMetadataProfileToXmlFile(File file, ArrayList<MetadataProfile> metadataProfiles, String projectName, ArrayList<Namespace> namespaces) throws JDOMException, IOException {
		XmlHandler.createMetadataProfileXmlFile(file, metadataProfiles, projectName, namespaces);
	}
	
	/**
	 * 
	 * @param fileOutputStream
	 * @param metadataProfiles
	 * @param projectName
	 * @param namespaces
	 * @throws JDOMException
	 * @throws IOException
	 */
	public static void exportMetadataProfileToXmlFile(FileOutputStream fileOutputStream, ArrayList<MetadataProfile> metadataProfiles, String projectName, ArrayList<Namespace> namespaces) throws JDOMException, IOException {
		XmlHandler.createMetadataProfileXmlFileSorted(fileOutputStream, metadataProfiles, projectName, namespaces);
	}
	
	
	/**
	 * 
	 * @param file
	 * @param projectName
	 * @param metadataEntities
	 * @param namespaces
	 * @throws JDOMException
	 * @throws IOException
	 */
	public static void exportMetadataProfileToXmlFile(File file, String projectName, ArrayList<MetaDataEntity> metadataEntities, ArrayList<Namespace> namespaces) throws JDOMException, IOException {
		XmlHandler.createMetadataProfileXmlFile(file, projectName, metadataEntities, namespaces);
	}
	
	/**
	 * 
	 * @param toMappedMetadataNames
	 * @param terms
	 * @throws Exception
	 */
	public void addTermToAllMetadataEntries(ArrayList<String> toMappedMetadataNames, Terms terms) throws Exception {		
		
		ArrayList<MetaDataEntity> entities = this.getProjectObject().getMetaDataEntities();	

		for (MetaDataEntity metaDataEntity : entities) {
//			if(metaDataEntity.getMetaDataProfile().getMetadataProfileName().contains("oFoto"))
//				continue;
			this.metadataController.convertAndAddAMetadataEntityToDCTermMetadata(metaDataEntity, toMappedMetadataNames, terms);
		}
	}

	/**
	 * 
	 * @param projectObject
	 * @return
	 */
	public ArrayList<MetadataProfile> getMetadataProfiles(ProfileObject projectObject) {
		ArrayList<MetadataProfile> metadataProfiles = new ArrayList<MetadataProfile>();
		
		ArrayList<MetaDataEntity> metadataEntities = projectObject.getMetaDataEntities();
		
		MetadataProfile metadataProfile = null;
		
		ArrayList<String> profileNames = new ArrayList<String>();
		
		for (MetaDataEntity metaDataEntity : metadataEntities) {
			String profileName = metaDataEntity.getMetaDataProfile().getMetadataProfileName();
			metadataProfile = metaDataEntity.getMetaDataProfile();
			profileNames.add(profileName);
			metadataProfile = metadataProfile.clone();
			metadataProfiles.add(metadataProfile);
		}
		
		return metadataProfiles;
	}
	
	/**
	 * 
	 * @param projectObject
	 * @return
	 */
	public ArrayList<MetadataProfile> getUniqueMetadataProfiles(ProfileObject projectObject) {
		ArrayList<MetadataProfile> metadataProfiles = new ArrayList<MetadataProfile>();
		
		ArrayList<MetaDataEntity> metadataEntities = projectObject.getMetaDataEntities();
		
		MetadataProfile metadataProfile = null;
		
		ArrayList<String> profileNames = new ArrayList<String>();
		
		for (MetaDataEntity metaDataEntity : metadataEntities) {
			String profileName = metaDataEntity.getMetaDataProfile().getMetadataProfileName();
			metadataProfile = metaDataEntity.getMetaDataProfile();
			if(!profileNames.contains(profileName)) {
				profileNames.add(profileName);
				metadataProfile = metadataProfile.clone();
				metadataProfile.clear();
				metadataProfiles.add(metadataProfile);
			}
		}
		
		return metadataProfiles;
	}

	/**
	 * 
	 * @param newProfileName
	 * @param profiles
	 * @return
	 */
	public MetadataProfile getMergedProfile(String newProfileName, ArrayList<MetadataProfile> profiles) {
		MetadataProfile mergedProfile = new MetadataProfile(newProfileName);
		
//		// good way but not if elemen has children
//		for (MetadataProfile metadataProfile : profiles) {
//			mergedProfile.getMetaDatas().putAll(metadataProfile.getMetaDatas());
//		}
		
		for (MetadataProfile metadataProfile : profiles) {
			Hashtable<String, Element> metadaTable = metadataProfile.getMetaDatas();
			
			Enumeration<String> mdKeys = metadaTable.keys();
			while(mdKeys.hasMoreElements()) {
				String mdKey = mdKeys.nextElement();			
				
				Element element = metadaTable.get(mdKey);
		      
				if(mergedProfile.getMetaDatas().get(mdKey) == null) {
					mergedProfile.getMetaDatas().put(mdKey, element);
				} else if(!element.getChildren().isEmpty()) {
					mergeElement(mergedProfile.getMetaDatas().get(mdKey),element);
				}				
		    }
			
		}
		
		return mergedProfile;
	}

	/**
	 * 
	 * @param elementToMerge
	 * @param originalElement
	 */
	private void mergeElement(Element elementToMerge, Element originalElement) {
		List<Element> kids = originalElement.getChildren();
		for (Element element : kids) {
			Element child = elementToMerge.getChild(element.getName());
			if(child == null) {
				elementToMerge.addContent(element.clone());
			} else if (child.getChildren().isEmpty()) {
				// nothing to do 
			} else {
				mergeElement(child,element);
			}
		}
	}

	/**
	 * 
	 * @param dctermsBean
	 */
	public void addDCTerm(int i, ZuseDCTermsBean dctermsBean) {
		this.dctermsController.add(i, dctermsBean);
		
	}

//	/**
//	 * 
//	 * @param file
//	 * @param projectName
//	 * @param profileName
//	 * @param dcTermsProfile
//	 * @throws Exception
//	 */
//	public void exportDCTermProfileToXmlFileWithRedundancy(File file, String projectName, String profileName,
//			ArrayList<ArrayList<DCTermsBean>> dcTermsProfile) throws Exception {
//		
//		this.dctermsController.exportProfileToXmlFile(file, projectName, profileName, dcTermsProfile);
//	}



//	/**
//	 * 
//	 * @param file
//	 * @param projectName
//	 * @param profileName
//	 * @param dcTermsProfile
//	 * @param projectObject
//	 * @throws Exception
//	 */
//	public void exportDCTermProfileToXmlFileWithoutRedundancy(File file, String projectName, String profileName,
//			ArrayList<ArrayList<DCTermsBean>> dcTermsProfile, ProjectObject projectObject) throws Exception {
//		
//		this.dctermsController.exportProfileToXmlFileWithoutRedundancy(file, projectName, profileName, dcTermsProfile, projectObject);
//	}
	
	/**
	 * 
	 * @param file
	 * @param projectName
	 * @param profileName
	 * @param dcTermsProfiles
	 * @param metadataProfiles
	 * @throws Exception
	 */
	public void exportDCTermProfileToXmlFileWithoutRedundancy(File file, String projectName, String profileName,
			ArrayList<ArrayList<ZuseDCTermsBean>> dcTermsProfiles, ArrayList<MetadataProfile> metadataProfiles) throws Exception {
		
		this.dctermsController.exportProfileToXmlFileWithoutRedundancy(file, projectName, profileName, dcTermsProfiles, metadataProfiles);
	}
	
	/**
	 * 
	 * @param fileOutputStream
	 * @param projectName
	 * @param profileName
	 * @param dcTermsProfiles
	 * @param metadataProfiles
	 * @throws Exception
	 */
	public void exportDCTermProfileToXmlFileWithoutRedundancy(FileOutputStream fileOutputStream, String projectName, String profileName,
			ArrayList<ArrayList<ZuseDCTermsBean>> dcTermsProfiles, ArrayList<MetadataProfile> metadataProfiles) throws Exception {
		
		this.dctermsController.exportProfileToXmlFileWithoutRedundancy(fileOutputStream, projectName, profileName, dcTermsProfiles, metadataProfiles);
	}

	public static void addAZuseSpecificMetadataTo(ArrayList<MetadataProfile> metadataProfiles) {
		
		
	}

	public static void addAZuseSpecificMetadataTo(
			ArrayList<MetadataProfile> mappedProfiles, ZuseDCTermsBean dctermb) {
		for (MetadataProfile metadataProfile : mappedProfiles) {
			Hashtable<String, Element> metadaTable = metadataProfile.getMetaDatas();			
			
			String filename = "";
			String filenameKey = "";			
			String sig = "";
			String bes = "";
			
			Enumeration<String> mdKeys = metadaTable.keys();
			
			boolean f1 = false;
			boolean f2 = false;
			boolean f3 = false;
			
			while(mdKeys.hasMoreElements()) {
				String mdKey = mdKeys.nextElement();			
				
				Element element = metadaTable.get(mdKey);
		      
				List<Attribute> attributes = element.getAttributes();
				
				for (Attribute attribute : attributes) {
					if(attribute.getName().equalsIgnoreCase(MetaDataEntity.LABEL)) {
						if(attribute.getValue().equalsIgnoreCase(ZuseDCTermsProfile.METADATA_SIGNATUR)) {
							sig = element.getValue().replace("/", "_");
							f1 = true;
						} else if(attribute.getValue().equalsIgnoreCase(ZuseDCTermsProfile.METADATA_BESTAND)) {
							bes = element.getValue().replace(" ", "_");
							f2 = true;
						} if(attribute.getValue().equalsIgnoreCase(ZuseDCTermsProfile.METADATA_DATEINAME)) {
							filenameKey = mdKey;
							f3 = true;
						}
					}
				}
				
				if(f1&&f2&&f3) {
					break;
				}
		    }
			
			Element e = metadaTable.get(filenameKey);			
			filename += "DMA_"+bes+"_"+sig+".jpg";
			e.setText(filename);
		}
	}

	public void exportDCTermProfileToXmlFileWithoutRedundancySortedLabel(
			FileOutputStream fileOutputStream, String projectName, String profileName,
			ArrayList<ArrayList<ZuseDCTermsBean>> dcTermsProfiles,
			ArrayList<MetadataProfile> metadataProfiles, String label) {		
		try {
			this.dctermsController.exportProfileToXmlFileWithoutRedundancySortedLabel(fileOutputStream, projectName, profileName, dcTermsProfiles, metadataProfiles, label);
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
