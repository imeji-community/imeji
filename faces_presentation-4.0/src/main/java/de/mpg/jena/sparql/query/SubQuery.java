package de.mpg.jena.sparql.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.controller.SearchCriterion.Operator;

public class SubQuery 
{
	private String name;
	
	private String type ="http://imeji.mpdl.mpg.de/image";
	
	private List<SearchCriterion> scList;
	
	private Operator op = Operator.AND;
	
	private String query ="";
	
	public SubQuery(List<SearchCriterion> scList, Map<String, QueryElement> els, String type, String name) 
	{
		this.type = type;
		this.name = name;
		this.scList = scList;
		if (scList.size() == 1) this.op = scList.get(0).getOperator();
		query = getQuery(els, name);
	
	}
	
	public String print()
	{
		return query;
	}
	
	public String getQuery(Map<String, QueryElement> els, String name)
	{
		String filter = "";
		for (SearchCriterion sc: scList)
		{
			if (!"".equals(filter)) filter += FilterFactory.getOperatorString(sc);
			filter += " " +  FilterFactory.getSimpleFilter(sc, els.get(sc.getNamespace().getNs()).getName());
		}
		
	 	String sq = "";
		if (!"".equals(filter.trim()))
		{
			sq ="SELECT DISTINCT ?" + name + " WHERE { ?" + name + " a <" + type + ">";
			QueryElementFactory elFactory = new QueryElementFactory();
			
			String ssq = "";
			
			// WRITE QUERY ELEMENTS
			Map<String, QueryElement> parentsMap = new LinkedHashMap<String, QueryElement>();
			for (SearchCriterion sc: scList)
			{
				QueryElement el = els.get(sc.getNamespace().getNs());
				for (QueryElement e : elFactory.getAllParents(el))
				{
					if (!parentsMap.containsKey(e.getName())) parentsMap.put(e.getName(), e);
				}
			}
			
			List<QueryElement> parents = new ArrayList<QueryElement>(parentsMap.values());
			
			if(op.equals(Operator.NOTAND) || op.equals(Operator.NOTOR)) ssq += " .MINUS {";
			
			for(QueryElement e : parents)
			{
				if (!" .MINUS {".equals(ssq)) ssq += " .";
				ssq += " OPTIONAL {";
				ssq +=  printSingleVariable(e, name);
			}
			
			// If no parents, print only this variable
			if (!" .MINUS {".equals(ssq)) ssq += " .";
			ssq += " OPTIONAL {";
			int i=0;
			for (SearchCriterion sc: scList)
			{
				if (i > 0) ssq += " . ";
				QueryElement el = els.get(sc.getNamespace().getNs());
				ssq += printSingleVariable(el, name);
				i++;
			}
			
			sq+= ssq;
			
			// CLOSE OPTIONALS
			sq += "}";
			for(QueryElement e : parents)
			{
				sq += "}";
			}
			
			// WRITE FILTERS SUBQUERY
			sq += " .FILTER(" + filter  + ")";
			
			if(op.equals(Operator.NOTAND) || op.equals(Operator.NOTOR)) sq += " }";
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

	public List<SearchCriterion> getScList() {
		return scList;
	}

	public void setScList(List<SearchCriterion> scList) {
		this.scList = scList;
	}

	public Operator getOp() {
		return op;
	}

	public void setOp(Operator op) {
		this.op = op;
	}

	
}
