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
import com.ocpsoft.pretty.PrettyContext;

import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.auth.authorization.AuthorizationPredefinedRoles;
import de.mpg.imeji.logic.auth.util.AuthUtil;
import de.mpg.imeji.logic.controller.GrantController;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.controller.UserGroupController;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.Album;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Container;
import de.mpg.imeji.logic.vo.Grant;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.logic.vo.UserGroup;
import de.mpg.imeji.presentation.history.PageURIHelper;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.user.util.EmailClient;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.ObjectLoader;
import de.mpg.imeji.presentation.util.UrlHelper;

@ManagedBean(name = "ShareBean")
@SessionScoped
public class ShareBean
{
    private static Logger logger = Logger.getLogger(ShareBean.class);
    private SessionBean sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
    @ManagedProperty(value = "#{SessionBean.user}")
    private User user;
    private String id;
    private String shareToUri;
    // the user whom the shareto object belongs
    private URI owner;
    private String title;
    private String profileUri;
    private String emailInput;
    private List<String> emailList = new ArrayList<String>();
    private List<String> errorList = new ArrayList<String>();
    private List<SharedHistory> sharedWith = new ArrayList<SharedHistory>();
    private boolean isAdmin;
    private List<SelectItem> grantItems = new ArrayList<SelectItem>();
    private List<String> selectedRoles = new ArrayList<String>();
    private boolean sendEmail = true;
    private UserGroup userGroup;
    private SharedObjectType type;
    // The url of the current share page (used for back link)
    private String pageUrl;
     
    public String getPageUrl() {
		return pageUrl;
	}

	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}

	public SharedObjectType getType() 
    {
		return type;
	}

	public void setType(SharedObjectType type) 
	{
		this.type = type;
	}

	public enum SharedObjectType
    {
        COLLECTION, ALBUM, ITEM
    }

    public enum ShareType
    {
        READ, CREATE, EDIT_ITEM, DELETE, EDIT_CONTAINER, EDIT_PROFILE, ADMIN
    }

    /**
     * Init {@link ShareBean} for {@link CollectionImeji}
     */
    public void initShareCollection()
    {
        this.shareToUri = null;
        this.profileUri = null;
        this.type = SharedObjectType.COLLECTION;
        this.grantItems = sb.getShareCollectionGrantItems();
        CollectionImeji collection = ObjectLoader.loadCollectionLazy(
                ObjectHelper.getURI(CollectionImeji.class, getId()), user);
        if (collection != null)
        {
            this.shareToUri = collection.getId().toString();
            this.profileUri = collection.getProfile().toString();
            this.title = collection.getMetadata().getTitle();
            this.owner = collection.getCreatedBy();
        }
        this.init();
    }

    /**
     * Init {@link ShareBean} for {@link Album}
     */
    public void initShareAlbum()
    {
        this.type = SharedObjectType.ALBUM;
        this.shareToUri = null;
        this.profileUri = null;
        this.grantItems = sb.getShareAlbumGrantItems();
        Album album = ObjectLoader.loadAlbumLazy(ObjectHelper.getURI(Album.class, getId()), user);
        if (album != null)
        {
            this.shareToUri = album.getId().toString();
            this.title = album.getMetadata().getTitle();
            this.owner = album.getCreatedBy();
        }
        this.init();
    }

    /**
     * Loaded when the shre component is called from the item page
     * 
     * @return
     */
    public String getInitShareItem()
    {
        this.type = SharedObjectType.ITEM;
        this.grantItems = sb.getShareItemGrantItems();
        this.profileUri = null;
        this.shareToUri = null;
        URI itemURI = PageURIHelper.extractId(PrettyContext.getCurrentInstance().getRequestURL().toString());
        Item item = ObjectLoader.loadItem(itemURI, user);
        if (item != null)
        {
            this.shareToUri = item.getCollection().toString();
            this.title = item.getFilename();
            this.owner = item.getCreatedBy();
        }
        this.init();
        return "";
    }

    /**
     * Init method for {@link ShareBean}
     */
    public void init()
    {
        this.sharedWith = new ArrayList<SharedHistory>();
        this.emailList = new ArrayList<String>();
        this.errorList = new ArrayList<String>();
        this.selectedRoles = checkGrants(type, new ArrayList<String>());
        this.retrieveSharedUserWithGrants();
        this.emailInput = "";
        this.isAdmin = AuthUtil.staticAuth().administrate(this.user, shareToUri);
        this.pageUrl = PrettyContext.getCurrentInstance().getRequestURL().toString() + PrettyContext.getCurrentInstance().getRequestQueryString();
        this.initShareWithGroup();
    }

    /**
     * Check in the url if a {@link UserGroup} should be shared with the current {@link Container}
     */
    private void initShareWithGroup()
    {
        this.userGroup = null;
        String groupToShareWithUri = UrlHelper.getParameterValue("group");
        if (groupToShareWithUri != null)
        {
            UserGroup group = retrieveGroup(groupToShareWithUri);
            if (group != null)
            {
                userGroup = group;
            }
        }
    }

    /**
     * Update the page accodring to new changes
     * 
     * @return
     */
    public String update()
    {
        for (SharedHistory sh : sharedWith)
        {
            sh.update();
        }
        return "pretty:";
    }

    /**
     * Reset all values of the page
     */
    public String reset()
    {
        setEmailInput("");
        emailList.clear();
        selectedRoles.clear();
        return "pretty:";
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
     * Called when user share with a group
     */
    public void shareWithGroup()
    {
        List<String> l = new ArrayList<String>();
        l.add(userGroup.getId().toString());
        shareTo(l);
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
     * Retrieve all {@link User} with {@link Grant} for the current {@link Container}
     */
    public void retrieveSharedUserWithGrants()
    {
        UserController uc = new UserController(Imeji.adminUser);
        Collection<User> allUser = uc.retrieveUserWithGrantFor(shareToUri);
        sharedWith = new ArrayList<>();
        for (User u : allUser)
        {
            //Do not display the creator of this collection here
            if (!u.getId().toString().equals(owner.toString()))
            {	
	            SharedHistory sh = new SharedHistory(u, type, shareToUri, profileUri, new ArrayList<String>());
	            sh.getSharedType().addAll(parseShareTypes((List<Grant>)u.getGrants(), shareToUri, profileUri));
	            sharedWith.add(sh);
            }
        }
        UserGroupController ugc = new UserGroupController();
        Collection<UserGroup> groups = ugc.searchByGrantFor(shareToUri, Imeji.adminUser);
        for (UserGroup group : groups)
        {
            SharedHistory sh = new SharedHistory(group, type, shareToUri, profileUri, new ArrayList<String>());
            sh.getSharedType().addAll(parseShareTypes((List<Grant>)group.getGrants(), shareToUri, profileUri));
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
            l.add(ShareType.CREATE.toString());
        }
        if (type == SharedObjectType.COLLECTION && hasEditItemGrants(grants, containerUri))
        {
            l.add(ShareType.EDIT_ITEM.toString());
        }
        if (type == SharedObjectType.COLLECTION  && hasDeleteItemGrants(grants, containerUri))
        {
            l.add(ShareType.DELETE.toString());
        }
        if (hasEditContainerGrants(grants, containerUri))
        {
            l.add(ShareType.EDIT_CONTAINER.toString());
        }
        if (type == SharedObjectType.COLLECTION  && hasEditProfileGrants(grants, profileUri))
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
     * Send an Email to all {@link User} of a {@link UserGroup}
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

    /**
     * Get the {@link List} of {@link Grant} which are required accoding to the selected Roles
     * 
     * @return
     */
    public static List<Grant> getGrantsAccordingtoRoles(List<String> roles, String containerUri, String profileUri)
    {
        List<Grant> grants = new ArrayList<Grant>();
        for (String g : roles)
        {
            switch (g)
            {
                case "READ":
                    grants.addAll(AuthorizationPredefinedRoles.read(containerUri, profileUri));
                    break;
                case "CREATE":
                    grants.addAll(AuthorizationPredefinedRoles.upload(containerUri, profileUri));
                    break;
                case "EDIT_ITEM":
                    grants.addAll(AuthorizationPredefinedRoles.edit(containerUri, profileUri));
                    break;
                case "DELETE":
                    grants.addAll(AuthorizationPredefinedRoles.delete(containerUri, profileUri));
                    break;
                case "EDIT_CONTAINER":
                    grants.addAll(AuthorizationPredefinedRoles.editContainer(containerUri, profileUri));
                    break;
                case "EDIT_PROFILE":
                    grants.addAll(AuthorizationPredefinedRoles.editProfile(profileUri));
                    break;
                case "ADMIN":
                    grants.addAll(AuthorizationPredefinedRoles.admin(containerUri, profileUri));
                    break;
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
        List<Grant> grants = getGrantsAccordingtoRoles(selectedRoles, shareToUri, profileUri);
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
                        sendEmail(u, title, shareToUri);
                }
                else
                {
                    gc.addGrants(retrieveGroup(to), grants, Imeji.adminUser);
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
    private UserGroup retrieveGroup(String uri)
    {
        UserGroupController c = new UserGroupController();
        try
        {
            return c.read(uri, Imeji.adminUser);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /**
     * True if {@link List} of {@link Grant} allows to read the containeruri and the profile uri
     * 
     * @param userGrants
     * @param containerUri
     * @param profileUri
     * @return
     */
    private boolean hasReadGrants(List<Grant> userGrants, String containerUri, String profileUri)
    {
        List<Grant> grants = AuthorizationPredefinedRoles.read(containerUri, profileUri);
        return !grantNotExist(userGrants, grants);
    }

    private boolean hasUploadGrants(List<Grant> userGrants, String containerUri)
    {
        List<Grant> grants = AuthorizationPredefinedRoles.upload(containerUri, null);
        return !grantNotExist(userGrants, grants);
    }

    private boolean hasEditItemGrants(List<Grant> userGrants, String containerUri)
    {
        List<Grant> grants = AuthorizationPredefinedRoles.edit(containerUri, null);
        return !grantNotExist(userGrants, grants);
    }

    private boolean hasDeleteItemGrants(List<Grant> userGrants, String containerUri)
    {
        List<Grant> grants = AuthorizationPredefinedRoles.delete(containerUri, null);
        return !grantNotExist(userGrants, grants);
    }

    private boolean hasEditContainerGrants(List<Grant> userGrants, String containerUri)
    {
        List<Grant> grants = AuthorizationPredefinedRoles.editContainer(containerUri, null);
        return !grantNotExist(userGrants, grants);
    }

    private boolean hasEditProfileGrants(List<Grant> userGrants, String profileUri)
    {
        List<Grant> grants = AuthorizationPredefinedRoles.editProfile(profileUri);
        return !grantNotExist(userGrants, grants);
    }

    private boolean hasAdminGrants(List<Grant> userGrants, String containerUri, String profileUri)
    {
        List<Grant> grants = AuthorizationPredefinedRoles.admin(containerUri, profileUri);
        return !grantNotExist(userGrants, grants);
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
        this.emailInput = emailInput.toLowerCase();
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

    public void checkGrants(List<String> selectedGrants)
    {
    	this.selectedRoles = checkGrants(type, selectedGrants);
    }
    
    /**
     * Check that Grants are consistent
     */
    public static List<String> checkGrants(SharedObjectType type, List<String> selectedGrants)
    {
    	switch (type) 
    	{
			case COLLECTION:
				if (selectedGrants.contains("ADMIN"))
	            {
	                selectedGrants.clear();
	                selectedGrants.add(ShareType.READ.toString());
	                selectedGrants.add(ShareType.CREATE.toString());
	                selectedGrants.add(ShareType.EDIT_ITEM.toString());
	                selectedGrants.add(ShareType.DELETE.toString());
	                selectedGrants.add(ShareType.EDIT_CONTAINER.toString());
	                selectedGrants.add(ShareType.EDIT_PROFILE.toString());
	                selectedGrants.add(ShareType.ADMIN.toString());
	            }
	            else
	            {
	                if (!selectedGrants.contains("READ"))
	                    selectedGrants.add(ShareType.READ.toString());
	            }
				break;
			case ALBUM:
				if (selectedGrants.contains("ADMIN"))
	            {
	                selectedGrants.clear();
	                selectedGrants.add(ShareType.READ.toString());
	                selectedGrants.add(ShareType.CREATE.toString());
	                selectedGrants.add(ShareType.EDIT_CONTAINER.toString());
	                selectedGrants.add(ShareType.ADMIN.toString());
	            }
	            else
	            {
	                if (!selectedGrants.contains("READ"))
	                    selectedGrants.add(ShareType.READ.toString());
	            }
				break;
			case ITEM:
				 selectedGrants.clear();
	             selectedGrants.add(ShareType.READ.toString());
				break;
		}
    	return selectedGrants;

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
        return shareToUri;
    }

    public void setContainerUri(String containerUri)
    {
        this.shareToUri = containerUri;
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

    /**
     * @return the userGroup
     */
    public UserGroup getUserGroup()
    {
        return userGroup;
    }

    /**
     * @param userGroup the userGroup to set
     */
    public void setUserGroup(UserGroup userGroup)
    {
        this.userGroup = userGroup;
    }
}
