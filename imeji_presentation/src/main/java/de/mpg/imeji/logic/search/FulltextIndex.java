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
    public void setFulltextIndex(String fulltext);

    public String getFulltextIndex();

    public void indexFulltext();
}
