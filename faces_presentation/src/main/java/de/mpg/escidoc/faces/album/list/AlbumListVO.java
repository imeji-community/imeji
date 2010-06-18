package de.mpg.escidoc.faces.album.list;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import de.mpg.escidoc.faces.album.AlbumVO;
import de.mpg.escidoc.faces.album.list.util.AlbumListParameters;

/**
 * 
 * @author saquet
 *
 */
public class AlbumListVO
{
    /**
     * The possible views to display a {@link AlbumListVO}
     * @author saquet
     *
     */
    public static enum ViewType{
        TABLE;
    }
    /**
     * Handler used to retrieve the list
     * @author saquet
     *
     */
    public static enum HandlerType
    {
        FILTER, SEARCH
    }
    /**
     * The list of albums.
     */
    private List<AlbumVO> list = new ArrayList<AlbumVO>();
    /**
     * The value for drop down menu
     */
    private List<SelectItem> menu = new ArrayList<SelectItem>();
    /**
     * The label of the list
     */
    private String label = "";
    /**
     * The size of the list.
     */
    private int size = 0;
    /**
     * The type the handler to retrieve the list
     */
    private HandlerType handler;
    /**
     * The filter to retrieve the list.
     * Used only if HandlerType is FILTER
     */
    private AlbumListParameters parameters = new AlbumListParameters();
    /**
     * The view used to display the list
     */
    private ViewType view = ViewType.TABLE;
    
    
    /**
     * Default constructor
     *  
     */
    public AlbumListVO() 
    {
    	
    }
    
    /**
     * Copy constructor
     * @param other
     */
    public AlbumListVO(AlbumListVO other)
    {
        this.handler = other.handler;
        this.label = other.label;
        this.list = other.list;
        this.parameters = other.parameters.clone();
        this.view = other.view;
        this.menu = other.menu;
        this.size = other.size;
    }
    
    /**
     * Constructor for customized new list.
     * @param list
     * @param filter
     * @param type
     */
    public AlbumListVO(List<AlbumVO> list, AlbumListParameters filter, HandlerType type)
    {
        this.list = list;
        size = this.list.size();
        this.parameters = filter;
        handler = type;
        init();
    }
    
    /**
     * Clone the current {@link AlbumListVO}
     * @return the clone
     */
    public AlbumListVO clone()
    {
        AlbumListVO clone = new AlbumListVO(this);
        
        return clone;
    }
    
    private void init()
    {
        menu.add(new SelectItem("", "Select an album"));
        
        for (int i = 0; i < size; i++)
        {
            menu.add(new SelectItem(list.get(i).getVersion().getObjectId(), list.get(i).getMdRecord().getTitle().getValue()));
        }
    }

    public List<AlbumVO> getList()
    {
        return list;
    }

    public void setList(List<AlbumVO> list)
    {
        this.list = list;
    }

    public int getSize()
    {
    	return size;
    }

    public void setSize(int size)
    {
    	this.size = size;
    }

    public HandlerType getHandler()
    {
        return handler;
    }

    public void setHandler(HandlerType handler)
    {
        this.handler = handler;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public List<SelectItem> getMenu()
    {
        return menu;
    }

    public void setMenu(List<SelectItem> menu)
    {
        this.menu = menu;
    }

    public AlbumListParameters getParameters()
    {
        return parameters;
    }

    public void setParameters(AlbumListParameters parameters)
    {
        this.parameters = parameters;
    }

    public ViewType getView()
    {
        return view;
    }

    public void setView(ViewType view)
    {
        this.view = view;
    }
    
    /**
     * Set the {@link ViewType} of the list from a {@link String}.
     * @param viewString
     */
    public void setViewFromStringValue(String viewString)
    {
        for (int i = 0; i < ViewType.values().length; i++)
        {
            if (ViewType.values()[i].toString().equalsIgnoreCase(viewString))
            {
                this.view = ViewType.values()[i];
            }
        }
    }

    
}
