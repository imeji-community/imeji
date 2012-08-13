package de.mpg.imeji.logic.search.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import de.mpg.imeji.logic.search.vo.ComparableSearchResult;
import de.mpg.imeji.logic.search.vo.SortCriterion.SortOrder;

public class SortHelper
{
    public final static String SORT_VALUE_REGEX = "XXX_SORT_VALUE_PATTERN_XXX";
    private static Pattern SORT_VALUE_PATTERN = Pattern.compile(SORT_VALUE_REGEX);

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
            long a = System.currentTimeMillis();
            Collections.sort(csrl);
            System.out.println("Sort: " + Long.valueOf(System.currentTimeMillis() - a));
            return toStringList(csrl);
        }
        return SortHelper.removeSortValue(l);
    }

    public final static String parseSortValue(String s)
    {
        String[] t = s.split(SortHelper.SORT_VALUE_REGEX);
        if (t.length > 1)
        {
            return t[1];
        }
        return "";
    }

    public final static String removeSortValue(String s)
    {
        return SORT_VALUE_PATTERN.split(s)[0];
    }

    public final static boolean isListToSort(List<String> l)
    {
        if (!l.isEmpty())
        {
            return SORT_VALUE_PATTERN.split(l.get(0)).length > 1;
        }
        return false;
    }

    public final static String addSortValue(String s, String sortValue)
    {
        return s + SORT_VALUE_REGEX + sortValue;
    }

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

    public final static List<String> toStringList(List<ComparableSearchResult> unsortedResults)
    {
        List<String> sortedResults = new ArrayList<String>(unsortedResults.size());
        for (ComparableSearchResult csr : unsortedResults)
        {
            sortedResults.add(csr.getValue());
        }
        return sortedResults;
    }

    public final static List<ComparableSearchResult> toComparableSearchResultList(List<String> l, SortOrder order)
    {
        long a = System.currentTimeMillis();
        List<ComparableSearchResult> csrl = new ArrayList<ComparableSearchResult>(l.size());
        for (String s : l)
        {
            csrl.add(new ComparableSearchResult(s, order));
        }
        System.out.println("toComparableSearchResultList: " + Long.valueOf(System.currentTimeMillis() - a));
        return csrl;
    }
}
