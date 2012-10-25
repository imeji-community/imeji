package de.mpg.imeji.logic.ingest.parser;

import java.util.ArrayList;
import java.util.List;

import de.mpg.imeji.logic.vo.Item;

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
        // here is done the parsing. The results is written into the list l
        // You can parse the list completely at once or parse one item after the other like I propose it here:
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
        return item;
    }

    private List<String> parseItemList2ListOfItems(String itemListXml)
    {
        List<String> l = new ArrayList<String>();
        return l;
    }
}
