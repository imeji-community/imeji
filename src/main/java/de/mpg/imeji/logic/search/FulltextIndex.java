package de.mpg.imeji.logic.search;

/**
 * Interface for object implementing fulltext search (i.e searchable via full text)
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public interface FulltextIndex
{
    /**
     * Setter
     * 
     * @param fulltext
     */
    public void setFulltextIndex(String fulltext);

    /**
     * Getter
     * 
     * @return
     */
    public String getFulltextIndex();

    /**
     * Perform operation to create fulltext seach value add set the value
     */
    public void indexFulltext();
}
