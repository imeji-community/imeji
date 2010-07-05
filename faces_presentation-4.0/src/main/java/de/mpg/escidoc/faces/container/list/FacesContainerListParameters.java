package de.mpg.escidoc.faces.container.list;

import java.util.ArrayList;
import java.util.List;

import de.mpg.escidoc.services.framework.PropertyReader;

public class FacesContainerListParameters 
{
	private String creator;
    private String contentModel;
    private String context;
    private String state = null;;
    private SortParameterType sortBy = SortParameterType.LAST_MODIFICATION_DATE;
    private OrderParameterType orderBy = OrderParameterType.ASCENDING;
    private int show = 10;
    private int page = 0;
    private List<String> id = null;
    private String mdSearch = null;

    
    public enum SortParameterType{
        STATE, NAME, LAST_MODIFICATION_DATE;
    }
    
    public enum OrderParameterType{
        ASCENDING, DESCENDING;
    }
    
    /**
     * Default constructor
     */
    public FacesContainerListParameters()
    {   
        try
        {
            contentModel = PropertyReader.getProperty("escidoc.faces.container.content-model.id");
            id = new ArrayList<String>();
        }
        catch (Exception e) 
        {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Constructor for {@link FacesContainerListParameters}
     * @param state state of the FacesContainers of the list.
     * @param sortBy sort value of the list.
     * @param orderBy order value of the list.
     * @param show number of FacesContainers of the list
     * @param page page number of the list
     */
    public FacesContainerListParameters(String state, SortParameterType sortBy, OrderParameterType orderBy, int show, int page, String creatorId, String mdSearch)
    {
        this();
        this.state = state;
        this.sortBy = sortBy;
        this.orderBy = orderBy;
        this.show = show;
        this.page = page;
        this.creator = creatorId;
        this.mdSearch = mdSearch;
    }
    
    /**
     * Clone the {@link FacesContainerListParameters}.
     */
    public FacesContainerListParameters clone()
    {
    	FacesContainerListParameters clone = new FacesContainerListParameters(this.state, this.sortBy, this.orderBy, this.show, this.page, this.creator, this.mdSearch);
        
        return clone;
    }
    
    /**
     * Generate the Search request for the parameters.
     * @return
     */
    public String getParametersAsSearchQuery()
    {
        String query =  "escidoc.content-model.objid=" + contentModel;
        query += " and escidoc.context.objid=" + context;
        
        if (creator != null)
        {
            query += " and escidoc.property.created-by.objid=" + creator;
        }
        if (mdSearch != null && !"".equals(mdSearch))
        {
            query += " and (escidoc.metadata=" + mdSearch + "*" + " or escidoc.property.latest-release.date=" + mdSearch + "*)";
        }
        if (id.size() > 0)
        {
            query += "escidoc.objid any \"";
            for (int i = 0; i < id.size() - 1; i++)
            {
                query += id.get(i) + " ";
            }
            // last id
            query += id.get(id.size() - 1) + "\"";
        }
        
        return query;
    }
    
    /**
     * Generate the sorting query for the parameters.
     * @return
     */
    public String getParametersAsSortingQuery()
    {
        String sortingQuery = null;
        
        if (sortBy != null && !"".equals(sortBy))
        {
          sortingQuery = getSearchIndex(sortBy) + ",,";
          
          if (sortingQuery != null)
          {
              sortingQuery += orderValue();
          }
          // If sort by owner, sort also by given name (case to creator have the same first name)
//          if (SortParameterType.CREATOR.equals(sortBy))
//          {
//              sortingQuery += "  " + "sort.escidoc.publication.creator.person.given-name" + ",," + orderValue();
//          }
        }
        
        return sortingQuery;
    }
    
    /**
     * Generate the Filter xml for the parameters
     * @return the xml
     */
    public String getParametersAsFilter()
    {
        String filter = "<param>" + "";
        if (context != null) 
        {
			filter += "<filter name=\"http://escidoc.de/core/01/structural-relations/context\">" +
            context +
            "</filter>";
		}
        
        if (contentModel != null) 
        {
        	 filter += "<filter name=\"http://escidoc.de/core/01/structural-relations/content-model\">"  + 
             contentModel +
             "</filter>";
		}
        
        if (creator != null)
        {
            filter +=   "<filter name=\"http://escidoc.de/core/01/structural-relations/created-by\">" +
                        creator + 
                        "</filter>";
        }

        if (state != null)
        {
            filter +=   "<filter name=\"http://escidoc.de/core/01/properties/public-status\">" + 
                            state +
                        "</filter>"; 
        }
        
        if (id.size() > 0)
        {
            filter += "<filter name=\"/id\">";
            for (int i = 0; i < id.size(); i++)
            {
                filter += "<id>" + id.get(i) + "</id>";      
            }
            filter += "</filter>";
        }
        
        if (sortBy != null)
        {
            filter +="<order-by sorting=\"" + orderBy.toString().toLowerCase()  + "\">" + getFilterIndex(sortBy) + "</order-by>";
        }
        
        if (show != 0)
        {
            filter += "<limit>" + show + "</limit>";
        }
        
        if (page != 0)
        {
            filter += "<offset>"+ ((page - 1)* show) + "</offset>";
        } 
        
        filter += "</param>";
        
        return filter;
    }
    
    /**
     * Return the value of the order for sorting query
     * @return
     */
    private String orderValue()
    {
        if (OrderParameterType.ASCENDING.equals(orderBy))
        {
            return "1";
        }
        else 
        {
            return "0";
        }
    }
    
    /**
     * Get the Filter index for a {@link SortParameterType}
     * @param sortedBy
     * @return
     */
    public String getFilterIndex(SortParameterType sortedBy)
    {
        if (SortParameterType.NAME.equals(sortedBy))
        {
            return "/properties/name";
        }
        if (SortParameterType.LAST_MODIFICATION_DATE.equals(sortedBy))
        {
            return "/last-modification-date";
        }
        //TODO complete filter
        if (SortParameterType.STATE.equals(sortedBy))
        {
            return "/properties/public-status";
        }
        return "";
    }
    
    /**
     * Get the Search index for a {@link SortParameterType}
     * @param sortedBy
     * @return
     */
    public String getSearchIndex(SortParameterType sortedBy)
    {
        if (SortParameterType.NAME.equals(sortedBy))
        {
            return "sort.escidoc.publication.title";
        }
        if (SortParameterType.LAST_MODIFICATION_DATE.equals(sortedBy))
        {
            return "sort.escidoc.property.latest-release.date";
        }
//        if (SortParameterType.CREATOR.equals(sortedBy))
//        {
//            return "sort.escidoc.publication.creator.person.family-name";
//        }
        return "";
    }

    public String getCreator()
    {
        return creator;
    }

    public void setCreator(String creator)
    {
        this.creator = creator;
    }

    public String getContentModel()
    {
        return contentModel;
    }

    public void setContentModel(String contentModel)
    {
        this.contentModel = contentModel;
    }

    public String getContext()
    {
        return context;
    }

    public void setContext(String context)
    {
        this.context = context;
    }

    public int getShow()
    {
        return show;
    }

    public void setShow(int show)
    {
        this.show = show;
    }

    public int getPage()
    {
        return page;
    }

    public void setPage(int page)
    {
        this.page = page;
    }

    public List<String> getId()
    {
        return id;
    }

    public void setId(List<String> id)
    {
        this.id = id;
    }

    public String getState()
    {
        return state;
    }

    public void setState(String state)
    {
        this.state = state;
    }
    
    public String getMdSearch()
    {
        return mdSearch;
    }

    public void setMdSearch(String mdSearch)
    {
        this.mdSearch = mdSearch;
    }

    public SortParameterType getSortBy()
    {
        return sortBy;
    }

    public void setSortBy(SortParameterType sortBy)
    {
        this.sortBy = sortBy;
    }

    public OrderParameterType getOrderBy()
    {
        return orderBy;
    }

    public void setOrderBy(OrderParameterType orderBy)
    {
        this.orderBy = orderBy;
    }
    
    /**
     * Set the label for the sort menu
     * TODO: Not nice, but works for now ;)
     * @param name
     * @return
     */
    public static String getSortLabel(String name)
    {       
        //TODO: Resource Bundle
        if (name.equals("NAME")) return "Name";
        if (name.equals("LAST_MODIFICATION_DATE")) return "Date last modified";
        //if (name.equals("CREATOR")) return "Author";
        if (name.equals("STATE")) return "State";
        else return name;
    }
    
}
