package de.mpg.jena.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections.ListUtils;
import org.apache.log4j.Logger;

import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.controller.SearchCriterion.Operator;
import de.mpg.jena.controller.SortCriterion;
import de.mpg.jena.sparql.ImejiSPARQL;
import de.mpg.jena.sparql.QuerySPARQL;
import de.mpg.jena.sparql.query.QuerySPARQLImpl;
import de.mpg.jena.sparql.query.SimpleQueryFactory;
import de.mpg.jena.vo.User;

public class SearchHelper 
{
	private String containerURI = null;
	private boolean isCollection = false;
	
	private static Logger logger = Logger.getLogger(SearchHelper.class);
	
	public SearchHelper(String containerURI) 
	{
		this.containerURI = containerURI;
		isCollection = (containerURI != null);
	}
	
	
	public List<String> advancedSearchImages(List<SearchCriterion> scList, SortCriterion sortCri, User user)
	{
		LinkedList<String> results = new LinkedList<String>();
		
		if (scList == null) scList = new ArrayList<SearchCriterion>(); 
		
		if (scList.isEmpty() || containerURI != null)
		{
			results = getAllURIs(sortCri, user);
		}
    	
    	for (SearchCriterion sc : scList) 
   		{
    		LinkedList<String> subResults = new LinkedList<String>();
    		
    		if (sc.getChildren().isEmpty())
    		{
    			subResults = (LinkedList<String>) searchImages(sc, sortCri, user);
    		}
    		else
    		{
    			subResults = (LinkedList<String>) advancedSearchImages(sc.getChildren(), sortCri, user);
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
	
	public List<String> searchImages(SearchCriterion sc, SortCriterion sortCri, User user)
	{
		QuerySPARQL querySPARQL = new QuerySPARQLImpl();
		LinkedList<String> results = new LinkedList<String>();
    	
		List<SearchCriterion> l = new ArrayList<SearchCriterion>();
		
		if (sc != null)
		{
			l.add(sc);
		}
		
		//String q =  querySPARQL.createQuery(l, sortCri , "http://imeji.mpdl.mpg.de/image", "", "", -1, 0, user, isCollection);
		String sq = SimpleQueryFactory.search("http://imeji.mpdl.mpg.de/image", sc, sortCri, user, isCollection);
//		
//		logger.info("current:" + q);
//		logger.info("experiment:" + sq);
//		
//		long a = System.currentTimeMillis();
		results =  ImejiSPARQL.exec(sq);
		//long b = System.currentTimeMillis();
		//LinkedList<String> resultsOld =  ImejiSPARQL.exec(q);
//		long c = System.currentTimeMillis();
//		logger.info("SEARCH FOR " + sc.getNamespace());
//		logger.info("simple query:" + Long.toString(b - a));
//		logger.info("Old query:" +  Long.toString(c - b));
//		if (ListUtils.subtract(results, resultsOld).size() > 0 || ListUtils.subtract(resultsOld, results).size()> 0)
//		{
//			logger.error("DIFFERENCE between old and new queries!!!!!");
//			System.out.println(ListUtils.subtract(results, resultsOld));
//			System.out.println(ListUtils.subtract(resultsOld, results));
//		}
		return results;
		//return resultsOld;
	}
	
	private LinkedList<String> getAllURIs(SortCriterion sortCri, User user)
	{
		QuerySPARQL querySPARQL = new QuerySPARQLImpl();
		String s = "";
		if (containerURI != null) 
		{
			s =  " . <" + containerURI + "> <http://imeji.mpdl.mpg.de/images> ?s";
		}
		String q = querySPARQL.createQuery(new ArrayList<SearchCriterion>(), sortCri , "http://imeji.mpdl.mpg.de/image", s, "", -1, 0, user, isCollection);

		return ImejiSPARQL.exec(q);
	}
}
