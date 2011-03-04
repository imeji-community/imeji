package de.mpg.imeji.filter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.collection.CollectionSessionBean;
import de.mpg.imeji.search.URLQueryTransformer;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.jena.controller.SearchCriterion;

public class FiltersBean
{
	private FiltersSession fs = (FiltersSession) BeanHelper.getSessionBean(FiltersSession.class);
	
	private int count = 0;
	
	public FiltersBean()
	{
	}
	
	public FiltersBean(String query, int count) 
	{	
		this.count = count;
		
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		
		String q = ec.getRequestParameterMap().get("q");
		String n = ec.getRequestParameterMap().get("f");
		
		if (n != null) n = n.toLowerCase();
		
		List<Filter> filters =  parseQuery(q, n);
		
		filters = setRemoveQueries(filters, q);
		
		fs.setFilters(filters);
	}
	
	private List<Filter> parseQuery(String q, String n)
	{
		List<Filter> filters = new ArrayList<Filter>();
		
		for (Filter f: fs.getFilters())
		{
			if (q != null && q.contains(f.getQuery()))
			{
				filters.add(f);
				q = q.replace(f.getQuery(), "");
			}
		}
		if (q != null && !"".equals(q.trim()))
		{
			Filter nf = new Filter(n, q, count);
			filters.add(nf);
		}
		return filters;
	}	
	
	private List<Filter> setRemoveQueries(List<Filter> filters, String q)
	{
		for (Filter f: filters)
		{
			f.setRemoveQuery(q.replace(f.getQuery(), ""));
		}
		return filters;
	}
	
}
