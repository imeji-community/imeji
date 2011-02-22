package de.mpg.imeji.filter;

import java.net.URI;

import de.mpg.jena.controller.SearchCriterion;

public class Filter 
{
	private SearchCriterion filter;
	private URI collectionID;
	private String label;
	
	public Filter(String label, SearchCriterion filter, URI collectionId) 
	{
		this.filter = filter;
		this.label = label;
		this.collectionID = collectionId;
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
	
	
}
