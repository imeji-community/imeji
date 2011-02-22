package de.mpg.imeji.filter;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

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
	
	public FiltersBean()
	{
		list = sb.getFilters();
	}
	
	public FiltersBean(String query) 
	{	
		sb.setFilters(parse(query));
	}
	
	public List<Filter> parse(String query)
	{
		try 
		{
			URLQueryTransformer transformer = new URLQueryTransformer();
			List<Filter> fList = new ArrayList<Filter>();
			List<List<SearchCriterion>> scList = transformer.transform2SCList(query);
			if (scList.size() > 0)
			{
				for (SearchCriterion sc : scList.get(0))
				{
					URI coll = null;
					if ( csb.getActive() != null) coll = csb.getActive().getId();
					fList.add(new Filter(sc.getValue(), sc, coll));
				}
			}
			return fList;
		} 
		catch (Exception e) 
		{
			throw new RuntimeException("Error parsing query", e);
		}
		
	}
	
	public String addFilter(Filter filter)
	{
		list.add(filter);
		return "";
	}
	
	public String removeFilter(ActionEvent event)
	{
		Object obj = event.getComponent().getAttributes().get("filter"); 
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
