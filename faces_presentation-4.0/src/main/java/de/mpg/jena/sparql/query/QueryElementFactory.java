package de.mpg.jena.sparql.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.controller.SearchCriterion.ImejiNamespaces;
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
		
		return els;
	}
	
	private void findMandatoryElements()
	{
		addElement(new QueryElement("s", root, null, false, false));
		
		if ("http://imeji.mpdl.mpg.de/image".equals(root))
		{
			addElement(new QueryElement("coll", "http://imeji.mpdl.mpg.de/collection", els.get(root), false, false));
			addElement(new QueryElement("visibility", "http://imeji.mpdl.mpg.de/visibility", els.get(root), false, false));
		}
		else if ("http://imeji.mpdl.mpg.de/collection".equals(root) || "http://imeji.mpdl.mpg.de/album".equals(root))
		{
			addElement(new QueryElement("props", "http://imeji.mpdl.mpg.de/properties", els.get(root), false, false));
			addElement(new QueryElement("status", "http://imeji.mpdl.mpg.de/status", els.get( "http://imeji.mpdl.mpg.de/properties"), false, false));
		}
	}
	
	private void findOptionalElements(List<SearchCriterion> scList)
	{
		for (SearchCriterion sc :scList)
		{
			if (sc.getChildren().isEmpty())
			{
				addElements(sc.getNamespace());
			}
			else
			{
				findOptionalElements(sc.getChildren());
			}
		}
	}
	
	private void addElements(ImejiNamespaces ns)
	{
		QueryElement parent = els.get(root);
		
		if (ns.getParent() != null)
		{
			// Add all parent namespaces
			addElements(ns.getParent());
			parent =  els.get(ns.getParent().getNs());
		}

		addElement( new QueryElement(null, ns.getNs(), parent, true, ns.isListType()));
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
