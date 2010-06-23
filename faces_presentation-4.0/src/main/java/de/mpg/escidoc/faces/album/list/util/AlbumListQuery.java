package de.mpg.escidoc.faces.album.list.util;

import java.util.ArrayList;
import java.util.List;

import de.mpg.escidoc.faces.beans.SessionBean;
import de.mpg.escidoc.faces.util.BeanHelper;
import de.mpg.escidoc.services.framework.PropertyReader;

/**
 * 
 * @author saquet
 *
 */
public class AlbumListQuery
{
    private String creator = null;
    private String contentModel;
    private String context;
    private String sortBy = null;
    private String orderBy = "ascending";
    private int limit = 0;
    private int offset = 0;
    private List<String> id = null;
    private String mdSearch = null;
    
    private SessionBean sessionBean = null;
    
    public AlbumListQuery()
    {  
        try
        {
            sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
            creator = sessionBean.getUser().getReference().getObjectId();
            contentModel = PropertyReader.getProperty("escidoc.faces.container.content-model.id");
            context = PropertyReader.getProperty("escidoc.faces.context.id");
            id = new ArrayList<String>();
        }
        catch (Exception e) 
        {
            throw new RuntimeException(e);
        }
    }
    
    public AlbumListQuery(String sortby, String orderBy, int limit, int offset, String mdSearch)
    {
        this();
        this.sortBy = sortby;
        this.orderBy = orderBy;
        this.limit = limit;
        this.offset = offset;
        this.mdSearch = mdSearch;
    }
    
    /**
     * Generate the request according to the values
     * @return the search request
     */
    public String getQuery()
    {
        String query =  "escidoc.content-model.objid=" + contentModel;
        query += " and escidoc.context.objid=" + context;
        
        if (creator != null)
        {
            query += " and escidoc.property.created-by.objid=" + creator;
        }
        if (mdSearch != null && !"".equals(mdSearch))
        {
            query += " and (escidoc.metadata=" + mdSearch + " or escidoc.property.latest-release.date=" + mdSearch + "*)";
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
     * Generate the query for the sorting of the list.
     * @return
     */
    public String getSortingQuery()
    {
        String sortingQuery = null;
        
        if (sortBy != null && !"".equals(sortBy))
        {
          sortingQuery =   formatSortBy(sortBy) + ",,";
          
          if (sortingQuery != null)
          {
              sortingQuery += orderValue();
          }
          if ("owner".equalsIgnoreCase(sortBy))
          {
              sortingQuery += "  " + formatSortBy("given-name") + ",," + orderValue();
          }
        }
        
        return sortingQuery;
    }
    
    private String orderValue()
    {
        if ("ascending".equals(orderBy))
        {
            return "1";
        }
        else 
        {
            return "0";
        }
    }
    
    private String formatSortBy(String sortedBy)
    {
        if ("name".equalsIgnoreCase(sortedBy))
        {
            return "sort." + sessionBean.getIndexBaseAlbum() + ".title";
        }
        if ("datepublished".equalsIgnoreCase(sortedBy))
        {
            return "sort.escidoc.property.latest-release.date";
        }
        if ("owner".equalsIgnoreCase(sortedBy))
        {
            return "sort." + sessionBean.getIndexBaseAlbum() + ".creator.person.family-name";
        }
        if ("given-name".equalsIgnoreCase(sortedBy))
        {
            return "sort." + sessionBean.getIndexBaseAlbum() + ".creator.person.given-name";
        }
        if ("affiliation".equalsIgnoreCase(sortedBy))
        {
            return "sort." + sessionBean.getIndexBaseAlbum() + ".creator.person.organization.organization-name";
        }
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

    public String getSortBy()
    {
        return sortBy;
    }

    public void setSortBy(String sortBy)
    {
        this.sortBy = sortBy;
    }

    public String getOrderBy()
    {
        return orderBy;
    }

    public void setOrderBy(String orderBy)
    {
        this.orderBy = orderBy;
    }

    public List<String> getId()
    {
        return id;
    }

    public void setId(List<String> id)
    {
        this.id = id;
    }

    public int getLimit()
    {
        return limit;
    }

    public void setLimit(int limit)
    {
        this.limit = limit;
    }

    public int getOffset()
    {
        return offset;
    }

    public void setOffset(int offset)
    {
        this.offset = offset;
    }
}
