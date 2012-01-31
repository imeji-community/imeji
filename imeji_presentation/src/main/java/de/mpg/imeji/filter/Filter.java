/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.imeji.filter;

import java.io.Serializable;
import java.net.URI;
import java.util.List;

import de.mpg.imeji.facet.Facet;
import de.mpg.imeji.search.URLQueryTransformer;
import de.mpg.jena.controller.SearchCriterion;

public class Filter extends Facet implements Serializable
{
	//private SearchCriterion filter;
	private String query ="";
	private URI collectionID;
	private String label = "Search";
	private int count = 0;
	private String removeQuery = "";
	private List<SearchCriterion> scList;
	
	public Filter(String label, String query, int count, FacetType type, URI metadataURI) 
	{
		super(null, label, count, type, metadataURI);
		this.label = label; 
		this.query = query;
		this.count = count;
		init();
	}
	
	public void init()
	{
		if (label == null) label = "Search";
		 
		try 
		{
			scList = URLQueryTransformer.transform2SCList(query);
			if (scList.size() == 1 && scList.get(0).getValue() != null) 
			{
				this.setMetadataURI(URI.create(scList.get(0).getValue()));
			}
		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

	public List<SearchCriterion> getScList() {
		return scList;
	}

	public void setScList(List<SearchCriterion> scList) {
		this.scList = scList;
	}

	
}
