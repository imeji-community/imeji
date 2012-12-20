/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.user;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

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

    /**
     * Construct the {@link ShareBean}
     */
    public ShareBean()
    {
        session = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        loadContainer();
        initMenus();
    }

    /**
     * Load the container (album or collection) to be shared
     */
    public void loadContainer()
    {
        URI uri = URI.create(UrlHelper.getParameterValue("uri"));
        if (uri.getPath().contains("/collection/"))
        {
            container = ObjectLoader.loadCollectionLazy(uri, session.getUser());
        }
        else if (uri.getPath().contains("/album/"))
        {
            container = ObjectLoader.loadAlbumLazy(uri, session.getUser());
        }
    }

    /**
     * Initialize the menus of the page
     */
    public void initMenus()
    {
        selectedGrant = GrantType.PRIVILEGED_VIEWER;
        grantsMenu = new ArrayList<SelectItem>();
        grantsMenu
                .add(new SelectItem(GrantType.PRIVILEGED_VIEWER, ((SessionBean)BeanHelper
                        .getSessionBean(SessionBean.class)).getLabel("role_viewer"),
                        "Can view all images for this collection"));
        grantsMenu.add(new SelectItem(GrantType.CONTAINER_EDITOR, ((SessionBean)BeanHelper
                .getSessionBean(SessionBean.class)).getLabel("role_collection_editor"),
                "Can edit informations about the collection"));
        grantsMenu.add(new SelectItem(GrantType.IMAGE_EDITOR, ((SessionBean)BeanHelper
                .getSessionBean(SessionBean.class)).getLabel("role_image_editor"),
                "Can view and edit all images for this collection"));
        grantsMenu.add(new SelectItem(GrantType.PROFILE_EDITOR, ((SessionBean)BeanHelper
                .getSessionBean(SessionBean.class)).getLabel("role_profile_editor"), "Can edit the metadata profile"));
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
        if (!GrantType.PROFILE_EDITOR.equals(selectedGrant))
        {
            shared = sm.share(retrieveCollection(id), session.getUser(), email, selectedGrant, true);
            message = session.getLabel("collection") + " " + id + " " + session.getLabel("shared_with") + " " + email
                    + " " + session.getLabel("as") + " " + selectedGrant.toString();
        }
        else
        {
            shared = sm.share(retrieveProfile(id), session.getUser(), email, selectedGrant, true);
            shared = sm.share(retrieveCollection(id), session.getUser(), email, GrantType.PRIVILEGED_VIEWER, true);
            message = session.getLabel("profile") + " " + id + " " + session.getLabel("shared_with") + " " + email
                    + " " + session.getLabel("as") + " " + selectedGrant.toString();
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
            emailClient.sendMail(dest.getEmail(), null, subject, message);
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
        return navigation.getApplicationUri() + container.getId().getPath();
    }

    public CollectionImeji retrieveCollection(String id)
    {
        return ObjectLoader.loadCollectionLazy(URI.create(id), session.getUser());
    }

    public MetadataProfile retrieveProfile(String collId)
    {
        return ObjectLoader.loadProfile(retrieveCollection(collId).getProfile(), session.getUser());
    }

    public Album retrieveAlbum(String albId)
    {
        return ObjectLoader.loadAlbumLazy(ObjectHelper.getURI(Album.class, albId), session.getUser());
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public GrantType getSelectedGrant()
    {
        return selectedGrant;
    }

    public void setSelectedGrant(GrantType selectedGrant)
    {
        this.selectedGrant = selectedGrant;
    }

    public String getColId()
    {
        return colId;
    }

    public List<SelectItem> getGrantsMenu()
    {
        return grantsMenu;
    }

    public void setGrantsMenu(List<SelectItem> grantsMenu)
    {
        this.grantsMenu = grantsMenu;
    }

    public Container getContainer()
    {
        return container;
    }

    public void setContainer(Container container)
    {
        this.container = container;
    }
}
