package de.mpg.imeji.search;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import de.mpg.imeji.beans.Navigation;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.jena.controller.SearchCriterion;

public class QuickSearchBean implements Serializable
{ 
    private String searchString ="";
    private String selectedSearchType = "images";

    public String search() throws IOException 
    {	
    	Navigation navigation = (Navigation) BeanHelper.getApplicationBean(Navigation.class);
    	
    	if (getSelectedSearchType() == null) setSelectedSearchType("images");
    	
        if(getSelectedSearchType().equals("collections"))
        {
        	FacesContext.getCurrentInstance().getExternalContext().redirect(navigation.getCollectionsUrl() + "?q=" + searchString);
        }
        else if (getSelectedSearchType().equals("images"))
        {
        	List<SearchCriterion> scl = new ArrayList<SearchCriterion>();
            try 
            {
            	if (searchString.startsWith("\"") && searchString.endsWith("\""))
            	{
            		scl.addAll(URLQueryTransformer.transform2SCList("( ANY_METADATA=\"" + searchString +"\" )"));
            	}
            	else
            	{
            		for(String s : searchString.split("\\s"))
                	{
                		scl.addAll(URLQueryTransformer.transform2SCList("( ANY_METADATA=\"" + s +"\" )"));
                	}
            	}
			} 
            catch (Exception e) 
			{
				throw new RuntimeException("Error creating quicksearch query: " + e);
			}
        	FacesContext.getCurrentInstance().getExternalContext().redirect(navigation.getImagesUrl() + "?q=" + URLQueryTransformer.transform2URL(scl));
        }
        return "";
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
}
