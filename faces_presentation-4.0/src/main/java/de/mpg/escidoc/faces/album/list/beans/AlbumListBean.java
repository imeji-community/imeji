package de.mpg.escidoc.faces.album.list.beans;

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
import javax.naming.InitialContext;
import javax.servlet.http.HttpServletRequest;

import de.mpg.escidoc.faces.album.AlbumVO;
import de.mpg.escidoc.faces.album.beans.AlbumSession;
import de.mpg.escidoc.faces.album.list.AlbumListVO;
import de.mpg.escidoc.faces.album.list.AlbumsListController;
import de.mpg.escidoc.faces.album.list.AlbumListVO.ViewType;
import de.mpg.escidoc.faces.album.list.util.AlbumListParameters;
import de.mpg.escidoc.faces.album.list.util.AlbumListParameters.OrderParameterType;
import de.mpg.escidoc.faces.album.list.util.AlbumListParameters.SortParameterType;
import de.mpg.escidoc.faces.beans.Navigation;
import de.mpg.escidoc.faces.beans.SessionBean;
import de.mpg.escidoc.faces.util.BeanHelper;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.valueobjects.ContainerVO.State;
import de.mpg.escidoc.services.framework.PropertyReader;

/**
 * 
 * @author saquet
 *
 */
public class AlbumListBean
{
    /**
     * Number of pages displayed in paginator.
     */
    private static final int DISPLAYED_PAGES = 5;
    /**
     * The list of currently displayed
     */
    private AlbumListVO list = null;
    /**
     * The menu for the value "show"
     */
    private List<SelectItem> showMenu;
    /**
     * The menu for the view.
     */
    private List<SelectItem> viewMenu;
    /**
     * The menu for the sorting.
     */
    private List<SelectItem> sortMenu;
    /**
     * THe menu for the filter
     */
    private List<SelectItem> filterMenu;
	/**
     * Map of links for each sorting values. Used for column sorting link.
     */
    private Map<String, String> columnSortValuesMap;
    /**
     * The url of the current Page.
     */
    private String url = null;
    /**
     * Search or filter
     */
    private String listType = "filter";

	//Other bean declaration
    private AlbumSession albumSession = null;
    private SessionBean sessionBean = null;
    private Navigation navigation = null;
    private XmlTransforming xmlTransforming = null; 
    
    /**
     * Default constructor
     * @throws Exception
     */
    public AlbumListBean() throws Exception
    {
        albumSession = (AlbumSession)BeanHelper.getSessionBean(AlbumSession.class);
        sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        navigation = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
        InitialContext context = new InitialContext();
        xmlTransforming = (XmlTransforming) context.lookup(XmlTransforming.SERVICE_NAME);
        initCurrentList();
        readUrlParameters();
        initPageMenu();
        defineFilter();
        reloadList();
        albumSession.getInitActiveAlbum();       
    }
    
    public String getInit()
    {
        return "";
    }
    
    /**
     * Initialize the list with list out of the session bean.
     */
    public void initCurrentList()
    {
        list = albumSession.getMyAlbums();
    }
    
    /**
     * Read the parameters defined in the url and set their value in the current list.
     */
    public void readUrlParameters()
    {
        HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
        
        if (request.getParameter("type") != null)
        {
            if (request.getParameter("type").equals("search")) 
            {
            	list = albumSession.getPublished();
            	listType = request.getParameter("type");
			}
        }
        else
        {
        	listType = "filter";
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
           albumSession.setSelectedMenu(request.getParameter("tab"));
        }
        
        if (request.getParameter("filter") != null) 
        {
        	albumSession.setFilter(request.getParameter("filter"));
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
    
    /**
     * Initialize the values of all menu of the page.
     * @throws URISyntaxException 
     * @throws IOException 
     */
    public void initPageMenu()
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
            String label = AlbumListParameters.getSortLabel(SortParameterType.values()[i].name());
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
     * Set the filter values before.
     */
    private void defineFilter()
    {
    	if (albumSession.getFilter().equalsIgnoreCase("all"))
    	{
    		list.getParameters().setCreator(null);
    		list.getParameters().setState(null);
		}
    	if (albumSession.getFilter().equalsIgnoreCase("user")) 
    	{
    		list.getParameters().setCreator(sessionBean.getUser().getReference().getObjectId());
    		list.getParameters().setState(null);
		}
    	if (albumSession.getFilter().equalsIgnoreCase("public")) 
    	{
    		list.getParameters().setCreator(null);
    		list.getParameters().setState("released");
		}
    	if (albumSession.getFilter().equalsIgnoreCase("mypublic")) 
    	{
    		list.getParameters().setCreator(sessionBean.getUser().getReference().getObjectId());
    		list.getParameters().setState("released");
		}
    	if (albumSession.getFilter().equalsIgnoreCase("private")) 
    	{
    		list.getParameters().setCreator(sessionBean.getUser().getReference().getObjectId());
    		list.getParameters().setState("pending");
		}
    	if (albumSession.getFilter().equalsIgnoreCase("withdrawn")) 
    	{
    		list.getParameters().setCreator(sessionBean.getUser().getReference().getObjectId());
    		list.getParameters().setState("withdrawn");
		}
    }
    
    /**
     * Reload the albums of the list.
     */
    public void reloadList()
    {
        AlbumsListController albumsListController = new AlbumsListController();
        try
        {
            list = albumsListController.retrieve(list, sessionBean.getUserHandle());
            if (listType.equals("search")) 
            {
            	albumSession.setPublished(list);
			}
            else 
            {
            	albumSession.setMyAlbums(list);
			}

        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        	//sessionBean.setMessage("Error updating album list");
        }
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

    public AlbumListVO getList()
    {
        return list;
    }

    public void setList(AlbumListVO list)
    {
        this.list = list;
    }
    
    /**
     * Create Album Page url with current list parameters.
     * @return
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
    
    /**
     * Create Album Page Url for the parameters
     * @param show
     * @param page
     * @param viewType.name()
     * @param sortParameterType
     * @param orderParameterType
     * @return
     */
    public String getUrl(int page, int show, ViewType view, SortParameterType sortBy, OrderParameterType orderBy)
    {
        String url = navigation.getAlbumsUrl();
        
        if (listType.equals("search")) 
        {
			url = navigation.getAlbumsSearchUrl();
		}
        
        url += "/" + view.name() + 
            "/" + page + 
            "/" + show + 
            "/" + sortBy.name() + 
            "/" + orderBy.name();
        
        return url;
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
     * Select an album out of the list
     * @param event
     * @throws Exception
     */
    public void selectAlbum(ValueChangeEvent event) throws Exception
    {
        boolean selected = ((Boolean)event.getNewValue()).booleanValue();
        AlbumVO album = (AlbumVO)event.getComponent().getAttributes().get("album");
        
        if (album != null)
        {
            album.setSelected(selected);
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
    
    public void setUrl(String url)
    {
        this.url = url;
    }

    public List<SelectItem> getShowMenu()
    {
        return showMenu;
    }

    public void setShowMenu(List<SelectItem> showMenu)
    {
        this.showMenu = showMenu;
    }

    public List<SelectItem> getViewMenu()
    {
        return viewMenu;
    }

    public void setViewMenu(List<SelectItem> viewMenu)
    {
        this.viewMenu = viewMenu;
    }

    public List<SelectItem> getSortMenu()
    {
        return sortMenu;
    }

    public void setSortMenu(List<SelectItem> sortMenu)
    {
        this.sortMenu = sortMenu;
    }

    public Map<String, String> getColumnSortValuesMap()
    {
        return columnSortValuesMap;
    }

    public void setColumnSortValuesMap(Map<String, String> columnSortValuesMap)
    {
        this.columnSortValuesMap = columnSortValuesMap;
    }
    
    public List<SelectItem> getFilterMenu() 
    {
		return filterMenu;
	}

	public void setFilterMenu(List<SelectItem> filterMenu) 
	{
		this.filterMenu = filterMenu;
	}

	public String getListType() 
	{
		return listType;
	}

	public void setListType(String listType) 
	{
		this.listType = listType;
	}
	
	
    
}
