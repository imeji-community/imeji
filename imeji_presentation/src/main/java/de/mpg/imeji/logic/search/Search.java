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
import de.mpg.imeji.logic.search.vo.SearchCriterion;
import de.mpg.imeji.logic.search.vo.SearchCriterion.Operator;
import de.mpg.imeji.logic.search.vo.SearchIndex;
import de.mpg.imeji.logic.search.vo.SortCriterion;
import de.mpg.imeji.logic.util.CollectionUtils;
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

    public SearchResult search(List<SearchCriterion> scList, SortCriterion sortCri, User user)
    {
        return new SearchResult(searchAdvanced(scList, sortCri, user));
    }

    @SuppressWarnings("unchecked")
    public List<String> searchAdvanced(List<SearchCriterion> scList, SortCriterion sortCri, User user)
    {
        List<String> results = new ArrayList<String>();
        if (scList == null)
            scList = new ArrayList<SearchCriterion>();
        if (scList.isEmpty() || containerURI != null)
        {
            results = searchSimple(null, sortCri, user);
        }
        boolean hasAnEmptySubResults = false;
        for (SearchCriterion sc : scList)
        {
            List<String> subResults = new ArrayList<String>();
            if (sc.getChildren().isEmpty())
            {
                subResults = searchSimple(sc, sortCri, user);
            }
            else
            {
                subResults = searchAdvanced(sc.getChildren(), sortCri, user);
            }
            if (subResults.isEmpty())
                hasAnEmptySubResults = true;
            if (results.isEmpty() && !hasAnEmptySubResults)
            {
                results = new ArrayList<String>(subResults);
            }
            if (Operator.AND.equals(sc.getOperator()) || Operator.NOTAND.equals(sc.getOperator()))
            {
                results = (List<String>)CollectionUtils.intersection(results, subResults);
            }
            else
            {
                results = (List<String>)CollectionUtils.union(results, subResults);
            }
        }
        return results;
    }

    public List<String> searchSimple(SearchCriterion sc, SortCriterion sortCri, User user)
    {
        String sq = SimpleQueryFactory.getQuery(type, sc, sortCri, user, (containerURI != null), getSpecificQuery());
        //logger.info(sq);
        return ImejiSPARQL.exec(sq);
    }

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
            //specificQuery += " . <" + containerURI + "> <http://imeji.org/terms/item> ?s";
        }
        return specificQuery;
    }
}
