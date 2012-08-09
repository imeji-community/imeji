/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.search;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.util.BeanHelper;

public class QuickSearchBean
{
    private String searchString = "";
    private String selectedSearchType = "images";

    public String search() throws IOException
    {
        Navigation navigation = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
        if (getSelectedSearchType() == null)
            setSelectedSearchType("images");
        if (getSelectedSearchType().equals("collections"))
        {
            FacesContext.getCurrentInstance().getExternalContext()
                    .redirect(navigation.getCollectionsUrl() + "?q=" + searchString);
        }
        else if (getSelectedSearchType().equals("images"))
        {
            FacesContext.getCurrentInstance().getExternalContext()
                    .redirect(navigation.getImagesUrl() + "?q=" + searchString);
        }
        else if (getSelectedSearchType().equals("albums"))
        {
            FacesContext.getCurrentInstance().getExternalContext()
                    .redirect(navigation.getAlbumsUrl() + "?q=" + searchString);
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
        if (selectedSearchType == null)
            selectedSearchType = "images";
        return selectedSearchType;
    }

    public void selectedSearchTypeListener(ValueChangeEvent event)
    {
        if (event.getNewValue() != null && !event.getNewValue().equals(event.getOldValue()))
        {
            selectedSearchType = (String)event.getNewValue();
        }
    }
}
