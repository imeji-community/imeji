package de.mpg.escidoc.faces.container.collection;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import de.mpg.escidoc.faces.beans.SessionBean;
import de.mpg.escidoc.faces.container.FacesContainerVO;
import de.mpg.escidoc.faces.container.album.AlbumVO;
import de.mpg.escidoc.faces.container.list.FacesContainerListController;
import de.mpg.escidoc.faces.container.list.FacesContainerListParameters;
import de.mpg.escidoc.faces.container.list.FacesContainerListVO;
import de.mpg.escidoc.faces.container.list.FacesContainerListParameters.OrderParameterType;
import de.mpg.escidoc.faces.container.list.FacesContainerListParameters.SortParameterType;
import de.mpg.escidoc.faces.container.list.FacesContainerListVO.HandlerType;
import de.mpg.escidoc.faces.metadata.ScreenConfiguration;
import de.mpg.escidoc.faces.util.BeanHelper;
import de.mpg.escidoc.services.framework.PropertyReader;

public class CollectionSession 
{
	// Collection currently displayed (Via CollectionBean)
	private CollectionVO current = null;
	// Collection active (image browsed are in that collection)
	private CollectionVO active = null;
	private CollectionListVO collectionList = null;
	private FacesContainerListController controller = null;
	private CollectionController collectionController = null;
	private SessionBean sessionBean = null;
	
	private String selectedMenu = "SORTING";
	
	public CollectionSession()
	{
		controller = new FacesContainerListController();
		collectionController = new CollectionController();
		sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
		
		try 
		{
			init();
		} 
		catch (Exception e) 
		{
			throw new RuntimeException("Error initializing collection session:",  e);
		}
	}
	
	public void init() throws Exception
	{
		current = new CollectionVO(new ScreenConfiguration());
		FacesContainerListParameters parameters = new FacesContainerListParameters(null, SortParameterType.LAST_MODIFICATION_DATE, OrderParameterType.DESCENDING, 10, 1, null, null);
		parameters.setContentModel(PropertyReader.getProperty("escidoc.faces.collection.content-model.id"));
		collectionList = new CollectionListVO( new ArrayList<FacesContainerVO>(), parameters, HandlerType.FILTER);
	}
	
	/**
     * Retrieve the current album
     */
    public String getInitCurrentCollection() throws Exception
    {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        
        if (request.getParameter("collection") != null 
                && !"".equals(request.getParameter("album")))
        {
            try 
            {
		current = (CollectionVO) collectionController.retrieve((String)request.getParameter("collection"), sessionBean.getUserHandle());
            } 
            catch (Exception e) 
            {
            	sessionBean.setMessage((String)request.getParameter("collection") + " not found!");
            }
        }
        else 
        {
            current = new CollectionVO(new ScreenConfiguration());
        }
        
        return "";
    }
	
	
	/**
	 * @return the current Collection in session
	 */
	public CollectionVO getCurrent()
	{
		return current;
	}

	/**
	 * Set a current collection in session
	 * @param currentCollection
	 */
	public void setCurrent(CollectionVO currentCollection) 
	{
		this.current= currentCollection;
	}

	public CollectionListVO getCollectionList() 
	{
		return collectionList;
	}

	public void setCollectionList(CollectionListVO collectionList) 
	{
		this.collectionList = collectionList;
	}
	
	/**
	 * @return the active
	 */
	public CollectionVO getActive() 
	{
		return active;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(CollectionVO active) 
	{
		this.active = active;
	}

	/**
	 * @return the selectedMenu
	 */
	public String getSelectedMenu()
	{
		return selectedMenu;
	}

	/**
	 * @param selectedMenu the selectedMenu to set
	 */
	public void setSelectedMenu(String selectedMenu) 
	{
		this.selectedMenu = selectedMenu;
	}


}
