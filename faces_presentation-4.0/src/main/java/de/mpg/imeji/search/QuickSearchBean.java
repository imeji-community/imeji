package de.mpg.imeji.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import de.mpg.imeji.collection.CollectionsBean;
import de.mpg.imeji.collection.CollectionsSearchResultBean;
import de.mpg.imeji.image.ImagesBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.jena.controller.SearchCriterion;

public class QuickSearchBean implements Serializable
{ 
    private String searchString ="";
    private String selectedSearchType = "images";

    public QuickSearchBean() {
	}

    public void setSearchString(String searchString)
    {
        this.searchString = searchString;
    }

    public String getSearchString()
    {
        return searchString;
    }

    public void setSelectedSearchType(String selectedSearchType)
    {
        this.selectedSearchType = selectedSearchType;
    }

    public String getSelectedSearchType()
    {
        if (selectedSearchType == null) selectedSearchType = "images";
    	return selectedSearchType;
    }

    public void selectedSearchTypeListener(ValueChangeEvent event)
    {
    	if (event.getNewValue() != null && !event.getNewValue().equals(event.getOldValue()))
    	{
    		selectedSearchType = (String) event.getNewValue();
    	}
    }
    
    public String search()
    {
    	if (getSelectedSearchType() == null) setSelectedSearchType("images");
    	
        if(getSelectedSearchType().equals("collections"))
        {
            //CollectionsSearchResultBean bean = (CollectionsSearchResultBean)BeanHelper.getSessionBean(CollectionsSearchResultBean.class);
        	CollectionsBean bean = (CollectionsBean)BeanHelper.getSessionBean(CollectionsBean.class);
            bean.setQuery(searchString);
            return "pretty:collections";
        }
        else if (getSelectedSearchType().equals("images"))
        {
            ImagesBean bean = (ImagesBean)BeanHelper.getSessionBean(ImagesBean.class);
            //bean.setQuery("( ANY_METADATA=\"" + searchString +"\" )");
            try 
            {
				List<SearchCriterion> scl = URLQueryTransformer.transform2SCList("( ANY_METADATA=\"" + searchString +"\" )");
				bean.setQuery(URLQueryTransformer.transform2URL(scl));
			} catch (Exception e) 
			{
				throw new RuntimeException("Error creating quicksearch query: " + e);
			}
            return "pretty:images";
        }
        return "pretty:";
    }
    
    
}
