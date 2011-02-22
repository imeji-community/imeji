package de.mpg.jena.sparql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.mpg.jena.controller.ImejiQueryVariable;
import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.controller.SortCriterion;
import de.mpg.jena.controller.SearchCriterion.Filtertype;
import de.mpg.jena.controller.SearchCriterion.ImejiNamespaces;
import de.mpg.jena.controller.SortCriterion.SortOrder;
import de.mpg.jena.util.ObjectHelper;
import de.mpg.jena.vo.Grant;
import de.mpg.jena.vo.User;
import de.mpg.jena.vo.Grant.GrantType;

public class QueryFactory 
{
	private  Map<String, QueryElement> els = new HashMap<String, QueryElement>();
	private String root = "http://imeji.mpdl.mpg.de/image";
	private List<String> names= new ArrayList<String>();
	private String optionals = "";

	public String createQuery(String mode, List<SearchCriterion> scList, SortCriterion sortCriterion, String type, String specificQuery, String specificFilter,
            int limit, int offset, User user) throws Exception
    {
		 String queryMode = "SELECT";
		 String querySpecific = "";
		 String queryVariables = "";
		 String queryFilter = "";
		 String queryOffSet = "";
		 String queryLimit = "";
		 root = type;
		
		 initElements(scList);
	     
		 // QUERY MODE
		 if ("SELECT".equals(mode)) mode += " DISTINCT ";
		 queryMode = mode;
		 
		 // QUERY SPECIFIC
		 querySpecific = specificQuery;
		 
		 // QUERY VARIABLE
		 queryVariables = printVariables();
		
		 // FILTERS
		 queryFilter = FilterFactory.getFilter(scList, els, user, specificFilter);
		 
		 // OFFSET
		 if (offset > 0) queryOffSet = "OFFSET " + Integer.toString(offset);
		 
		 // LIMIT 
		 if (limit > 0) queryLimit = "LIMIT " +  Integer.toString(limit);
		 
		 // CREATE COMPLETE QUERY
	     String query = queryMode + " ?s WHERE { ?s a <" + root + "> " + querySpecific + queryVariables + queryFilter + "} " + queryLimit + queryOffSet;
	       
	     System.out.println("NEW QUERY: " + query);
	     return query;
    }
	
	private String printVariables()
	{
		String mandatory ="";
		String optionals ="";
		for (QueryElement el : els.values())
		{
			if (el.getParent() != null)
			{
				if (!el.isOptional())
				{
					mandatory += " . " + printSingleVariable(el);
				}
			}
		}
		optionals += printOptionals(new ArrayList<QueryElement>(els.values()), els.get(root));
		return mandatory + " " + optionals;
	}
	
	private String printOptionals(List<QueryElement> els, QueryElement root)
	{
		String optional = "";
		
		for (QueryElement el : els)
		{
			if (el.isOptional() && root.equals(el.getParent()))
			{
				List<QueryElement> childs = el.getChilds();
				if (childs.isEmpty())
				{
					optional += " . OPTIONAL { " + printSingleVariable(el) + "}";
				}
				else
				{
					optional += " . OPTIONAL { " +  printSingleVariable(el);
					optional += printOptionals(childs, el);
					optional += " }";
				}
			}
		}
		
		return optional;
	}
	
	private String printSingleVariable(QueryElement el)
	{
		String str = "?" + el.getParent().getName() +" <" + el.getNameSpace() + "> ?" + el.getName();
		if (el.isList())
		{
			str = "?" + el.getParent().getName() +" <" + el.getNameSpace() + "> ?" + getElementName("http://www.w3.org/2000/01/rdf-schema#member");
			str += " . ?" +  getElementName("http://www.w3.org/2000/01/rdf-schema#member") + " <http://www.w3.org/2000/01/rdf-schema#member> ?" + el.getName() ;
		}
		return str;
	}
	
	private void initElements(List<SearchCriterion> scList)
	{
		findMandatoryElements();
		findOptionalElements(scList);
	}
	
	private void findMandatoryElements()
	{
		addElement(new QueryElement("s", root, null, true, false));
		addElement(new QueryElement("coll", "http://imeji.mpdl.mpg.de/collection", els.get(root), false, false));
		addElement(new QueryElement("visibility", "http://imeji.mpdl.mpg.de/visibility", els.get(root), false, false));
		//addElement(new QueryElement("props", "http://imeji.mpdl.mpg.de/properties", els.get(root), false));
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
	
	private String getElementName(String namespace)
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
	
	
	
	
	
	
	
	
	
	 public static String createQueryOld(String mode, List<List<SearchCriterion>> scList, SortCriterion sortCriterion, String type, String specificQuery, String specificFilter,
	            int limit, int offset) throws Exception
	    {
	        boolean inverse = false;
	        if (scList != null && scList.size() > 0 && scList.get(0) != null && scList.get(0).size() > 0)
	        {
	            inverse = scList.get(0).get(0).isInverse();
	        }
	        // contains a list of root ImejiQueryVariables for each submlist
	        Map<List<SearchCriterion>, List<ImejiQueryVariable>> roots = new HashMap<List<SearchCriterion>, List<ImejiQueryVariable>>();
	        // contains a list of root ImejiQueryVariables for each sublist
	        Map<List<SearchCriterion>, Map<ImejiNamespaces, ImejiQueryVariable>> map = new HashMap<List<SearchCriterion>, Map<ImejiNamespaces, ImejiQueryVariable>>();
	        // Map<SearchCriterion, ImejiQueryVariable> treeMap = new HashMap<SearchCriterion, ImejiQueryVariable>();
	        // Add variables for user management
	        String query = "";
	        query += specificQuery;
	        String filter = "";
	        if (scList != null && scList.size() > 0)
	        {
	            for (List<SearchCriterion> scSubList : scList)
	            {
	                Map<ImejiNamespaces, ImejiQueryVariable> currentMap = new HashMap<ImejiNamespaces, ImejiQueryVariable>();
	                map.put(scSubList, currentMap);
	                List<ImejiQueryVariable> currentRoots = new ArrayList<ImejiQueryVariable>();
	                roots.put(scSubList, currentRoots);
	                for (SearchCriterion sc : scSubList)
	                {
	                    if (!sc.getNamespace().equals(ImejiNamespaces.ID_URI))
	                    {
	                        ImejiNamespaces ns = sc.getNamespace();
	                        ImejiQueryVariable child = null;
	                        while (ns != null)
	                        {
	                            ImejiQueryVariable qv;
	                            if (!currentMap.containsKey(ns))
	                            {
	                                List<ImejiQueryVariable> qvList = new ArrayList<ImejiQueryVariable>();
	                                if (child != null)
	                                    qvList.add(child);
	                                qv = new ImejiQueryVariable(ns, qvList);
	                                currentMap.put(ns, qv);
	                            }
	                            else
	                            {
	                                qv = currentMap.get(ns);
	                                if (child != null && !qv.getChildren().contains(child))
	                                    qv.getChildren().add(child);
	                            }
	                            child = qv;
	                            ns = ns.getParent();
	                        }
	                        if (!currentRoots.contains(child))
	                        {
	                            currentRoots.add(child);
	                        }
	                    }
	                }
	            }
	            List<ImejiQueryVariable> allRoots = new ArrayList<ImejiQueryVariable>();
	            for (List<ImejiQueryVariable> list : roots.values())
	            {
	                allRoots.addAll(list);
	            }
	            String subquery = createSubQuery(allRoots, 0 , "?s", inverse);
	            query += subquery;
	        }
	        if (scList != null && scList.size() > 0)
	        {
	            filter = " . FILTER(";
	            int j = 0;
	            boolean operatorWritten = false;
	            for (List<SearchCriterion> subList : scList)
	            {
	                if (scList.indexOf(subList) > 0 && subList.size() > 0)
	                {
	                    if (subList.get(0).getOperator().equals(SearchCriterion.Operator.AND))
	                        filter += " && ";
	                    else if (subList.get(0).getOperator().equals(SearchCriterion.Operator.OR))
	                        filter += " || ";
	                }
	                filter += "(";
	                for (SearchCriterion sc : subList)
	                {
	                    if (subList.indexOf(sc) > 0)
	                    {
	                        if (sc.getOperator().equals(SearchCriterion.Operator.AND))
	                            filter += " && ";
	                        else if (sc.getOperator().equals(SearchCriterion.Operator.OR))
	                            filter += " || ";
	                    }
	                    if (sc.getValue() != null)
	                    {
	                        if (sc.getFilterType().equals(Filtertype.URI)
	                                && !sc.getNamespace().equals(ImejiNamespaces.ID_URI))
	                        {
	                            filter += "str(" + map.get(subList).get(sc.getNamespace()).getVariable() + ")='"
	                                    + sc.getValue() + "'";
	                        }
	                        else if (sc.getFilterType().equals(Filtertype.URI)
	                                && sc.getNamespace().equals(ImejiNamespaces.ID_URI))
	                        {
	                            filter += "?s" + "=<" + sc.getValue() + ">";
	                        }
	                        else if (sc.getFilterType().equals(Filtertype.REGEX))
	                        {
	                            filter += "regex(" + map.get(subList).get(sc.getNamespace()).getVariable() + ", '"
	                                    + sc.getValue() + "', 'i')";
	                        }
	                        else if (sc.getFilterType().equals(Filtertype.EQUALS))
	                        {
	                            filter += map.get(subList).get(sc.getNamespace()).getVariable() + "='" + sc.getValue()
	                                    + "'";
	                        }
	                        else if (sc.getFilterType().equals(Filtertype.BOUND))
	                        {
	                            filter += "bound(" + map.get(subList).get(sc.getNamespace()).getVariable() + ")="
	                                    + sc.getValue() + "";
	                        }
	                        else if (sc.getFilterType().equals(Filtertype.EQUALS_NUMBER))
	                        {
	                            filter += map.get(subList).get(sc.getNamespace()).getVariable() + "='" + sc.getValue()
	                                    + "'^^<http://www.w3.org/2001/XMLSchema#double>";
	                        }
	                        else if (sc.getFilterType().equals(Filtertype.GREATER_NUMBER))
	                        {
	                            filter += map.get(subList).get(sc.getNamespace()).getVariable() + ">='" + sc.getValue()
	                                    + "'^^<http://www.w3.org/2001/XMLSchema#double>";
	                        }
	                        else if (sc.getFilterType().equals(Filtertype.LESSER_NUMBER))
	                        {
	                            filter += map.get(subList).get(sc.getNamespace()).getVariable() + "<='" + sc.getValue()
	                                    + "'^^<http://www.w3.org/2001/XMLSchema#double>";
	                        }
	                        else if (sc.getFilterType().equals(Filtertype.EQUALS_DATE))
	                        {
	                            filter += map.get(subList).get(sc.getNamespace()).getVariable() + "='" + sc.getValue()
	                                    + "'^^<http://www.w3.org/2001/XMLSchema#dateTime>";
	                        }
	                        else if (sc.getFilterType().equals(Filtertype.GREATER_DATE))
	                        {
	                            filter += map.get(subList).get(sc.getNamespace()).getVariable() + ">='" + sc.getValue()
	                                    + "'^^<http://www.w3.org/2001/XMLSchema#dateTime>";
	                        }
	                        else if (sc.getFilterType().equals(Filtertype.LESSER_DATE))
	                        {
	                            filter += map.get(subList).get(sc.getNamespace()).getVariable() + "<='" + sc.getValue()
	                                    + "'^^<http://www.w3.org/2001/XMLSchema#dateTime>";
	                        }
	                    }
	                    j++;
	                }
	                filter += ")";
	            }
	            filter += ")";
	        }
	        specificFilter = ". FILTER(" + specificFilter + ")";
	        // Add sort criterion
	        String sortQuery = "";
	        String sortVariable = "?sort0";
	        if (sortCriterion != null)
	        {
	            /*
	             * if(treeMap.containsKey(sortCriterion.getSortingCriterion())) { sortVariable =
	             * treeMap.get(sortCriterion).getVariable(); } else {
	             */
	            ImejiNamespaces ns = sortCriterion.getSortingCriterion();
	            int i = 0;
	            String variablename = "";
	            while (ns != null)
	            {
	                variablename = "?sort" + String.valueOf(i + 1);
	                String lastVariablename = "?sort" + String.valueOf(i);
	                query = ". " + variablename + " <" + ns.getNs() + "> " + lastVariablename + " " + query;
	                ns = ns.getParent();
	                i++;
	            }
	            query = query.replaceAll(java.util.regex.Pattern.quote(variablename), "?s");
	            // }
	            if (sortCriterion.getSortOrder().equals(SortOrder.DESCENDING))
	                sortQuery = "ORDER BY DESC(" + sortVariable + ")";
	            else
	            {
	                sortQuery = "ORDER BY " + sortVariable;
	            }
	        }
	        // offset++;
	        String limitString = "";
	        if (limit > -1)
	        {
	            limitString = " LIMIT " + limit;
	        }
	        // If inverse search add filter to MINUS part
	        if (inverse)
	        {
	            query = query.substring(0, query.lastIndexOf("}"));
	            query += filter + "}";
	        }
	        else
	        {
	            query += filter;
	        }
	        if ("SELECT".equals(mode)) mode += " DISTINCT ";
	        String completeQuery = mode + " ?s WHERE { ?s a <" + type + "> " + query + specificFilter + " } "
	                + sortQuery + limitString + " OFFSET " + offset;
	        System.out.println(completeQuery);
	        return completeQuery;
	    }

	    private static String createSubQuery(List<ImejiQueryVariable> qvList, int y, String oldSubjectVar,
	            boolean inverse)
	    {
	        String subquery = "";
	        if (qvList != null)
	        {
	            for (ImejiQueryVariable qv : qvList)
	            {
	                String subject = oldSubjectVar;
	                String object = "?v" + y;
	                y++;
	                if (subject.equals("?s") && inverse)
	                {
	                    subquery = subquery + " . MINUS { " + subject + " <" + qv.getNamespace().getNs() + "> " + object;
	                    inverse = false;
	                }
	                else
	                {
	                    subquery = subquery + " . OPTIONAL { " + subject + " <" + qv.getNamespace().getNs() + "> " + object;
	                }
	                qv.setVariable(object);
	                if (qv.getNamespace().isListType())
	                {
	                    String listObject = "?v" + y;
	                    y++;
	                    subquery = subquery + " . " + object + " <http://www.w3.org/2000/01/rdf-schema#member> "
	                            + listObject;
	                    qv.setVariable(listObject);
	                    object = listObject;
	                }
	                subquery = subquery + createSubQuery(qv.getChildren(), y, object, inverse);
	                subquery = subquery + " }";
	            }
	        }
	        return subquery;
	    }
	 
}
