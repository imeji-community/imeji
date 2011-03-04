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
	
	public boolean isFilter(String name)
	{
		for (Filter f : filters)
		{
			if (f.getLabel().equalsIgnoreCase(name)) return true;
			if (f.getLabel().equalsIgnoreCase("no " + name)) return true;
		}
		return false;
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
