package de.mpg.escidoc.faces.beans;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import de.mpg.escidoc.faces.album.AlbumController;
import de.mpg.escidoc.faces.album.AlbumVO;
import de.mpg.escidoc.faces.album.beans.AlbumSession;
import de.mpg.escidoc.faces.item.ItemVO;
import de.mpg.escidoc.faces.util.BeanHelper;
import de.mpg.escidoc.faces.util.QueryHelper;

public class Canvas
{
    private AlbumSession albumSession = null;
    private QueryHelper queryHelper = null;
    private FacesContext context = null;
    private HttpServletRequest request = null; 
    private Navigation navigation = null;
    private SessionBean sessionBean = null;    
    /**
     * The list of the albums to be used in the canvas
     */
    private List<SelectItem> albums = null;
    /**
     * The list of items of the current Album.
     */
    private List<ItemVO> items = null;
    /**
     * The selected value of the list
     */
    private String selected = null;
    /**
     * The position of the first item displayed
     */
    private int firstItemPosition = 1;
    
    /**
     * Default constructor
     */
    public Canvas()
    {
        context = FacesContext.getCurrentInstance();
        request = (HttpServletRequest) context.getExternalContext().getRequest();
        navigation = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
        albumSession = (AlbumSession)BeanHelper.getSessionBean(AlbumSession.class);
        sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        // Initialize albums
        initAlbums();
        // Initialize items
        initItems();
    }
    
    /**
     * Initialize the Drop down list of Albums
     */
    private void initAlbums()
    {
        albums = new ArrayList<SelectItem>();
        if (sessionBean.getUser() != null)
        {
            albums.add(new SelectItem(navigation.getApplicationUrl() + "jsf/Canvas.jsp", "Choose an Album"));
            
            try
            {
                for (int i = 0; i < albumSession.getMyAlbums().getSize(); i++)
                {
                    albums.add(new SelectItem( navigation.getApplicationUrl() + "jsf/Canvas.jsp?album=" 
                            +  albumSession.getMyAlbums().getList().get(i).getVersion().getObjectId()
                            , albumSession.getMyAlbums().getList().get(i).getMdRecord().getTitle().getValue()));
                }
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }       
            
            selected = navigation.getApplicationUrl() + "jsf/Canvas.jsp";
            
            if (request.getParameter("album") != null)
            {
                selected += "?album=" + request.getParameter("album");
            }
        }
    }
    
    /**
     * Retrieve all the items of the current album
     */
    private void initItems()
    {
        if (sessionBean.getUser() != null)
        {
            AlbumController albumController = new AlbumController();
            AlbumVO album = null;
			try 
			{
				album = albumController.retrieve(request.getParameter("album"), sessionBean.getUserHandle());
			} 
			catch (Exception e1) 
			{
				sessionBean.setMessage(request.getParameter("album") + " Not Found");
			}
            
            try
            {
                queryHelper = new QueryHelper();
                String query = queryHelper.createAlbumQuery(album);
                
                if (!"".equals(query))
                {
                    queryHelper.executeQuery(query, album.getSize(), firstItemPosition, "");
                    items = queryHelper.getItems();
                }
                
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
    }
    
    

    public List<SelectItem> getAlbums()
    {
        return albums;
    }

    public void setAlbums(List<SelectItem> albums)
    {
        this.albums = albums;
    }

    public String getSelected()
    {
        return selected;
    }

    public void setSelected(String selected)
    {
        this.selected = selected;
    }

    public List<ItemVO> getItems()
    {
        return items;
    }

    public void setItems(List<ItemVO> items)
    {
        this.items = items;
    }
}
