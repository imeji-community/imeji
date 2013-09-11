/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.search.vo;

import de.mpg.imeji.logic.search.Search;

/**
 * A sort criterion for a {@link Search}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class SortCriterion
{
    public enum SortOrder
    {
        ASCENDING, DESCENDING;
    }

    private SearchIndex index;
    // private SearchIndexes sortingCriterion;
    private SortOrder sortOrder;

    public SortCriterion(SearchIndex index, SortOrder so)
    {
        this.index = index;
        this.sortOrder = so;
    }

    public SortCriterion()
    {
        this.sortOrder = SortOrder.ASCENDING;
    }

    /**
     * Toggle the order the the {@link SortCriterion}
     */
    public void toggle()
    {
        sortOrder = (SortOrder.ASCENDING.equals(sortOrder) ? SortOrder.DESCENDING : SortOrder.ASCENDING);
    }

    public void setSortOrder(SortOrder sortOrder)
    {
        this.sortOrder = sortOrder;
    }

    public SortOrder getSortOrder()
    {
        return sortOrder;
    }

    public void setIndex(SearchIndex index)
    {
        this.index = index;
    }

    public SearchIndex getIndex()
    {
        return index;
    }
}
