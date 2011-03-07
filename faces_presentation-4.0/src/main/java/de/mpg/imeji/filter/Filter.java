package de.mpg.imeji.filter;

import java.net.URI;

import de.mpg.jena.controller.SearchCriterion;

public class Filter 
{
	private SearchCriterion filter;
	private String query ="";
	private URI collectionID;
	private String label = "Search";
	private int count = 0;
	private String removeQuery = "";
	
	public Filter(String label, SearchCriterion filter, URI collectionId) 
	{
		this.filter = filter;
		if (label != null) this.label = label;
		this.collectionID = collectionId;
	}
	
	public Filter(String label, URI collectionId, int count) 
	{
		this.count = count;
		if (label != null) this.label = label;
		this.collectionID = collectionId;
		init();
	}
	
	public Filter(String label, String query, int count) 
	{
		this.label = label; 
		this.query = query;
		this.count = count;
		this.removeQuery = removeQuery;
		init();
	}
	
	public void init()
	{
//		if("".equals(query))label = "All";
//		else if (label == null) label = "No name";
		 if (label == null) label = "Search";
	}

	public SearchCriterion getFilter() {
		return filter;
	}

	public void setFilter(SearchCriterion filter) {
		this.filter = filter;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public URI getCollectionID() {
		return collectionID;
	}

	public void setCollectionID(URI collectionID) {
		this.collectionID = collectionID;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getRemoveQuery() {
		return removeQuery;
	}

	public void setRemoveQuery(String removeQuery) {
		this.removeQuery = removeQuery;
	}
	
}
