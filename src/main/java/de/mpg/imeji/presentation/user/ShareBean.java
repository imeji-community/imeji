package de.mpg.imeji.presentation.user;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.auth.Authorization;
import de.mpg.imeji.logic.auth.authorization.AuthorizationPredefinedRoles;
import de.mpg.imeji.logic.controller.GrantController;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.Album;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Grant;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.ObjectLoader;

@ManagedBean(name = "ShareBean")
@SessionScoped
public class ShareBean {
	private static Logger logger = Logger.getLogger(ShareBean.class);
	
    private SessionBean sb;
    

	@ManagedProperty(value = "#{SessionBean.user}")
	private User user;
    private String id;	
    private String oldID;
    
    private String containerUri;
    private boolean isCollection;
    
    private String title;

    private String profileUri;

    private String emailInput;
	private List<String> emailList = new ArrayList<String>();
	private List<String> errorList = new ArrayList<String>();
	
	private List<SharedHistory> sharedWithNew = new ArrayList<SharedHistory >();
	private List<SharedHistory >  sharedWith = new ArrayList<SharedHistory >();
	
    private static Authorization auth = new Authorization();
    private boolean isAdmin;
    private List<SelectItem> grantItems = new ArrayList<SelectItem>();
    private List<String> selectedGrants = new ArrayList<String>();
    


    
    public enum ShareType
    {
    	READ, ADD, UPLOAD, EDIT, DELETE, EDIT_COLLECTION, EDIT_PROFILE, EDIT_ALBUM, ADMIN
    }
    
    public ShareBean()
    {
        super();
        sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
		if(sb == null || sb.getUser() == null)
		{
			try {
				ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
				ec.redirect(ec.getRequestContextPath() + "/");
			} catch (IOException e) {
				logger.error("Could not load ShareCollcetionBean", e);
				e.printStackTrace();
			} 
		}
    }
    
    public void init()
    {
    	if(user == null && sb != null && sb.getUser() != null)
    		user = sb.getUser();
    	if((oldID == null && getId() != null) || (oldID != null && !oldID.equals(getId())))
    	{
    		loadContainer(getId());
    		setOldID(getId());
    		this.isAdmin = auth.administrate(this.user, containerUri);
    	}
    	else
    	{
    		this.emailInput = "";
//    		this.emailList.clear();
//    		this.selectedGrants.clear();

    	}
    	
    }

    
    public void loadContainer(String id)
    {
		this.sharedWith.clear();
		this.sharedWithNew.clear();
    	CollectionImeji collection = ObjectLoader.loadCollection(ObjectHelper.getURI(CollectionImeji.class, getId()), user);
		if(collection != null)
		{
			this.isCollection = true;
        	this.containerUri = collection.getId() .toString(); 
        	this.profileUri = collection.getProfile().toString();
        	this.grantItems = sb.getShareCollectionGrantItems();
        	this.title = collection.getMetadata().getTitle();
		}
    	else
    	{
    		Album album = ObjectLoader.loadAlbum(ObjectHelper.getURI(Album.class, getId()), user);
    		if(album != null)
    		{
    			this.containerUri = album.getId().toString();
    			this.isCollection = false;
        		this.grantItems = sb.getShareAlbumGrantItems();   
        		this.title = album.getMetadata().getTitle();
    		}    		

    	}
    	
    }
    

    public String checkInputEmail()
    {
    	if(getEmailInput() != null)
    	{ 
        	List<String> emails = Arrays.asList(getEmailInput().split("\\s*;\\s*"));
        	for(String e : emails)
        	{
        		Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
        		Matcher m = p.matcher(e);
        		if(!m.matches())
        		{
        			this.errorList.add(e + " -- invalid Input");   			
                    BeanHelper.error(e + " -- invalid Input");
        			logger.error(e + " -- invalid Input");
        		}
        		else
        		{
//        			UserController uc = new UserController(Imeji.adminUser);
        			UserController uc = new UserController(Imeji.adminUser);
        			try {
    					User u = uc.retrieve(e);  
    					this.emailList.add(e);
    				} catch (Exception e1) {
    					BeanHelper.error(e + " -- this user doesn't exist");
    					logger.error(e + " -- this user doesn't exist", e1);
    				}
        			/*
        			if(e.equalsIgnoreCase(user.getEmail()))
        			{
        				this.errorList.add("try to share own collection");
                        BeanHelper.error("try to share own collection");
        				logger.error("try to share own collection");
        			}
        			
        			else
        			{
        			
            			UserController uc = new UserController(Imeji.adminUser);
            			try {
        					User u = uc.retrieve(e);  
        					this.emailList.add(e);
        				} catch (Exception e1) {
        					BeanHelper.error(e + " -- this user doesn't exist");
        					logger.error(e + " -- this user doesn't exist", e1);
        				}
        			}
        			*/

        		}
        	}
    	}

    	return "pretty:shareCollection";
    }
    
    public String retrieveSharedUserWithGrants()
    {
    	UserController uc = new UserController(Imeji.adminUser);
    	Collection<User> allUser = uc.retrieveAll();

    	if(isCollection)
    	{
	    	for(User u : allUser)
	    	{
	        	SharedHistory sh = new SharedHistory();
	    		if(hasReadGrants(u, containerUri, profileUri))
	    		{
	    			sh.setUser(u);
	    			sh.getSharedType().add(ShareType.READ.toString());
	    		}
	    		if(hasUploadGrants(u, containerUri))
	    		{
	    			sh.setUser(u);
	    			sh.getSharedType().add(ShareType.UPLOAD.toString());
	    		}
	    		if(hasEditGrants(u, containerUri))
	    		{
	    			sh.setUser(u);
	    			sh.getSharedType().add(ShareType.EDIT.toString());
	    		}
	    		if(hasDeleteGrants(u, containerUri))
	    		{
	    			sh.setUser(u);
	    			sh.getSharedType().add(ShareType.DELETE.toString());
	    		}
	    		if(hasEditContainerGrants(u, containerUri))
	    		{
	    			sh.setUser(u);
	    			sh.getSharedType().add(ShareType.EDIT_COLLECTION.toString());
	    		}
	    		if(hasEditPrifileGrants(u, profileUri))
	    		{
	    			sh.setUser(u);
	    			sh.getSharedType().add(ShareType.EDIT_PROFILE.toString());
	    		}
	    		if(hasAdminGrants(u, containerUri, profileUri))
	    		{
	    			sh.setUser(u);
	    			sh.getSharedType().add(ShareType.ADMIN.toString());
	    		}
	    		if(sh.getUser()!=null)
	    			this.sharedWith.add(sh);  			
	    			
	    	}
    	}
    	else
    	{
	    	for(User u : allUser)
	    	{
	        	SharedHistory sh = new SharedHistory();
	    		if(hasReadGrants(u, containerUri))
	    		{
	    			sh.setUser(u);
	    			sh.getSharedType().add(ShareType.READ.toString());
	    		}
	    		if(hasAddGrants(u, containerUri))
	    		{
	    			sh.setUser(u);
	    			sh.getSharedType().add(ShareType.ADD.toString());
	    		}
	    		if(hasDeleteGrants(u, containerUri))
	    		{
	    			sh.setUser(u);
	    			sh.getSharedType().add(ShareType.DELETE.toString());
	    		}
	    		if(hasEditContainerGrants(u, containerUri))
	    		{
	    			sh.setUser(u);
	    			sh.getSharedType().add(ShareType.EDIT_ALBUM.toString());
	    		}
	    		if(hasAdminGrants(u, containerUri))
	    		{
	    			sh.setUser(u);
	    			sh.getSharedType().add(ShareType.ADMIN.toString());
	    		}
	    		if(sh.getUser()!=null)
	    			this.sharedWith.add(sh);  			
	    			
	    	}
    	}
    	return "pretty:shareCollection";
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
    	List<Grant> grants = AuthorizationPredefinedRoles.upload(containerUri);
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
    
    public String save()
    {

    	this.sharedWithNew.clear();
    	UserController uc = new UserController(Imeji.adminUser);
    	if(isCollection)
    	{
        	for(String e : emailList)
        	{
        		try {
    				User u = uc.retrieve(e);
    				GrantController gc = new GrantController();
    				List<String> sharedType = new ArrayList<String>(); 
    				
    				for(String g : selectedGrants)
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
    					if(grantNotExist(u, newGrants))
    					{
    						gc.addGrants(u, newGrants, u);
    					}
    					sharedType.add(g);
    				}
    				SharedHistory csh = new SharedHistory(u, isCollection, containerUri, profileUri, sharedType);
    				sharedWithNew.add(csh);
    			} catch (Exception e1) {
    				logger.error("CollectionSharedHistory--could not update User (email: " + user.getEmail() + " ) ", e1);
    			}
        		
        	} 
    	}else{

        	for(String e : emailList)
        	{
        		try {
    				User u = uc.retrieve(e);
    				GrantController gc = new GrantController();
    				List<String> sharedType = new ArrayList<String>(); 
    				
    				for(String g : selectedGrants)
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
    					if(grantNotExist(u, newGrants))
    					{
    						gc.addGrants(u, newGrants, u);
    					}
    					sharedType.add(g);
    				}
    				SharedHistory csh = new SharedHistory(u, isCollection, containerUri, profileUri, sharedType);
    				sharedWithNew.add(csh);
    			} catch (Exception e1) {
    				logger.error("CollectionSharedHistory--could not update User (email: " + user.getEmail() + " ) ", e1);
    			}
        		
        	} 
    		
    		
    		
    	}
  	
    	reset();
    	clearError();
    	return "pretty:shareCollection";
    }
    
    public static boolean grantNotExist(User u, List<Grant> grantList)
    {
    	boolean b = false;
    	List<Grant> userGrants = (List<Grant>) u.getGrants();
    	for(Grant g : grantList)
    		if(!userGrants.contains(g))
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

    	
	protected String getNavigationString() {
		return null;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOldID() {
		return oldID;
	}

	public void setOldID(String oldID) {
		this.oldID = oldID;
	}

	public String getEmailInput() {
		return emailInput;
	}

	public void setEmailInput(String emailInput) {
		this.emailInput = emailInput;
	}

	public List<String> getEmailList() {
		return emailList;
	}

	public void setEmailList(List<String> emailList) {
		this.emailList = emailList;
	}

	public List<String> getErrorList() {
		return errorList;
	}

	public void setErrorList(List<String> errorList) {
		this.errorList = errorList;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	public List<SelectItem> getGrantItems() {
		return grantItems;
	}

	public void setGrantItems(List<SelectItem> grantItems) {
		this.grantItems = grantItems;
	}
 
	public List<String> getSelectedGrants() {
		if(isCollection)
		{
			if(selectedGrants.contains("ADMIN"))
	        { 
	        	this.selectedGrants.clear();
	        	this.selectedGrants.add(ShareType.READ.toString());
	        	this.selectedGrants.add(ShareType.UPLOAD.toString());
	        	this.selectedGrants.add(ShareType.EDIT.toString());
	        	this.selectedGrants.add(ShareType.DELETE.toString());
	        	this.selectedGrants.add(ShareType.EDIT_COLLECTION.toString());
	        	this.selectedGrants.add(ShareType.EDIT_PROFILE.toString());
	        	this.selectedGrants.add(ShareType.ADMIN.toString());     	
	        }
			else 
			{
				if(!selectedGrants.contains("READ"))
					selectedGrants.add(ShareType.READ.toString());
			}
		}
		else
		{
			if(selectedGrants.contains("ADMIN"))
	        { 
	        	this.selectedGrants.clear();
	        	this.selectedGrants.add(ShareType.READ.toString());
	        	this.selectedGrants.add(ShareType.ADD.toString());
	        	this.selectedGrants.add(ShareType.DELETE.toString());
	        	this.selectedGrants.add(ShareType.EDIT_ALBUM.toString());
	        	this.selectedGrants.add(ShareType.ADMIN.toString());     	
	        }
			else 
			{
				if(!selectedGrants.contains("READ"))
					selectedGrants.add(ShareType.READ.toString());
			}
		}
		return selectedGrants;
	}

	public void setSelectedGrants(List<String> selectedGrants) {
		this.selectedGrants = selectedGrants;
	}

	public String getContainerUri() {
		return containerUri;
	}

	public void setContainerUri(String containerUri) {
		this.containerUri= containerUri;
	}

	public String getProfileUri() {
		return profileUri;
	}

	public void setProfileUri(String profileUri) {
		this.profileUri = profileUri;
	} 
 
	public List<SharedHistory> getSharedWithNew() {
		return sharedWithNew;
	}

	public void setSharedWithNew(List<SharedHistory> sharedWithNew) {
		this.sharedWithNew = sharedWithNew;
	}

	public List<SharedHistory> getSharedWith() {
		return sharedWith;
	}

	public void setSharedWith(List<SharedHistory> sharedWith) {
		this.sharedWith = sharedWith;
	}

	
	public String clearNewHistory()
	{
		this.sharedWithNew.clear();
		return "pretty:shareCollection";
	}
	
	public String updateSharedWithNew()
	{
		for(SharedHistory shn : sharedWithNew)
		{
			shn.update();
		}
		return "pretty:shareCollection";
	}
	
	public String updateSharedWith()
	{
		for(SharedHistory sh : sharedWith)
		{
			sh.update();
		}
		return "pretty:shareCollection";
	}

	public boolean isCollection() {
		return isCollection;
	}

	public void setAlbum(boolean isCollection) {
		this.isCollection = isCollection;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	

}
