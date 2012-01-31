/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.jena.search;

import java.util.ArrayList;
import java.util.List;

import de.mpg.jena.controller.SortCriterion;


public class SearchResult 
{
	private int numberOfRecords = 0;
	private List<String> results = new ArrayList<String>();
	private String query = null;
	private SortCriterion sort = null;
	
	public SearchResult(List<String> results) 
	{
		this.results = results;
		numberOfRecords = results.size();
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

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public SortCriterion getSort() {
		return sort;
	}

	public void setSort(SortCriterion sort) {
		this.sort = sort;
	}
	
}
