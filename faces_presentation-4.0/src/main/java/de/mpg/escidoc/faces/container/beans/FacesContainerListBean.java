package de.mpg.escidoc.faces.container.beans;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import de.mpg.escidoc.faces.beans.Navigation;
import de.mpg.escidoc.faces.beans.SessionBean;
import de.mpg.escidoc.faces.container.FacesContainerVO;
import de.mpg.escidoc.faces.container.album.AlbumSession;
import de.mpg.escidoc.faces.container.collection.CollectionSession;
import de.mpg.escidoc.faces.container.list.FacesContainerListController;
import de.mpg.escidoc.faces.container.list.FacesContainerListParameters;
import de.mpg.escidoc.faces.container.list.FacesContainerListVO;
import de.mpg.escidoc.faces.container.list.FacesContainerListParameters.OrderParameterType;
import de.mpg.escidoc.faces.container.list.FacesContainerListParameters.SortParameterType;
import de.mpg.escidoc.faces.container.list.FacesContainerListVO.ViewType;
import de.mpg.escidoc.faces.util.BeanHelper;
import de.mpg.escidoc.services.common.valueobjects.ContainerVO.State;
import de.mpg.escidoc.services.framework.PropertyReader;

public class FacesContainerListBean 
{
	public enum ContainerListType
	{
		ALBUMS, COLLECTIONS, PUBLICATIONS;
	}
	
	private static final int DISPLAYED_PAGES = 5;
	
	private FacesContainerListVO list = null;
	private FacesContainerListController controller = null;
	private Navigation navigation = null;
	private SessionBean sessionBean = null;
	private CollectionSession collectionSession = null;
	private AlbumSession albumSession = null;
		
	private String url = null;
	private ContainerListType type = ContainerListType.COLLECTIONS;
	//All menus if the page
    private List<SelectItem> showMenu = null;
    private List<SelectItem> viewMenu = null;
    private List<SelectItem> sortMenu = null;
    private List<SelectItem> filterMenu = null;
    private Map<String, String> columnSortValuesMap;
    
	
	public FacesContainerListBean() 
	{
		list = new FacesContainerListVO();
		controller = new FacesContainerListController();
		
		navigation = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
		sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
		albumSession = (AlbumSession)BeanHelper.getSessionBean(AlbumSession.class);
		collectionSession = (CollectionSession)BeanHelper.getSessionBean(CollectionSession.class);
		
		initUrlParameters();
		initPageMenus();
		initList();
	}
	
	public String getInit()
	{
		 return "";
	}
	
	public void initUrlParameters()
	{
		HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
		
		if (request.getParameter("list") != null)
        {
			if (ContainerListType.COLLECTIONS.name().equalsIgnoreCase(request.getParameter("list"))) 
            {
            	list = collectionSession.getCollectionList();
            	type = ContainerListType.COLLECTIONS;
			}
            if (ContainerListType.ALBUMS.name().equalsIgnoreCase(request.getParameter("list"))) 
            {
            	list = albumSession.getMyAlbums();
            	type = ContainerListType.ALBUMS;
			}
            if (ContainerListType.PUBLICATIONS.name().equalsIgnoreCase(request.getParameter("list"))) 
            {
            	list = albumSession.getPublished();
            	type = ContainerListType.PUBLICATIONS;
			}
		}
		
        if (request.getParameter("show") != null)
        {
            list.getParameters().setShow(Integer.parseInt(request.getParameter("show")));
        }
        
        if (request.getParameter("page") != null)
        {
            list.getParameters().setPage(Integer.parseInt(request.getParameter("page")));
        }
        
        if (request.getParameter("view") != null)
        {
            list.setViewFromStringValue(request.getParameter("view"));
        }
        
        if (request.getParameter("sort") != null)
        {
           for (int i = 0; i < SortParameterType.values().length; i++)
            {
                if (SortParameterType.values()[i].name().equals(request.getParameter("sort")))
                {
                    list.getParameters().setSortBy(SortParameterType.values()[i]);
                }
            }
        }
        
        if (request.getParameter("order") != null)
        {
            for (int i = 0; i < OrderParameterType.values().length; i++)
            {
                if (OrderParameterType.values()[i].name().equals(request.getParameter("order")))
                {
                    list.getParameters().setOrderBy(OrderParameterType.values()[i]);
                }
            }
        }
        
        if (request.getParameter("tab") != null)
        {
        	if (ContainerListType.COLLECTIONS.equals(type)) 
        	{
        		collectionSession.setSelectedMenu(request.getParameter("tab"));
			}
        	else 
        	{
        		albumSession.setSelectedMenu(request.getParameter("tab"));
			}
        }
        
        if (request.getParameter("filter") != null) 
        {
        	if (ContainerListType.COLLECTIONS.equals(type)) 
        	{
        		collectionSession.setSelectedMenu(request.getParameter("filter"));
			}
        	else 
        	{
        		albumSession.setFilter(request.getParameter("filter"));
			}
		}
        
        if (request.getParameter("query") != null) 
        {
        	list.getParameters().setMdSearch(request.getParameter("query"));
		}
        else
        {
        	list.getParameters().setMdSearch(null);
        }
	}
	
	public void initList()
	{
		try 
		{
			list = controller.retrieve(list, sessionBean.getUserHandle());
		} 
		catch (Exception e) 
		{
			throw new RuntimeException("Error retrieving list: " + e);
		}
	}
	
	public String getListType()
	{
		return list.getHandler().toString();
	}

	/**
	 * @return the collectionListVO
	 */
	public FacesContainerListVO getList() 
	{
		return list;
	}

	/**
	 * @param collectionListVO the collectionListVO to set
	 */
	public void setList(FacesContainerListVO facesContainerListVO) 
	{
		this.list = facesContainerListVO;
	}
	
	 /**
     * Change the sorting order on click of the column.
     * @param event
     * @throws Exception
     */
    public void changeColumnSortField(ActionEvent event) throws Exception
    {
        Object newSortField = event.getComponent().getAttributes().get("sortField");
        
        for (int i = 0; i < SortParameterType.values().length; i++)
        {
            if (SortParameterType.values()[i].toString().equals(newSortField.toString()))
            {
                list.getParameters().setSortBy(SortParameterType.values()[i]);
                list.getParameters().setOrderBy(OrderParameterType.ASCENDING);
                list.getParameters().setPage(1);
            }
            else 
            {
                sessionBean.setMessage("Unknown sort value");
            }
        }
    }
    
    /**
     * Toggles the sorting order of this list between "ascending" and "descending".
     */
    public String getToggleOrder()
    {
        String url = "";
        
        if (OrderParameterType.ASCENDING.equals(list.getParameters().getOrderBy()))
        {
            url = this.getUrl(list.getParameters().getPage()
                    , list.getParameters().getShow()
                    , list.getView()
                    , list.getParameters().getSortBy()
                    , OrderParameterType.DESCENDING);
        }
        else
        {
            url = this.getUrl(list.getParameters().getPage()
                    , list.getParameters().getShow()
                    , list.getView()
                    , list.getParameters().getSortBy()
                    , OrderParameterType.ASCENDING);
        }
        
        return url;
    }
    
    /**
     * @return The total number of Pages of the current album list.
     */
    public int getTotalNumberOfPages()
    {
        int result = list.getSize() / list.getParameters().getShow();
        
        if (result * list.getParameters().getShow() < list.getSize())
        {
            result++;
        }
        
        if (result < 1)
        {
            result =1;
        }
        
        return result;
    }
    
    /**
     * Return url of the previous page in paginator.
     * @return
     */
    public String getPreviousPage()
    {
        if (list.getParameters().getPage() > 1)
        {
            String url = this.getUrl(list.getParameters().getPage() - 1
                    , list.getParameters().getShow()
                    , list.getView()
                    , list.getParameters().getSortBy()
                    , list.getParameters().getOrderBy());
            return url;
        }
        return this.getUrl();
    }
    
    /**
     * Returns Url of the previous page in paginator.
     * @return
     */
    public String getNextPage()
    {
        if (list.getParameters().getPage() <  getTotalNumberOfPages())
        {
            String url = this.getUrl(list.getParameters().getPage() + 1
                    , list.getParameters().getShow()
                    , list.getView()
                    , list.getParameters().getSortBy()
                    , list.getParameters().getOrderBy());
            return url;
        }
        return this.getUrl();
    }
    
    /**
     * Returns url of the Fist Page in paginator 
     * @return
     */
    public String getFirstPage()
    {
        String url = this.getUrl(1
                , list.getParameters().getShow()
                , list.getView()
                , list.getParameters().getSortBy()
                , list.getParameters().getOrderBy());
        return url;
    }
    
    /**
     * Returns url of the last page in paginator.
     * @return
     */
    public String getLastPage()
    {
        String url = this.getUrl(getTotalNumberOfPages()
                , list.getParameters().getShow()
                , list.getView()
                , list.getParameters().getSortBy()
                , list.getParameters().getOrderBy());
        return url;
    }
    
    /**
     * Get the list of pages in the paginator
     * @return
     */
    public List<SelectItem> getPages()
    {
        List<SelectItem> pages = new ArrayList<SelectItem>();
        
        int minPage = 1;
        
        if (list.getParameters().getPage() > DISPLAYED_PAGES / 2 + 1)
        {
            minPage = list.getParameters().getPage() - (int) (DISPLAYED_PAGES / 2);
        }
        
        int maxPage = minPage + DISPLAYED_PAGES - 1;
        
        if (maxPage >= getTotalNumberOfPages())
        {
            maxPage = getTotalNumberOfPages();
            minPage = getTotalNumberOfPages() - DISPLAYED_PAGES + 1;
            if (minPage < 1)
            {
                minPage = 1;
            }
        }
        
        for (int i = 0; i < maxPage - minPage + 1; i++)
        {
            pages.add(new SelectItem(getUrl(minPage + i
                        , list.getParameters().getShow()
                        , list.getView()
                        , list.getParameters().getSortBy()
                        , list.getParameters().getOrderBy())
                    , Integer.toString(minPage + i)));
        }
        
        return pages;
    }
    
    /**
     * Go to page value triggered by action event.
     * @param event
     * @throws IOException 
     */
    public void goToPage(ValueChangeEvent event) throws IOException
    {
        int newPage = Integer.parseInt(event.getNewValue().toString());

        if (newPage <= getTotalNumberOfPages() && newPage > 0)
        {
            FacesContext
                .getCurrentInstance()
                .getExternalContext()
                .redirect(
                        this.getUrl(newPage
                                        , list.getParameters().getShow()
                                        , list.getView()
                                        , list.getParameters().getSortBy()
                                        , list.getParameters().getOrderBy()));
        }
        else if(newPage > getTotalNumberOfPages() || newPage < 1)
        {
            sessionBean.setMessage(sessionBean.getMessage("message_go_to_page_01") + "  "
                    + newPage + "  " +  sessionBean.getMessage("message_go_to_page_02"));
            FacesContext
                .getCurrentInstance()
                .getExternalContext()
                .redirect(
                        this.getUrl(list.getParameters().getPage()
                                    , list.getParameters().getShow()
                                    , list.getView()
                                    , list.getParameters().getSortBy()
                                    , list.getParameters().getOrderBy()));
        }
    }
    
    /**
     * Action when user click on delete
     * @throws IOException
     */
    public void deleteLink() throws IOException
    {
    	boolean valid = false;
    	
    	for (int i = 0; i < list.getList().size(); i++) 
    	{
    		if (list.getList().get(i).isSelected())
    		{
    			valid = true;
			}
		}
    	if (valid) 
    	{
    		FacesContext
	    	.getCurrentInstance()
		    	.getExternalContext()
		    		.redirect(navigation.getConfirmationUrl()+ "/selected/delete");
		}
    	else
    	{
    		sessionBean.setMessage(sessionBean.getMessage("message_delete_multiple_select"));
    		FacesContext
		    	.getCurrentInstance()
			    	.getExternalContext()
			    		.redirect(getUrl());
		}
    }
    

	/**
	 * @return the url
	 */
	public String getUrl() 
	{
		String url = getUrl(list.getParameters().getPage()
	            , list.getParameters().getShow()
	            , list.getView()
	            , list.getParameters().getSortBy()
	            , list.getParameters().getOrderBy()) ;
		return url;
	}
	
	public String getUrl(int page, int show, ViewType view, SortParameterType sortBy, OrderParameterType orderBy)
    {
        String url = navigation.getApplicationUrl();
        
        switch (type) 
        {
			case ALBUMS:
				url += ContainerListType.ALBUMS.name();
				break;
			case COLLECTIONS:
				url += ContainerListType.COLLECTIONS.name();
				break;
			case PUBLICATIONS:
				url += ContainerListType.PUBLICATIONS.name();
				break;
			default:
				url += "";
				break;
		}
        
        url += "?view=" + view.name() + "&page=" + page + "&show=" + show + "&sort=" + sortBy.name() + "&order=" + orderBy.name();
        
        return url.toLowerCase();
    }
	
	 /**
     * Initialize the values of all menu of the page.
     * @throws URISyntaxException 
     * @throws IOException 
     */
    public void initPageMenus()
    {      
        viewMenu = new ArrayList<SelectItem>();
        showMenu = new ArrayList<SelectItem>();
        sortMenu = new ArrayList<SelectItem>();
        filterMenu = new ArrayList<SelectItem>();
        columnSortValuesMap = new HashMap<String, String>();
        
        for (int i = 0; i < ViewType.values().length; i++)
        {
            viewMenu.add(
                    new SelectItem(this.getUrl(1
                        , list.getParameters().getShow()
                        , ViewType.values()[i]
                        , list.getParameters().getSortBy()
                        , list.getParameters().getOrderBy())
                    , ViewType.values()[i].toString().substring(0, 1) + ViewType.values()[i].toString().substring(1).toLowerCase()));
        }
        String showOptionString = "";
        try
        {
            showOptionString = PropertyReader.getProperty("escidoc.faces.album.list.size.options");
        }
        catch (Exception e)
        {
            sessionBean.setMessage("Property for Show menu not found");
        }
        for (String option : showOptionString.split(","))
        {
            showMenu.add(
                    new SelectItem(this.getUrl(1
                        , Integer.parseInt(option)
                        , list.getView()
                        , list.getParameters().getSortBy()
                        , list.getParameters().getOrderBy())
                    , option));
        }
        for (int i = 0; i < SortParameterType.values().length; i++)
        {
            // Initialize the menu
            String label = FacesContainerListParameters.getSortLabel(SortParameterType.values()[i].name());
            sortMenu.add(
                    new SelectItem( getUrl(list.getParameters().getPage()
                        , list.getParameters().getShow()
                        , list.getView()
                        , SortParameterType.values()[i]
                        , list.getParameters().getOrderBy())
                    , label));
            
            // Initialize the map
            OrderParameterType order = OrderParameterType.ASCENDING;
            
            if (list.getParameters().getSortBy().equals( SortParameterType.values()[i])
                    && list.getParameters().getOrderBy().equals(OrderParameterType.ASCENDING))
            {
                order = OrderParameterType.DESCENDING;
            }
            
            columnSortValuesMap.put(SortParameterType.values()[i].name()
                            , getUrl(list.getParameters().getPage()
                                    , list.getParameters().getShow()
                                    , list.getView()
                                    , SortParameterType.values()[i]
                                    , order));
        }
        
        String url = getUrl(1
                , list.getParameters().getShow()
                , list.getView()
                , list.getParameters().getSortBy()
                , list.getParameters().getOrderBy()) ;
        
        filterMenu.add(new SelectItem(url + "?filter=all", "all albums"));
        filterMenu.add(new SelectItem(url + "?filter=public", "all public albums"));
        filterMenu.add(new SelectItem(url + "?filter=user", "my albums"));
        filterMenu.add(new SelectItem(url + "?filter=mypublic", "my public albums"));
        filterMenu.add(new SelectItem(url + "?filter=private", "my private albums"));
        filterMenu.add(new SelectItem(url + "?filter=withdrawn", "my withdrawn albums"));
    }

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) 
	{
		this.url = url;
	}
	
	  /**
     * Select an album out of the list
     * @param event
     * @throws Exception
     */
    public void selectContainer(ValueChangeEvent event) throws Exception
    {
        boolean selected = ((Boolean)event.getNewValue()).booleanValue();
        FacesContainerVO container = (FacesContainerVO)event.getComponent().getAttributes().get("id");
        
        if (container != null)
        {
        	container.setSelected(selected);
        }
    }
    
    /**
     * Select all item of the current list.
     * @throws IOException 
     */
    public void selectAll() throws IOException
    {
    	for (int i = 0; i < this.list.getList().size(); i++) 
    	{
    		if (list.getList().get(i).getState().equals(State.PENDING)) 
    		{
    			list.getList().get(i).setSelected(true);
			}
		}
    	
    	FacesContext
		    .getCurrentInstance()
			    .getExternalContext()
			    	.redirect(this.getUrl());
    }
    
    /**
     * Unselect all items of the current list.
     * @throws IOException 
     */
    public void selectNone() throws IOException
    {
    	for (int i = 0; i < this.list.getList().size(); i++) 
    	{
    		list.getList().get(i).setSelected(false);
		}
    	
    	FacesContext
		    .getCurrentInstance()
		    	.getExternalContext()
		    		.redirect(this.getUrl());
    }

	/**
	 * @return the filterMenu
	 */
	public List<SelectItem> getFilterMenu() 
	{
		return filterMenu;
	}

	/**
	 * @param filterMenu the filterMenu to set
	 */
	public void setFilterMenu(List<SelectItem> filterMenu) 
	{
		this.filterMenu = filterMenu;
	}

	/**
	 * @return the sortMenu
	 */
	public List<SelectItem> getSortMenu() 
	{
		return sortMenu;
	}

	/**
	 * @param sortMenu the sortMenu to set
	 */
	public void setSortMenu(List<SelectItem> sortMenu) 
	{
		this.sortMenu = sortMenu;
	}

	/**
	 * @return the showMenu
	 */
	public List<SelectItem> getShowMenu() {
		return showMenu;
	}

	/**
	 * @param showMenu the showMenu to set
	 */
	public void setShowMenu(List<SelectItem> showMenu) {
		this.showMenu = showMenu;
	}

	/**
	 * @return the viewMenu
	 */
	public List<SelectItem> getViewMenu() {
		return viewMenu;
	}

	/**
	 * @param viewMenu the viewMenu to set
	 */
	public void setViewMenu(List<SelectItem> viewMenu) {
		this.viewMenu = viewMenu;
	}

	/**
	 * @return the columnSortValuesMap
	 */
	public Map<String, String> getColumnSortValuesMap() {
		return columnSortValuesMap;
	}

	/**
	 * @param columnSortValuesMap the columnSortValuesMap to set
	 */
	public void setColumnSortValuesMap(Map<String, String> columnSortValuesMap) {
		this.columnSortValuesMap = columnSortValuesMap;
	}
	
	
}
