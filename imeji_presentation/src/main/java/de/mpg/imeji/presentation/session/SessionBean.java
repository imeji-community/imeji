/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.session;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.security.Security;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.Album;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.beans.Navigation.Page;
import de.mpg.imeji.presentation.util.PropertyReader;

/**
 * The session Bean for imeji.
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class SessionBean
{
    private static Logger logger = Logger.getLogger(SessionBean.class);
    private User user = null;
    // Bundle
    public static final String LABEL_BUNDLE = "labels";
    public static final String MESSAGES_BUNDLE = "messages";
    public static final String METADATA_BUNDLE = "metadata";
    // imeji locale
    private Locale locale;
    private Page currentPage;
    private List<String> selected;
    private List<URI> selectedCollections;
    private List<URI> selectedAlbums;
    private Album activeAlbum;
    private Map<URI, MetadataProfile> profileCached;
    private Map<URI, CollectionImeji> collectionCached;
    private String selectedImagesContext = null;

    /**
     * The session Bean for imeji
     */
    public SessionBean()
    {
        selected = new ArrayList<String>();
        selectedCollections = new ArrayList<URI>();
        selectedAlbums = new ArrayList<URI>();
        profileCached = new HashMap<URI, MetadataProfile>();
        collectionCached = new HashMap<URI, CollectionImeji>();
        initLocale();
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

    /**
     * Read the language in the Request, and set it as current local, Called when the session is new
     */
    private void initLocale()
    {
        FacesContext fc = FacesContext.getCurrentInstance();
        HttpServletRequest req = (HttpServletRequest)fc.getExternalContext().getRequest();
        if (req.getLocale() != null)
        {
            locale = req.getLocale();
        }
        else
        {
            locale = new Locale("en");
        }
    }

    /**
     * Getter
     * 
     * @return
     */
    public Locale getLocale()
    {
        return locale;
    }

    /**
     * Setter
     * 
     * @param userLocale
     */
    public void setLocale(final Locale userLocale)
    {
        this.locale = userLocale;
    }

    /**
     * Get the context of the images (item, collection, album)
     * 
     * @return
     */
    public String getSelectedImagesContext()
    {
        return selectedImagesContext;
    }

    public void setSelectedImagesContext(String selectedImagesContext)
    {
        this.selectedImagesContext = selectedImagesContext;
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

    /**
     * @return the collectionCached
     */
    public Map<URI, CollectionImeji> getCollectionCached()
    {
        return collectionCached;
    }

    /**
     * @param collectionCached the collectionCached to set
     */
    public void setCollectionCached(Map<URI, CollectionImeji> collectionCached)
    {
        this.collectionCached = collectionCached;
    }
}
