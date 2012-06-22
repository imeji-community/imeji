package de.mpg.imeji.logic.factory;

import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Item;

public class ItemFactory extends ImejiFactory
{
    public static Item create(CollectionImeji collection)
    {
        Item item = new Item();
        item.setCollection(collection.getId());
        item.getMetadataSets().add(MetadataSetFactory.create(item, collection.getProfile()));
        return item;
    }
}
