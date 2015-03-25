package de.mpg.imeji.presentation.user;

import com.hp.hpl.jena.sparql.pfunction.library.container;
import com.ocpsoft.pretty.PrettyContext;
import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.auth.authorization.AuthorizationPredefinedRoles;
import de.mpg.imeji.logic.auth.util.AuthUtil;
import de.mpg.imeji.logic.controller.GrantController;
import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.controller.UserGroupController;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.util.UrlHelper;
import de.mpg.imeji.logic.vo.*;
import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.history.HistoryUtil;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.user.util.EmailClient;
import de.mpg.imeji.presentation.user.util.EmailMessages;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.ObjectLoader;
import org.apache.log4j.Logger;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@ManagedBean(name = "ShareBean")
@ViewScoped
public class ShareBean implements Serializable
{
    private static final long serialVersionUID = 8106762709528360926L;
    private static Logger logger = Logger.getLogger(ShareBean.class);
    @ManagedProperty(value = "#{SessionBean.user}")
    private User user;
    private String id;
    private URI uri;
    // The object (collection, album or item) which is going to be shared
    private Object shareTo;
    // private String shareToUri;
    // the user whom the shareto object belongs
    private URI owner;
    private String title;
    private String profileUri;
    private String emailInput;
    private List<String> emailList = new ArrayList<String>();
    private List<String> errorList = new ArrayList<String>();
    private List<SharedHistory> sharedWith = new ArrayList<SharedHistory>();
    private boolean isAdmin;
    // private List<SelectItem> grantItems = new ArrayList<SelectItem>();
    private List<String> selectedRoles = new ArrayList<String>();
    private boolean sendEmail = false;
    private UserGroup userGroup;
    private SharedObjectType type;
    // The url of the current share page (used for back link)
    private String pageUrl;
    private boolean hasContent = false;
    @ManagedProperty( "#{sessionBean}")
    private SessionBean sb;

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
     * @throws Exception 
     */
    public void initShareCollection() throws Exception
    {
        this.shareTo = null;
        this.profileUri = null;
        this.type = SharedObjectType.COLLECTION;
        this.uri = ObjectHelper.getURI(CollectionImeji.class, getId());
        CollectionImeji collection = ObjectLoader.loadCollectionLazy(uri, user);
        if (collection != null)
        {
            this.shareTo = collection;
            this.profileUri = collection.getProfile().toString();
            this.title = collection.getMetadata().getTitle();
            this.owner = collection.getCreatedBy();
            this.hasContent = hasContent(collection);
        }
        this.init();
    }

    /**
     * Init {@link ShareBean} for {@link Album}
     * @throws Exception 
     */
    public void initShareAlbum() throws Exception
    {
        this.type = SharedObjectType.ALBUM;
        this.shareTo = null;
        this.profileUri = null;
        this.uri = ObjectHelper.getURI(Album.class, getId());
        Album album = ObjectLoader.loadAlbumLazy(uri, user);
        if (album != null)
        {
            this.shareTo = album;
            this.title = album.getMetadata().getTitle();
            this.owner = album.getCreatedBy();
            this.hasContent = hasContent(album);
        }
        this.init();
    }

    /**
     * Loaded when the shre component is called from the item page
     * 
     * @return
     * @throws Exception 
     */
    public String getInitShareItem() throws Exception
    {
        this.type = SharedObjectType.ITEM;
        this.profileUri = null;
        this.shareTo = null;
        this.uri = HistoryUtil.extractURI(PrettyContext.getCurrentInstance().getRequestURL().toString());
        Item item = ObjectLoader.loadItem(uri, user);
        if (item != null)
        {
            this.shareTo = item;
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
        this.isAdmin = AuthUtil.staticAuth().administrate(this.user, shareTo);
        this.pageUrl = PrettyContext.getCurrentInstance().getRequestURL().toString()
                + PrettyContext.getCurrentInstance().getRequestQueryString();
        this.pageUrl = this.pageUrl.split("[&\\?]group=")[0];
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
    public void update()
    {
        for (SharedHistory sh : sharedWith)
        {
            sh.update();
        }
        reloadPage();
    }

    /**
     * Check the input and add all correct entry to the list of elements to be saved
     */
    public void share()
    {
        shareTo(checkInput());
        // init();
        reloadPage();
    }

    /**
     * Reload the current page
     */
    private void reloadPage()
    {
        try
        {
            UserController c = new UserController(user);
            user = c.retrieve(user.getId());
            Navigation navigation = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
            if (AuthUtil.staticAuth().administrate(user, uri.toString()))
            {
                // user has still rights to read the collection
                FacesContext.getCurrentInstance().getExternalContext()
                        .redirect(navigation.getApplicationUri() + pageUrl);
            }
            else if (AuthUtil.staticAuth().read(user, uri.toString()))
            {
                FacesContext.getCurrentInstance().getExternalContext()
                        .redirect(navigation.getApplicationUri() + pageUrl.replace("share", ""));
            }
            else
            {
                // user has not right anymore to read the collection
                switch (type)
                {
                    case COLLECTION:
                        FacesContext.getCurrentInstance().getExternalContext().redirect(navigation.getCollectionsUrl());
                        break;
                    case ALBUM:
                        FacesContext.getCurrentInstance().getExternalContext().redirect(navigation.getAlbumsUrl());
                        break;
                    case ITEM:
                        FacesContext.getCurrentInstance().getExternalContext().redirect(navigation.getBrowseUrl());
                        break;
                }
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error reloading page " + pageUrl);
        }
    }

    /**
     * Called when user share with a group
     */
    public void shareWithGroup()
    {
        List<String> l = new ArrayList<String>();
        l.add(userGroup.getId().toString());
        shareTo(l);
        // init();
        reloadPage();
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
        reloadPage();
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
                if (UserCreationBean.isValidEmail(value))
                {
                    try {
                    	UserController uc = new UserController(Imeji.adminUser);
						uc.retrieve(value);
						emailList.add(value);  

					} catch (Exception e) {
	                    this.errorList.add(sb.getMessage("error_share_invalid_user").replace("XXX_VALUE_XXX", value));
			            BeanHelper.error(sb.getMessage("error_share_invalid_user").replace("XXX_VALUE_XXX", value));			            
	                    logger.error(sb.getMessage("error_share_invalid_user").replace("XXX_VALUE_XXX", value));
					}                    	                 
                }
                else
                {
                    this.errorList.add(sb.getMessage("error_share_invalid_email").replace("XXX_VALUE_XXX", value));
		            BeanHelper.error(sb.getMessage("error_share_invalid_email").replace("XXX_VALUE_XXX", value));			            
                    logger.error(sb.getMessage("error_share_invalid_email").replace("XXX_VALUE_XXX", value));
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
        Collection<User> allUser = uc.searchByGrantFor(getShareToUri());
        sharedWith = new ArrayList<>();
        for (User u : allUser)
        {
            // Do not display the creator of this collection here
            if (!u.getId().toString().equals(owner.toString()))
            {
                sharedWith.add(new SharedHistory(u, type, getShareToUri(), profileUri, null));
            }
        }
        UserGroupController ugc = new UserGroupController();
        Collection<UserGroup> groups = ugc.searchByGrantFor(getShareToUri(), Imeji.adminUser);
        for (UserGroup group : groups)
        {
            sharedWith.add(new SharedHistory(group, type, getShareToUri(), profileUri, null));
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
    public static List<String> parseShareTypes(List<Grant> grants, String containerUri, String profileUri,
            SharedObjectType type)
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
        if (type == SharedObjectType.COLLECTION && hasDeleteItemGrants(grants, containerUri))
        {
            l.add(ShareType.DELETE.toString());
        }
        if (hasEditContainerGrants(grants, containerUri))
        {
            l.add(ShareType.EDIT_CONTAINER.toString());
        }
        if (type == SharedObjectType.COLLECTION && hasEditProfileGrants(grants, profileUri))
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
    private void sendEmail(User dest, String subject, List<Grant> grants)
    {
        EmailClient emailClient = new EmailClient();
        SessionBean sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        if (grants != null && grants.size() > 0)
        {
            this.getEmailMessage(this.user.getName(), dest.getName(), title, getShareToUri());
            try
            {
                this.addRoles(grants);
                emailClient.sendMail(dest.getEmail(), null,
                        subject.replaceAll("XXX_INSTANCE_NAME_XXX", sb.getInstanceName()), this.emailInput);
            }
            catch (Exception e)
            {
                logger.error("Error sending email", e);
                BeanHelper.error(sb.getMessage("error") + ": Email not sent");
            }
        }
        // System.out.println("EMAIL" + this.emailInput);
    }

    private void addRoles(List<Grant> grants)
    {
        String grantsStr = "";
        List<String> roles = ShareBean.parseShareTypes(grants, getShareToUri(), profileUri, type);
        SessionBean sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        if (this.type.equals(SharedObjectType.ALBUM))
        {
            for (int i = 0; i < roles.size(); i++)
            {
                String role = roles.get(i);
                switch (role)
                {
                    case "READ":
                        grantsStr += "- " + sb.getLabel("album_share_read") + "\n";
                        break;
                    case "CREATE":
                        grantsStr += "- " + sb.getLabel("album_share_image_add") + "\n";
                        break;
                    case "EDIT_CONTAINER":
                        grantsStr += "- " + sb.getLabel("album_share_album_edit") + "\n";
                        break;
                    case "ADMIN":
                        grantsStr += "- " + sb.getLabel("album_share_admin") + "\n";
                        break;
                }
            }
        }
        if (this.type.equals(SharedObjectType.COLLECTION))
        {
            for (int i = 0; i < roles.size(); i++)
            {
                String role = roles.get(i);
                switch (role)
                {
                    case "READ":
                        grantsStr += "- " + sb.getLabel("collection_share_read") + "\n";
                        break;
                    case "CREATE":
                        grantsStr += "- " + sb.getLabel("collection_share_image_upload") + "\n";
                        break;
                    case "EDIT_ITEM":
                        grantsStr += "- " + sb.getLabel("collection_share_image_edit") + "\n";
                        break;
                    case "DELETE":
                        grantsStr += "- " + sb.getLabel("collection_share_image_delete") + "\n";
                        break;
                    case "EDIT_CONTAINER":
                        grantsStr += "- " + sb.getLabel("collection_share_collection_edit") + "\n";
                        break;
                    case "EDIT_PROFILE":
                        grantsStr += "- " + sb.getLabel("collection_share_profile_edit") + "\n";
                        break;
                    case "ADMIN":
                        grantsStr += "- " + sb.getLabel("collection_share_admin") + "\n";
                        break;
                }
            }
        }
        if (this.type.equals(SharedObjectType.ITEM))
        {
            for (int i = 0; i < roles.size(); i++)
            {
                String role = roles.get(i);
                switch (role)
                {
                    case "READ":
                        grantsStr += "- " + sb.getLabel("collection_share_read");
                        break;
                }
            }
        }
        this.emailInput = this.emailInput.replaceAll("XXX_RIGHTS_XXX", grantsStr.trim());
    }

    /**
     * Send an Email to all {@link User} of a {@link UserGroup}
     * 
     * @param group
     * @param subject
     * @param message
     */
    private void sendEmailToGroup(UserGroup group, String subject)
    {
        UserController c = new UserController(Imeji.adminUser);
        SessionBean sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        for (URI uri : group.getUsers())
        {
            try
            {
                List<Grant> grants = getGrantsAccordingtoRoles(selectedRoles, getShareToUri(), profileUri);
//                User u = ObjectLoader.loadUser(uri, Imeji.adminUser);
//                GrantController gc = new GrantController();
//                gc.addGrants(u, grants, u);
                sendEmail(c.retrieve(uri), subject, grants);
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
        for (String to : toList)
        {
            List<Grant> grants = getGrantsAccordingtoRoles(selectedRoles, getShareToUri(), profileUri);
            try
            {
                GrantController gc = new GrantController();
                if (UserCreationBean.isValidEmail(to))
                {
                    User u = ObjectLoader.loadUser(to, Imeji.adminUser);
                    gc.addGrants(u, grants, u);
                    if (sendEmail)
                    {
                        // sendEmail(u, title, getShareToUri());
                        sendEmail(u, title, grants);
                    }
                }
                else
                {
                    gc.addGrants(retrieveGroup(to), grants, Imeji.adminUser);
                    if (sendEmail)
                    {
                        sendEmailToGroup(retrieveGroup(to), title);
                    }
                }
            }
            catch (Exception e)
            {
                logger.error("CollectionSharedHistory--could not update User (email: " + user.getEmail() + " ) ", e);
            }
        }
        clearError();
    }

    private void getEmailMessage(String from, String to, String name, String link)
    {
        EmailMessages emailMessages = new EmailMessages();
        this.emailInput = "";
        if (this.type.equals(SharedObjectType.COLLECTION))
        {
            this.emailInput = emailMessages.getSharedCollectionMessage(from, to, name, link);
        }
        if (this.type.equals(SharedObjectType.ALBUM))
        {
            this.emailInput = emailMessages.getSharedAlbumMessage(from, to, name, link);
        }
        if (this.type.equals(SharedObjectType.ITEM))
        {
            this.emailInput = emailMessages.getSharedItemMessage(from, to, name, link);
        }
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
    private static boolean hasReadGrants(List<Grant> userGrants, String containerUri, String profileUri)
    {
        List<Grant> grants = AuthorizationPredefinedRoles.read(containerUri, profileUri);
        return !grantNotExist(userGrants, grants);
    }

    private static boolean hasUploadGrants(List<Grant> userGrants, String containerUri)
    {
        List<Grant> grants = AuthorizationPredefinedRoles.upload(containerUri, null);
        return !grantNotExist(userGrants, grants);
    }

    private static boolean hasEditItemGrants(List<Grant> userGrants, String containerUri)
    {
        List<Grant> grants = AuthorizationPredefinedRoles.edit(containerUri, null);
        return !grantNotExist(userGrants, grants);
    }

    private static boolean hasDeleteItemGrants(List<Grant> userGrants, String containerUri)
    {
        List<Grant> grants = AuthorizationPredefinedRoles.delete(containerUri, null);
        return !grantNotExist(userGrants, grants);
    }

    private static boolean hasEditContainerGrants(List<Grant> userGrants, String containerUri)
    {
        List<Grant> grants = AuthorizationPredefinedRoles.editContainer(containerUri, null);
        return !grantNotExist(userGrants, grants);
    }

    private static boolean hasEditProfileGrants(List<Grant> userGrants, String profileUri)
    {
        List<Grant> grants = AuthorizationPredefinedRoles.editProfile(profileUri);
        return !grantNotExist(userGrants, grants);
    }

    private static boolean hasAdminGrants(List<Grant> userGrants, String containerUri, String profileUri)
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

    /**
     * REturn true if the {@link Container} has item
     * 
     * @param c
     * @return
     */
    private boolean hasContent(Container c)
    {
        ItemController ic = new ItemController();
        return ic.searchAndSetContainerItemsFast(c, user, 1).getImages().size() > 0;
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

    public String getShareToUri()
    {
        if (shareTo instanceof Properties)
            return ((Properties)shareTo).getId().toString();
        return null;
    }

    public Object getShareTo()
    {
        return shareTo;
    }

    public void setShareToUri(Object shareTo)
    {
        this.shareTo = shareTo;
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

    public String getPageUrl()
    {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl)
    {
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

    /**
     * @return the hasContent
     */
    public boolean isHasContent()
    {
        return hasContent;
    }

    /**
     * @param hasContent the hasContent to set
     */
    public void setHasContent(boolean hasContent)
    {
        this.hasContent = hasContent;
    }

	public SessionBean getSb() {
		return sb;
	}

	public void setSb(SessionBean sb) {
		this.sb = sb;
	}
    
    
}
