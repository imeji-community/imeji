package de.mpg.imeji.logic.ingest.parser;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

import de.mpg.imeji.logic.ingest.jaxb.JaxbIngestProfile;
import de.mpg.imeji.logic.ingest.jaxb.interfaces.IJaxbItem;
import de.mpg.imeji.logic.ingest.jaxb.interfaces.IJaxbMetadataProfile;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.presentation.ingest.IngestBean;

public class ItemParser
{
    /**
     * Parse a list of item
     * 
     * @param itemListXml
     * @return
     */
    public List<Item> parseItemList(String itemListXml)
    {
        List<Item> l = new ArrayList<Item>();
        //TODO
        // here is done the parsing. The results is written into the list l
        // You can parse the list completely at once or parse one item after the other like I propose it here:
        // Perser might use the item.xsd, that is created by ItemSchemaFactory
        for (String itemXml : parseItemList2ListOfItems(itemListXml))
        {
            l.add(parseItem(itemXml));
        }
        return l;
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
      //TODO
        return item;
    }

    private List<String> parseItemList2ListOfItems(String itemListXml)
    {
        List<String> l = new ArrayList<String>();
      //TODO
        return l;
    }
    
    public Item getItems( String xmlFile ) throws JAXBException, SAXException
	{
    	return new JaxbIngestProfile().unmarshalItem(xmlFile);
	}
}
