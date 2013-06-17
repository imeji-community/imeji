package de.mpg.imeji.logic.ingest.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

import de.mpg.imeji.logic.ingest.jaxb.JaxbGenericObject;
import de.mpg.imeji.logic.ingest.vo.Items;
import de.mpg.imeji.logic.vo.Item;

public class ItemParser
{
    /**
     * Parse a list of item
     * 
     * @param itemListXml
     * @return
     * @throws SAXException
     * @throws JAXBException
     */
    public List<Item> parseItemList(File itemListXmlFile) throws JAXBException, SAXException 
    {
        return new JaxbGenericObject<Items>(Items.class).unmarshal(itemListXmlFile).getItem();
    }

    /**
     * Parse a single item
     * 
     * @param itemXml
     * @return
     * @throws SAXException
     * @throws JAXBException
     */
    public Item parseItem(String itemXml) throws JAXBException, SAXException
    {
        return new JaxbGenericObject<Item>(Item.class).unmarshal(itemXml);
    }

    @SuppressWarnings("unused")
    private List<String> parseItemList2ListOfItems(File itemListXmlFile) throws Exception
    {
        List<String> l = new ArrayList<String>();
        List<Item> items = parseItemList(itemListXmlFile);
        for (Item item : items)
        {
            l.add(item.getId().toString());
        }
        return l;
    }
}
