/**
 * 
 */
package de.mpg.imeji.logic.ingest.jaxb.interfaces;

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
	 */
	public void marshalMdProfile( String xmlFile, MetadataProfile mdp) throws JAXBException, SAXException;
	
	
	/**
	 * This method generates the meta data profile from the xml file.
	 * @param xmlFile, the xml file specified the content of the meta data information
	 * @return the MetadataProfile object
	 * @throws JAXBException
	 * @throws SAXException
	 */
	public MetadataProfile unmarshalMdProfile(String xmlFile) throws JAXBException, SAXException;
}
