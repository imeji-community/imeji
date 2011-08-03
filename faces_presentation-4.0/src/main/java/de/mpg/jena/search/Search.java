package de.mpg.jena.search;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections.ListUtils;
import org.apache.log4j.Logger;

import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.controller.SearchCriterion.Operator;
import de.mpg.jena.controller.SortCriterion;
import de.mpg.jena.sparql.ImejiSPARQL;
import de.mpg.jena.sparql.query.SimpleQueryFactory;
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
	
	public List<String> searchAdvanced(List<SearchCriterion> scList, SortCriterion sortCri, User user)
	{
		LinkedList<String> results = new LinkedList<String>();
		
		if (scList == null) scList = new ArrayList<SearchCriterion>(); 
		
		if (scList.isEmpty() || containerURI != null)
		{
			results = (LinkedList<String>) searchSimple(null, sortCri, user);
		}
    	
    	for (SearchCriterion sc : scList) 
   		{
    		LinkedList<String> subResults = new LinkedList<String>();
    		
    		if (sc.getChildren().isEmpty())
    		{
    			subResults = (LinkedList<String>) searchSimple(sc, sortCri, user);
    		}
    		else
    		{
    			subResults = (LinkedList<String>) searchAdvanced(sc.getChildren(), sortCri, user);
    		}
    		
    		if (results.isEmpty())
    		{
    			results =  new LinkedList<String>(subResults);
    		}

    		if (Operator.AND.equals(sc.getOperator()) || Operator.NOTAND.equals(sc.getOperator()))
    		{
    			List<String> inter = ListUtils.intersection(results, subResults);
       			results = new LinkedList<String>(inter);
    		}
    		else
    		{
    			List<String> sum = ListUtils.sum(results, subResults);
       			results = new LinkedList<String>(sum);
    		}
   		}
    	
		return results;
	}
	
	public List<String> searchSimple(SearchCriterion sc, SortCriterion sortCri, User user)
	{	
		String sq = SimpleQueryFactory.search(type, sc, sortCri, user, (containerURI != null), getSpecificQuery());
		return  ImejiSPARQL.exec(sq);
	}
	
	private LinkedList<String> getAllURIs(SortCriterion sortCri, User user)
	{
		String sq = SimpleQueryFactory.search(type, null, sortCri, user, (containerURI != null), getSpecificQuery());
		return ImejiSPARQL.exec(sq);
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
