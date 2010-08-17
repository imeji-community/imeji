package de.mpg.imeji.vo.list;


import de.mpg.imeji.vo.list.util.ListParameters;

/**
 * A paginated List for Imeji VOs
 * @author saquet
 *
 */
public class ListVO
{
    /**
     * Views type
     */
    public static enum ViewType{
        TABLE;
    }
    /**
     * Handler used to retrieve the list
     */
    public static enum HandlerType
    {
        FILTER, SEARCH
    }

    protected String label = "";
    protected int size = 0;
    protected HandlerType handler;
    protected ListParameters parameters = new ListParameters();
    protected ViewType view = ViewType.TABLE;
    protected String name = null;
    
    
    /**
     * Default constructor
     *  
     */
    public ListVO() 
    {
    	handler = HandlerType.FILTER;
    }
    
    /**
     * Copy constructor
     * @param other
     */
    public ListVO(ListVO other)
    {
        this.handler = other.handler;
        this.label = other.label;
        this.parameters = other.parameters.clone();
        this.view = other.view;
        this.size = other.size;
    }
    

    /**
     * Clone the current {@link ContainerListVO}
     * @return the clone
     */
    public ListVO clone()
    {
    	ListVO clone = new ListVO(this);
        
        return clone;
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

    public ListParameters getParameters()
    {
        return parameters;
    }

    public void setParameters(ListParameters parameters)
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

    /**
     * @return the name
     */
    public String getName() 
    {
	return name;
    }	
    
    /**
     * @param name the name to set
     */
    public void setName(String name) 
    {
	this.name = name;
    }
    
}
