package de.mpg.imeji.search;

import java.io.Serializable;

import de.mpg.imeji.collection.CollectionsSearchResultBean;
import de.mpg.imeji.image.ImagesBean;
import de.mpg.imeji.util.BeanHelper;

public class QuickSearchBean implements Serializable
{
    
    private String searchString;
    private String selectedSearchType = "images";

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
        return selectedSearchType;
    }
    
    public String search()
    {
        if(getSelectedSearchType().equals("collections"))
        {
            CollectionsSearchResultBean bean = (CollectionsSearchResultBean)BeanHelper.getSessionBean(CollectionsSearchResultBean.class);
            bean.setQuery(searchString);
            return "pretty:collectionsSearchResults";
        }
        else if (getSelectedSearchType().equals("images"))
        {
            ImagesBean bean = (ImagesBean)BeanHelper.getSessionBean(ImagesBean.class);
            bean.setQuery("( ANY_METADATA=\"" + searchString +"\" )");
            return "pretty:images";
        }
        return "pretty:";
    }
    
}
