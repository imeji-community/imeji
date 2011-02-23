package de.mpg.imeji.filter;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
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
	private List<Filter> list = null;
	private SessionBean sb = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
	private CollectionSessionBean csb = (CollectionSessionBean) BeanHelper.getSessionBean(CollectionSessionBean.class);
	private int count = 0;
	public FiltersBean()
	{
		list = sb.getFilters();
	}
	
	public FiltersBean(String query, int count) 
	{	
		this.count = count;
		List<Filter> filters = parse(query);
		//filters = setLabels(filters);
		sb.setFilters(filters);
	}
	
	public List<Filter> parse(String query)
	{
		try 
		{	
			ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
			Iterator<String> params = ec.getRequestParameterNames();
			List<Filter> fList = new ArrayList<Filter>();
			while (params.hasNext()) 
			{
				String name = (String) params.next();
				if(name.matches("f[0-9]+"))
				{
					fList.add(new Filter(null, ec.getRequestParameterMap().get(name), count));
				}
				if("q".equals(name))
				{
					fList.add(new Filter("Search", ec.getRequestParameterMap().get(name), count));
				}
			}
			return fList;
		} 
		catch (Exception e) 
		{
			throw new RuntimeException("Error parsing query", e);
		}
		
	}
	
	private List<Filter> setLabels(List<Filter> filters)
	{
		for (Filter f : filters) 
		{
			Filter f1 = getFilter(f.getQuery());
			if (f1 != null)
			{
				f.setLabel(f1.getLabel());
			}
		}
		return filters;
	}
	
	
	public Filter getFilter(String query)
	{
		for (Filter f : sb.getFilters())
		{
			if (query != null && query.equals(f.getQuery()))
			{
				return f;
			}
		}
		return null;
	}
	
	
	public String addFilter(Filter filter)
	{
		list.add(filter);
		return "";
	}
	
	public String removeFilter(ActionEvent event)
	{
		Filter toRemove = (Filter) event.getComponent().getAttributes().get("filter"); 
		list.remove(toRemove);
		return "";	
	}

	public List<Filter> getList() {
		return sb.getFilters();
	}

	public void setList(List<Filter> filters) {
		this.list = filters;
	}
	
	
}
