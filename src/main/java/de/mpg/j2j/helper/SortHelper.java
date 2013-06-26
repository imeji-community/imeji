package de.mpg.j2j.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import de.mpg.imeji.logic.search.vo.ComparableSearchResult;
import de.mpg.imeji.logic.search.vo.SortCriterion.SortOrder;

/**
 * Helper for sort parameters in SPARQL queries
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class SortHelper
{
    public final static String SORT_VALUE_REGEX = "XXX_SORT_VALUE_PATTERN_XXX";
    public static Pattern SORT_VALUE_PATTERN = Pattern.compile(SORT_VALUE_REGEX);

    /**
     * If a list has a sortValue parameter, sort it, otherwise remove the sortValue pattern
     * 
     * @param l
     * @return
     */
    public final static List<String> sort(List<String> l, SortOrder order)
    {
        if (SortHelper.isListToSort(l))
        {
            List<ComparableSearchResult> csrl = SortHelper.toComparableSearchResultList(l, order);
            Collections.sort(csrl);
            return toStringList(csrl);
        }
        return SortHelper.removeSortValue(l);
    }

    /**
     * Extract the value used by the sortering for this {@link String}. If no Sort value, return an emtpy {@link String}
     * 
     * @param s
     * @return
     */
    public final static String parseSortValue(String s)
    {
        String[] t = s.split(SortHelper.SORT_VALUE_REGEX);
        if (t.length > 1)
        {
            return t[1];
        }
        return "";
    }

    /**
     * Remove the Sort value in the given String.
     * 
     * @param s
     * @return
     */
    public final static String removeSortValue(String s)
    {
        return SORT_VALUE_PATTERN.split(s)[0];
    }

    /**
     * True if the {@link List} of String is sortable (i.e. that the String are defined with a sort value)
     * 
     * @param l
     * @return
     */
    public final static boolean isListToSort(List<String> l)
    {
        if (!l.isEmpty())
        {
            return SORT_VALUE_PATTERN.split(l.get(0)).length > 1;
        }
        return false;
    }

    /**
     * A a sort value to a {@link String}. The String is then sortable by imeji Sort implementation
     * 
     * @param s
     * @param sortValue
     * @return
     */
    public final static String addSortValue(String s, String sortValue)
    {
        return s + SORT_VALUE_REGEX + sortValue;
    }

    /**
     * Remove the sort value to all element of the {@link List}
     * 
     * @param l
     * @return
     */
    public final static List<String> removeSortValue(List<String> l)
    {
        if (!isListToSort(l))
        {
            return l;
        }
        List<String> l1 = new ArrayList<String>(l.size());
        for (String s : l)
        {
            l1.add(removeSortValue(s));
        }
        return l1;
    }

    /**
     * Transform a {@link List} of {@link ComparableSearchResult} into a {@link List} of {@link String}
     * 
     * @param unsortedResults
     * @return
     */
    public final static List<String> toStringList(List<ComparableSearchResult> unsortedResults)
    {
        List<String> sortedResults = new ArrayList<String>(unsortedResults.size());
        for (ComparableSearchResult csr : unsortedResults)
        {
            sortedResults.add(csr.getValue());
        }
        return sortedResults;
    }

    /**
     * Transform a {@link List} of {@link String} into a {@link List} of {@link ComparableSearchResult} for the give
     * {@link SortOrder}
     * 
     * @param l
     * @param order
     * @return
     */
    public final static List<ComparableSearchResult> toComparableSearchResultList(List<String> l, SortOrder order)
    {
        List<ComparableSearchResult> csrl = new ArrayList<ComparableSearchResult>(l.size());
        for (String s : l)
        {
            csrl.add(new ComparableSearchResult(s, order));
        }
        return csrl;
    }
}
