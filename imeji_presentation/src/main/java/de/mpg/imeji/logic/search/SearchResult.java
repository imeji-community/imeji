/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.search;

import java.util.ArrayList;
import java.util.List;

import de.mpg.imeji.logic.search.util.SortHelper;
import de.mpg.imeji.logic.search.vo.SortCriterion;

public class SearchResult
{
    private int numberOfRecords = 0;
    private List<String> results = new ArrayList<String>();
    private String query = null;
    private SortCriterion sort = new SortCriterion();

    public SearchResult(List<String> unsortedResults, SortCriterion sort)
    {
        numberOfRecords = unsortedResults.size();
        if (sort != null)
            this.sort = sort;
        results = SortHelper.sort(unsortedResults, this.sort.getSortOrder());
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
