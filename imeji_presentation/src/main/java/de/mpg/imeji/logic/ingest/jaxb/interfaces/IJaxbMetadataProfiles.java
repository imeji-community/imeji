/**
 * 
 */
package de.mpg.imeji.logic.ingest.jaxb.interfaces;

import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

import de.mpg.imeji.logic.ingest.util.ImejiNamespacePrefixMapper;
import de.mpg.imeji.logic.ingest.vo.MetadataProfiles;

/**
 * @author hnguyen
 *
 */
public interface IJaxbMetadataProfiles {
	public String xsdFile = ImejiNamespacePrefixMapper.IMEJI_METADATAPROFILE_XSDFILE;
	/**
	 * This method exports the meta data profiles to the xml file through the given schema file.
	 * @param xmlFile, the xml file to output
	 * @throws JAXBException
	 * @throws SAXException
	 */
	public void marshalMdProfiles( String xmlFile, MetadataProfiles mdp) throws JAXBException, SAXException;	
	
	/**
	 * This method generates the meta data profiles from the xml file.
	 * @param xmlFile, the xml file specified a list of meta data profile
	 * @return the MetadataProfile object
	 * @throws JAXBException
	 * @throws SAXException
	 */	
	public MetadataProfiles unmarshalMdProfiles(String xmlFile) throws JAXBException, SAXException;
}
