/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.search.vo;

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

    public void setSortOrder(SortOrder sortOrder)
    {
        this.sortOrder = sortOrder;
    }

    public SortOrder getSortOrder()
    {
        return sortOrder;
    }

    // public void setSortingCriterion(SearchIndexes sortingCriterion)
    // {
    // this.sortingCriterion = sortingCriterion;
    // }
    //
    // public SearchIndexes getSortingCriterion()
    // {
    // return sortingCriterion;
    // }
    public void setIndex(SearchIndex index)
    {
        this.index = index;
    }

    public SearchIndex getIndex()
    {
        return index;
    }
}
