package de.mpg.escidoc.faces.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.mpg.escidoc.faces.metadata.Metadata;
import de.mpg.escidoc.faces.metadata.ScreenConfiguration;

/**
 * Parse an url search query according to a screen configuration.. 
 * @author saquet
 *
 */
public class UrlQueryParser 
{
	/**
	 * Tha map the search parameters.
	 */
	private Map<String, Metadata>  searchParameterMap = null;

	/**
	 * The identifier of the collection 
	 */
	private String collectionId = null;
	
	
	/**
	 * Constructor
	 * @param urlQuery
	 */
	public UrlQueryParser(ScreenConfiguration screen) 
	{
		initializesearchParameterMap(screen);
	}
	
    /**
     * Initialize the search parameters out of the screen configuration.
     */
    private void initializesearchParameterMap(ScreenConfiguration screen)
    {
    	searchParameterMap = new HashMap<String, Metadata>();
    	
    	for (int i = 0; i < screen.getMdList().size(); i++)
        {
            searchParameterMap.put(screen.getMdList().get(i).getIndex(), screen.getMdList().get(i));
        }
    }
	
	/**
	 * Parse the url query.
	 * @return
	 */
	public Map<String, Metadata> parse(String urlQuery)
	{
		String searchParameters[] = null;
		
		if (urlQuery != null) 
		{
			searchParameters = urlQuery.split("_");
		}
		
        for (int i = 0; i < searchParameters.length; i++)
        {
            String index[] = searchParameters[i].split("\\.");
            String name = "";
            int valueLength = 1;
            String range = null;
            
            // Check if the parameter is a min or a max
            if (index.length > 2 
                       && ("max".equals(index[index.length - 2])
                       || "min".equals(index[index.length - 2])))
            {
                valueLength = 2;
                range = index[index.length - 2];
            }
            
            // Build the name of the parameter            
            for (int j = 0; j < index.length - valueLength ; j++)
            {
                if ("".equals(name))
                {
                    name += index[j];
                }
                else
                {
                    name += "." + index[j];
                }
            }
            
            // Get the value of the parameter
            String value = index[index.length - 1];
            
            if (searchParameterMap.get(name) != null)
            {
                // Initialize the value
                if (searchParameterMap.get(name).getValue() == null)
                {
                    List<String> init = new ArrayList<String>();
                    searchParameterMap.get(name).setValue(init);
                }
                // Set the value to the parameter
                if (range == null)
                {
                	searchParameterMap.get(name).getValue().add(value);
                    
                    // clean leer values
                    for (int j = 0; j < searchParameterMap.get(name).getValue().size(); j++)
                    {
                        if ("".equals(searchParameterMap.get(name).getValue().get(j)))
                        {
                        	searchParameterMap.get(name).getValue().remove(j);
                        } 
                    }
                }
                else if ("min".equals(range))
                {
                	searchParameterMap.get(name).setMin(value);
                }
                else if ("max".equals(range))
                {
                	searchParameterMap.get(name).setMax(value);
                }
            }
            
            index = searchParameters[i].split(":");
            
            if (index.length == 3 && "collection".equalsIgnoreCase(index[0]))
            {
            	collectionId = index[1] + ":" + index[2];
            }
        }
		
		return searchParameterMap;
	}

	public String getCollectionId() 
	{
		return collectionId;
	}

	public void setCollectionId(String collectionId) 
	{
		this.collectionId = collectionId;
	}
	
	public Map<String, Metadata> getSearchParameterMap() 
	{
		return searchParameterMap;
	}

	public void setSearchParameterMap(Map<String, Metadata> searchParameterMap) 
	{
		this.searchParameterMap = searchParameterMap;
	}

}
