/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.mpg.imeji.logic.search.vo.ComparableSearchResult;
import de.mpg.imeji.logic.search.vo.SortCriterion;

public class SearchResult
{
    private int numberOfRecords = 0;
    private List<String> results = new ArrayList<String>();
    private String query = null;
    private SortCriterion sort = null;

    public SearchResult(List<String> unsortedResults)
    {
        numberOfRecords = unsortedResults.size();
        long before = System.currentTimeMillis();
        List<ComparableSearchResult> csrl = getAsComparableSearchResultList(unsortedResults);
        if (!csrl.isEmpty() && !csrl.get(0).getSortValue().equals(""))
        {
            long sort = System.currentTimeMillis();
            Collections.sort(csrl);
            System.out.println("[SearchResult] sorting in " + Long.valueOf(System.currentTimeMillis() - sort) + " ms of " + numberOfRecords + " items");
        }
        this.results = getAsStringList(csrl);
        System.out.println("[SearchResult] Init in " + Long.valueOf(System.currentTimeMillis() - before) + " ms of "
                + numberOfRecords + " items");
    }

    private List<ComparableSearchResult> getAsComparableSearchResultList(List<String> l)
    {
        List<ComparableSearchResult> csrl = new ArrayList<ComparableSearchResult>(l.size());
        for (String s : l)
        {
            csrl.add(new ComparableSearchResult(s));
        }
        return csrl;
    }

    private List<String> getAsStringList(List<ComparableSearchResult> unsortedResults)
    {
        List<String> sortedResults = new ArrayList<String>(unsortedResults.size());
        for (ComparableSearchResult csr : unsortedResults)
        {
            sortedResults.add(csr.getValue());
        }
        return sortedResults;
    }

    public int getNumberOfRecords()
    {
        return numberOfRecords;
    }

    public void setNumberOfRecords(int numberOfRecords)
    {
        this.numberOfRecords = numberOfRecords;
    }

    public List<String> getResults()
    {
        return results;
    }

    public void setResults(List<String> results)
    {
        this.results = results;
    }

    public String getQuery()
    {
        return query;
    }

    public void setQuery(String query)
    {
        this.query = query;
    }

    public SortCriterion getSort()
    {
        return sort;
    }

    public void setSort(SortCriterion sort)
    {
        this.sort = sort;
    }
}
