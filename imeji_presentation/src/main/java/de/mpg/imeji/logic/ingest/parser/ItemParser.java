package de.mpg.imeji.logic.ingest.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

import de.mpg.imeji.logic.ingest.jaxb.JaxbIngestProfile;
import de.mpg.imeji.logic.ingest.jaxb.JaxbUtil;
import de.mpg.imeji.logic.vo.Item;

public class ItemParser
{
    /**
     * Parse a list of item
     * 
     * @param itemListXml
     * @return
     */
    public List<Item> parseItemList(File itemListXmlFile)
    {
        List<Item> itemList = new ArrayList<Item>();
        //TODO
        // here is done the parsing. The results is written into the list l
        // You can parse the list completely at once or parse one item after the other like I propose it here:
        // Parser might use the item.xsd, that is created by ItemSchemaFactory
        
		try {
			itemList = new JaxbIngestProfile().unmarshalItems(itemListXmlFile).getItem();
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		
        return itemList;
    }

    /**
     * Parse a single item
     * 
     * @param itemXml
     * @return
     */
    public Item parseItem(String itemXml)
    {
        Item item = new Item();
        
        try {
			item = new JaxbIngestProfile().unmarshalItem(itemXml);
		} catch (JAXBException e) {			
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
        
        return item;
    }

	@SuppressWarnings("unused")
	private List<String> parseItemList2ListOfItems(File itemListXmlFile)
    {
        List<String> l = new ArrayList<String>();
        
        List<Item> items = parseItemList(itemListXmlFile);
       
        for (Item item : items) {
			l.add(item.getId().toString());
		}
        
        return l;
    }
}
