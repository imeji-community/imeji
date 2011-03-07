package de.mpg.jena.sparql.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.controller.SearchCriterion.Operator;

public class SubQuery 
{
	private String name;
	
	private String type ="http://imeji.mpdl.mpg.de/image";
	
	private SearchCriterion sc;
	
	private String query ="";
	
	public SubQuery(SearchCriterion sc, Map<String, QueryElement> els, String type, String name) 
	{
		this.type = type;
		this.name = name;
		this.sc = sc;
		query = getQuery(els, name);
	}
	
	public String print()
	{
		return query;
	}
	
	private String getQuery(Map<String, QueryElement> els, String name)
	{
		String filter = FilterFactory.getSimpleFilter(sc, els.get(sc.getNamespace().getNs()).getName());
		String sq = "";
		if (!"".equals(filter.trim()))
		{
			sq ="SELECT DISTINCT ?" + name + " WHERE { ?" + name + " a <" + type + ">";
			QueryElementFactory elFactory = new QueryElementFactory();
			
			String ssq = "";
			// WRITE QUERY ELEMENTS
			QueryElement el = els.get(sc.getNamespace().getNs());
			List<QueryElement> parents = elFactory.getAllParents(el);
			
			if(sc.getOperator().equals(Operator.NOTAND) || sc.getOperator().equals(Operator.NOTOR)) ssq += " .MINUS {";
			
			for(QueryElement e : parents)
			{
				if ("".equals(ssq)) ssq += ".";
				ssq += " OPTIONAL {";
				ssq +=  printSingleVariable(e, name);
			}
			
			// If no parents, print only this variable
			if (!"".equals(ssq)) ssq += ".";
			ssq += " OPTIONAL {";
			ssq += printSingleVariable(el, name);
			
			sq+= ssq;
			
			// CLOSE OPTIONALS
			sq += "}";
			for(QueryElement e : parents)
			{
				sq += "}";
			}
			
			// WRITE FILTERS SUBQUERY
			List<SearchCriterion> scList = new ArrayList<SearchCriterion>();
			scList.add(sc);		
			sq += " .FILTER(" + filter  + ")";
			
			if(sc.getOperator().equals(Operator.NOTAND) || sc.getOperator().equals(Operator.NOTOR)) sq += " }";
			sq += "}";
		}
		return sq;
	}
	
	private String printSingleVariable(QueryElement el, String name)
	{
		String parentName = el.getParent().getName();
		if ("s".equals(parentName)) parentName = name; 
		String str = "?" + parentName +" <" + el.getNameSpace() + "> ?" + el.getName();
		return str;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public SearchCriterion getSc() {
		return sc;
	}

	public void setSc(SearchCriterion sc) {
		this.sc = sc;
	}
	
}
