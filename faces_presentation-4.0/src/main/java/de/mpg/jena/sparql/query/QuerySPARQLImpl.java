package de.mpg.jena.sparql.query;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import thewebsemantic.Bean2RDF;
import thewebsemantic.vocabulary.DublinCore;

import de.mpg.jena.ImejiJena;
import de.mpg.jena.controller.ImejiQueryVariable;
import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.controller.SearchCriterion.Filtertype;
import de.mpg.jena.controller.SearchCriterion.ImejiNamespaces;
import de.mpg.jena.controller.SearchCriterion.Operator;
import de.mpg.jena.controller.SortCriterion;
import de.mpg.jena.controller.SortCriterion.SortOrder;
import de.mpg.jena.sparql.QuerySPARQL;
import de.mpg.jena.vo.User;

public class QuerySPARQLImpl implements QuerySPARQL
{
	private Map<String, QueryElement> els = new HashMap<String, QueryElement>();
	private QueryElementFactory qeFact = new QueryElementFactory();
	
	private String variables = "";
	private String securityFilter = "";
	private String limit = "";
	private String offset = "";
	private String sortQuery ="";
	private String selectQuery="";
	
//	private String type = "http://imeji.mpdl.mpg.de/image";
//	private Map<String, SearchCriterion> subqueries = new HashMap<String, SearchCriterion>();

	public String createQuery(List<SearchCriterion> scList, SortCriterion sortCriterion, String root, String specificQuery, String specificFilter, int limit, int offset, User user)
    {
		//String select = printSelect(scList,sortCriterion, root, specificQuery, specificFilter, limit, offset, user);
		System.out.println("create query...");
		init(scList,sortCriterion, root, specificQuery, specificFilter, limit, offset, user); 
		String query = "SELECT DISTINCT ?s WHERE {" + selectQuery + "} " + this.sortQuery + " " + this.limit + " " + this.offset;
		//ImejiJena.imageModel.write(System.out, "RDF/XML-ABBREV");
		//System.out.println(query);
	//	query="SELECT DISTINCT ?s WHERE {?s a <http://imeji.mpdl.mpg.de/image> . ?s <http://imeji.mpdl.mpg.de/collection> ?coll . ?s <http://imeji.mpdl.mpg.de/visibility> ?visibility .FILTER(?visibility=<http://imeji.mpdl.mpg.de/image/visibility/PUBLIC> || ?coll=<http://imeji.mpdl.mpg.de/collection/45> || ?coll=<http://imeji.mpdl.mpg.de/collection/14> || ?coll=<http://imeji.mpdl.mpg.de/collection/30> || ?coll=<http://imeji.mpdl.mpg.de/album/36> || ?coll=<http://imeji.mpdl.mpg.de/album/38> || ?coll=<http://imeji.mpdl.mpg.de/collection/4> || ?coll=<http://imeji.mpdl.mpg.de/album/37> || ?coll=<http://imeji.mpdl.mpg.de/collection/50>) . OPTIONAL{ SELECT DISTINCT ?s5 WHERE { ?s5 a <http://imeji.mpdl.mpg.de/image> . OPTIONAL {?s5 <http://imeji.mpdl.mpg.de/metadataSet> ?v2 . OPTIONAL {?v2 <http://imeji.mpdl.mpg.de/metadata> ?v3 . OPTIONAL {?v3 <http://imeji.mpdl.mpg.de/metadata/person> ?v6 . OPTIONAL {?v6 <http://purl.org/escidoc/metadata/profiles/0.1/organizationalunit> ?v9 . OPTIONAL {?v9 <http://purl.org/dc/elements/1.1/title> ?v10}}}}} .FILTER(regex(?v10, 'joujou', 'i'))} } . OPTIONAL{ SELECT DISTINCT ?s0 WHERE { ?s0 a <http://imeji.mpdl.mpg.de/image> . OPTIONAL {?s0 <http://imeji.mpdl.mpg.de/filename> ?v1} .FILTER(regex(?v1, 'joujou', 'i'))} } . OPTIONAL{ SELECT DISTINCT ?s7 WHERE { ?s7 a <http://imeji.mpdl.mpg.de/image> . OPTIONAL {?s7 <http://imeji.mpdl.mpg.de/metadataSet> ?v2 . OPTIONAL {?v2 <http://imeji.mpdl.mpg.de/metadata> ?v3 . OPTIONAL {?v3 <http://imeji.mpdl.mpg.de/complexTypes> ?v13}}} .FILTER(str(?v13)='http://imeji.mpdl.mpg.de/complexTypes/DATE')} } . OPTIONAL{ SELECT DISTINCT ?s4 WHERE { ?s4 a <http://imeji.mpdl.mpg.de/image> . OPTIONAL {?s4 <http://imeji.mpdl.mpg.de/metadataSet> ?v2 . OPTIONAL {?v2 <http://imeji.mpdl.mpg.de/metadata> ?v3 . OPTIONAL {?v3 <http://imeji.mpdl.mpg.de/metadata/person> ?v6 . OPTIONAL {?v6 <http://purl.org/escidoc/metadata/terms/0.1/given-name> ?v8}}}} .FILTER(regex(?v8, 'joujou', 'i'))} } . OPTIONAL{ SELECT DISTINCT ?s6 WHERE { ?s6 a <http://imeji.mpdl.mpg.de/image> . OPTIONAL {?s6 <http://imeji.mpdl.mpg.de/properties> ?v11 . OPTIONAL {?v11 <http://imeji.mpdl.mpg.de/createdBy> ?v12}} .FILTER(str(?v12)='http://xmlns.com/foaf/0.1/Person/saquet%40mpdl.mpg.de')} } . OPTIONAL{ SELECT DISTINCT ?s1 WHERE { ?s1 a <http://imeji.mpdl.mpg.de/image> . OPTIONAL {?s1 <http://imeji.mpdl.mpg.de/metadataSet> ?v2 . OPTIONAL {?v2 <http://imeji.mpdl.mpg.de/metadata> ?v3 . OPTIONAL {?v3 <http://imeji.mpdl.mpg.de/metadata/text> ?v4}}} .FILTER(regex(?v4, 'joujou', 'i'))} } . OPTIONAL{ SELECT DISTINCT ?s3 WHERE { ?s3 a <http://imeji.mpdl.mpg.de/image> . OPTIONAL {?s3 <http://imeji.mpdl.mpg.de/metadataSet> ?v2 . OPTIONAL {?v2 <http://imeji.mpdl.mpg.de/metadata> ?v3 . OPTIONAL {?v3 <http://imeji.mpdl.mpg.de/metadata/person> ?v6 . OPTIONAL {?v6 <http://purl.org/escidoc/metadata/terms/0.1/family-name> ?v7}}}} .FILTER(regex(?v7, 'joujou', 'i'))} }.FILTER( (?s=?s0 || ?s=?s1 || ?s=?s3 || ?s=?s4 || ?s=?s5 && ?s=?s6) && ?s=?s7 )}";
		System.out.println("...done");
		return query;
    }
	
	public String createCountQuery(List<SearchCriterion> scList, SortCriterion sortCriterion, String root, String specificQuery, String specificFilter, int limit, int offset, User user) 
	{
		//String select = printSelect(scList,sortCriterion, root, specificQuery, specificFilter, limit, offset, user);
		init(scList,sortCriterion, root, specificQuery, specificFilter, limit, offset, user); 
		String query= "SELECT ?s count(DISTINCT ?s) WHERE {" + selectQuery + "} " + this.limit + " " + this.offset;
		//System.out.println("COUNT:" + query);
		return  query;
	}
	
	/**
	 * TODO not working
	 */
	public String createConstructQuery(List<SearchCriterion> scList, SortCriterion sortCriterion, String root, String specificQuery, String specificFilter, int limit, int offset, User user) 
	{
		String select = printSelect(scList,sortCriterion, root, specificQuery, specificFilter, limit, offset, user);
		//System.out.println("SELECT ?s count(DISTINCT ?s) WHERE { ?s a <" + root + "> " + variables +  filters + "} " + this.limit + " " + this.offset);
		return "CONSTRUCT { ?s a <" + root + "> . ?md a <http://imeji.mpdl.mpg.de/image/metadata> . ?type a <http://purl.org/dc/terms/type> } WHERE { " + variables.substring(2) +   securityFilter + "} " + this.limit + " " + this.offset;
	}
	
	private void init(List<SearchCriterion> scList, SortCriterion sortCriterion, String root, String specificQuery, String specificFilter, int limit, int offset, User user)
	{
		this.els = qeFact.createElements(scList, root, sortCriterion);
		if (offset > 0) this.offset = "OFFSET " + Integer.toString(offset);
		if (limit > 0) this.limit = "LIMIT " +  Integer.toString(limit);
		this.selectQuery = printSelect(scList, sortCriterion, root, specificQuery, specificFilter, limit, offset, user);
		this.sortQuery = SortQueryFactory.create(sortCriterion, els);
	}
	
	private String printSelect(List<SearchCriterion> scList, SortCriterion sortCriterion, String root, String specificQuery, String specificFilter, int limit, int offset, User user)
	{
		securityFilter = FilterFactory.generateUserFilters(user, els);
		
		Map<String, SubQuery> subQueries = new HashMap<String, SubQuery>();
		
		if (scList == null) scList = new ArrayList<SearchCriterion>();

		SecurityQueryFactory sqf = new SecurityQueryFactory(els, root, user);
		scList = sqf.setSecuritySearchCriterion(scList);

		// Create one sub-query for each search criterion
		int i=0;
		for (SearchCriterion sc: getAllSearchCriterion(scList))
		{
			if (sc.getNamespace() != null && !sc.getNamespace().equals(ImejiNamespaces.ID_URI) && els.get(sc.getNamespace().getNs()).isOptional())
			{
				subQueries.put(sc.getNamespace().getNs() + sc.getFilterType() + sc.getValue(), new SubQuery(sc, els, root, "s" + i));
				i++;
			}
		}
		
		// Print all sub-queries
		String query = "";
		for (SubQuery sq : subQueries.values())
		{
			String subquery = sq.print();
			if (!"".equals(subquery.trim()))
			{
				query += " .";
				if(!(sq.getSc().getOperator().equals(Operator.NOTAND) && sq.getSc().getOperator().equals(Operator.NOTOR))) query += " OPTIONAL";
				query += "{ " +  subquery +  " }";
			}
		}
		
		// Print the advanced filter (where all subqueries are related)
		String filter = FilterFactory.getAdvancedFilter(scList, subQueries, els);
		if (!"".equals(filter)) filter = ".FILTER( " + filter + " )";
		
		String s =  "?s a <" + root + ">" + sqf.getVariablesAsSparql() + specificQuery + sqf.getSecurityFilter() + query + filter + printSortVariables();
		return s;
	}
	
	private List<SearchCriterion> getAllSearchCriterion(List<SearchCriterion> scList)
	{
		List<SearchCriterion> all = new ArrayList<SearchCriterion>();
		for (SearchCriterion sc : scList)
		{
			all.add(sc);
			if (!sc.getChildren().isEmpty())
			{
				all.addAll(all.size() -1, getAllSearchCriterion(sc.getChildren()));
			}
		}
		return all;
	}
	
	private String printSortVariables()
	{
		String str ="";
		for (QueryElement el : els.values())
		{
			if (el.getParent() != null)
			{
				if (el.getName().contains("sort"))
				{
					str += " . "+ printSingleVariable(el);
				}
			}
		}
		return str;
	}
	

	private String printSingleVariable(QueryElement el)
	{
		String str = "?" + el.getParent().getName() +" <" + el.getNameSpace() + "> ?" + el.getName();
		return str;
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
