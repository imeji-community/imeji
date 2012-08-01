/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.query.Syntax;

import de.mpg.imeji.logic.ImejiJena;
import de.mpg.imeji.logic.search.query.SimpleQueryFactory;
import de.mpg.imeji.logic.search.util.CollectionUtils;
import de.mpg.imeji.logic.search.util.SearchIndexInitializer;
import de.mpg.imeji.logic.search.util.SortHelper;
import de.mpg.imeji.logic.search.vo.SearchElement;
import de.mpg.imeji.logic.search.vo.SearchGroup;
import de.mpg.imeji.logic.search.vo.SearchIndex;
import de.mpg.imeji.logic.search.vo.SearchLogicalRelation;
import de.mpg.imeji.logic.search.vo.SearchLogicalRelation.LOGICAL_RELATIONS;
import de.mpg.imeji.logic.search.vo.SearchPair;
import de.mpg.imeji.logic.search.vo.SearchQuery;
import de.mpg.imeji.logic.search.vo.SortCriterion;
import de.mpg.imeji.logic.vo.User;

public class Search
{
    private String containerURI = null;
    private String type = "http://imeji.org/terms/item";
    private static Logger logger = Logger.getLogger(Search.class);
    public static Map<String, SearchIndex> indexes = SearchIndexInitializer.init();

    public Search(String type, String containerURI)
    {
        this.containerURI = containerURI;
        if (type != null)
        {
            this.type = type;
        }
    }

    /**
     * Search for {@link SearchQuery} according to {@link User} permissions
     * @param sq
     * @param sortCri
     * @param user
     * @return
     */
    public SearchResult search(SearchQuery sq, SortCriterion sortCri, User user)
    {
        if (sortCri == null)
            sortCri = new SortCriterion();
        return new SearchResult(advanced(sq, sortCri, user), sortCri);
    }

    /**
     * Search for {@link SearchQuery} according to {@link User} permissions, within a set of possible results
     * @param previousResults
     * @param sq
     * @param sortCri
     * @param user
     * @return
     */
    public SearchResult search(List<String> previousResults, SearchQuery sq, SortCriterion sortCri, User user)
    {
        if (sortCri == null)
            sortCri = new SortCriterion();
        return new SearchResult(advanced(previousResults, sq, sortCri, user), sortCri);
    }

    /**
     * Search for with query following spaql syntax
     * @param sparqlQuery
     * @param sortCri
     * @return
     */
    public List<String> searchSimpleForQuery(String sparqlQuery, SortCriterion sortCri)
    {
        SearchResult sr = new SearchResult(ImejiSPARQL.exec(sparqlQuery, getModelName(type)), sortCri);
        return sr.getResults();
    }

    private List<String> advanced(SearchQuery sq, SortCriterion sortCri, User user)
    {
        return advanced(new ArrayList<String>(), sq, sortCri, user);
    }

    private List<String> advanced(List<String> previousResults, SearchQuery sq, SortCriterion sortCri, User user)
    {
        List<String> results = new ArrayList<String>(previousResults);
        // second case is useless so far, since all query within a container are container specific.
        if (sq.isEmpty() || (containerURI != null && results.isEmpty() && false))
        {
            results = simple(null, sortCri, user);
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
                            user));
                    results = doLogicalOperation(SortHelper.removeSortValue(subResults), logic,
                            SortHelper.removeSortValue(subResults));
                    break;
                case PAIR:
                    subResults = new ArrayList<String>(simple((SearchPair)se, sortCri, user));
                    results = doLogicalOperation(SortHelper.removeSortValue(subResults), logic,
                            SortHelper.removeSortValue(subResults));
                    break;
                case LOGICAL_RELATIONS:
                    logic = ((SearchLogicalRelation)se).getLogicalRelation();
                    break;
            }
            if (isFirstResult)
            {
                results = new ArrayList<String>(subResults);
            }
            isFirstResult = false;
        }
        return results;
    }

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

    private List<String> simple(SearchPair pair, SortCriterion sortCri, User user)
    {
        String sparqlQuery = SimpleQueryFactory.getQuery(type, pair, sortCri, user, (containerURI != null),
                getSpecificQuery());
        // sparqlQuery="PREFIX fn: <http://www.w3.org/2005/xpath-functions#> SELECT DISTINCT ?s WHERE {?s a <http://imeji.org/terms/item> . ?s <http://imeji.org/terms/properties> ?props . ?props <http://imeji.org/terms/status> ?status . ?s <http://imeji.org/terms/collection> ?c .FILTER(?status!=<http://imeji.org/terms/status#WITHDRAWN> && ( (?status=<http://imeji.org/terms/status#RELEASED> || ?c=<http://imeji.org/collection/13>)))  . ?props <http://purl.org/dc/terms/created> ?sort0}  ORDER BY DESC(?sort0)";
        // logger.info(sparqlQuery);
        return ImejiSPARQL.exec(sparqlQuery, getModelName(type));
    }

    private String getSpecificQuery()
    {
        String specificQuery = "";
        if (containerURI != null)
        {
            specificQuery = " <" + containerURI + "> <http://imeji.org/terms/item> ?s . ";
            specificQuery = " ?s <http://imeji.org/terms/collection> <" + containerURI + "> . ";
        }
        if ("http://imeji.org/terms/item".equals(type))
        {
            specificQuery += " ?s <http://imeji.org/terms/collection> ?c . ";
        }
        return specificQuery;
    }

    private String getModelName(String type)
    {
        if ("http://imeji.org/terms/collection".equals(type))
        {
            return ImejiJena.collectionModel;
        }
        else if ("http://imeji.org/terms/album".equals(type))
        {
            return ImejiJena.albumModel;
        }
        else
        {
            return null;
        }
    }

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

    public static SearchIndex getIndex(SearchIndex.names indexname)
    {
        return getIndex(indexname.name());
    }
}
