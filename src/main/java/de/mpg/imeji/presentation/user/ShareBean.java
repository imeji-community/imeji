/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.user;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.Album;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Container;
import de.mpg.imeji.logic.vo.Grant.GrantType;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.history.HistorySession;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.user.util.EmailClient;
import de.mpg.imeji.presentation.user.util.EmailMessages;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.ObjectLoader;
import de.mpg.imeji.presentation.util.UrlHelper;

/**
 * Bean for the share page
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class ShareBean
{
    private SessionBean session;
    private String email;
    private List<SelectItem> grantsMenu;
    private GrantType selectedGrant;
    private String colId;
    private Container container;
    private static Logger logger = Logger.getLogger(ShareBean.class);
    private boolean isAlbum = false;

    /**
     * Construct the {@link ShareBean}
     */
    public ShareBean()
    {
        session = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
    }

    /**
     * Called when the page is viewed
     */
    public String getInit()
    {
        URI uri = readURI();
        if (uri != null)
        {
            loadContainer(uri);
            initMenus(uri);
        }
        return "";
    }

    /**
     * Load the container (album or collection) to be shared
     */
    public void loadContainer(URI uri)
    {
        if (isCollection(uri))
        {
            this.isAlbum = false;
            container = ObjectLoader.loadCollectionLazy(uri, session.getUser());
        }
        else
        {
            this.isAlbum = true;
            container = ObjectLoader.loadAlbumLazy(uri, session.getUser());
        }
    }

    /**
     * Initialize the menus of the page
     */
    public void initMenus(URI uri)
    {
        selectedGrant = GrantType.VIEWER;
        grantsMenu = new ArrayList<SelectItem>();
        if (isCollection(uri))
        {
            grantsMenu.add(new SelectItem(GrantType.VIEWER, ((SessionBean)BeanHelper.getSessionBean(SessionBean.class))
                    .getLabel("role_viewer"), "Can view all content for this collection"));
            grantsMenu.add(new SelectItem(GrantType.CONTAINER_EDITOR, ((SessionBean)BeanHelper
                    .getSessionBean(SessionBean.class)).getLabel("role_collection_editor"),
                    "Can edit informations about the collection"));
            grantsMenu.add(new SelectItem(GrantType.IMAGE_EDITOR, ((SessionBean)BeanHelper
                    .getSessionBean(SessionBean.class)).getLabel("role_image_editor"),
                    "Can view and edit all images for this collection"));
            grantsMenu.add(new SelectItem(GrantType.PROFILE_EDITOR, ((SessionBean)BeanHelper
                    .getSessionBean(SessionBean.class)).getLabel("role_profile_editor"),
                    "Can edit the metadata profile"));
        }
        else
        {
            grantsMenu.add(new SelectItem(GrantType.VIEWER, ((SessionBean)BeanHelper.getSessionBean(SessionBean.class))
                    .getLabel("role_viewer"), "Can view all images for this collection"));
            grantsMenu.add(new SelectItem(GrantType.CONTAINER_EDITOR, ((SessionBean)BeanHelper
                    .getSessionBean(SessionBean.class)).getLabel("role_album_editor"),
                    "Can edit information about the collection"));
        }
    }

    /**
     * share the {@link Container}
     */
    public void share()
    {
        if (container != null)
        {
            if (container instanceof CollectionImeji)
            {
                shareCollection(container.getId().toString(), container.getMetadata().getTitle());
            }
            else if (container instanceof Album)
            {
                shareAlbum(container.getId().toString(), container.getMetadata().getTitle());
            }
        }
        HistorySession historySession = (HistorySession)BeanHelper.getSessionBean(HistorySession.class);
        try
        {
            FacesContext.getCurrentInstance().getExternalContext()
                    .redirect(historySession.getCurrentPage().getUri().toString());
        }
        catch (IOException e)
        {
            logger.error("Error redirecting to previous page");
        }
    }

    /**
     * Share a {@link CollectionImeji}
     * 
     * @param id
     * @param name
     */
    private void shareCollection(String id, String name)
    {
        SharingManager sm = new SharingManager();
        boolean shared = false;
        String message = "";
        String role = "";
        if (selectedGrant.toString().equals(GrantType.VIEWER.name()))
        {
            role = ((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getLabel("role_viewer");
        }
        if (selectedGrant.toString().equals(GrantType.CONTAINER_EDITOR.name()))
        {
            role = ((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getLabel("role_collection_editor");
        }
        if (selectedGrant.toString().equals(GrantType.IMAGE_EDITOR.name()))
        {
            role = ((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getLabel("role_image_editor");
        }
        if (selectedGrant.toString().equals(GrantType.PROFILE_EDITOR.name()))
        {
            role = ((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getLabel("role_profile_editor");
        }
        if (!GrantType.PROFILE_EDITOR.equals(selectedGrant))
        {
            shared = sm.share(retrieveCollection(id), session.getUser(), email, selectedGrant, true);
            message = session.getLabel("collection") + " " + id + " " + session.getLabel("shared_with") + " " + email
                    + " " + session.getLabel("share_as") + " " + role;
        }
        else
        {
            shared = sm.share(retrieveProfile(id), session.getUser(), email, selectedGrant, true);
            message = session.getLabel("profile") + " " + id + " " + session.getLabel("shared_with") + " " + email
                    + " " + session.getLabel("share_as") + " " + role;
            // Add grant VIEWER so the user can see the collection
            if (GrantType.PROFILE_EDITOR.equals(selectedGrant))
            {
                shared = sm.share(retrieveCollection(id), session.getUser(), email, GrantType.VIEWER, true);
            }
        }
        if (shared)
        {
            User dest = ObjectLoader.loadUser(email, session.getUser());
            EmailMessages emailMessages = new EmailMessages();
            sendEmail(dest, session.getMessage("email_shared_collection_subject"),
                    emailMessages.getSharedCollectionMessage(session.getUser().getName(), dest.getName(), name,
                            getContainerHome()));
            BeanHelper.info(session.getMessage("success_share"));
            BeanHelper.info(message);
        }
    }

    /**
     * Check if an imeji object with the given {@link URI} is a {@link CollectionImeji}
     * 
     * @param uri
     * @return
     */
    public boolean isCollection(URI uri)
    {
        return uri.getPath().contains("/collection/");
    }

    /**
     * Read the uri parameter in the url
     * 
     * @return
     */
    private URI readURI()
    {
        String str = UrlHelper.getParameterValue("uri");
        if (str != null)
            return URI.create(str);
        return null;
    }

    /**
     * Share an {@link Album}
     * 
     * @param id
     * @param name
     */
    private void shareAlbum(String id, String name)
    {
        SharingManager sm = new SharingManager();
        boolean shared = false;
        String message = "";
        shared = sm.share(retrieveAlbum(id), session.getUser(), email, selectedGrant, true);
        message = session.getLabel("album") + " " + id + " " + session.getLabel("shared_with") + " " + email + " "
                + session.getLabel("as") + " " + selectedGrant.toString();
        if (shared)
        {
            User dest = ObjectLoader.loadUser(email, session.getUser());
            EmailMessages emailMessages = new EmailMessages();
            sendEmail(dest, session.getMessage("email_shared_album_subject"), emailMessages.getSharedAlbumMessage(
                    session.getUser().getName(), dest.getName(), name, getContainerHome()));
            BeanHelper.info(session.getMessage("success_share"));
            BeanHelper.info(message);
        }
    }

    /**
     * Send email to the person to share with
     * 
     * @param dest
     * @param subject
     * @param message
     */
    private void sendEmail(User dest, String subject, String message)
    {
        EmailClient emailClient = new EmailClient();
        try
        {
            emailClient.sendMail(dest.getEmail(), null,
                    subject.replaceAll("XXX_INSTANCE_NAME_XXX", session.getInstanceName()), message);
        }
        catch (Exception e)
        {
            logger.error("Error sending email", e);
            BeanHelper.error(session.getMessage("error") + ": Email not sent");
        }
    }

    /**
     * Get url of the {@link Container} Home page
     * 
     * @param id
     * @return
     */
    public String getContainerHome()
    {
        Navigation navigation = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
        String id = ObjectHelper.getId(container.getId());
        if (isAlbum)
            return navigation.getAlbumUrl() + id;
        return navigation.getCollectionUrl() + id;
    }

    /**
     * Retrieve the collection to share with the specified Id in the url
     * 
     * @param id
     * @return
     */
    public CollectionImeji retrieveCollection(String id)
    {
        return ObjectLoader.loadCollectionLazy(URI.create(id), session.getUser());
    }

    /**
     * Retrieve the profile to share with the specified collection Id in the url
     * 
     * @param collId
     * @return
     */
    public MetadataProfile retrieveProfile(String collId)
    {
        return ObjectLoader.loadProfile(retrieveCollection(collId).getProfile(), session.getUser());
    }

    /**
     * Retrieve the album to share with the specified Id in the url
     * 
     * @param albId
     * @return
     */
    public Album retrieveAlbum(String albId)
    {
        return ObjectLoader.loadAlbumLazy(URI.create(albId), session.getUser());
    }

    /**
     * getter
     * 
     * @return
     */
    public String getEmail()
    {
        return email;
    }

    /**
     * setter
     * 
     * @param email
     */
    public void setEmail(String email)
    {
        this.email = email;
    }

    /**
     * gettet
     * 
     * @return
     */
    public GrantType getSelectedGrant()
    {
        return selectedGrant;
    }

    /**
     * setter
     * 
     * @param selectedGrant
     */
    public void setSelectedGrant(GrantType selectedGrant)
    {
        this.selectedGrant = selectedGrant;
    }

    /**
     * getter
     * 
     * @return
     */
    public String getColId()
    {
        return colId;
    }

    /**
     * getter
     * 
     * @return
     */
    public List<SelectItem> getGrantsMenu()
    {
        return grantsMenu;
    }

    /**
     * setter
     * 
     * @param grantsMenu
     */
    public void setGrantsMenu(List<SelectItem> grantsMenu)
    {
        this.grantsMenu = grantsMenu;
    }

    /**
     * getter
     * 
     * @return
     */
    public Container getContainer()
    {
        return container;
    }

    /**
     * setter
     * 
     * @param container
     */
    public void setContainer(Container container)
    {
        this.container = container;
    }

    /**
     * getter
     * 
     * @return
     */
    public boolean isAlbum()
    {
        return this.isAlbum;
    }
}
