/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.beans;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.security.Security;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.Album;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.beans.Navigation.Page;
import de.mpg.imeji.presentation.util.PropertyReader;

public class SessionBean
{
    private static Logger logger = Logger.getLogger(SessionBean.class);
    private User user = null;
    // Bundle
    public static final String LABEL_BUNDLE = "labels";
    public static final String MESSAGES_BUNDLE = "messages";
    public static final String METADATA_BUNDLE = "metadata";
    // His locale
    private Locale locale = new Locale("en");
    private Page currentPage = null;
    private List<String> selected;
    private List<URI> selectedCollections;
    private List<URI> selectedAlbums;
    private Album activeAlbum;
    private Map<URI, MetadataProfile> profileCached;
    private String selectedImagesContext = null;

    public SessionBean()
    {
        selected = new ArrayList<String>();
        selectedCollections = new ArrayList<URI>();
        selectedAlbums = new ArrayList<URI>();
        profileCached = new HashMap<URI, MetadataProfile>();
    }

    public String getSelectedImagesContext()
    {
        return selectedImagesContext;
    }

    public void setSelectedImagesContext(String selectedImagesContext)
    {
        this.selectedImagesContext = selectedImagesContext;
    }

    /**
     * Returns the label according to the current user locale.
     * 
     * @param placeholder A string containing the name of a label.
     * @return The label.
     */
    public String getLabel(String placeholder)
    {
        try
        {
            return ResourceBundle.getBundle(this.getSelectedLabelBundle()).getString(placeholder);
        }
        catch (Exception e)
        {
            return placeholder;
        }
    }

    /**
     * Returns the message according to the current user locale.
     * 
     * @param placeholder A string containing the name of a message.
     * @return The label.
     */
    public String getMessage(String placeholder)
    {
        try
        {
            return ResourceBundle.getBundle(this.getSelectedMessagesBundle()).getString(placeholder);
        }
        catch (Exception e)
        {
            return placeholder;
        }
    }

    // Getters and Setters
    public String getSelectedLabelBundle()
    {
        return LABEL_BUNDLE + "_" + locale.getLanguage();
    }

    public String getSelectedMessagesBundle()
    {
        return MESSAGES_BUNDLE + "_" + locale.getLanguage();
    }

    public String getSelectedMetadataBundle()
    {
        return METADATA_BUNDLE + "_" + locale.getLanguage();
    }

    public String getVersion()
    {
        return PropertyReader.getVersion();
    }

    public void toggleLocale(ActionEvent event)
    {
        FacesContext fc = FacesContext.getCurrentInstance();
        //
        // toggle the locale
        Locale locale = null;
        Map<String, String> map = fc.getExternalContext().getRequestParameterMap();
        String language = (String)map.get("language");
        String country = (String)map.get("country");
        try
        {
            locale = new Locale(language, country);
            fc.getViewRoot().setLocale(locale);
            Locale.setDefault(locale);
            this.locale = locale;
            logger.debug("New locale: " + language + "_" + country + " : " + locale);
        }
        catch (Exception e)
        {
            logger.error("unable to switch to locale using language = " + language + " and country = " + country, e);
        }
    }

    public Locale getLocale()
    {
        return locale;
    }

    public void setLocale(final Locale userLocale)
    {
        this.locale = userLocale;
    }

    public String getReloadActiveAlbum()
    {
        if (activeAlbum != null)
        {
            ItemController ic = new ItemController(user);
            activeAlbum = (Album)ic.loadContainerItems(activeAlbum, user, -1, 0);
        }
        return "";
    }

    /**
     * @return the user
     */
    public User getUser()
    {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(User user)
    {
        this.user = user;
    }

    public Page getCurrentPage()
    {
        return currentPage;
    }

    public void setCurrentPage(Page currentPage)
    {
        this.currentPage = currentPage;
    }

    public boolean isAdmin()
    {
        Security security = new Security();
        return security.isSysAdmin(user);
    }

    public List<String> getSelected()
    {
        return selected;
    }

    public void setSelected(List<String> selected)
    {
        this.selected = selected;
    }

    public int getSelectedSize()
    {
        return selected.size();
    }

    public List<URI> getSelectedCollections()
    {
        return selectedCollections;
    }

    public void setSelectedCollections(List<URI> selectedCollections)
    {
        this.selectedCollections = selectedCollections;
    }

    public int getSelectCollectionsSize()
    {
        return this.selectedCollections.size();
    }

    public List<URI> getSelectedAlbums()
    {
        return selectedAlbums;
    }

    public void setSelectedAlbums(List<URI> selectedAlbums)
    {
        this.selectedAlbums = selectedAlbums;
    }

    public int getSelectedAlbumsSize()
    {
        return this.selectedAlbums.size();
    }

    public void setActiveAlbum(Album activeAlbum)
    {
        this.activeAlbum = activeAlbum;
    }

    public Album getActiveAlbum()
    {
        return activeAlbum;
    }

    public String getActiveAlbumId()
    {
        return ObjectHelper.getId(activeAlbum.getId());
    }

    public int getActiveAlbumSize()
    {
        return activeAlbum.getImages().size();
    }

    public Map<URI, MetadataProfile> getProfileCached()
    {
        return profileCached;
    }

    public void setProfileCached(Map<URI, MetadataProfile> profileCached)
    {
        this.profileCached = profileCached;
    }
}
