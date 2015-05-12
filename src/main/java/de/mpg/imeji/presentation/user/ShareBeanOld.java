///**
// * License: src/main/resources/license/escidoc.license
// */
//package de.mpg.imeji.presentation.user;
//
//import java.io.IOException;
//import java.net.URI;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import javax.faces.bean.ManagedBean;
//import javax.faces.bean.ManagedProperty;
//import javax.faces.bean.SessionScoped;
//import javax.faces.context.ExternalContext;
//import javax.faces.context.FacesContext;
//import javax.faces.model.SelectItem;
//
//import org.apache.log4j.Logger;
//
//import de.mpg.imeji.logic.Imeji;
//import de.mpg.imeji.logic.auth.Authorization;
//import de.mpg.imeji.logic.controller.UserController;
//import de.mpg.imeji.logic.vo.Album;
//import de.mpg.imeji.logic.vo.CollectionImeji;
//import de.mpg.imeji.logic.vo.Container;
//import de.mpg.imeji.logic.vo.Grant.GrantType;
//import de.mpg.imeji.logic.vo.MetadataProfile;
//import de.mpg.imeji.logic.vo.User;
//import de.mpg.imeji.presentation.album.AlbumBean;
//import de.mpg.imeji.presentation.collection.CollectionBean;
//import de.mpg.imeji.presentation.collection.CollectionListItem;
//import de.mpg.imeji.presentation.session.SessionBean;
//import de.mpg.imeji.presentation.user.util.EmailClient;
//import de.mpg.imeji.presentation.user.util.EmailMessages;
//import de.mpg.imeji.presentation.util.BeanHelper;
//import de.mpg.imeji.presentation.util.ObjectLoader;
//import de.mpg.imeji.logic.util.UrlHelper;
//
///**
// * Bean for the share page
// * 
// * @author saquet (initial creation)
// * @author $Author$ (last modification)
// * @version $Revision$ $LastChangedDate$
// */
//@ManagedBean(name = "ShareBeanOld")
//@SessionScoped
//public class ShareBeanOld{
//	private static Logger logger = Logger.getLogger(ShareBeanOld.class);
//	
//    private SessionBean sb;
//	@ManagedProperty(value = "#{SessionBean.user}")
//	private User user;
//    private String emailInput;
//	private List<String> emailList = new ArrayList<String>();
//	private List<String> errorList = new ArrayList<String>();
//    private static Authorization auth = new Authorization();
//    private String containerUri;
//    private String newUri;
//    private boolean isAdmin;
//    private Container container;
//    private CollectionBean collection;
//    private AlbumBean album;
//    private boolean isAlbum = false;
//    private List<SelectItem> grantItems = new ArrayList<SelectItem>();
//
//    
//
//    private GrantType selectedGrant;
//    private String colId;
//
//
//
//    /**
//     * Construct the {@link ShareBean}
//     */
//    public ShareBeanOld() 
//    {
//        this.sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
//		if(sb == null || sb.getUser() == null)
//		{
//			try {
//				ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
//				ec.redirect(ec.getRequestContextPath() + "/");
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} 
//		}
//
//    }
//     
//    /**
//     * Called when the page is viewed
//     */
//    public String getInit()
//    {
//        this.newUri = UrlHelper.getParameterValue("uri");               
//        
//        if ((newUri !=null && containerUri == null) || (newUri != null && newUri != containerUri))
//        {
//        	URI uri = URI.create(newUri);
//            loadContainer(uri);
//            initMenus(uri);
//            this.isAdmin = auth.administrate(this.user, this.newUri);
//        }
//        return "";
//    }
//
//    /**
//     * Load the container (album or collection) to be shared
//     */
//    public void loadContainer(URI uri)
//    {
//        if (isCollection(uri))
//        {
//        	container = ObjectLoader.loadCollectionLazy(uri, this.user);
//        }
//        else
//        {
//            this.isAlbum = true;
//            container = ObjectLoader.loadCollectionLazy(uri, this.user);
//        }
//    }
//
//    /**
//     * Initialize the menus of the page
//     */
//    public void initMenus(URI uri)
//    {
//        //TODO implemtents new or remove this method
//    }
//    
//    public String checkInputEmail()
//    {
//    	if(emailInputValid())
//    		return null;
//    	else
//    		return null;
//    }
//    
//    
//    public boolean emailInputValid()
//    {  
//    	if(getEmailInput() != null)
//    	{ 
//        	List<String> emails = Arrays.asList(getEmailInput().split("\\s*;\\s*"));
//        	for(String e : emails)
//        	{
//        		Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
//        		Matcher m = p.matcher(e);
//        		if(!m.matches())
//        		{
//        			this.errorList.add(e + " -- invalid Input");   			
//                    BeanHelper.error(e + " -- invalid Input");
//        			logger.error(e + " -- invalid Input");
//        		}
//        		else
//        		{
//        			if(e.equalsIgnoreCase(user.getEmail()))
//        			{
//        				this.errorList.add("try to share own collection");
//                        BeanHelper.error("try to share own collection");
//        				logger.error("try to share own collection");
//        			}
//        			else
//        			{
//            			UserController uc = new UserController(Imeji.adminUser);
//            			try {
//        					User u = uc.retrieve(e);  
//        					this.emailList.add(e);
//        				} catch (Exception e1) {
//        					BeanHelper.error(e + " -- this user doesn't exist");
//        					logger.error(e + " -- this user doesn't exist", e1);
//        				}
//        			}
//
//        		}
//        	}
//    	}
//
//    	if(errorList.size() >0)
//    	{
//    		return false;
//    	}
//    	else
//    		return true;
//    }
//    
//    
//
//    public List<String> getErrorList() {
//		return errorList;
//	}
//
//	public void setErrorList(List<String> errorList) {
//		this.errorList = errorList;
//	}
//
//	public User getUser() {
//		return user;
//	}
//
//	public void setUser(User user) {
//		this.user = user;
//	}
//
//	public List<String> getEmailList() {
//		return emailList;
//	}
//
//	public void setEmailList(List<String> emailList) {
//		this.emailList = emailList;
//	}
//
//	public boolean isAdmin()
//	{
//		return this.isAdmin;
//	}
//	
//	
//	
//	
//
//	public String getEmailInput() {
//		return emailInput;
//	}
//
//	public void setEmailInput(String emailInput) {
//		this.emailInput = emailInput;
//	}
//
//	public String getContainerUri() {
//		return containerUri;
//	}
//
//	public void setContainerUri(String containerUri) {
//		this.containerUri = containerUri;
//	}
//
//	public String getNewUri() {
//		return newUri;
//	}
//
//	public void setNewUri(String newUri) {
//		this.newUri = newUri;
//	}
//
//	public Container getContainer() {
//		return container;
//	}
//
//	public void setContainer(Container container) {
//		this.container = container;
//	}
//
//	public void setAlbum(boolean isAlbum) {
//		this.isAlbum = isAlbum;
//	}
//
//	/**
//     * share the {@link Container}
//     */
//    public void share()
//    {
////        if (container != null)
////        {
////            if (container instanceof CollectionImeji)
////            {
////                shareCollection(container.getId().toString(), container.getMetadata().getTitle());
////            }
////            else if (container instanceof Album)
////            {
////                shareAlbum(container.getId().toString(), container.getMetadata().getTitle());
////            }
////        }
////        HistorySession historySession = (HistorySession)BeanHelper.getSessionBean(HistorySession.class);
////        try
////        {
////            FacesContext.getCurrentInstance().getExternalContext()
////                    .redirect(historySession.getCurrentPage().getUri().toString());
////        }
////        catch (IOException e)
////        {
////            logger.error("Error redirecting to previous page");
////        }
//    }
//
//    /**
//     * Share a {@link CollectionImeji}
//     * 
//     * @param id
//     * @param name
//     */
//    private void shareCollection(String id, String name)
//    {
//        SharingManager sm = new SharingManager();
//        boolean shared = false;
//        String message = "";
//        String role = "";
//        if (shared)
//        {
////            User dest = ObjectLoader.loadUser(this.getEmail(), sb.getUser());
//            EmailMessages emailMessages = new EmailMessages();
////            sendEmail(dest, sb.getMessage("email_shared_collection_subject"),
////                    emailMessages.getSharedCollectionMessage(sb.getUser().getName(), dest.getName(), name,
////                            getContainerHome(), role));
//            BeanHelper.info(sb.getMessage("success_share"));
//            BeanHelper.info(message);
//        }
//    }
//
//    /**
//     * Check if an imeji object with the given {@link URI} is a {@link CollectionImeji}
//     * 
//     * @param uri
//     * @return
//     */
//    public boolean isCollection(URI uri)
//    {
//        return uri.getPath().contains("/collection/");
//    }
//
//    /**
//     * Read the uri parameter in the url
//     * 
//     * @return
//     */
//    private URI readURI()
//    {
////        this.colUri = UrlHelper.getParameterValue("uri");
////        if (colUri != null)
////            return URI.create(colUri);
//        return null;
//    }
//
//    /**
//     * Share an {@link Album}
//     * 
//     * @param id
//     * @param name
//     */
//    private void shareAlbum(String id, String name)
//    {
//        SharingManager sm = new SharingManager();
//        boolean shared = false;
//        String message = "";
//        shared = sm.share(retrieveAlbum(id), sb.getUser(), this.getEmailInput(), selectedGrant, true);
//        message = sb.getLabel("album") + " " + id + " " + sb.getLabel("shared_with") + " " + this.getEmailInput() + " "
//                + sb.getLabel("as") + " " + selectedGrant.toString();
//        if (shared)
//        {
//            User dest = ObjectLoader.loadUser(this.getEmailInput(), sb.getUser());
//            EmailMessages emailMessages = new EmailMessages();
////            sendEmail(dest, sb.getMessage("email_shared_album_subject"), emailMessages.getSharedAlbumMessage(
////                    sb.getUser().getName(), dest.getName(), name, getContainerHome(), selectedGrant.toString()));
//            BeanHelper.info(sb.getMessage("success_share"));
//            BeanHelper.info(message);
//        }
//    }
//
//    /**
//     * Send email to the person to share with
//     * 
//     * @param dest
//     * @param subject
//     * @param message
//     */
//    private void sendEmail(User dest, String subject, String message)
//    {
//        EmailClient emailClient = new EmailClient();
//        try
//        {
//            emailClient.sendMail(dest.getEmail(), null,
//                    subject.replaceAll("XXX_INSTANCE_NAME_XXX", sb.getInstanceName()), message);
//        }
//        catch (Exception e)
//        {
//            logger.error("Error sending email", e);
//            BeanHelper.error(sb.getMessage("error") + ": Email not sent");
//        }
//    }
//
//    /**
//     * Get url of the {@link Container} Home page
//     * 
//     * @param id
//     * @return
//     */
////    public String getContainerHome()
////    {
////        Navigation navigation = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
////        String id = ObjectHelper.getId(container.getId());
////        if (isAlbum)
////            return navigation.getAlbumUrl() + id;
////        return navigation.getCollectionUrl() + id;
////    }
//
//    /**
//     * Retrieve the collection to share with the specified Id in the url
//     * 
//     * @param id
//     * @return
//     */
//    public CollectionImeji retrieveCollection(String id)
//    {
//        return ObjectLoader.loadCollectionLazy(URI.create(id), sb.getUser());
//    }
//
//    /**
//     * Retrieve the profile to share with the specified collection Id in the url
//     * 
//     * @param collId
//     * @return
//     */
//    public MetadataProfile retrieveProfile(String collId)
//    {
//        return ObjectLoader.loadProfile(retrieveCollection(collId).getProfile(), sb.getUser());
//    }
//
//    /**
//     * Retrieve the album to share with the specified Id in the url
//     * 
//     * @param albId
//     * @return
//     */
//    public Album retrieveAlbum(String albId)
//    {
//        return ObjectLoader.loadAlbumLazy(URI.create(albId), sb.getUser());
//    }
//
//    /**
//     * gettet
//     * 
//     * @return
//     */
//    public GrantType getSelectedGrant()
//    {
//        return selectedGrant;
//    }
//
//    /**
//     * setter
//     * 
//     * @param selectedGrant
//     */
//    public void setSelectedGrant(GrantType selectedGrant)
//    {
//        this.selectedGrant = selectedGrant;
//    }
//
//    /**
//     * getter
//     * 
//     * @return
//     */
//    public String getColId()
//    {
//        return colId;
//    }
//
//
//	/**
//     * getter
//     * 
//     * @return
//     */
//    public boolean isAlbum()
//    {
//        return this.isAlbum;
//    }
//}
