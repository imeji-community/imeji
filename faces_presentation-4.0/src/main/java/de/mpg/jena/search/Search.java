/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.jena.search;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.ListUtils;
import org.apache.log4j.Logger;

import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.controller.SearchCriterion.Operator;
import de.mpg.jena.controller.SortCriterion;
import de.mpg.jena.search.query.SimpleQueryFactory;
import de.mpg.jena.util.CollectionUtils;
import de.mpg.jena.vo.User;

public class Search 
{
	private String containerURI = null;
	private String type = "http://imeji.mpdl.mpg.de/image";

	private static Logger logger = Logger.getLogger(Search.class);

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

		if (scList == null) scList = new ArrayList<SearchCriterion>(); 

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

			if (subResults.isEmpty()) hasAnEmptySubResults = true;

			if (results.isEmpty() && !hasAnEmptySubResults)
			{
				results =  new ArrayList<String>(subResults);
			}
			if (Operator.AND.equals(sc.getOperator()) || Operator.NOTAND.equals(sc.getOperator()))
			{
				results = (List<String>) CollectionUtils.intersection(results, subResults);
			}
			else
			{    			
				results = (List<String>) CollectionUtils.union(results, subResults);
			}
		}

		return results;
	}

	public List<String> searchSimple(SearchCriterion sc, SortCriterion sortCri, User user)
	{	
		String sq = SimpleQueryFactory.getQuery(type, sc, sortCri, user, (containerURI != null), getSpecificQuery());
		//logger.info(ImejiSPARQL.exec(sq).size() + sq);
		//sq = "PREFIX fn: <http://www.w3.org/2005/xpath-functions#> SELECT DISTINCT ?s WHERE {?s a <http://imeji.mpdl.mpg.de/image> . ?s <http://imeji.mpdl.mpg.de/properties> ?props . ?props <http://imeji.mpdl.mpg.de/status> ?status . ?s <http://imeji.mpdl.mpg.de/collection> ?c  . ?s <http://imeji.mpdl.mpg.de/metadataSet> ?mds . ?mds <http://imeji.mpdl.mpg.de/metadata> ?md  . ?md <http://imeji.mpdl.mpg.de/metadata/person> ?p . ?p <http://purl.org/escidoc/metadata/terms/0.1/family-name> ?el  .FILTER(regex(?el, 'TODOROKI', 'i')) . ?props <http://imeji.mpdl.mpg.de/creationDate> ?sort0}  ORDER BY DESC(?sort0)";
		return  ImejiSPARQL.exec(sq);
	}
	
	private String getSpecificQuery()
	{
		String specificQuery = "";
		if ("http://imeji.mpdl.mpg.de/image".equals(type))
		{
			specificQuery += ". ?s <http://imeji.mpdl.mpg.de/collection> ?c ";
		}
		if (containerURI != null) 
		{
			specificQuery +=  " . <" + containerURI + "> <http://imeji.mpdl.mpg.de/images> ?s";
		}
		return specificQuery;
	}
}
