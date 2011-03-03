package de.mpg.imeji.filter;

import java.util.ArrayList;
import java.util.List;

public class FiltersSession 
{
	private List<Filter> filters = new ArrayList<Filter>();
	private String wholeQuery = "";
	
	public FiltersSession() 
	{
		
	}
	
	

	public List<Filter> getFilters() {
		return filters;
	}

	public void setFilters(List<Filter> filters) {
		this.filters = filters;
	}

	public String getWholeQuery() {
		return wholeQuery;
	}

	public void setWholeQuery(String wholeQuery) {
		this.wholeQuery = wholeQuery;
	}
	
	
}
