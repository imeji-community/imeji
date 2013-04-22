package de.mpg.imeji.logic.ingest.template;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

@SuppressWarnings("hiding")
public class DuplicatedObject<Class, T> {

    private List<String> duplicateFilenames;
    private Hashtable<String, Class> hashTableFilename;

    public DuplicatedObject()
    {
        this.setDuplicateFilenames(new ArrayList<String>());
        this.setHashTableFilename(new Hashtable<String, Class>());
    }

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

    /**
     * @return the hashTableFilename
     */
    public Hashtable<String, Class> getHashTableFilename()
    {
        return hashTableFilename;
    }

    /**
     * @param hashtable the hashTableFilename to set
     */
    public void setHashTableFilename(Hashtable<String, Class> hashtable)
    {
        this.hashTableFilename = hashtable;
    }
}
