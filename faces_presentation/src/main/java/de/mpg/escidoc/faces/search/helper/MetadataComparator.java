package de.mpg.escidoc.faces.search.helper;

import java.util.Comparator;

import de.mpg.escidoc.faces.metadata.Metadata;

/**
 * Comparator between 2 Metadata according to their alphabetic index.
 * @author saquet
 *
 * @param <T>
 */
public class MetadataComparator<T> implements Comparator<T>
{
    public MetadataComparator()
    {
        // TODO Auto-generated constructor stub
    }
    
    /**
     * Compare 2 Metadata according to their alphabetic index.
     */
    public int compare(T o1, T o2)
    {
        if (o1 instanceof Metadata && o1 instanceof Metadata)
        {
            String i1 = ( (Metadata) o1).getIndex();
            String i2 = ( (Metadata) o2).getIndex();
             
            return ((String) i1).toLowerCase().compareTo(((String) i2).toLowerCase());
        }
        
        return 0;
    }
}
