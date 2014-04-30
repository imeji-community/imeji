package de.mpg.imeji.logic.search.vo;

import java.util.List;

import de.mpg.imeji.logic.search.vo.SortCriterion.SortOrder;
import de.mpg.j2j.helper.SortHelper;

/**
 * Search result (as {@link String} entry of a {@link List} of uris) which can be sorted
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class ComparableSearchResult implements Comparable<ComparableSearchResult>
{
    private String value = null;
    private String sortValue = "";
    private SortOrder order = SortOrder.DESCENDING;

    public ComparableSearchResult(String s, SortOrder order)
    {
        this.value = SortHelper.removeSortValue(s);
        this.sortValue = SortHelper.parseSortValue(s);
        this.order = order;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public String getSortValue()
    {
        return sortValue;
    }

    public void setSortValue(String sortValue)
    {
        this.sortValue = sortValue;
    }

    @Override
    public int compareTo(ComparableSearchResult o)
    {
        return o.getSortValue().compareToIgnoreCase(sortValue) * orderAsInteger();
    }

    private int orderAsInteger()
    {
        if (SortOrder.DESCENDING.equals(order))
        {
            return 1;
        }
        return -1;
    }

    public void setOrder(SortOrder order)
    {
        this.order = order;
    }

    public SortOrder getOrder()
    {
        return order;
    }
}
