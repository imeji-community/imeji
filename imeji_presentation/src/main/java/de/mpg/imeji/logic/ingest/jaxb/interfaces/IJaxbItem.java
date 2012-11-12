/**
 * 
 */
package de.mpg.imeji.logic.ingest.jaxb.interfaces;


import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

import de.mpg.imeji.logic.ingest.util.ImejiNamespacePrefixMapper;
import de.mpg.imeji.logic.vo.Item;

/**
 * @author hnguyen
 *
 */
public interface IJaxbItem {	
	
	public String xsdFilename = ImejiNamespacePrefixMapper.IMEJI_ITEM_XSDFILE;
	
	/**
	 * This method exports the item to the xml file through the given schema file.
	 * @param xmlFile, the xml file to output
	 * @throws JAXBException
	 * @throws SAXException
	 */
	public void marshalItem( String xmlFile, Item item) throws JAXBException, SAXException;

	
	/**
	 * This method generates the item from the xml file.
	 * @param xmlFile, the xml file specified the content of the item information
	 * @return the MetadataProfile object
	 * @throws JAXBException
	 * @throws SAXException
	 */
	public Item unmarshalItem(String xmlFile) throws JAXBException, SAXException;
}
