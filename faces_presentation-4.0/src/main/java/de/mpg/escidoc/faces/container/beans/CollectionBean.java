package de.mpg.escidoc.faces.container.beans;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import de.mpg.escidoc.faces.beans.SessionBean;
import de.mpg.escidoc.faces.container.FacesContainerVO;
import de.mpg.escidoc.faces.container.collection.CollectionController;
import de.mpg.escidoc.faces.container.collection.CollectionSession;
import de.mpg.escidoc.faces.container.collection.CollectionVO;
import de.mpg.escidoc.faces.metadata.Metadata;
import de.mpg.escidoc.faces.metadata.MetadataBean;
import de.mpg.escidoc.faces.metadata.ScreenConfiguration;
import de.mpg.escidoc.faces.metadata.MetadataBean.ConstraintBean;
import de.mpg.escidoc.faces.util.BeanHelper;
import de.mpg.escidoc.faces.util.ContextHelper;
import de.mpg.escidoc.faces.util.UserHelper;
import de.mpg.escidoc.services.common.referenceobjects.ContextRO;
import de.mpg.escidoc.services.common.valueobjects.GrantVO;
import de.mpg.escidoc.services.common.valueobjects.GrantVO.PredefinedRoles;

/**
 * JSF bean for {@link CollectionVO}
 * 
 * @author saquet
 *
 */
public class CollectionBean 
{
	/**
	 * All type of web pages supported by {@link CollectionBean}
	 * @author saquet
	 *
	 */
	public enum CollectionPageType
	{
		CREATE, EDIT, VIEW;
	}
	
	private CollectionVO collection = null;
	private CollectionController collectionController = null;
	private SessionBean sessionBean = null;
	private CollectionSession collectionSession = null;
	private ScreenConfiguration screenManager = null;
	private List<SelectItem> collectionsMenu = null;
	private CollectionPageType pageType = CollectionPageType.CREATE;
	private HttpServletRequest request = null;
	private FacesContext fc = null;
	private List<SelectItem> userDepositorGrants = null;
	private String selectedContext = null;
	
	
	private MdProfileSession mdProfileSession = null;
	private List<Metadata> metadataList = new ArrayList<Metadata>();
	private List<SelectItem> metadataMenu = new ArrayList<SelectItem>();
    private List<MetadataBean> metadataBeanList = new ArrayList<MetadataBean>();
	private int profilePosition;
	
	private static Logger logger = Logger.getLogger(CollectionBean.class);
	
	/**
	 * Default Constructor
	 */
	public CollectionBean() 
	{
		collectionController = new CollectionController();
		sessionBean = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
		collectionSession = (CollectionSession) BeanHelper.getSessionBean(CollectionSession.class);
		mdProfileSession = (MdProfileSession) BeanHelper.getSessionBean(MdProfileSession.class);
		collectionsMenu = new ArrayList<SelectItem>();
		userDepositorGrants = new ArrayList<SelectItem>();
		fc =  FacesContext.getCurrentInstance();
		request = (HttpServletRequest) fc.getExternalContext().getRequest();
		
		metadataList = mdProfileSession.getMetadataList();
		metadataBeanList = mdProfileSession.getMetadataBeanList();
		
		try 
		{
			init();
		} 
		catch (Exception e) 
		{
			sessionBean.setMessage("Error Initializing Collection Bean" + e);
			logger.error("Error initializing Collection Bean", e);
		}
	}
	
	/**
	 * Manage initialization of the bean according to url's parameters
	 * @throws Exception
	 */
	public void init() throws Exception
	{
		//metadataBeanList.clear();
		for (Metadata m : metadataList)
		{
		    if (m.getSimpleValue() == null)
		    {
			m.setSimpleValue("");
		    }
		    metadataMenu.add(new SelectItem(m.getIndex(), m.getLabel()));
		}
		/*
		if (metadataBeanList.size() == 0)
		{
		    metadataBeanList.add(new MetadataBean(metadataList));
		}
		*/
		
		
		String collectionId = request.getParameter("id"); 
		String page = request.getParameter("page");
		
		if (collectionId != null && CollectionPageType.EDIT.name().equalsIgnoreCase(page)) 
		{
			pageType = CollectionPageType.EDIT;
			collection = (CollectionVO)collectionController.retrieve(collectionId, sessionBean.getUserHandle());
		}
		else if (collectionId != null && CollectionPageType.VIEW.name().equalsIgnoreCase(page))
		{
			pageType = CollectionPageType.VIEW;
			collection = (CollectionVO)collectionController.retrieve(collectionId, sessionBean.getUserHandle());
		}
		else 
		{
			pageType = CollectionPageType.CREATE;
			collection = new CollectionVO(collectionSession.getCurrent().getScreenConfiguration());
			
			//collection.setMdProfile(new );
		}
		
		collectionSession.setCurrent(collection);
		
		for (FacesContainerVO c : collectionSession.getCollectionList().getList()) 
		{
			collectionsMenu.add(
					new SelectItem(c.getContentModel()
					, c.getMdRecord().getTitle().getValue()));
		}
		
		for (GrantVO g : UserHelper.getGrants(sessionBean.getUserHandle()))
		{
		    if (PredefinedRoles.DEPOSITOR.frameworkValue().equals(g.getRole()))
		    {
			userDepositorGrants.add(new SelectItem(g.getObjectRef()
				, ContextHelper.getContext(g.getObjectRef(), sessionBean.getUserHandle()).getName()));
		    }
		}
		
		
		selectedContext = "";
		//userDepositorGrants.add(new SelectItem("", "Select a context"));
	}
	
	/**
	 * Method to:
	 * <br> * Create a new collection
	 * <br> * Update a collection with new values
	 * @throws Exception
	 */
	public void save() throws Exception
	{
		collection.getMdProfile().getMetadataList().clear();
		collection.getMdProfile().getMetadataList().add(new Metadata("title","Title","http://purl.org/dc/elements/1.1"));
		collection.getMdProfile().getMetadataList().add(new Metadata("description","Description","http://purl.org/dc/elements/1.1"));
		int i = 2;
		for (MetadataBean m : mdProfileSession.getMetadataBeanList())
		{
		   // profile.getMetadataList().add(new Metadata(m.getCurrent().getName(), m.getCurrent().getIndex(), m.getCurrent().getNamespace()));
		    collection.getMdProfile().getMetadataList().add(new Metadata(m.getCurrent()));
		    collection.getMdProfile().getMetadataList().get(i).getConstraint().clear();
		    
		    for (ConstraintBean c : m.getConstraints())
		    {
			if (!"".equals(c.getValue()))
			{
				 collection.getMdProfile().getMetadataList().get(i).getConstraint().add(c.getValue());
			} 
		    }
		    i++;
		}
		
		if (CollectionPageType.CREATE.equals(this.pageType)) 
		{
			collectionController.create(collection, sessionBean.getUserHandle());
			sessionBean.setInformation("Collection successfully created");
		}
		if (CollectionPageType.EDIT.equals(this.pageType)) 
		{
			collectionController.edit(collection, sessionBean.getUserHandle());
			sessionBean.setInformation("Collection successfully edited");
		}
	}
	
	public CollectionVO getCollection() 
	{
		return collection;
	}

	public void setCollection(CollectionVO collectionVO) 
	{
		this.collection = collectionVO;
	}

	public ScreenConfiguration getScreenManager() 
	{
		return screenManager;
	}

	public void setScreenManager(ScreenConfiguration screenManager) 
	{
		this.screenManager = screenManager;
	}

	public List<SelectItem> getCollectionsMenu() 
	{
		return collectionsMenu;
	}

	public void setCollectionsMenu(List<SelectItem> collectionsMenu) 
	{
		this.collectionsMenu = collectionsMenu;
	}

	/**
	 * @return the pageType
	 */
	public CollectionPageType getPageType() 
	{
		return pageType;
	}

	/**
	 * @param pageType the pageType to set
	 */
	public void setPageType(CollectionPageType pageType) 
	{
		this.pageType = pageType;
	}
	
	 /**
     * JSF Listener for the title value
     * @param event
     */
    public void titleListener(ValueChangeEvent event) 
    {
    	if (event.getNewValue() != null && !event.getNewValue().equals(event.getOldValue())) 
    	{
			collectionSession.getCurrent().getMdRecord().getTitle().setValue(event.getNewValue().toString());
		}
    }
    
    /**
     * JSF Listener for the abstract value
     * @param event
     */
    public void descriptionListener(ValueChangeEvent event)
    {
    	if (event.getNewValue() != null && !event.getNewValue().equals(event.getOldValue())) 
    	{
    		collectionSession.getCurrent().getMdRecord().getAbstracts().get(0).setValue(event.getNewValue().toString());
		}
    }

    /**
     * @return the userDepositorGrants
     */
    public List<SelectItem> getUserDepositorGrants()
    {
        return userDepositorGrants;
    }

    /**
     * @param userDepositorGrants the userDepositorGrants to set
     */
    public void setUserDepositorGrants(List<SelectItem> userDepositorGrants)
    {
        this.userDepositorGrants = userDepositorGrants;
    }

    /**
     * @return the selectedContext
     */
    public String getSelectedContext()
    {
        return selectedContext;
    }

    /**
     * @param selectedContext the selectedContext to set
     */
    public void setSelectedContext(String selectedContext)
    {
        this.selectedContext = selectedContext;
    }
    
    
    public void selectContextListener(ValueChangeEvent event)
    {
	if (event.getNewValue() != null && !event.getNewValue().equals(event.getOldValue())) 
    	{
	    selectedContext = event.getNewValue().toString();
	    collection.setContext(new ContextRO(selectedContext));
	}
    }
    
    public String addMetadata()
    {
    	if(metadataBeanList.size()==0)
    	{
    		 metadataBeanList.add(new MetadataBean(metadataList));
    	}
    	else
    	{
    		metadataBeanList.add(getProfilePosition() + 1, new MetadataBean(metadataList));
    	}
	    
		return "";
	}
    
    public String removeMetadata()
    {
	    metadataBeanList.remove(getProfilePosition());
		return "";
		
    }
    
    /**
     * @return the mdProfile
     */
    public List<MetadataBean> getMetadataBeanList()
    {
        return metadataBeanList;
    }

    /**
     * @param mdProfile the mdProfile to set
     */
    public void setMetadataBeanList(List<MetadataBean> metadataBeanList)
    {
        this.metadataBeanList = metadataBeanList;
    }
    
    /**
     * @return the metadataMenu
     */
    public List<SelectItem> getMetadataMenu()
    {
        return metadataMenu;
    }

    /**
     * @param metadataMenu the metadataMenu to set
     */
    public void setMetadataMenu(List<SelectItem> metadataMenu)
    {
        this.metadataMenu = metadataMenu;
    }

	public void setProfilePosition(int profilePosition) {
		this.profilePosition = profilePosition;
	}

	public int getProfilePosition() {
		return profilePosition;
	}
    
}
