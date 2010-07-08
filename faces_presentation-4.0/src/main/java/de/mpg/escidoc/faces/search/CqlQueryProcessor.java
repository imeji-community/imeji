package de.mpg.escidoc.faces.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import de.mpg.escidoc.faces.container.album.AlbumController;
import de.mpg.escidoc.faces.container.album.AlbumVO;
import de.mpg.escidoc.faces.metadata.Metadata;
import de.mpg.escidoc.faces.search.helper.MetadataComparator;

/**
 * This class manage Faces search guery.
 * @author saquet
 *
 */
public class CqlQueryProcessor 
{
	/**
	 * The list of parameters to be processed in the the query.
	 */
	private List<Metadata> params = null;
	/**
	 * The cql query used by escidoc.
	 */
	private String cqlQuery = null;
	/**
	 * The user-friendly query.
	 */
	private String prettyQuery = null;
	/**
	 * The collection to search within
	 */
	private AlbumVO collectionVO = null;
	private Set<String> subQueries = null;
	
	/**
	 * Default constructor
	 */
	public CqlQueryProcessor() 
	{
		params = new ArrayList<Metadata>();
		subQueries = new HashSet<String>();
	}
	
	/**
	 * Constructor for non login user
	 * @param params the Map of parameters used for the search.
	 */
	public CqlQueryProcessor(List<Metadata> params) 
	{
		this();
		this.params = params;
	}
	
	/**
	 * Constructor for a {@link UrlQueryParser}
	 * @param urlQueryParser
	 */
	public CqlQueryProcessor(UrlQueryParser urlQueryParser)
	{
            this();
            params.addAll(urlQueryParser.getSearchParameterMap().values());
            
            if (urlQueryParser.getCollectionId() != null) 
            {
                AlbumController albumController = new AlbumController();
                try 
                {
            	collectionVO = (AlbumVO) albumController.retrieve(urlQueryParser.getCollectionId(), null);
                } 
                catch (Exception e) 
                {
            	throw new RuntimeException(e);
                }
            }
	}

	/**
	 * Process the query
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String process()
	{
        // Sort alphabetically the list of parameters
        Collections.sort(params, new MetadataComparator());
       
        processMetadata();
        processCollection();
        
        // close the query
        if (cqlQuery != null)
        {
            cqlQuery += ")";
            prettyQuery += " )";
        }
        
		return cqlQuery;
	}
	
	/**
	 * Write the part of the query related to the metadata search.
	 */
	private void processMetadata()
	{
		// For each metadata which has a value, then compute the query.
        for (int i = 0; i < params.size(); i++)
        {
            for (int j = 0; j <params.get(i).getValue().size(); j++)
            {
                if (!"".equals( params.get(i).getValue().get(j))
                        ||!"".equals(params.get(i).getMin())
                        || !"".equals(params.get(i).getMax()))
                {
                    computeQuery(params.get(i), params.get(i).getValue().get(j));
                }
            }
        }
	}
	
	/**
	 * Write the part of the query related to the collection search.
	 */
	private void processCollection() 
	{
		if ( collectionVO != null && cqlQuery != null)
        {
            if (cqlQuery == null)
            {
                cqlQuery = "(";
            }
            else
            {
                cqlQuery += ") and (";
            }
            
            if (collectionVO.getSize() > 0)
            {
                cqlQuery += "escidoc.objid any \"";
                for (int j = 0; j <collectionVO.getMembers().size() - 1; j++)
                {
                    cqlQuery += collectionVO.getMembers().get(j).getObjectId() + " ";
                }
                // last id
                cqlQuery += collectionVO.getMembers()
                            .get(collectionVO.getSize()-1).getObjectId()
                            + "\"";
            }
        }
	}
	 /**
     * Add to the search query the value of a metadata
     */
    private void computeQuery(Metadata md, String value)
    {
        // Initialize the query
        if (cqlQuery == null)
        {
            cqlQuery = "(";
            prettyQuery = "( ";
        }  
        
        // Create the query
        if (!"".equals(value))
        {
            // Add "AND" or "OR"
            addLogicalRelation(md.getGroup());
            cqlQuery += md.getIndex() + "=\"" + value + "\"";
            prettyQuery += md.getLabel() + "=\"" + value + "\"";
        }
        
        addRangeValuesToQueries(md);
    }
    
    /**
     * Add the range values to the queries if the md has some.
     * @param md
     */
    private void addRangeValuesToQueries(Metadata md)
    {
    	// Add minimum value
    	if (!"".equals(md.getMin()))
        {
            addLogicalRelation(md.getGroup());
            if (!"".equals(md.getMax()))
            {
                cqlQuery += "(";
            }
            
            cqlQuery += md.getIndex() + ">=" +  md.getMin();
            prettyQuery += md.getMin() + " <= " + md.getLabel();
        }
    	
    	//Add maximum value
        if (!"".equals(md.getMax()))
        {
            if (!"".equals(md.getMin()))
            {
                cqlQuery += " and ";
                prettyQuery += " <= " + md.getMax();
                cqlQuery +=  md.getIndex() + "<=" +  md.getMax() + ")";
            }
            else 
            {
                addLogicalRelation(md.getGroup());
                prettyQuery +=  md.getLabel() +" <= " + md.getMax();
                cqlQuery +=  md.getIndex() + "<=" +  md.getMax();
            }
        }
    }
    
    /**
     * Add the logical relation to the query according to the group of the metadata that should be added to the query.
     * @param searchgroup
     */
    private void addLogicalRelation(String searchgroup)
    {
        if (subQueries.contains(searchgroup))
        {
            cqlQuery += " or ";
            prettyQuery += " OR ";
        }
        else
        {
            subQueries.add(searchgroup);
            
            if (!"(".equals(cqlQuery))
            {
                cqlQuery += ") and (";
                prettyQuery += " ) AND ( ";
            }
        }
    }
    
    public String getPrettyQuery() 
    {
		return prettyQuery;
	}

	public void setPrettyQuery(String prettyQuery) 
	{
		this.prettyQuery = prettyQuery;
	}


    public AlbumVO getCollectionVO() 
    {
		return collectionVO;
	}

	public void setCollectionVO(AlbumVO collectionVO) 
	{
		this.collectionVO = collectionVO;
	}
}
