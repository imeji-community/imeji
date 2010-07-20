package de.mpg.escidoc.faces.pictures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import de.mpg.escidoc.faces.pictures.BrowseParameter.BrowseParemeterType;
import de.mpg.escidoc.faces.pictures.SortingParameter.OrderType;
import de.mpg.escidoc.faces.pictures.SortingParameter.SortParameterType;

/**
 * Java class for Faces Browse feature
 * @author saquet
 *
 */
public class Browse 
{
    public enum BrowseType
    {
	ALBUM, PERSON, SEARCHRESULT, PICTURES, COLLECTION;
    }
    
    private List<SortingParameter> sortingParameters = new ArrayList<SortingParameter>();
    private Map<BrowseParemeterType, BrowseParameter> browseParameters = new HashMap<BrowseParemeterType, BrowseParameter>();
	
    /**
     * Constructor  for an {@link HttpServletRequest}
     * @param request
     */
    public Browse(HttpServletRequest request) 
    {
        initParameters();
    }
    
    public void initParameters()
    {
	browseParameters.put(BrowseParemeterType.PAGE, new BrowseParameter(BrowseParemeterType.PAGE, "1"));
	browseParameters.put(BrowseParemeterType.SHOW, new BrowseParameter(BrowseParemeterType.SHOW, "12"));
    }
	
    /**
     * Parse Url for of the Browse page
     */
    public void parseUrl(HttpServletRequest request)
    {
        for (BrowseParemeterType param : BrowseParemeterType.values()) 
        {
            if (request.getParameter(param.name().toLowerCase()) != null
        	    || param != BrowseParemeterType.ALBUM
        	    || param != BrowseParemeterType.QUERY
        	    || param != BrowseParemeterType.PERSON
        	    || param != BrowseParemeterType.SELECTION
        	    || param != BrowseParemeterType.COLLECTION) 
            {
        	browseParameters.put(param, new BrowseParameter(param, request.getParameter(param.name().toLowerCase())));
            }
        }
    	
        List<SortingParameter> list = new ArrayList<SortingParameter>();
        
        for (SortParameterType param : SortParameterType.values()) 
        {
            String order = param.name().replace("SORT", "ORDER");
        		
            if (request.getParameter(param.name().toLowerCase()) != null 
       			&& request.getParameter(order.toLowerCase()) != null) 
            {
       		String orderValue = request.getParameter(order.toLowerCase());
       		SortingParameter sortParam = new SortingParameter(request.getParameter(param.name().toLowerCase()), OrderType.valueOf(orderValue.toUpperCase()));
        	list.add(sortParam);
            }	
        }
        
        if (list.size() > 0)
	{
            sortingParameters = list;
	}
    }
    
    public BrowseType getType()
    {
	if (getSearchQuery() != null)
	{	
	    return BrowseType.SEARCHRESULT;
	}
	else if (getPersonId() != null)
	{
	    return BrowseType.PERSON;
	}
	else if (getCollectionId() != null)
	{	
	    return BrowseType.COLLECTION;
	}
	else if (getAlbumId() != null)
	{
	    return BrowseType.ALBUM;
	}
	else
	{
	    return BrowseType.PICTURES;
	}
    }
    
    /**
     * Return the Sortkeys in CQL format
     * @return
     */
    public String getSorkeys()
    {
        String sortKeys = "";
    	
        for (SortingParameter param : sortingParameters) 
        {
            sortKeys += param.getCqlQuery() + " ";
        }
    	
        return sortKeys;
    }
    
    public String getCollectionId() 
    {
	if (browseParameters.get(BrowseParemeterType.COLLECTION) != null)
        {
	    return browseParameters.get(BrowseParemeterType.COLLECTION).getValue();
        }
        
        return null;
    }
    
    public String getAlbumId()
    {
	if (browseParameters.get(BrowseParemeterType.ALBUM) != null)
        {
	    return browseParameters.get(BrowseParemeterType.ALBUM).getValue();
        }
        
        return null;
    }
    
    public String getPersonId()
    {
        if (browseParameters.get(BrowseParemeterType.PERSON) != null)
        {
            return browseParameters.get(BrowseParemeterType.PERSON).getValue();
        }
        
        return null;
    }
    
    public String getSearchQuery()
    {
        if (browseParameters.get(BrowseParemeterType.QUERY) != null)
        {
            return browseParameters.get(BrowseParemeterType.QUERY).getValue();
        }
        
        return null;
    }
    
    public int getStartRecord()
    {
        return (getPage() - 1) * getShow() + 1;
    }
    
    public int getPage()
    {
        if (browseParameters.get(BrowseParemeterType.PAGE) != null
        	&& browseParameters.get(BrowseParemeterType.PAGE).getValue() != null)
	{
            return Integer.parseInt(browseParameters.get(BrowseParemeterType.PAGE).getValue());
	}
	
        return 1;
    }
    
    public int getShow()
    {
        if (browseParameters.get(BrowseParemeterType.SHOW) != null
        	&& browseParameters.get(BrowseParemeterType.SHOW).getValue() != null)
	{
            return Integer.parseInt(browseParameters.get(BrowseParemeterType.SHOW).getValue());
	}
	
        return 12;
    }
}
