package de.mpg.jena.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import thewebsemantic.Bean2RDF;
import thewebsemantic.NotFoundException;
import thewebsemantic.RDF2Bean;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.shared.Lock;
import com.hp.hpl.jena.sparql.function.library.substring;


import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.jena.controller.SearchCriterion.Filtertype;
import de.mpg.jena.controller.SearchCriterion.ImejiNamespaces;
import de.mpg.jena.controller.SortCriterion.SortOrder;
import de.mpg.jena.util.Counter;
import de.mpg.jena.util.ObjectHelper;
import de.mpg.jena.vo.Properties;
import de.mpg.jena.vo.User;

public abstract class ImejiController {
	
	
	private static Logger logger = Logger.getLogger(ImejiController.class);
	
	protected User user;
	
	
	protected static Model base = null;
	static
	{
		try {
			String tdbPath = PropertyReader.getProperty("imeji.tdb.path");
			base = DataFactory.model(tdbPath);
			/*
			IndexBuilderString larqBuilder = new IndexBuilderString() ;
			larqBuilder.indexStatements(base.listStatements()) ;
			base.register(larqBuilder);
			larqBuilder.closeWriter();
			IndexLARQ index = larqBuilder.getIndex() ;
			LARQ.setDefaultIndex(index) ;
			 */
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	/*
	protected static Model base = null;
	protected static Store store = null;
	
	static{
        
        
        StoreDesc storeDesc = new StoreDesc(LayoutType.LayoutTripleNodesHash, DatabaseType.MySQL) ;
        JDBC.loadDriverMySQL();
        String jdbcURL = "jdbc:mysql://dev-faces.mpdl.mpg.de:5432/imeji"; 
        SDBConnection conn = new SDBConnection(jdbcURL, "imeji", "dev-faces") ;
        store = SDBFactory.connectStore(conn, storeDesc);
        store.getLoader().setChunkSize(5000000);
        store.getLoader().setUseThreading(true);
        base = SDBFactory.connectDefaultModel(store);
        
    }
	*/
	
	/*
	
	static{
		
		
		StoreDesc storeDesc = new StoreDesc(LayoutType.LayoutTripleNodesHash, DatabaseType.PostgreSQL) ;
		JDBC.loadDriverPGSQL();
		String jdbcURL = "jdbc:postgresql://localhost:5432/imeji"; 
		SDBConnection conn = new SDBConnection(jdbcURL, "postgres", "postgres") ;
		store = SDBFactory.connectStore(conn, storeDesc);
		//store.getLoader().setChunkSize(50000);
		store.getLoader().
		base = SDBFactory.connectDefaultModel(store);
		

	}
	*/
	
	
	
	/*
	static
	{
		try {
			String path = PropertyReader.getProperty("imeji.tdb.path");
			System.out.println("TDB at: " + path);
			base = DataFactory.model(path);
		} catch (Exception e) {
			
		}
	}
	*/
	protected static Bean2RDF bean2RDF = new Bean2RDF(base);
	protected static RDF2Bean rdf2Bean = new RDF2Bean(base);

	public ImejiController(User user2) {
		this.user = user2;
	}

	protected static void writeCreateProperties(Properties properties, User user) {
		Date now = new Date();
		properties.setCreatedBy(ObjectHelper.getURI(User.class, user.getEmail())); 
		properties.setModifiedBy(ObjectHelper.getURI(User.class, user.getEmail()));
		properties.setCreationDate(now);
		properties.setLastModificationDate(now);

	}
	
	protected static void writeUpdateProperties(Properties properties, User user) {
		Date now = new Date();
		properties.setModifiedBy(ObjectHelper.getURI(User.class, user.getEmail()));
		properties.setLastModificationDate(now);
	}
	
	
	
//	protected String createQuery(List<SearchCriterion> scList, SortCriterion sortCriterion, String type, int limit, int offset) throws Exception
//	{
//		
//	    //Add variables for user management
//        String query = "";
//		query += getSpecificQuery();
//       
//		String filter = "";
//		Map<ImejiNamespaces, String> variableMap = new HashMap<ImejiNamespaces, String>();
//		
//		//Create query for searchCriterions
//		if(scList!=null && scList.size()>0)
//		{
//		    //query += ". OPTIONAL { ";
//    		int j = 0;
//    		for(SearchCriterion sc : scList)
//    		{
//        		    if(!sc.getNamespace().equals(ImejiNamespaces.ID_URI))
//        		    {
//        		        
//        		    
//        		    String subquery = "";
//        			ImejiNamespaces ns = sc.getNamespace();
//        			int i = 0;
//        			String variablename = "";
//        			String lastVariablename = "";
//        			boolean replace =true;
//        			while (ns != null) {
//        				
//        			    /*
//        			    if(variableMap.containsKey(ns))
//        				{
//        				    replace=false;
//        				    break;
//        				    
//        				}
//        				
//        			    
//        				if(variableMap.containsKey(ns.getParent()))
//    			        {
//        				    variablename=variableMap.get(ns.getParent());
//    			        }
//        				else
//        				{
//        				*/
//        				    variablename = "?v" +  String.valueOf(i+1) + String.valueOf(j);
//        				//}
//        				
//        				 //variablename = "?v" +  String.valueOf(i+1) + String.valueOf(j);
//                         lastVariablename = "?v" +  String.valueOf(i) + String.valueOf(j);
//                        
//                         subquery = ". " + variablename + " <" + ns.getNs() + "> " + lastVariablename + " "  + subquery;
//                         
//                         variableMap.put(ns, lastVariablename);
//        				
//        				ns = ns.getParent();
//        				i++;
//        			}
//        			
//        			//Remove first dot in first round
//        			//if(j==0)
//        			//{
//        			    subquery = subquery.substring(2, subquery.length());
//        			//}
//        			
//    			    
//        			
//        			if(replace)
//        			{
//        			    subquery = subquery.replaceAll(java.util.regex.Pattern.quote(variablename), "?s");
//        			}
//        			
//        			
//        			j++;
//        			query += " . OPTIONAL { " + subquery + " }";
//        		}
//    		}
//    		//query += " }";
//		}
//    		
//    		
//		filter = " . FILTER(";
//		
//		filter+=getSpecificFilter();
//		
//		if(scList!=null && scList.size()>0)
//	    {
//    		//Add regex filters
//    		if(getSpecificFilter() != null || !getSpecificFilter().isEmpty()) 
//		    {
//    		    filter += " && (";
//		    }
//    		
//		    int j=0;
//    		for(SearchCriterion sc : scList)
//    		{
//    			if (j > 0)
//    			{
//    				if(sc.getOperator().equals(SearchCriterion.Operator.AND))
//    					filter += " && ";
//    				else if(sc.getOperator().equals(SearchCriterion.Operator.OR))
//    					filter += " || ";
//    			}
//    			if(sc.getValue()!=null)
//    			{
//    			    if(sc.getFilterType().equals(Filtertype.URI) && !sc.getNamespace().equals(ImejiNamespaces.ID_URI))
//                    {
//                        filter += "?v0" + j + "=<" + sc.getValue() + ">";
//                    }
//    			    else if(sc.getFilterType().equals(Filtertype.URI) && sc.getNamespace().equals(ImejiNamespaces.ID_URI))
//                    {
//    			        filter += "?s=<" + sc.getValue() + ">";
//                    }
//                    else if(sc.getFilterType().equals(Filtertype.REGEX))
//                    {
//                        filter += "regex(?v0" + j + ", '" + sc.getValue() + "', 'i')";
//                    }
//                    else if(sc.getFilterType().equals(Filtertype.EQUALS))
//                    {
//                        filter += "?v0" + j + "='" + sc.getValue() + "'";
//                    }
//    			}
//   
//    			j++;
//    			
//    		}
//    		filter+=")";
//		
//		}
//		filter+=")";
//		
//		//Add sort criterion
//        String sortQuery = "";
//        String sortVariable = "?sort0";
//       
//        
//        if(sortCriterion!=null)
//        {
//            if(variableMap.containsKey(sortCriterion.getSortingCriterion()))
//            {
//                sortVariable = variableMap.get(sortCriterion.getSortingCriterion());
//            }
//            else
//            {
//                ImejiNamespaces ns = sortCriterion.getSortingCriterion();
//                int i = 0;
//                String variablename = "";
//                while (ns != null) {
//                    
//                    
//                    
//                    variablename = "?sort" +  String.valueOf(i+1);
//                    String lastVariablename = "?sort" +  String.valueOf(i);
//                    query = ". " + variablename + " <" + ns.getNs() + "> " + lastVariablename + " "  + query;
//                    ns = ns.getParent();
//                    i++;
//                }
//                query = query.replaceAll(java.util.regex.Pattern.quote(variablename), "?s");
//                
//               
//            }
//            if(sortCriterion.getSortOrder().equals(SortOrder.DESCENDING))
//                sortQuery="ORDER BY DESC(" + sortVariable + ")";
//            else
//            {
//                sortQuery="ORDER BY " + sortVariable;
//            }
//
//        }
//
//        String limitString = "";
//        if (limit > -1)
//        {
//         limitString = " LIMIT " + limit;   
//        }
//		String completeQuery = "SELECT DISTINCT ?s WHERE { ?s a <" + type + "> " + query + filter + " } " + sortQuery + limitString + " OFFSET " + offset;
//			
//		System.out.println("Created Query:\n"+completeQuery);
//		return completeQuery;
//	}
	
	
	
	
	
	
	
	
	private static String createSubQuery(List<ImejiQueryVariable> qvList, VarCounter y, String oldSubjectVar, boolean inverse)
	{
	    String subquery = "";
	    if(qvList!=null)
	    {
	        
	        for(ImejiQueryVariable qv : qvList)
	        {
	            String subject = oldSubjectVar;
	            
	            
	            String object = "?v" + y;
	            y.increase();
	            if (subject.equals("?s") && inverse)
	            {
	                subquery = subquery + " . MINUS { " + subject +" <" + qv.getNamespace().getNs()+  "> " + object;
	                inverse=false;
	            }
	            else
	            {
	                subquery = subquery + " . OPTIONAL { " + subject +" <" + qv.getNamespace().getNs()+  "> " + object;
	            }
	            qv.setVariable(object);
	            if(qv.getNamespace().isListType())
	            { 
	                String listObject = "?v" + y;
	                y.increase();
	                subquery = subquery + " . " + object +" <http://www.w3.org/2000/01/rdf-schema#member> " + listObject;
	                qv.setVariable(listObject);
	                object=listObject;
	               
	            }
	           
	            subquery = subquery + createSubQuery(qv.getChildren(), y, object, inverse);
	            subquery = subquery + " }";
	        }
	    }
	    return subquery;
	   
	}
	
	/*
	private Map<SearchCriterion, ImejiQueryVariable> createOntology(List<SearchCriterion> scList, Map<SearchCriterion, ImejiQueryVariable> map, Map<ImejiNamespaces, ImejiQueryVariable> currentMap, List<ImejiQueryVariable> roots)
	{
	     
	    if(scList!=null && scList.size()>0)
        {

                for(SearchCriterion sc : scList)
                {
                    if(!sc.getNamespace().equals(ImejiNamespaces.ID_URI))
                    {
                            if(sc.getNamespace()!=null)
                            {
                            ImejiNamespaces ns = sc.getNamespace();
                            ImejiQueryVariable child = null;
                            while (ns!= null)
                            {
                                ImejiQueryVariable qv;
                                
                                if(!currentMap.containsKey(ns) || (currentMap.containsKey(ns) && ns.isCollectionType()))
                                {
                                    List<ImejiQueryVariable> qvList = new ArrayList<ImejiQueryVariable>();
                                    
                                    if(child!=null) qvList.add(child);
                                    
                                    qv = new ImejiQueryVariable(ns, qvList);
                                    map.put(sc, qv);
                                    currentMap.put(ns, qv);
                                }
                                else
                                {
                                   qv = currentMap.get(ns);
                                   if(child!=null && !qv.getChildren().contains(child)) qv.getChildren().add(child);
                                }
                                
                                child = qv;
                                ns = ns.getParent();
                            }
                           if(!roots.contains(child))
                           {
                               roots.add(child);
                           }
                           
                        }
                        createOntology(sc.getChildren(), map, currentMap, roots);
                    }
                }
        }
	    
	}
	*/
	
	
	protected String createQuery(List<List<SearchCriterion>> scList, SortCriterion sortCriterion, String type, int limit, int offset) throws Exception
    {
	    boolean inverse = false;
	    if(scList!=null && scList.size()>0 && scList.get(0)!=null && scList.get(0).size()>0)
	    {
	        inverse = scList.get(0).get(0).isInverse();
	    }
	    
	    //contains a list of root ImejiQueryVariables for each submlist
	    Map<List<SearchCriterion>, List<ImejiQueryVariable>> roots = new HashMap<List<SearchCriterion>, List<ImejiQueryVariable>>(); 
	    
	    //contains a list of root ImejiQueryVariables for each sublist
	    Map<List<SearchCriterion>, Map<ImejiNamespaces, ImejiQueryVariable>> map = new HashMap<List<SearchCriterion>, Map<ImejiNamespaces,ImejiQueryVariable>>();
	    
	    //Map<SearchCriterion, ImejiQueryVariable> treeMap = new HashMap<SearchCriterion, ImejiQueryVariable>();
	    
	    //Add variables for user management
        String query = "";
        query += getSpecificQuery();
       
        String filter = "";
        
        
	    
	    if(scList!=null && scList.size()>0)
	    {
    	    for(List<SearchCriterion> scSubList: scList)
    	    {
    	        Map<ImejiNamespaces, ImejiQueryVariable> currentMap = new HashMap<ImejiNamespaces, ImejiQueryVariable>();
    	        map.put(scSubList, currentMap);
    	        List<ImejiQueryVariable> currentRoots = new ArrayList<ImejiQueryVariable>();
    	        roots.put(scSubList, currentRoots);
    	        
    	        for(SearchCriterion sc : scSubList)
    	        {
        	        if(!sc.getNamespace().equals(ImejiNamespaces.ID_URI))
        	        {
        	            ImejiNamespaces ns = sc.getNamespace();
                        ImejiQueryVariable child = null;
                        while (ns!= null)
                        {
                            ImejiQueryVariable qv;
                            
                            if(!currentMap.containsKey(ns))
                            {
                                List<ImejiQueryVariable> qvList = new ArrayList<ImejiQueryVariable>();
                                
                                if(child!=null) qvList.add(child);
                                
                                qv = new ImejiQueryVariable(ns, qvList);
                                currentMap.put(ns, qv);
                            }
                            else
                            {
                               qv = currentMap.get(ns);
                               if(child!=null && !qv.getChildren().contains(child)) qv.getChildren().add(child);
                            }
                            
                            child = qv;
                            ns = ns.getParent();
                        }
                       if(!currentRoots.contains(child))
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
    	  
    	   String subquery = createSubQuery(allRoots, new VarCounter(), "?s", inverse);
    	   query += subquery;
        }
	    
        

        
        
        if(scList!=null && scList.size()>0)
        {
            filter = " . FILTER(";
            
            
            int j=0;
            boolean operatorWritten = false;
            for(List<SearchCriterion> subList : scList)
            {

                if (scList.indexOf(subList)>0 && subList.size()>0)
                {
                    if(subList.get(0).getOperator().equals(SearchCriterion.Operator.AND))
                        filter += " && ";
                    else if(subList.get(0).getOperator().equals(SearchCriterion.Operator.OR))
                        filter += " || ";
                    
                }
                
                filter+="(";
                
                for(SearchCriterion sc : subList)
                {
                    
                    if (subList.indexOf(sc) > 0)
                    {
                        if(sc.getOperator().equals(SearchCriterion.Operator.AND))
                            filter += " && ";
                        else if(sc.getOperator().equals(SearchCriterion.Operator.OR))
                            filter += " || ";
                    }
                    
                    if(sc.getValue()!=null)
                    {
                        if(sc.getFilterType().equals(Filtertype.URI) && !sc.getNamespace().equals(ImejiNamespaces.ID_URI))
                        {
                            filter += "str(" + map.get(subList).get(sc.getNamespace()).getVariable() + ")='" + sc.getValue() + "'";
                        }
                        else if(sc.getFilterType().equals(Filtertype.URI) && sc.getNamespace().equals(ImejiNamespaces.ID_URI))
                        {
                            filter += "?s" + "=<" + sc.getValue() + ">";
                        }
                        else if(sc.getFilterType().equals(Filtertype.REGEX))
                        {
                            filter += "regex(" + map.get(subList).get(sc.getNamespace()).getVariable() + ", '" + sc.getValue() + "', 'i')";
                        }
                        else if(sc.getFilterType().equals(Filtertype.EQUALS))
                        {
                            filter += map.get(subList).get(sc.getNamespace()).getVariable() + "='" + sc.getValue() + "'";
                        }
                        else if(sc.getFilterType().equals(Filtertype.BOUND))
                        {
                            filter += "bound(" +  map.get(subList).get(sc.getNamespace()).getVariable() + ")=" + sc.getValue() + "";
                        }
                        else if(sc.getFilterType().equals(Filtertype.EQUALS_NUMBER))
                        {
                            filter += map.get(subList).get(sc.getNamespace()).getVariable() + "='" + sc.getValue()+"'^^<http://www.w3.org/2001/XMLSchema#double>";
                        }
                        else if(sc.getFilterType().equals(Filtertype.GREATER_NUMBER))
                        {
                            filter += map.get(subList).get(sc.getNamespace()).getVariable() + ">='" + sc.getValue()+"'^^<http://www.w3.org/2001/XMLSchema#double>";
                        }
                        else if(sc.getFilterType().equals(Filtertype.LESSER_NUMBER))
                        {
                            filter += map.get(subList).get(sc.getNamespace()).getVariable() + "<='" + sc.getValue()+"'^^<http://www.w3.org/2001/XMLSchema#double>";
                        }
                        else if(sc.getFilterType().equals(Filtertype.EQUALS_DATE))
                        {
                            filter += map.get(subList).get(sc.getNamespace()).getVariable() + "='" + sc.getValue()+"'^^<http://www.w3.org/2001/XMLSchema#dateTime>";
                        }
                        else if(sc.getFilterType().equals(Filtertype.GREATER_DATE))
                        {
                            filter += map.get(subList).get(sc.getNamespace()).getVariable() + ">='" + sc.getValue()+"'^^<http://www.w3.org/2001/XMLSchema#dateTime>";
                        }
                        else if(sc.getFilterType().equals(Filtertype.LESSER_DATE))
                        {
                            filter += map.get(subList).get(sc.getNamespace()).getVariable() + "<='" + sc.getValue()+"'^^<http://www.w3.org/2001/XMLSchema#dateTime>";
                        }
                        
                    }
       
                    j++;
                    
                }
                filter+=")";
            }
            filter+=")";
        
        }
        
        
       
        
        
        String specificFilter= ". FILTER(" + getSpecificFilter() + ")";
        
        
        //Add sort criterion
        String sortQuery = "";
        String sortVariable = "?sort0";
       
        
        if(sortCriterion!=null)
        {
            /*
            if(treeMap.containsKey(sortCriterion.getSortingCriterion()))
            {
                sortVariable = treeMap.get(sortCriterion).getVariable();
            }
            else
            {
            */
                ImejiNamespaces ns = sortCriterion.getSortingCriterion();
                int i = 0;
                String variablename = "";
                while (ns != null) {
                    
                    
                    
                    variablename = "?sort" +  String.valueOf(i+1);
                    String lastVariablename = "?sort" +  String.valueOf(i);
                    query = ". " + variablename + " <" + ns.getNs() + "> " + lastVariablename + " "  + query;
                    ns = ns.getParent();
                    i++;
                }
                query = query.replaceAll(java.util.regex.Pattern.quote(variablename), "?s");
                
               
            //}
            if(sortCriterion.getSortOrder().equals(SortOrder.DESCENDING))
                sortQuery="ORDER BY DESC(" + sortVariable + ")";
            else
            {
                sortQuery="ORDER BY " + sortVariable;
            }

        }
        

        
        //offset++;
        String limitString = "";
        if (limit > -1)
        {
         limitString = " LIMIT " + limit;   
        }
        
        //If inverse search add filter to MINUS part
        if(inverse)
        {
            query = query.substring(0, query.lastIndexOf("}"));
            query += filter + "}";
        }
        else
        {
            query += filter;
        }
        
        
        String completeQuery = "SELECT DISTINCT ?s WHERE { ?s a <" + type + "> " + query + specificFilter + " } " + sortQuery + limitString + " OFFSET " + offset;
            
        return completeQuery;
    }
	
	
	private static void createOntology(Map<ImejiNamespaces, ImejiQueryVariable> variableMap, List<SearchCriterion> scList, List<ImejiQueryVariable> currentRoots)
	{

	    if(scList!=null)
	    {
            for(SearchCriterion sc : scList)
            {
                if(sc.getNamespace()!=null)
                {
                    
                
                    ImejiNamespaces ns = sc.getNamespace();
                    ImejiQueryVariable child = null;
                    
                    
                    while (ns!= null)
                    {
                        
                        ImejiQueryVariable qv;
                        if(!variableMap.containsKey(ns))
                        {
                            List<ImejiQueryVariable> qvList = new ArrayList<ImejiQueryVariable>();
                            
                            if(child!=null) qvList.add(child);
                            
                            qv = new ImejiQueryVariable(ns, qvList);
                            variableMap.put(ns, qv);
                        }
                        else
                        {
                           qv = variableMap.get(ns);
                           if(child!=null && !qv.getChildren().contains(child)) qv.getChildren().add(child);
                        }
                        
                        child = qv;
                        ns = ns.getParent();
                    }
                    if(!currentRoots.contains(child))
                    {
                        currentRoots.add(child);
                    }
                   
                }
                createOntology(variableMap, sc.getChildren(), currentRoots);
            }
	    }
	}
	
	public static String createFilter(List<SearchCriterion> scList, Map<ImejiNamespaces, ImejiQueryVariable> variableMap)
	{
	    String filter="";
        for(SearchCriterion sc : scList)
        {
                if(sc.getOperator()!=null)
                {
    
                    if(sc.getOperator().equals(SearchCriterion.Operator.AND))
                        filter += " && ";
                    else if(sc.getOperator().equals(SearchCriterion.Operator.OR))
                        filter += " || ";
                }
            
                if(sc.getValue()!=null && sc.getNamespace()!=null)
                {
                    if(sc.getFilterType().equals(Filtertype.URI) && !sc.getNamespace().equals(ImejiNamespaces.ID_URI))
                    {
                        filter += variableMap.get(sc.getNamespace()).getVariable() + "=<" + sc.getValue() + ">";
                    }
                    else if(sc.getFilterType().equals(Filtertype.URI) && sc.getNamespace().equals(ImejiNamespaces.ID_URI))
                    {
                        filter += "?s" + "=<" + sc.getValue() + ">";
                    }
                    else if(sc.getFilterType().equals(Filtertype.REGEX))
                    {
                        filter += "regex(" + variableMap.get(sc.getNamespace()).getVariable() + ", '" + sc.getValue() + "', 'i')";
                    }
                    else if(sc.getFilterType().equals(Filtertype.EQUALS))
                    {
                        filter += variableMap.get(sc.getNamespace()).getVariable() + "='" + sc.getValue() + "'";
                    }
                    else if(sc.getFilterType().equals(Filtertype.BOUND))
                    {
                        filter += "bound(" +  variableMap.get(sc.getNamespace()).getVariable() + ")=" + sc.getValue() + "";
                    }
                    
                }
                
                if(sc.getChildren()!=null && sc.getChildren().size()>0)
                {
                    filter += " ( "; 
                    filter = filter + createFilter(sc.getChildren(), variableMap);
                    filter += " ) "; 
                }

        }
        return filter;
	}

	
	/*
	 * In development!
	 */
	public String createQuery2(List<SearchCriterion> scList, SortCriterion sortCriterion, String type, int limit, int offset) throws Exception
    {
       
       
        String query = "";
        query += getSpecificQuery();
       
        String filter = "";

        Map<ImejiNamespaces, ImejiQueryVariable> variableMap = new HashMap<ImejiNamespaces, ImejiQueryVariable>();
        List<ImejiQueryVariable> roots = new ArrayList<ImejiQueryVariable>();
        
        if(scList!=null && scList.size()>0)
        {

           createOntology(variableMap, scList, roots);
          
           String subquery = createSubQuery2(roots, new VarCounter(), "?s", false);
           System.out.println(subquery);
           query += subquery;
           
           filter = createFilter(scList, variableMap);
           System.out.println(filter);
        }
        
        
        /*
        if(scList!=null && scList.size()>0)
        {
            filter = " . FILTER(";
            
            
            int j=0;
            boolean operatorWritten = false;
            for(List<SearchCriterion> subList : scList)
            {

                if (scList.indexOf(subList)>0 && subList.size()>0)
                {
                    if(subList.get(0).getOperator().equals(SearchCriterion.Operator.AND))
                        filter += " && ";
                    else if(subList.get(0).getOperator().equals(SearchCriterion.Operator.OR))
                        filter += " || ";
                    
                }
                
                filter+="(";
                
                for(SearchCriterion sc : subList)
                {
                    
                    if (subList.indexOf(sc) > 0)
                    {
                        if(sc.getOperator().equals(SearchCriterion.Operator.AND))
                            filter += " && ";
                        else if(sc.getOperator().equals(SearchCriterion.Operator.OR))
                            filter += " || ";
                    }
                    
                    if(sc.getValue()!=null)
                    {
                        if(sc.getFilterType().equals(Filtertype.URI) && !sc.getNamespace().equals(ImejiNamespaces.ID_URI))
                        {
                            filter += map.get(subList).get(sc.getNamespace()).getVariable() + "=<" + sc.getValue() + ">";
                        }
                        else if(sc.getFilterType().equals(Filtertype.URI) && sc.getNamespace().equals(ImejiNamespaces.ID_URI))
                        {
                            filter += "?s" + "=<" + sc.getValue() + ">";
                        }
                        else if(sc.getFilterType().equals(Filtertype.REGEX))
                        {
                            filter += "regex(" + map.get(subList).get(sc.getNamespace()).getVariable() + ", '" + sc.getValue() + "', 'i')";
                        }
                        else if(sc.getFilterType().equals(Filtertype.EQUALS))
                        {
                            filter += map.get(subList).get(sc.getNamespace()).getVariable() + "='" + sc.getValue() + "'";
                        }
                        else if(sc.getFilterType().equals(Filtertype.BOUND))
                        {
                            filter += "bound(" +  map.get(subList).get(sc.getNamespace()).getVariable() + ")=" + sc.getValue() + "";
                        }
                        
                    }
       
                    j++;
                    
                }
                filter+=")";
            }
            filter+=")";
        
        }
        
        
       */
        
        
        String specificFilter= ". FILTER(" + getSpecificFilter() + ")";
        
        
        //Add sort criterion
        String sortQuery = "";
        String sortVariable = "?sort0";
       
        
        if(sortCriterion!=null)
        {
            /*
            if(treeMap.containsKey(sortCriterion.getSortingCriterion()))
            {
                sortVariable = treeMap.get(sortCriterion).getVariable();
            }
            else
            {
            */
                ImejiNamespaces ns = sortCriterion.getSortingCriterion();
                int i = 0;
                String variablename = "";
                while (ns != null) {
                    
                    
                    
                    variablename = "?sort" +  String.valueOf(i+1);
                    String lastVariablename = "?sort" +  String.valueOf(i);
                    query = ". " + variablename + " <" + ns.getNs() + "> " + lastVariablename + " "  + query;
                    ns = ns.getParent();
                    i++;
                }
                query = query.replaceAll(java.util.regex.Pattern.quote(variablename), "?s");
                
               
            //}
            if(sortCriterion.getSortOrder().equals(SortOrder.DESCENDING))
                sortQuery="ORDER BY DESC(" + sortVariable + ")";
            else
            {
                sortQuery="ORDER BY " + sortVariable;
            }

        }
        

        
        //offset++;
        String limitString = "";
        if (limit > -1)
        {
         limitString = " LIMIT " + limit;   
        }
        
        //If inverse search add filter to MINUS part
        /*
        if(inverse)
        {
            query = query.substring(0, query.lastIndexOf("}"));
            query += filter + "}";
        }
        else
        {
            query += filter;
        }
        */
        
        String completeQuery = "SELECT DISTINCT ?s WHERE { ?s a <" + type + "> " + query + specificFilter + " } " + sortQuery + limitString + " OFFSET " + offset;
            
        System.out.println("Created Query:\n"+completeQuery);
        return completeQuery;
    }
	
	
	
	
	private static String createSubQuery2(List<ImejiQueryVariable> qvList, VarCounter y, String oldSubjectVar, boolean inverse)
    {
        String subquery = "";
        if(qvList!=null)
        {
            
            for(ImejiQueryVariable qv : qvList)
            {
                String subject = oldSubjectVar;
                
                
                String object = "?v" + y;
                y.increase();
                if (subject.equals("?s") && inverse)
                {
                    subquery = subquery + " . MINUS { " + subject +" <" + qv.getNamespace().getNs()+  "> " + object;
                    inverse=false;
                }
                else
                {
                    subquery = subquery + " . OPTIONAL { " + subject +" <" + qv.getNamespace().getNs()+  "> " + object;
                }
                qv.setVariable(object);
                if(qv.getNamespace().isListType())
                { 
                    String listObject = "?v" + y;
                    y.increase();
                    subquery = subquery + " . " + object +" <http://www.w3.org/2000/01/rdf-schema#member> " + listObject;
                    qv.setVariable(listObject);
                    object=listObject;
                   
                }
               
                subquery = subquery + createSubQuery(qv.getChildren(), y, object, inverse);
                subquery = subquery + " }";
            }
        }
        return subquery;
       
    }
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*
	protected String getPartQuery(ImejiNamespaces ns)
	{
	    String query = "";
        int i = 0;
        String variablename = "";
        while (ns != null) {
            variablename = "?v" +  String.valueOf(i+1) + String.valueOf(j);
            String lastVariablename = "?v" +  String.valueOf(i) + String.valueOf(j);
            query = ". " + variablename + " <" + ns.getNs() + "> " + lastVariablename + " "  + query;
            ns = ns.getParent();
            i++;
        }
        
        query = query.replaceAll(java.util.regex.Pattern.quote(variablename), "?s");
        j++;
	}
	*/
	
	
	
	protected static int getUniqueId()
	{
		base.begin();
		Counter c = new Counter();
		try {
			c = new RDF2Bean(base).load(Counter.class, 0);
		} catch (NotFoundException e) {
			bean2RDF.save(c);
			logger.warn("New Counter created", e);
		}
		int id = c.getCounter();
		logger.info("Counter : Requested id : " + id);
		c.setCounter(c.getCounter()+1);
		bean2RDF.save(c);
		base.commit();
		return id;
		
	}
	
	protected Model getModel()
	{
	    try
        {
            String tdbPath = PropertyReader.getProperty("imeji.tdb.path");
            base = DataFactory.model(tdbPath);
            return base;
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (URISyntaxException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
	}
	
	protected void closeModel()
    {
       base.close();
    }
	
	/**Removes lost, anonymous nodes from graph. They are produces during updates of lists/collections. Bug of JenaBean.
	 * 
	 */
	protected synchronized void cleanGraph()
	{
	    try
        {
            base.enterCriticalSection(Lock.WRITE);
            String q = "SELECT DISTINCT ?s WHERE { ?s ?p ?o . OPTIONAL {?s2 ?p2 ?s} . FILTER (isBlank(?s) && !bound(?s2))}";

            Query queryObject = QueryFactory.create(q);
            QueryExecution qe = QueryExecutionFactory.create(queryObject, base);
            ResultSet results = qe.execSelect();
 
            while(results.hasNext())
            {
               QuerySolution qs =  results.next();
               Resource s = qs.getResource("?s");
               s.removeProperties();
            }
   
            qe.close();
        }
        finally 
        {
           base.leaveCriticalSection();
        }
	    
	}
    
	
	protected abstract String getSpecificQuery() throws Exception;
	protected abstract String getSpecificFilter() throws Exception;
	
	private class VarCounter{
	    private int value = 0;

        public void setValue(int value)
        {
            this.value = value;
        }

        public int getValue()
        {
            return value;
        }
        
        public void increase()
        {
            value++;
        }
        
        public String toString()
        {
            return String.valueOf(value);
        }
	    
	}
	
}
