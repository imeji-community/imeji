/**
 * 
 */
package de.mpg.imeji.logic.ingest.jaxb.interfaces;

import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

import de.mpg.imeji.logic.ingest.util.ImejiNamespacePrefixMapper;
import de.mpg.imeji.logic.ingest.vo.IngestProfile;

/**
 * @author hnguyen
 *
 */
public interface IJaxbIngestProfile {
	public String xsdFile = ImejiNamespacePrefixMapper.IMEJI_INGEST_PROFILE_XSDFILE;
	/**
	 * This method exports the meta data profiles and the items to the xml file through the given schema file.
	 * @param xmlFile, the xml file to output
	 * @throws JAXBException
	 * @throws SAXException
	 */
	public void marshalIngestProfile( String xmlFile, IngestProfile ingestProfile) throws JAXBException, SAXException;	
	
	/**
	 * This method generates the ingest profile from the xml file.
	 * @param xmlFile, the xml file specified a list of ingest profile
	 * @return the MetadataProfile object
	 * @throws JAXBException
	 * @throws SAXException
	 */	
	public IngestProfile unmarshalIngestProfile(String xmlFile) throws JAXBException, SAXException;

}
