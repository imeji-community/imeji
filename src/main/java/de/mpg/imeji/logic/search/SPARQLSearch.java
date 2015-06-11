/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.search;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.vocabulary.RDF;
import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.ImejiSPARQL;
import de.mpg.imeji.logic.search.query.SimpleQueryFactory;
import de.mpg.imeji.logic.search.util.CollectionUtils;
import de.mpg.imeji.logic.search.util.SearchIndexInitializer;
import de.mpg.imeji.logic.search.vo.*;
import de.mpg.imeji.logic.search.vo.SearchIndex.IndexNames;
import de.mpg.imeji.logic.search.vo.SearchLogicalRelation.LOGICAL_RELATIONS;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.*;
import de.mpg.j2j.helper.J2JHelper;
import de.mpg.j2j.helper.SortHelper;
import org.apache.log4j.Logger;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * imeji Search, using sparql query
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class SPARQLSearch implements Search
{
    private String containerURI = null;
    private SearchType type = SearchType.ITEM;
    private static Logger logger = Logger.getLogger(SPARQLSearch.class);
    public static Map<String, SearchIndex> indexes = SearchIndexInitializer.init();

    /**
     * Initialize the search
     * 
     * @param type
     * @param containerURI
     */
    public SPARQLSearch(SearchType type, String containerURI)
    {
        this.containerURI = containerURI;
        if (type != null)
        {
            this.type = type;
        }
    }

    /**
     * Search for {@link SearchQuery} according to {@link User} permissions
     * 
     * @param sq
     * @param sortCri
     * @param user
     * @return
     */
    public SearchResult search(SearchQuery sq, SortCriterion sortCri, User user, String spaceId)
    {
    	 return new SearchResult(advanced(sq, sortCri, user, spaceId), sortCri);
    }

 
    /**
     * Search for {@link SearchQuery} according to {@link User} permissions, within a set of possible results
     * @param sq
     * @param sortCri
     * @param user
     * @param uris
     * 
     * @return
     */
    public SearchResult search(SearchQuery sq, SortCriterion sortCri, User user, List<String> uris, String spaceId)
    {
        return new SearchResult(advanced(uris, sq, sortCri, user, spaceId), sortCri);
    }
    

    /**
     * Search for with query following sparql syntax
     * 
     * @param sparqlQuery
     * @return
     */
    public SearchResult searchSimpleForQuery(String sparqlQuery)
    {
        return new SearchResult(ImejiSPARQL.exec(sparqlQuery, getModelName(type)), null);
    }

    /**
     * Search for complex queries over the complete imeji data
     * 
     * @param sq
     * @param sortCri
     * @param user
     * @return
     */
    private List<String> advanced(SearchQuery sq, SortCriterion sortCri, User user, String spaceId)
    {
        return advanced(new ArrayList<String>(), sq, sortCri, user, spaceId);
    }

    /**
     * Search for complex queries over a set of data (defined by the previousResults parameter as a {@link List} of
     * {@link Object} uris):<br/>
     * - do a list of simple search and perform logical relations over the results
     * 
     * @param previousResults
     * @param sq
     * @param sortCri
     * @param user
     * @return
     */
    private List<String> advanced(List<String> previousResults, SearchQuery sq, SortCriterion sortCri, User user, String spaceId)
    {
        //indexes = SearchIndexInitializer.init();
        // Set null parameters
        if (sq == null)
            sq = new SearchQuery();
        if (sortCri == null)
            sortCri = new SortCriterion();
        List<String> results = null;
        if (previousResults == null)
            results = new ArrayList<String>();
        else
            results = new ArrayList<String>(previousResults);
        // second case is useless so far, since all query within a container are container specific.
        if (sq.isEmpty() || (containerURI != null && results.isEmpty() && false))
        {
            results = simple(null, sortCri, user, spaceId);
        }
        boolean isFirstResult = results.isEmpty();
        LOGICAL_RELATIONS logic = LOGICAL_RELATIONS.AND;
        for (SearchElement se : sq.getElements())
        {
            List<String> subResults = new ArrayList<String>();
            switch (se.getType())
            {
                case GROUP:
                    subResults = new ArrayList<String>(advanced(new SearchQuery(((SearchGroup)se).getGroup()), sortCri,
                            user, spaceId));
                    results = doLogicalOperation(SortHelper.removeSortValue(subResults), logic,
                            SortHelper.removeSortValue(results));
                    break;
                case PAIR:
                    subResults = new ArrayList<String>(simple((SearchPair)se, sortCri, user, spaceId));
                    results = doLogicalOperation(SortHelper.removeSortValue(subResults), logic,
                            SortHelper.removeSortValue(results));
                    break;
                case METADATA:
                    subResults = new ArrayList<String>(simple((SearchPair)se, sortCri, user, spaceId));
                    results = doLogicalOperation(SortHelper.removeSortValue(subResults), logic,
                            SortHelper.removeSortValue(results));
                    break;
                case LOGICAL_RELATIONS:
                    logic = ((SearchLogicalRelation)se).getLogicalRelation();
                    break;
                default:
                    break;
            }
            if (se.getType() != SearchElement.SEARCH_ELEMENTS.LOGICAL_RELATIONS)
            {
                // if the query has started with a logical relation, it should not be counted as a first result
                if (isFirstResult)
                {
                    // if is is the first subresults of a query, add it to the results, instead of or/and operation
                    results = new ArrayList<String>(subResults);
                }
                isFirstResult = false;
            }
        }
        return results;
    }
    
     /**
     * Simple search for search with one {@link SearchPair}
     * 
     * @param pair
     * @param sortCri
     * @param user
     * @return
     */
    private List<String> simple(SearchPair pair, SortCriterion sortCri, User user, String spaceId)
    {
    	String sparqlQuery = SimpleQueryFactory.getQuery(getModelName(type), getRDFType(type), pair, sortCri, user,  (containerURI != null), getSpecificQuery(user), spaceId);
    	//System.out.println(sparqlQuery);
    	return ImejiSPARQL.exec(sparqlQuery, null);
    }

    
    /**
     * Perform {@link LOGICAL_RELATIONS} between 2 {@link List} of {@link String}
     * 
     * @param l1
     * @param logic
     * @param l2
     * @return
     */
    @SuppressWarnings("unchecked")
    private List<String> doLogicalOperation(List<String> l1, LOGICAL_RELATIONS logic, List<String> l2)
    {
        switch (logic)
        {
            case AND:
                l1 = (List<String>)CollectionUtils.intersection(l1, l2);
                break;
            case OR:
                l1 = (List<String>)CollectionUtils.union(l1, l2);
                break;
        }
        return l1;
    }

    /**
     * REturn a sparql query {@link String} to be added to search query in some specific cases
     * 
     * @return
     */
    private String getSpecificQuery(User user)
    {
        String specificQuery = "";
        if (containerURI != null)
        {
            String id = ObjectHelper.getId(URI.create(containerURI));
            if (containerURI.equals(ObjectHelper.getURI(CollectionImeji.class, id).toString()))
            {
                specificQuery = "?s <http://imeji.org/terms/collection> <" + containerURI + ">  .";
                //specificQuery = "FILTER (?c=<" + containerURI + ">) .";
            }
            else if (containerURI.equals(ObjectHelper.getURI(Album.class, id).toString()))
            {
                type = SearchType.ALL;
                specificQuery = "<" + containerURI + "> <http://imeji.org/terms/item> ?s .";
            }
        }
        if (SearchType.ITEM.equals(type) || SearchType.ALL.equals(type))
        {
        	//below is already included in the security query, no need to do it again
        	//       if (containerURI == null && user != null)
        	//              specificQuery += "?s <http://imeji.org/terms/collection> ?c .";
        }
        if (SearchType.PROFILE.equals(type))
        {
            specificQuery = "";
        }
        return specificQuery;
    }

    /**
     * Return the name of the {@link Model} as a {@link String} according to the current {@link SearchType}
     * 
     * @param type
     * @return
     */
    private String getModelName(SearchType type)
    {
        switch (type)
        {
            case ITEM:
                return Imeji.imageModel;
            case COLLECTION:
                return Imeji.collectionModel;
            case ALBUM:
                return Imeji.albumModel;
            case PROFILE:
                return Imeji.profileModel;
            case USER:
            	return Imeji.userModel;
            default:
                return null;
        }
    }

    /**
     * Return the {@link RDF}.type of object searched according to the {@link SearchType}
     * 
     * @param type
     * @return
     */
    private String getRDFType(SearchType type)
    {
        switch (type)
        {
            case COLLECTION:
                return J2JHelper.getResourceNamespace(new CollectionImeji());
            case ALBUM:
                return J2JHelper.getResourceNamespace(new Album());
            case PROFILE:
                return J2JHelper.getResourceNamespace(new MetadataProfile());
            default:
                return J2JHelper.getResourceNamespace(new Item());
        }
    }

    /**
     * Get {@link SearchIndex} from its {@link String} name
     * 
     * @param indexName
     * @return
     */
    public static SearchIndex getIndex(String indexName)
    {
        SearchIndex index = indexes.get(indexName);
        if (index == null)
        {
            logger.error("Unknown index: " + indexName);
            throw new RuntimeException("Unknown index: " + indexName);
        }
        return index;
    }

    /**
     * Get {@link SearchIndex} from its {@link IndexNames}
     * 
     * @param indexname
     * @return
     */
    public static SearchIndex getIndex(SearchIndex.IndexNames indexname)
    {
        return getIndex(indexname.name());
    }
}
