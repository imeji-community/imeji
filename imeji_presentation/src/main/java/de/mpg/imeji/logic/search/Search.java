/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.search.query.SimpleQueryFactory;
import de.mpg.imeji.logic.search.util.SearchIndexInitializer;
import de.mpg.imeji.logic.search.vo.SearchElement;
import de.mpg.imeji.logic.search.vo.SearchGroup;
import de.mpg.imeji.logic.search.vo.SearchIndex;
import de.mpg.imeji.logic.search.vo.SearchLogicalRelation;
import de.mpg.imeji.logic.search.vo.SearchLogicalRelation.LOGICAL_RELATIONS;
import de.mpg.imeji.logic.search.vo.SearchPair;
import de.mpg.imeji.logic.search.vo.SearchQuery;
import de.mpg.imeji.logic.search.vo.SortCriterion;
import de.mpg.imeji.logic.util.CollectionUtils;
import de.mpg.imeji.logic.vo.User;

public class Search
{
    private String containerURI = null;
    private String type = "http://imeji.org/terms/item";
    private static Logger logger = Logger.getLogger(Search.class);
    public static Map<String, SearchIndex> indexes = SearchIndexInitializer.init();

    public static SearchIndex getIndex(String indexName)
    {
        SearchIndex index = indexes.get(indexName);
        if (index == null)
        {
            SearchIndexInitializer.init();
            logger.error("Unknown index: " + indexName);
            throw new RuntimeException("Unknown index: " + indexName);
        }
        return index;
    }

    public static SearchIndex getIndex(SearchIndex.names indexname)
    {
        return getIndex(indexname.name());
    }

    public Search(String type, String containerURI)
    {
        this.containerURI = containerURI;
        if (type != null)
        {
            this.type = type;
        }
    }

    // public SearchResult search(List<SearchCriterion> scList, SortCriterion sortCri, User user)
    // {
    // return new SearchResult(searchAdvanced(scList, sortCri, user));
    // }
    public SearchResult search(SearchQuery sq, SortCriterion sortCri, User user)
    {
        return new SearchResult(advanced(sq, sortCri, user));
    }

    public List<String> advanced(SearchQuery sq, SortCriterion sortCri, User user)
    {
        return advanced(new ArrayList<String>(), sq, sortCri, user);
    }

    public List<String> advanced(List<String> results, SearchQuery sq, SortCriterion sortCri, User user)
    {
        if (sq.isEmpty() || (containerURI != null && results.isEmpty()))
        {
            results = simple(null, sortCri, user);
        }
        for (SearchElement se : sq.getElements())
        {
            List<String> subResults = new ArrayList<String>();
            LOGICAL_RELATIONS logic = LOGICAL_RELATIONS.AND;
            switch (se.getType())
            {
                case GROUP:
                    subResults = new ArrayList<String>(advanced(new SearchQuery(((SearchGroup)se).getGroup()), sortCri,
                            user));
                    results = doLogicalOperation(results, logic, subResults);
                    break;
                case PAIR:
                    subResults = new ArrayList<String>(simple((SearchPair)se, sortCri, user));
                    results = doLogicalOperation(results, logic, subResults);
                    break;
                case LOGICAL_RELATIONS:
                    logic = ((SearchLogicalRelation)se).getLogicalRelation();
                    break;
            }
        }
        return results;
    }

    private List<String> doLogicalOperation(List<String> l1, LOGICAL_RELATIONS logic, List<String> l2)
    {
        if (!l2.isEmpty() && l1.isEmpty())
        {
            l1 = new ArrayList<String>(l2);
        }
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

    @SuppressWarnings("unchecked")
    // public List<String> searchAdvanced(List<SearchCriterion> scList, SortCriterion sortCri, User user)
    // {
    // List<String> results = new ArrayList<String>();
    // if (scList == null)
    // scList = new ArrayList<SearchCriterion>();
    // if (scList.isEmpty() || containerURI != null)
    // {
    // results = searchSimple(null, sortCri, user);
    // }
    // boolean hasAnEmptySubResults = false;
    // for (SearchCriterion sc : scList)
    // {
    // List<String> subResults = new ArrayList<String>();
    // if (sc.getChildren().isEmpty())
    // {
    // subResults = searchSimple(sc, sortCri, user);
    // }
    // else
    // {
    // subResults = searchAdvanced(sc.getChildren(), sortCri, user);
    // }
    // if (subResults.isEmpty())
    // hasAnEmptySubResults = true;
    // if (results.isEmpty() && !hasAnEmptySubResults)
    // {
    // results = new ArrayList<String>(subResults);
    // }
    // if (Operator.AND.equals(sc.getOperator()) || Operator.NOTAND.equals(sc.getOperator()))
    // {
    // results = (List<String>)CollectionUtils.intersection(results, subResults);
    // }
    // else
    // {
    // results = (List<String>)CollectionUtils.union(results, subResults);
    // }
    // }
    // return results;
    // }
    private List<String> simple(SearchPair pair, SortCriterion sortCri, User user)
    {
        String sparqlQuery = SimpleQueryFactory.getQuery(type, pair, sortCri, user, (containerURI != null),
                getSpecificQuery());
        // sparqlQuery="PREFIX fn: <http://www.w3.org/2005/xpath-functions#> SELECT DISTINCT ?s WHERE {?s a <http://imeji.org/terms/item> . ?s <http://imeji.org/terms/properties> ?props . ?props <http://imeji.org/terms/status> ?status . ?s <http://imeji.org/terms/collection> ?c  . <http://imeji.org/collection/3> <http://imeji.org/terms/item> ?s  .FILTER(?status!=<http://imeji.org/terms/status#WITHDRAWN> && ( (?status=<http://imeji.org/terms/status#RELEASED> || ?c=<http://imeji.org/collection/3>))) . ?s <http://imeji.org/terms/metadataSet> ?mds . ?mds <http://imeji.org/terms/metadata> ?md  . ?md <http://imeji.org/terms/statement> ?el  .FILTER(?el=<http://imeji.org/statement/58d7d957-e311-44e3-a151-0336b5f78e4e>) }";
        logger.info(sparqlQuery);
        return ImejiSPARQL.exec(sparqlQuery);
    }

    // public List<String> searchSimple(SearchCriterion sc, SortCriterion sortCri, User user)
    // {
    // String sq = SimpleQueryFactory.getQuery(type, sc, sortCri, user, (containerURI != null), getSpecificQuery());
    // // logger.info(sq);
    // return ImejiSPARQL.exec(sq);
    // }
    public List<String> searchSimpleForQuery(String query)
    {
        return ImejiSPARQL.exec(query);
    }

    private String getSpecificQuery()
    {
        String specificQuery = "";
        if ("http://imeji.org/terms/item".equals(type))
        {
            specificQuery += ". ?s <http://imeji.org/terms/collection> ?c ";
        }
        if (containerURI != null)
        {
            specificQuery += " . <" + containerURI + "> <http://imeji.org/terms/item> ?s";
        }
        return specificQuery;
    }
}
