/**
 * 
 */
package de.mpg.imeji.logic.ingest.jaxb.interfaces;

import java.io.File;
import java.io.FileNotFoundException;

import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

import de.mpg.imeji.logic.ingest.util.ImejiSchemaFilename;
import de.mpg.imeji.logic.vo.MetadataProfile;


/**
 * @author hnguyen
 *
 */
public interface IJaxbMetadataProfile {
	
	public String xsdFilename = ImejiSchemaFilename.IMEJI_METADATAPROFILE_XSDFILE;
	
	/**
	 * This method exports the meta data profile to the xml file through the giveen schema file.
	 * @param xmlFile, the xml file to output
	 * @throws JAXBException
	 * @throws SAXException
	 * @throws FileNotFoundException 
	 */
	public void marshalMdProfile(String xmlFilename, MetadataProfile mdp) throws JAXBException, SAXException, FileNotFoundException;
	
	
	/**
	 * This method generates the meta data profile from the xml file.
	 * @param xmlFile, the xml file specified the content of the meta data information
	 * @return the MetadataProfile object
	 * @throws JAXBException
	 * @throws SAXException
	 */
	public MetadataProfile unmarshalMdProfile(String xmlFilename) throws JAXBException, SAXException;
	
	/**
	 * This method exports the meta data profile to the xml file through the giveen schema file.
	 * @param xmlFile, the xml file to output
	 * @throws JAXBException
	 * @throws SAXException
	 * @throws FileNotFoundException 
	 */
	public void marshalMdProfile(File xmlFile, MetadataProfile mdp) throws JAXBException, SAXException, FileNotFoundException;
	
	
	/**
	 * This method generates the meta data profile from the xml file.
	 * @param xmlFile, the xml file specified the content of the meta data information
	 * @return the MetadataProfile object
	 * @throws JAXBException
	 * @throws SAXException
	 */
	public MetadataProfile unmarshalMdProfile(File xmlFile) throws JAXBException, SAXException;
}
