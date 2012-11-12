/**
 * 
 */
package de.mpg.imeji.logic.ingest.jaxb.interfaces;


import java.util.List;

import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

import de.mpg.imeji.logic.ingest.util.ImejiNamespacePrefixMapper;
import de.mpg.imeji.logic.ingest.vo.Items;

/**
 * @author hnguyen
 *
 */
public interface IJaxbItems {	
	
	public String xsdFilename = ImejiNamespacePrefixMapper.IMEJI_ITEMS_XSDFILE;
	
	/**
	 * This method exports the items to the xml file through the given schema file.
	 * @param xmlFileName, the xml filename to output
	 * @throws JAXBException
	 * @throws SAXException
	 */
	public void marshalItems( String xmlFileName, Items items) throws JAXBException, SAXException;

	
	/**
	 * This method generates the items from the xml file.
	 * @param xmlFileName, the xml filename specified the content of the item information
	 * @return the MetadataProfile object
	 * @throws JAXBException
	 * @throws SAXException
	 */
	public Items unmarshalItems(String xmlFilename) throws JAXBException, SAXException;
}
