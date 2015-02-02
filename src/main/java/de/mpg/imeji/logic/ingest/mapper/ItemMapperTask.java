/**
 * 
 */
package de.mpg.imeji.logic.ingest.mapper;

import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;

import javax.swing.SwingWorker;

import de.mpg.imeji.logic.ingest.template.DuplicatedObject;
import de.mpg.imeji.logic.vo.Item;

/**
 * @author hnguyen
 */
public class ItemMapperTask extends SwingWorker<Collection<Item>, Void>
{
    private DuplicatedObject<Item, ?> dupItems;
    private Collection<Item> itemList;

    /**
     * @throws URISyntaxException
     */
    public ItemMapperTask(List<Item> itemList)
    {
        this.itemList = itemList;
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

    private Collection<Item> getMappedItemObjects()
    {
        return this.getUniqueFilenameListsAsItemList();
    }

    public Collection<String> getMappedItemKeys()
    {
        return this.getUniqueFilenameListsAsStringList();
    }

    private DuplicatedObject<Item, ?> process(Collection<Item> itemList)
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

    @Override
    protected Collection<Item> doInBackground() 
    {
        this.dupItems = this.process(this.itemList);
        return this.getMappedItemObjects();
    }

    @Override
    public void done()
    {
    }
}
