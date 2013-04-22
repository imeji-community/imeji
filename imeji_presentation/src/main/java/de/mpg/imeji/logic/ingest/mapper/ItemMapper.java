/**
 * 
 */
package de.mpg.imeji.logic.ingest.mapper;

import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;

import de.mpg.imeji.logic.ingest.template.DuplicatedObject;
import de.mpg.imeji.logic.vo.Item;

/**
 * @author hnguyen
 */
public class ItemMapper
{
    

    private DuplicatedObject<Item, ?> dupItems;

    /**
     * @throws URISyntaxException
     */
    public ItemMapper(List<Item> itemList)
    {
        this.dupItems = this.process(itemList);
    }

    private DuplicatedObject<Item, ?> process(List<Item> itemList)
    {
    	DuplicatedObject<Item, ?> dupItems = new DuplicatedObject<Item, Object>();
        for (Item item : itemList)
        {
            Item itemAsFilename = dupItems.getHashTableFilename().get(item.getFilename());
            
            if (itemAsFilename == null)
            {
                dupItems.getHashTableFilename().put(new String(item.getFilename()), item);
            }
            else
            {
                dupItems.getDuplicateFilenames().add(new String(item.getFilename()));
            }
        }
        return dupItems;
    }

    public List<String> getDuplicateFilenames()
    {
        return this.dupItems.getDuplicateFilenames();
    }

    public boolean hasDuplicateFilenames()
    {
        return !this.getDuplicateFilenames().isEmpty();
    }

    private Collection<Item> getUniqueFilenameListsAsItemList()
    {
        return this.dupItems.getHashTableFilename().values();
    }

    private Collection<String> getUniqueFilenameListsAsStringList()
    {
        return this.dupItems.getHashTableFilename().keySet();
    }

    public Collection<Item> getMappedItemObjects()
    {
        return this.getUniqueFilenameListsAsItemList();
    }

    public Collection<String> getMappedItemKeys()
    {
        return this.getUniqueFilenameListsAsStringList();
    }
}
