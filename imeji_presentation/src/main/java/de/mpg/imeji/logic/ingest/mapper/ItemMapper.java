/**
 * 
 */
package de.mpg.imeji.logic.ingest.mapper;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;

import de.mpg.imeji.logic.vo.Item;

/**
 * @author hnguyen
 */
public class ItemMapper
{
    private class DuplicateItemObject
    {
        // private List<String> duplicateIDs;
        private List<String> duplicateFilenames;
        // private List<String> duplicateEscidocIDs;
        // Hashtable<String, Item> hashTableItemId;
        Hashtable<String, Item> hashTableFilename;

        // Hashtable<String, Item> hashTableEscidocId;
        public DuplicateItemObject()
        {
            // this.setDuplicateIDs(new ArrayList<String>());
            this.setDuplicateFilenames(new ArrayList<String>());
            // this.setDuplicateEscidocIDs(new ArrayList<String>());
            // this.setHashTableItemId(new Hashtable<String, Item>());
            this.setHashTableFilename(new Hashtable<String, Item>());
            // this.setHashTableEscidocId(new Hashtable<String, Item>());
        }

        // /**
        // * @return the duplicateIDs
        // */
        // public List<String> getDuplicateIDs() {
        // return duplicateIDs;
        // }
        //
        // /**
        // * @param duplicateIDs the duplicateIDs to set
        // */
        // public void setDuplicateIDs(List<String> duplicateIDs) {
        // this.duplicateIDs = duplicateIDs;
        // }
        /**
         * @return the duplicateFilenames
         */
        public List<String> getDuplicateFilenames()
        {
            return duplicateFilenames;
        }

        /**
         * @param duplicateFilenames the duplicateFilenames to set
         */
        public void setDuplicateFilenames(List<String> duplicateFilenames)
        {
            this.duplicateFilenames = duplicateFilenames;
        }

        // /**
        // * @return the duplicateEscidocIDs
        // */
        // public List<String> getDuplicateEscidocIDs() {
        // return duplicateEscidocIDs;
        // }
        //
        // /**
        // * @param duplicateEscidocIDs the duplicateEscidocIDs to set
        // */
        // public void setDuplicateEscidocIDs(List<String> duplicateEscidocIDs) {
        // this.duplicateEscidocIDs = duplicateEscidocIDs;
        // }
        //
        // /**
        // * @return the hashTableItemId
        // */
        // public Hashtable<String, Item> getHashTableItemId() {
        // return hashTableItemId;
        // }
        //
        // /**
        // * @param hashTableItemId the hashTableItemId to set
        // */
        // public void setHashTableItemId(Hashtable<String, Item> hashTableItemId) {
        // this.hashTableItemId = hashTableItemId;
        // }
        /**
         * @return the hashTableFilename
         */
        public Hashtable<String, Item> getHashTableFilename()
        {
            return hashTableFilename;
        }

        /**
         * @param hashTableFilename the hashTableFilename to set
         */
        public void setHashTableFilename(Hashtable<String, Item> hashTableFilename)
        {
            this.hashTableFilename = hashTableFilename;
        }
        // /**
        // * @return the hashTableEscidocId
        // */
        // public Hashtable<String, Item> getHashTableEscidocId() {
        // return hashTableEscidocId;
        // }
        //
        // /**
        // * @param hashTableEscidocId the hashTableEscidocId to set
        // */
        // public void setHashTableEscidocId(Hashtable<String, Item> hashTableEscidocId) {
        // this.hashTableEscidocId = hashTableEscidocId;
        // }
    }

    private DuplicateItemObject dupItems;

    /**
     * @throws URISyntaxException
     */
    public ItemMapper(List<Item> itemList)
    {
        this.dupItems = this.process(itemList);
    }

    private DuplicateItemObject process(List<Item> itemList)
    {
        DuplicateItemObject dupItems = new DuplicateItemObject();
        for (Item item : itemList)
        {
            // Item itemAsId = dupItems.getHashTableItemId().get(item.getId().toString());
            Item itemAsFilename = dupItems.getHashTableFilename().get(item.getFilename());
            // Item itemAsEscidocId = dupItems.getHashTableEscidocId().get(item.getEscidocId());
            // if (itemAsId == null) {
            // dupItems.getHashTableItemId().put(new String(item.getId().toString()), item);
            // } else {
            // duplicateItemIdKeyList.add(new String(item.getId().toString()));
            // }
            if (itemAsFilename == null)
            {
                dupItems.getHashTableFilename().put(new String(item.getFilename()), item);
            }
            else
            {
                dupItems.getDuplicateFilenames().add(new String(item.getFilename()));
            }
            // if (itemAsEscidocId == null) {
            // dupItems.getHashTableEscidocId().put(new String(item.getEscidocId()), item);
            // } else {
            // duplicateEscidocIdKeyList.add(new String(item.getEscidocId()));
            // }
        }
        return dupItems;
    }

    // public List<String> getDuplicateIDs() {
    // return this.dupItems.getDuplicateIDs();
    // }
    //
    // public boolean hasDuplicateIDs() {
    // return !this.getDuplicateIDs().isEmpty();
    // }
    public List<String> getDuplicateFilenames()
    {
        return this.dupItems.getDuplicateFilenames();
    }

    public boolean hasDuplicateFilenames()
    {
        return !this.getDuplicateFilenames().isEmpty();
    }

    // public List<String> getDuplicateEscidocIDs() {
    // return this.dupItems.getDuplicateEscidocIDs();
    // }
    //
    // public boolean hasDuplicateEscidocIDs() {
    // return !this.getDuplicateEscidocIDs().isEmpty();
    // }
    // private List<Item> getUniqueFilenameListsAsItemList() {
    // return new ArrayList<Item>(this.dupItems.hashTableFilename.values());
    // }
    private Collection<Item> getUniqueFilenameListsAsItemList()
    {
        return this.dupItems.hashTableFilename.values();
    }

    private Collection<String> getUniqueFilenameListsAsStringList()
    {
        return this.dupItems.hashTableFilename.keySet();
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
