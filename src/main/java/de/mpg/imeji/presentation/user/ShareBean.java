package de.mpg.imeji.presentation.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.sparql.pfunction.library.container;

import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.auth.Authorization;
import de.mpg.imeji.logic.auth.authorization.AuthorizationPredefinedRoles;
import de.mpg.imeji.logic.controller.GrantController;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.Album;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Container;
import de.mpg.imeji.logic.vo.Grant;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.logic.vo.UserGroup;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.user.util.EmailClient;
import de.mpg.imeji.presentation.user.util.EmailMessages;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.ObjectLoader;

@ManagedBean(name = "ShareBean")
@SessionScoped
public class ShareBean
{
    private static Logger logger = Logger.getLogger(ShareBean.class);
    private SessionBean sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
    @ManagedProperty(value = "#{SessionBean.user}")
    private User user;
    private String id;
    private String containerUri;
    private boolean isCollection;
    private String title;
    private String profileUri;
    private String emailInput;
    private List<String> emailList = new ArrayList<String>();
    private List<String> errorList = new ArrayList<String>();
    private List<SharedHistory> sharedWith = new ArrayList<SharedHistory>();
    private static Authorization auth = new Authorization();
    private boolean isAdmin;
    private List<SelectItem> grantItems = new ArrayList<SelectItem>();
    private List<String> selectedGrants = new ArrayList<String>();
    private boolean sendEmail = true;

    public enum ShareType
    {
        READ, ADD, UPLOAD, EDIT, DELETE, EDIT_COLLECTION, EDIT_PROFILE, EDIT_ALBUM, ADMIN
    }

    /**
     * Init {@link ShareBean} for {@link CollectionImeji}
     */
    public void initShareCollection()
    {
        CollectionImeji collection = ObjectLoader.loadCollectionLazy(
                ObjectHelper.getURI(CollectionImeji.class, getId()), user);
        if (collection != null)
        {
            isCollection = true;
            containerUri = collection.getId().toString();
            profileUri = collection.getProfile().toString();
            grantItems = sb.getShareCollectionGrantItems();
            title = collection.getMetadata().getTitle();
        }
        init();
    }

    /**
     * Init {@link ShareBean} for {@link Album}
     */
    public void initShareAlbum()
    {
        Album album = ObjectLoader.loadAlbumLazy(ObjectHelper.getURI(Album.class, getId()), user);
        if (album != null)
        {
            this.containerUri = album.getId().toString();
            this.isCollection = false;
            this.grantItems = sb.getShareAlbumGrantItems();
            this.title = album.getMetadata().getTitle();
        }
        init();
    }

    /**
     * Init method for {@link ShareBean}
     */
    public void init()
    {
        sharedWith = new ArrayList<SharedHistory>();
        emailList = new ArrayList<String>();
        errorList = new ArrayList<String>();
        selectedGrants = new ArrayList<String>();
        checkGrants(selectedGrants);
        retrieveSharedUserWithGrants();
        this.emailInput = "";
        this.isAdmin = auth.administrate(this.user, containerUri);
    }

    /**
     * Check the input and add all correct entry to the list of elements to be saved
     */
    public void share()
    {
        shareTo(checkInputEmail());
        init();
    }

    /**
     * Unshare the {@link Container} for one {@link User} (i.e, remove all {@link Grant} of this {@link User} related to
     * the {@link container})
     * 
     * @param sh
     */
    public void unshare(SharedHistory sh)
    {
        sh.getSharedType().clear();
        sh.update();
        init();
    }

    /**
     * Check the input values with the emails
     * 
     * @return
     */
    public List<String> checkInputEmail()
    {
        List<String> emailList = new ArrayList<>();
        if (getEmailInput() != null)
        {
            List<String> emails = Arrays.asList(getEmailInput().split("\\s*;\\s*"));
            for (String e : emails)
            {
                if (!UserCreationBean.isValidEmail(e))
                {
                    this.errorList.add(e + " -- invalid Input");
                    BeanHelper.error(e + " -- invalid Input");
                    logger.error(e + " -- invalid Input");
                }
                else
                {
                    UserController uc = new UserController(Imeji.adminUser);
                    try
                    {
                        uc.retrieve(e);
                        emailList.add(e);
                    }
                    catch (Exception e1)
                    {
                        BeanHelper.error(e + " -- this user doesn't exist");
                        logger.error(e + " -- this user doesn't exist", e1);
                    }
                }
            }
        }
        return emailList;
    }

    /**
     * Retrieve all {@link User} with {@link Grant} for the current {@link Container}
     */
    public void retrieveSharedUserWithGrants()
    {
        UserController uc = new UserController(Imeji.adminUser);
        Collection<User> allUser = uc.retrieveUserWithGrantFor(containerUri);
        sharedWith = new ArrayList<>();
        if (isCollection)
        {
            for (User u : allUser)
            {
                SharedHistory sh = new SharedHistory(u, true, containerUri, profileUri, new ArrayList<String>());
                if (hasReadGrants(u, containerUri, profileUri))
                {
                    sh.setUser(u);
                    sh.getSharedType().add(ShareType.READ.toString());
                }
                if (hasUploadGrants(u, containerUri))
                {
                    sh.setUser(u);
                    sh.getSharedType().add(ShareType.UPLOAD.toString());
                }
                if (hasEditGrants(u, containerUri))
                {
                    sh.setUser(u);
                    sh.getSharedType().add(ShareType.EDIT.toString());
                }
                if (hasDeleteGrants(u, containerUri))
                {
                    sh.setUser(u);
                    sh.getSharedType().add(ShareType.DELETE.toString());
                }
                if (hasEditContainerGrants(u, containerUri))
                {
                    sh.setUser(u);
                    sh.getSharedType().add(ShareType.EDIT_COLLECTION.toString());
                }
                if (hasEditPrifileGrants(u, profileUri))
                {
                    sh.setUser(u);
                    sh.getSharedType().add(ShareType.EDIT_PROFILE.toString());
                }
                if (hasAdminGrants(u, containerUri, profileUri))
                {
                    sh.setUser(u);
                    sh.getSharedType().add(ShareType.ADMIN.toString());
                }
                if (sh.getUser() != null)
                    sharedWith.add(sh);
            }
        }
        else
        {
            for (User u : allUser)
            {
                SharedHistory sh = new SharedHistory(u, false, containerUri, null, new ArrayList<String>());
                if (hasReadGrants(u, containerUri))
                {
                    sh.setUser(u);
                    sh.getSharedType().add(ShareType.READ.toString());
                }
                if (hasAddGrants(u, containerUri))
                {
                    sh.setUser(u);
                    sh.getSharedType().add(ShareType.ADD.toString());
                }
                if (hasDeleteGrants(u, containerUri))
                {
                    sh.setUser(u);
                    sh.getSharedType().add(ShareType.DELETE.toString());
                }
                if (hasEditContainerGrants(u, containerUri))
                {
                    sh.setUser(u);
                    sh.getSharedType().add(ShareType.EDIT_ALBUM.toString());
                }
                if (hasAdminGrants(u, containerUri))
                {
                    sh.setUser(u);
                    sh.getSharedType().add(ShareType.ADMIN.toString());
                }
                if (sh.getUser() != null)
                    this.sharedWith.add(sh);
            }
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
                    subject.replaceAll("XXX_INSTANCE_NAME_XXX", sb.getInstanceName()), message);
        }
        catch (Exception e)
        {
            logger.error("Error sending email", e);
            BeanHelper.error(sb.getMessage("error") + ": Email not sent");
        }
    }

    public static boolean hasAddGrants(User user, String containerUri)
    {
        List<Grant> grants = AuthorizationPredefinedRoles.add(containerUri);
        return !grantNotExist(user, grants);
    }

    public static boolean hasReadGrants(User user, String containerUri, String profileUri)
    {
        List<Grant> grants = AuthorizationPredefinedRoles.read(containerUri, profileUri);
        return !grantNotExist(user, grants);
    }

    public static boolean hasReadGrants(User user, String containerUri)
    {
        List<Grant> grants = AuthorizationPredefinedRoles.read(containerUri);
        return !grantNotExist(user, grants);
    }

    public static boolean hasUploadGrants(User user, String containerUri)
    {
        List<Grant> grants = AuthorizationPredefinedRoles.upload(containerUri);
        return !grantNotExist(user, grants);
    }

    public static boolean hasEditGrants(User user, String containerUri)
    {
        List<Grant> grants = AuthorizationPredefinedRoles.edit(containerUri);
        return !grantNotExist(user, grants);
    }

    public static boolean hasDeleteGrants(User user, String containerUri)
    {
        List<Grant> grants = AuthorizationPredefinedRoles.delete(containerUri);
        return !grantNotExist(user, grants);
    }

    public static boolean hasEditContainerGrants(User user, String containerUri)
    {
        List<Grant> grants = AuthorizationPredefinedRoles.editContainer(containerUri);
        return !grantNotExist(user, grants);
    }

    public static boolean hasEditPrifileGrants(User user, String profileUri)
    {
        List<Grant> grants = AuthorizationPredefinedRoles.editProfile(profileUri);
        return !grantNotExist(user, grants);
    }

    public static boolean hasAdminGrants(User user, String containerUri, String profileUri)
    {
        List<Grant> grants = AuthorizationPredefinedRoles.admin(containerUri, profileUri);
        return !grantNotExist(user, grants);
    }

    public static boolean hasAdminGrants(User user, String containerUri)
    {
        List<Grant> grants = AuthorizationPredefinedRoles.admin(containerUri);
        return !grantNotExist(user, grants);
    }

    /**
     * Share the {@link Container} with the {@link List} of {@link User} and {@link UserGroup}
     * 
     * @param emailList
     * @return
     */
    public void shareTo(List<String> emailList)
    {
        if (isCollection)
        {
            for (String e : emailList)
            {
                try
                {
                    User u = ObjectLoader.loadUser(e, Imeji.adminUser);
                    GrantController gc = new GrantController();
                    List<String> sharedType = new ArrayList<String>();
                    for (String g : selectedGrants)
                    {
                        List<Grant> newGrants = new ArrayList<Grant>();
                        switch (g)
                        {
                            case "READ":
                                newGrants = AuthorizationPredefinedRoles.read(containerUri, profileUri);
                                break;
                            case "UPLOAD":
                                newGrants = AuthorizationPredefinedRoles.upload(containerUri);
                                break;
                            case "EDIT":
                                newGrants = AuthorizationPredefinedRoles.edit(containerUri);
                                break;
                            case "DELETE":
                                newGrants = AuthorizationPredefinedRoles.delete(containerUri);
                                break;
                            case "EDIT_COLLECTION":
                                newGrants = AuthorizationPredefinedRoles.editContainer(containerUri);
                                break;
                            case "EDIT_PROFILE":
                                newGrants = AuthorizationPredefinedRoles.editProfile(profileUri);
                                break;
                            case "ADMIN":
                                newGrants = AuthorizationPredefinedRoles.admin(containerUri, profileUri);
                                break;
                        }
                        if (grantNotExist(u, newGrants))
                        {
                            gc.addGrants(u, newGrants, u);
                        }
                        sharedType.add(g);
                    }
                    if(sendEmail)
                    {
                        EmailMessages emailMessages = new EmailMessages();
                        sendEmail(u, sb.getMessage("email_shared_album_subject"), emailMessages.getSharedCollectionMessage(user.getName(), u.getName(), title, containerUri));
                    }
                 
                }
                catch (Exception e1)
                {
                    logger.error("CollectionSharedHistory--could not update User (email: " + user.getEmail() + " ) ",
                            e1);
                }
            }
        }
        else
        {
            for (String e : emailList)
            {
                try
                {
                    User u = ObjectLoader.loadUser(e, Imeji.adminUser);
                    GrantController gc = new GrantController();
                    List<String> sharedType = new ArrayList<String>();
                    for (String g : selectedGrants)
                    {
                        List<Grant> newGrants = new ArrayList<Grant>();
                        switch (g)
                        {
                            case "READ":
                                newGrants = AuthorizationPredefinedRoles.read(containerUri);
                                break;
                            case "ADD":
                                newGrants = AuthorizationPredefinedRoles.add(containerUri);
                                break;
                            case "DELETE":
                                newGrants = AuthorizationPredefinedRoles.delete(containerUri);
                                break;
                            case "EDIT_ALBUM":
                                newGrants = AuthorizationPredefinedRoles.editContainer(containerUri);
                                break;
                            case "ADMIN":
                                newGrants = AuthorizationPredefinedRoles.admin(containerUri);
                                break;
                        }
                        if (grantNotExist(u, newGrants))
                        {
                            gc.addGrants(u, newGrants, u);
                        }
                        sharedType.add(g);
                    }
                    if(sendEmail)
                    {
                        EmailMessages emailMessages = new EmailMessages();
                        sendEmail(u, sb.getMessage("email_shared_album_subject"), emailMessages.getSharedAlbumMessage(user.getName(), u.getName(), title, containerUri));
                    }
                }
                catch (Exception e1)
                {
                    logger.error("CollectionSharedHistory--could not update User (email: " + user.getEmail() + " ) ",
                            e1);
                }
            }
        }
        reset();
        clearError();
    }

    public static boolean grantNotExist(User u, List<Grant> grantList)
    {
        boolean b = false;
        List<Grant> userGrants = (List<Grant>)u.getGrants();
        for (Grant g : grantList)
            if (!userGrants.contains(g))
                b = true;
        return b;
    }

    public void reset()
    {
        setEmailInput("");
        emailList.clear();
        selectedGrants.clear();
    }

    public void clearError()
    {
        errorList.clear();
    }

    protected String getNavigationString()
    {
        return null;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getEmailInput()
    {
        return emailInput;
    }

    public void setEmailInput(String emailInput)
    {
        this.emailInput = emailInput;
    }

    public List<String> getEmailList()
    {
        return emailList;
    }

    public void setEmailList(List<String> emailList)
    {
        this.emailList = emailList;
    }

    public List<String> getErrorList()
    {
        return errorList;
    }

    public void setErrorList(List<String> errorList)
    {
        this.errorList = errorList;
    }

    public boolean isAdmin()
    {
        return isAdmin;
    }

    public void setAdmin(boolean isAdmin)
    {
        this.isAdmin = isAdmin;
    }

    public List<SelectItem> getGrantItems()
    {
        return grantItems;
    }

    public void setGrantItems(List<SelectItem> grantItems)
    {
        this.grantItems = grantItems;
    }

    /**
     * Check that Grants are consistent
     */
    public void checkGrants(List<String> selectedGrants)
    {
        if (isCollection)
        {
            if (selectedGrants.contains("ADMIN"))
            {
                selectedGrants.clear();
                selectedGrants.add(ShareType.READ.toString());
                selectedGrants.add(ShareType.UPLOAD.toString());
                selectedGrants.add(ShareType.EDIT.toString());
                selectedGrants.add(ShareType.DELETE.toString());
                selectedGrants.add(ShareType.EDIT_COLLECTION.toString());
                selectedGrants.add(ShareType.EDIT_PROFILE.toString());
                selectedGrants.add(ShareType.ADMIN.toString());
            }
            else
            {
                if (!selectedGrants.contains("READ"))
                    selectedGrants.add(ShareType.READ.toString());
            }
        }
        else
        {
            if (selectedGrants.contains("ADMIN"))
            {
                selectedGrants.clear();
                selectedGrants.add(ShareType.READ.toString());
                selectedGrants.add(ShareType.ADD.toString());
                selectedGrants.add(ShareType.DELETE.toString());
                selectedGrants.add(ShareType.EDIT_ALBUM.toString());
                selectedGrants.add(ShareType.ADMIN.toString());
            }
            else
            {
                if (!selectedGrants.contains("READ"))
                    selectedGrants.add(ShareType.READ.toString());
            }
        }
    }

    public List<String> getSelectedGrants()
    {
        return selectedGrants;
    }

    public void setSelectedGrants(List<String> selectedGrants)
    {
        this.selectedGrants = selectedGrants;
    }

    public String getContainerUri()
    {
        return containerUri;
    }

    public void setContainerUri(String containerUri)
    {
        this.containerUri = containerUri;
    }

    public String getProfileUri()
    {
        return profileUri;
    }

    public void setProfileUri(String profileUri)
    {
        this.profileUri = profileUri;
    }

    public List<SharedHistory> getSharedWith()
    {
        return sharedWith;
    }

    public void setSharedWith(List<SharedHistory> sharedWith)
    {
        this.sharedWith = sharedWith;
    }

    public String updateSharedWith()
    {
        for (SharedHistory sh : sharedWith)
        {
            sh.update();
        }
        return "pretty:shareCollection";
    }

    public boolean isCollection()
    {
        return isCollection;
    }

    public void setAlbum(boolean isCollection)
    {
        this.isCollection = isCollection;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * @return the sendEmail
     */
    public boolean isSendEmail()
    {
        return sendEmail;
    }

    /**
     * @param sendEmail the sendEmail to set
     */
    public void setSendEmail(boolean sendEmail)
    {
        this.sendEmail = sendEmail;
    }
}
