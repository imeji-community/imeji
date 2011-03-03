package de.mpg.jena.sparql.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.controller.SearchCriterion.ImejiNamespaces;
import de.mpg.jena.controller.SearchCriterion.Operator;
import de.mpg.jena.vo.CollectionImeji;

public class QueryElementFactory 
{
	private String root = null;
	private Map<String, QueryElement> els;
	private List<String> names= new ArrayList<String>();
	
	public Map<String, QueryElement> createElements(List<SearchCriterion> scList, String root)
	{
		els = new HashMap<String, QueryElement>();
		this.root = root;
		
		findMandatoryElements();
		findOptionalElements(scList);
		setOperatorNot();
		return els;
	}
	
	
	private void findMandatoryElements()
	{
		addElement(new QueryElement("s", root, null, false));
		
		if ("http://imeji.mpdl.mpg.de/image".equals(root))
		{
			addElement(new QueryElement("coll", "http://imeji.mpdl.mpg.de/collection", els.get(root), false));
			addElement(new QueryElement("visibility", "http://imeji.mpdl.mpg.de/visibility", els.get(root), false));
		}
		else if ("http://imeji.mpdl.mpg.de/collection".equals(root) || "http://imeji.mpdl.mpg.de/album".equals(root))
		{
			addElement(new QueryElement("props", "http://imeji.mpdl.mpg.de/properties", els.get(root), false));
			addElement(new QueryElement("status", "http://imeji.mpdl.mpg.de/status", els.get( "http://imeji.mpdl.mpg.de/properties"), false));
		}
	}
	
	private void findOptionalElements(List<SearchCriterion> scList)
	{
		if (scList == null) scList = new ArrayList<SearchCriterion>();
		for (SearchCriterion sc :scList)
		{
			if (sc.getChildren().isEmpty())
			{
				addElements(sc.getNamespace(), sc.getOperator());
			}
			else
			{
				findOptionalElements(sc.getChildren());
			}
		}
	}
	
	public List<QueryElement> getAllParents(QueryElement el)
	{
		List<QueryElement> els = new ArrayList<QueryElement>();
		
		if(el.getParent() != null && !"s".equals(el.getParent().getName()))
		{
			els.add(el.getParent());
			for (QueryElement e : getAllParents(el.getParent()))
			{
				els.add(e);
			}
		}
		
		return els;
	}
	
	private QueryElement findLastParent(QueryElement qe)
	{
		if (qe.getParent() != null && !"s".equals(qe.getParent().getName())) findLastParent(qe.getParent());
		return qe.getParent();
	}
	
	private void addElements(ImejiNamespaces ns, Operator op)
	{
		QueryElement parent = els.get(root);
		
		if (ns.getParent() != null)
		{
			// Add all parent namespaces
			addElements(ns.getParent(), op);
			parent =  els.get(ns.getParent().getNs());
		}
		addElement( new QueryElement(null, ns.getNs(), parent, true));
	}
	
	private void addElement(QueryElement el)
	{
		if (el.getName() == null)
		{
			// Create a variable name if it does have one.
			el.setName(getElementName(el.getNameSpace()));
		}
		if (!els.containsKey(el.getNameSpace()))
		{
			// add to elements
			els.put(el.getNameSpace(),el);
			// Save Name
			names.add(el.getName());
			// Add as child to it's parent
			if (el.getParent() != null)
			{
				el.getParent().getChilds().add(el);
			}
			
		}
	}
	
	private void setOperatorNot()
	{
//		for(QueryElement qe :els.values())
//		{
//			if (qe.isNot())
//			{
//				QueryElement lastParent = findLastParent(qe);
//				if (lastParent != null) 
//				{
//					lastParent.setNot(true);
//					qe.setNot(false);
//				}
//			}
//		}
	}
	
	
	
	public String getElementName(String namespace)
	{
		if (els.get(namespace) != null)
		{
			return els.get(namespace).getName();
		}
		else
		{
			int lastIndex = Integer.valueOf(names.get(names.size() -1).replaceAll("[a-z]", "0"));
	 		return "v" + String.valueOf(lastIndex + 1);
		}
	}
}
