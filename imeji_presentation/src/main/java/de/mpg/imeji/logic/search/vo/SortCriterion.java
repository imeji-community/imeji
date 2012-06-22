/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.imeji.logic.search.vo;


public class SortCriterion
{
    public enum SortOrder {
        ASCENDING, DESCENDING;
    }
    
    private SearchIndexes sortingCriterion;
    
    private SortOrder sortOrder;

    public SortCriterion(SearchIndexes sc, SortOrder so)
    {
        this.sortingCriterion = sc;
        this.sortOrder = so;
    }
    
    public SortCriterion()
    {
        this.sortOrder = SortOrder.ASCENDING;
    }
    
    public void setSortOrder(SortOrder sortOrder)
    {
        this.sortOrder = sortOrder;
    }

    public SortOrder getSortOrder()
    {
        return sortOrder;
    }

    public void setSortingCriterion(SearchIndexes sortingCriterion)
    {
        this.sortingCriterion = sortingCriterion;
    }

    public SearchIndexes getSortingCriterion()
    {
        return sortingCriterion;
    }
    
}
