/**
 * 
 */
package de.mpg.imeji.logic.ingest.jaxb.interfaces;

import java.io.File;
import java.io.FileNotFoundException;

import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

import de.mpg.imeji.logic.ingest.util.ImejiSchemaFilename;
import de.mpg.imeji.logic.ingest.vo.Items;

/**
 * @author hnguyen
 *
 */
public interface IJaxbItems {	
	
	public String xsdFilename = ImejiSchemaFilename.IMEJI_ITEMS_XSDFILE;
	
	/**
	 * This method exports the items to the xml file through the given schema file.
	 * @param xmlFileName, the xml filename to output
	 * @throws JAXBException
	 * @throws SAXException
	 * @throws FileNotFoundException 
	 */
	public void marshalItems( String xmlFileName, Items items) throws JAXBException, SAXException, FileNotFoundException;

	
	/**
	 * This method generates the items from the xml file.
	 * @param xmlFileName, the xml filename specified the content of the item information
	 * @return the MetadataProfile object
	 * @throws JAXBException
	 * @throws SAXException
	 */
	public Items unmarshalItems(String xmlFilename) throws JAXBException, SAXException;
	
	/**
	 * This method exports the items to the xml file through the given schema file.
	 * @param xmlFileName, the xml filename to output
	 * @throws JAXBException
	 * @throws SAXException
	 * @throws FileNotFoundException 
	 */
	public void marshalItems(File xmlFile, Items items) throws JAXBException, SAXException, FileNotFoundException;

	
	/**
	 * This method generates the items from the xml file.
	 * @param xmlFileName, the xml filename specified the content of the item information
	 * @return the MetadataProfile object
	 * @throws JAXBException
	 * @throws SAXException
	 */
	public Items unmarshalItems(File xmlFile) throws JAXBException, SAXException;
}
