package de.mpg.imeji.presentation.user;

import java.net.URI;
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
import de.mpg.imeji.logic.controller.ProfileController;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.controller.UserGroupController;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.Album;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Container;
import de.mpg.imeji.logic.vo.Grant;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.logic.vo.UserGroup;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.user.util.EmailClient;
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
    private List<String> selectedRoles = new ArrayList<String>();
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
        selectedRoles = new ArrayList<String>();
        checkGrants(selectedRoles);
        retrieveSharedUserWithGrants();
        this.emailInput = "";
        this.isAdmin = auth.administrate(this.user, containerUri);
    }

    /**
     * Check the input and add all correct entry to the list of elements to be saved
     */
    public void share()
    {
        shareTo(checkInput());
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
    private List<String> checkInput()
    {
        List<String> emailList = new ArrayList<>();
        if (getEmailInput() != null)
        {
            List<String> inputValues = Arrays.asList(getEmailInput().split("\\s*[|,;\\n]\\s*"));
            for (String value : inputValues)
            {
                if (UserCreationBean.isValidEmail(value) && isExistingUser(value))
                {
                    emailList.add(value);
                }
                else if (isExistingUserGroup(value))
                {
                    emailList.add(value);
                }
                else
                {
                    this.errorList.add(value + " -- invalid Input");
                    BeanHelper.error(value + " -- invalid Input");
                    logger.error(value + " -- invalid Input");
                }
            }
        }
        return emailList;
    }

    /**
     * True if the email fits to an existing {@link User}
     * 
     * @param email
     * @return
     */
    private boolean isExistingUser(String email)
    {
        try
        {
            UserController uc = new UserController(Imeji.adminUser);
            uc.retrieve(email);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    /**
     * True if the name fits the name of an existing {@link UserGroup}
     * 
     * @param name
     * @return
     */
    private boolean isExistingUserGroup(String name)
    {
        return searchGroup(name) != null;
    }

    /**
     * Retrieve all {@link User} with {@link Grant} for the current {@link Container}
     */
    public void retrieveSharedUserWithGrants()
    {
        UserController uc = new UserController(Imeji.adminUser);
        Collection<User> allUser = uc.retrieveUserWithGrantFor(containerUri);
        sharedWith = new ArrayList<>();
        for (User u : allUser)
        {
            SharedHistory sh = new SharedHistory(u, true, containerUri, profileUri, new ArrayList<String>());
            sh.getSharedType().addAll(parseShareTypes((List<Grant>)u.getGrants(), containerUri, profileUri));
            sharedWith.add(sh);
        }
        UserGroupController ugc = new UserGroupController();
        Collection<UserGroup> groups = ugc.searchByGrantFor(containerUri, Imeji.adminUser);
        for (UserGroup group : groups)
        {
            SharedHistory sh = new SharedHistory(group, true, containerUri, profileUri, new ArrayList<String>());
            sh.getSharedType().addAll(parseShareTypes((List<Grant>)group.getGrants(), containerUri, profileUri));
            sharedWith.add(sh);
        }
    }

    /**
     * Parse the {@link ShareType} out of the {@link List} of {@link Grant} and the current {@link container} and
     * {@link MetadataProfile}
     * 
     * @param grants
     * @param containerUri
     * @param profileUri
     * @return
     */
    private List<String> parseShareTypes(List<Grant> grants, String containerUri, String profileUri)
    {
        List<String> l = new ArrayList<>();
        if (hasReadGrants(grants, containerUri, profileUri))
        {
            l.add(ShareType.READ.toString());
        }
        if (hasUploadGrants(grants, containerUri))
        {
            if (isCollection)
                l.add(ShareType.UPLOAD.toString());
            else
                l.add(ShareType.ADD.toString());
        }
        if (hasEditGrants(grants, containerUri))
        {
            l.add(ShareType.EDIT.toString());
        }
        if (hasDeleteGrants(grants, containerUri))
        {
            l.add(ShareType.DELETE.toString());
        }
        if (hasEditContainerGrants(grants, containerUri))
        {
            l.add(ShareType.EDIT_COLLECTION.toString());
        }
        if (hasEditProfileGrants(grants, profileUri))
        {
            l.add(ShareType.EDIT_PROFILE.toString());
        }
        if (hasAdminGrants(grants, containerUri, profileUri))
        {
            l.add(ShareType.ADMIN.toString());
        }
        return l;
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

    /**
     * Send an Emailto all {@link User} of a {@link UserGroup}
     * 
     * @param group
     * @param subject
     * @param message
     */
    private void sendEmailToGroup(UserGroup group, String subject, String message)
    {
        UserController c = new UserController(Imeji.adminUser);
        for (URI uri : group.getUsers())
        {
            try
            {
                sendEmail(c.retrieve(uri), subject, message);
            }
            catch (Exception e)
            {
                logger.error("Error sending email", e);
                BeanHelper.error(sb.getMessage("error") + ": Email not sent");
            }
        }
    }

    public static boolean hasReadGrants(List<Grant> userGrants, String containerUri, String profileUri)
    {
        List<Grant> grants = AuthorizationPredefinedRoles.read(containerUri, profileUri);
        return !grantNotExist(userGrants, grants);
    }

    public static boolean hasUploadGrants(List<Grant> userGrants, String containerUri)
    {
        List<Grant> grants = AuthorizationPredefinedRoles.upload(containerUri);
        return !grantNotExist(userGrants, grants);
    }

    public static boolean hasEditGrants(List<Grant> userGrants, String containerUri)
    {
        List<Grant> grants = AuthorizationPredefinedRoles.edit(containerUri);
        return !grantNotExist(userGrants, grants);
    }

    public static boolean hasDeleteGrants(List<Grant> userGrants, String containerUri)
    {
        List<Grant> grants = AuthorizationPredefinedRoles.delete(containerUri);
        return !grantNotExist(userGrants, grants);
    }

    public static boolean hasEditContainerGrants(List<Grant> userGrants, String containerUri)
    {
        List<Grant> grants = AuthorizationPredefinedRoles.editContainer(containerUri);
        return !grantNotExist(userGrants, grants);
    }

    public static boolean hasEditProfileGrants(List<Grant> userGrants, String profileUri)
    {
        List<Grant> grants = AuthorizationPredefinedRoles.editProfile(profileUri);
        return !grantNotExist(userGrants, grants);
    }

    public static boolean hasAdminGrants(List<Grant> userGrants, String containerUri, String profileUri)
    {
        List<Grant> grants = AuthorizationPredefinedRoles.admin(containerUri, profileUri);
        return !grantNotExist(userGrants, grants);
    }

    /**
     * Get the {@link List} of {@link Grant} which are required accoding to the selected Roles
     * 
     * @return
     */
    private List<Grant> getGrantsAccordingtoSelectedRoles()
    {
        List<Grant> grants = new ArrayList<Grant>();
        for (String g : selectedRoles)
        {
            switch (g)
            {
                case "READ":
                    grants.addAll(AuthorizationPredefinedRoles.read(containerUri, profileUri));
                    break;
                case "UPLOAD":
                    grants.addAll(AuthorizationPredefinedRoles.upload(containerUri));
                    break;
                case "EDIT":
                    grants.addAll(AuthorizationPredefinedRoles.edit(containerUri));
                    break;
                case "DELETE":
                    grants.addAll(AuthorizationPredefinedRoles.delete(containerUri));
                    break;
                case "EDIT_COLLECTION":
                    grants.addAll(AuthorizationPredefinedRoles.editContainer(containerUri));
                    break;
                case "EDIT_PROFILE":
                    grants.addAll(AuthorizationPredefinedRoles.editProfile(profileUri));
                    break;
                case "ADMIN":
                    grants.addAll(AuthorizationPredefinedRoles.admin(containerUri, profileUri));
                    break;
                case "EDIT_ALBUM":
                    grants.addAll(AuthorizationPredefinedRoles.editContainer(containerUri));
            }
        }
        return grants;
    }

    /**
     * Share the {@link Container} with the {@link List} of {@link User} and {@link UserGroup}
     * 
     * @param toList
     * @return
     */
    public void shareTo(List<String> toList)
    {
        List<Grant> grants = getGrantsAccordingtoSelectedRoles();
        for (String to : toList)
        {
            try
            {
                GrantController gc = new GrantController();
                if (UserCreationBean.isValidEmail(to))
                {
                    User u = ObjectLoader.loadUser(to, Imeji.adminUser);
                    gc.addGrants(u, grants, u);
                    if (sendEmail)
                        sendEmail(u, title, containerUri);
                }
                else
                {
                    UserGroup g = searchGroup(to);
                    gc.addGrants(g, grants, Imeji.adminUser);
                    if (sendEmail)
                        sendEmailToGroup(g, title, containerUri);
                }
            }
            catch (Exception e)
            {
                logger.error("CollectionSharedHistory--could not update User (email: " + user.getEmail() + " ) ", e);
            }
        }
        reset();
        clearError();
    }

    /**
     * Search a {@link UserGroup} by name
     * 
     * @param q
     * @return
     */
    private UserGroup searchGroup(String q)
    {
        UserGroupController c = new UserGroupController();
        List<UserGroup> found = (List<UserGroup>)c.searchByName(q, Imeji.adminUser);
        if (found.size() > 0)
            return found.get(0);
        return null;
    }

    /**
     * True if ???
     * 
     * @param userGrants
     * @param grantList
     * @return
     */
    public static boolean grantNotExist(List<Grant> userGrants, List<Grant> grantList)
    {
        boolean b = false;
        for (Grant g : grantList)
            if (!userGrants.contains(g))
                b = true;
        return b;
    }

    public void reset()
    {
        setEmailInput("");
        emailList.clear();
        selectedRoles.clear();
    }

    public void clearError()
    {
        errorList.clear();
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
        return selectedRoles;
    }

    public void setSelectedGrants(List<String> selectedGrants)
    {
        this.selectedRoles = selectedGrants;
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
