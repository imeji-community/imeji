//package de.mpg.jena.sparql.query;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import de.mpg.jena.controller.SearchCriterion;
//import de.mpg.jena.controller.SearchCriterion.ImejiNamespaces;
//import de.mpg.jena.controller.SearchCriterion.Operator;
//import de.mpg.jena.controller.SortCriterion;
//
//public class QueryElementFactory 
//{
//	private String root = null;
//	private Map<String, QueryElement> els;
//	private List<String> names= new ArrayList<String>();
//	
//	public Map<String, QueryElement> createElements(List<SearchCriterion> scList, String root, SortCriterion scSort)
//	{
//		els = new HashMap<String, QueryElement>();
//		this.root = root;
//		
//		findMandatoryElements();
//		findOptionalElements(scList);
//		findSortElement(scSort);
//		for (QueryElement qe : els.values())
//		{
//			if (qe.getName().equals("http://imeji.mpdl.mpg.de/visibility")) qe.setOptional(false);
//			if (qe.getName().equals("http://imeji.mpdl.mpg.de/status")) qe.setOptional(false);
//		}
//		return els;
//	}
//	
//	private void findSortElement(SortCriterion scSort)
//	{
//		if (scSort != null)
//		{
//			if (scSort.getSortingCriterion().equals(ImejiNamespaces.CONTAINER_METADATA_TITLE))
//			{
//				addElement(new QueryElement("contMd", ImejiNamespaces.CONTAINER_METADATA.getNs(), els.get(root), false));
//				addElement(new QueryElement("sort0", ImejiNamespaces.CONTAINER_METADATA_TITLE.getNs(), els.get(ImejiNamespaces.CONTAINER_METADATA.getNs()), false));
//			}
//			else if (scSort.getSortingCriterion().getParent() == null || !scSort.getSortingCriterion().getParent().getNs().equals(els.get(root).getNameSpace()))
//			{	
//				addElement(new QueryElement("props", "http://imeji.mpdl.mpg.de/properties", els.get(root), false));
//				addElement(new QueryElement("sort0", scSort.getSortingCriterion().getNs(), els.get("http://imeji.mpdl.mpg.de/properties"), false));
//			}
//			else
//			{
//				addElement(new QueryElement("sort0", scSort.getSortingCriterion().getNs(), els.get(root), false));
//			}
//		}
//	}
//	
//	private void findMandatoryElements()
//	{
//		addElement(new QueryElement("s", root, null, false));
//		addElement(new QueryElement("props", "http://imeji.mpdl.mpg.de/properties", els.get(root), false));
//		addElement(new QueryElement("status", "http://imeji.mpdl.mpg.de/status", els.get( "http://imeji.mpdl.mpg.de/properties"), false));
//		
//		if ("http://imeji.mpdl.mpg.de/image".equals(root))
//		{
//			addElement(new QueryElement("coll", "http://imeji.mpdl.mpg.de/collection", els.get(root), false));
//			addElement(new QueryElement("visibility", "http://imeji.mpdl.mpg.de/visibility", els.get(root), false));
//		}
//	}
//	
//	private void findOptionalElements(List<SearchCriterion> scList)
//	{
//		if (scList == null) scList = new ArrayList<SearchCriterion>();
//		for (SearchCriterion sc :scList)
//		{
//			if (sc.getChildren().isEmpty())
//			{
//				addElements(sc.getNamespace(), sc.getOperator());
//			}
//			else
//			{
//				findOptionalElements(sc.getChildren());
//			}
//		}
//	}
//	
//	public List<QueryElement> getAllParents(QueryElement el)
//	{
//		List<QueryElement> els = new ArrayList<QueryElement>();
//		List<String> elsNames = new ArrayList<String>();
//		
//		if(el.getParent() != null && !"s".equals(el.getParent().getName()))
//		{
//			for (QueryElement e : getAllParents(el.getParent()))
//			{
//				els.add(e);
//				elsNames.add(e.getName());
//			}
//			els.add(el.getParent());
//			elsNames.add(el.getParent().getName());
//		}
//		return els;
//	}
//	
//	private void addElements(ImejiNamespaces ns, Operator op)
//	{
//		QueryElement parent = els.get(root);
//		
//		if (ns.getParent() != null)
//		{
//			// Add all parent namespaces
//			addElements(ns.getParent(), op);
//			parent =  els.get(ns.getParent().getNs());
//		}
//		addElement(new QueryElement(null, ns.getNs(), parent, true));
//	}
//	
//	private void addElement(QueryElement el)
//	{
//		if (el.getName() == null)
//		{
//			// Create a variable name if it does have one.
//			el.setName(getElementName(el.getNameSpace()));
//		}
//		if (!els.containsKey(el.getNameSpace()))
//		{
//			// add to elements
//			els.put(el.getNameSpace(),el);
//			// Save Name
//			names.add(el.getName());
//			// Add as child to it's parent
//			if (el.getParent() != null)
//			{
//				el.getParent().getChilds().add(el);
//			}
//			
//		}
//	}
//		
//	
//	public String getElementName(String namespace)
//	{
//		if (els.get(namespace) != null)
//		{
//			return els.get(namespace).getName();
//		}
//		else
//		{
//			int lastIndex = Integer.valueOf(names.get(names.size() -1).replaceAll("[a-z]", "0"));
//	 		return "v" + String.valueOf(lastIndex + 1);
//		}
//	}
//}
