package de.mpg.escidoc.faces.container.beans;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import de.mpg.escidoc.faces.beans.SessionBean;
import de.mpg.escidoc.faces.container.FacesContainerVO;
import de.mpg.escidoc.faces.container.collection.CollectionController;
import de.mpg.escidoc.faces.container.collection.CollectionSession;
import de.mpg.escidoc.faces.container.collection.CollectionVO;
import de.mpg.escidoc.faces.metadata.ScreenConfiguration;
import de.mpg.escidoc.faces.util.BeanHelper;

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
	
	/**
	 * Default Constructor
	 */
	public CollectionBean() 
	{
		collectionController = new CollectionController();
		sessionBean = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
		collectionSession = (CollectionSession) BeanHelper.getSessionBean(CollectionSession.class);
		collectionsMenu = new ArrayList<SelectItem>();
		fc =  FacesContext.getCurrentInstance();
		request = (HttpServletRequest) fc.getExternalContext().getRequest();
		
		try 
		{
			init();
		} 
		catch (Exception e) 
		{
			sessionBean.setMessage("Error Initializing Collection Bean" + e);
		}
	}
	
	/**
	 * Manage initialization of the bean according to url's parameters
	 * @throws Exception
	 */
	public void init() throws Exception
	{
		String collectionId = request.getParameter("id");
		String page = request.getParameter("page");
		
		if (collectionId != null && CollectionPageType.EDIT.name().equalsIgnoreCase(page)) 
		{
			pageType = CollectionPageType.EDIT;
			collection = collectionController.retrieve(collectionId, sessionBean.getUserHandle());
		}
		else if (collectionId != null && CollectionPageType.VIEW.name().equalsIgnoreCase(page))
		{
			pageType = CollectionPageType.VIEW;
			collection = collectionController.retrieve(collectionId, sessionBean.getUserHandle());
		}
		else 
		{
			pageType = CollectionPageType.CREATE;
			collection = new CollectionVO(collectionSession.getCurrent().getScreenConfiguration());
		}
		
		collectionSession.setCurrent(collection);
		
		
		for (FacesContainerVO c : collectionSession.getCollectionList().getList()) 
		{
			collectionsMenu.add(
					new SelectItem(c.getContentModel()
					, c.getMdRecord().getTitle().getValue()));
		}
	}
	
	/**
	 * Method to:
	 * <br> * Create a new collection
	 * <br> * Update a collection with new values
	 * @throws Exception
	 */
	public void save() throws Exception
	{
		if (CollectionPageType.CREATE.equals(this.pageType)) 
		{
			collectionController.create(collection, sessionBean.getUserHandle());
		}
		if (CollectionPageType.EDIT.equals(this.pageType)) 
		{
			collectionController.edit(collection, sessionBean.getUserHandle());
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

	
}
