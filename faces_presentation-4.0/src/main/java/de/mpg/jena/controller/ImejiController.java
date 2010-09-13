package de.mpg.jena.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import thewebsemantic.Bean2RDF;
import thewebsemantic.NotFoundException;
import thewebsemantic.RDF2Bean;

import com.hp.hpl.jena.rdf.model.Model;

import de.mpg.escidoc.faces.metastore_test.DataFactory;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.jena.controller.SearchCriterion.Filtertype;
import de.mpg.jena.controller.SearchCriterion.ImejiNamespaces;
import de.mpg.jena.controller.SortCriterion.SortOrder;
import de.mpg.jena.util.Counter;
import de.mpg.jena.util.ObjectHelper;
import de.mpg.jena.vo.Properties;
import de.mpg.jena.vo.User;

public abstract class ImejiController {
	
	
	
	
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
	
	protected String createQuery(List<SearchCriterion> scList, SortCriterion sortCriterion, String type, int limit, int offset) throws Exception
	{
		
	    //Add variables for user management
        String query = "";
		query += getSpecificQuery();
       
		String filter = "";
		Map<ImejiNamespaces, String> variableMap = new HashMap<ImejiNamespaces, String>();
		
		//Create query for searchCriterions
		if(scList!=null && scList.size()>0)
		{
		    query += ". OPTIONAL { ";
    		int j = 0;
    		for(SearchCriterion sc : scList)
    		{
        		    if(!sc.getNamespace().equals(ImejiNamespaces.ID_URI))
        		    {
        		        
        		    
        		    String subquery = "";
        			ImejiNamespaces ns = sc.getNamespace();
        			int i = 0;
        			String variablename = "";
        			String lastVariablename = "";
        			boolean replace =true;
        			while (ns != null) {
        				
        			    
        			    if(variableMap.containsKey(ns))
        				{
        				    replace=false;
        				    break;
        				    
        				}
        				
        			    
        				if(variableMap.containsKey(ns.getParent()))
    			        {
        				    variablename=variableMap.get(ns.getParent());
    			        }
        				else
        				{
        				    variablename = "?v" +  String.valueOf(i+1) + String.valueOf(j);
        				}
        				
        				 //variablename = "?v" +  String.valueOf(i+1) + String.valueOf(j);
                         lastVariablename = "?v" +  String.valueOf(i) + String.valueOf(j);
                         subquery = ". " + variablename + " <" + ns.getNs() + "> " + lastVariablename + " "  + subquery;
                         variableMap.put(ns, lastVariablename);
        				
        				ns = ns.getParent();
        				i++;
        			}
        			
        			//variableMap.put(sc.getNamespace(), lastVariablename);
        			if(replace)
        			{
        			    subquery = subquery.replaceAll(". " + java.util.regex.Pattern.quote(variablename), "?s");
        			}
        			
        			
        			j++;
        			query += subquery;
        		}
    		}
    		query += " }";
		}
    		
    		
		filter = " . FILTER(";
		
		filter+=getSpecificFilter();
		
		if(scList!=null && scList.size()>0)
	    {
    		//Add regex filters
    		if(getSpecificFilter() != null || !getSpecificFilter().isEmpty()) 
		    {
    		    filter += " && (";
		    }
    		
		    int j=0;
    		for(SearchCriterion sc : scList)
    		{
    			if (j > 0)
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
                        filter += "?v0" + j + "=<" + sc.getValue() + ">";
                    }
    			    else if(sc.getFilterType().equals(Filtertype.URI) && sc.getNamespace().equals(ImejiNamespaces.ID_URI))
                    {
    			        filter += "?s=<" + sc.getValue() + ">";
                    }
                    else if(sc.getFilterType().equals(Filtertype.REGEX))
                    {
                        filter += "regex(?v0" + j + ", '" + sc.getValue() + "', 'i')";
                    }
                    else if(sc.getFilterType().equals(Filtertype.EQUALS))
                    {
                        filter += "?v0" + j + "='" + sc.getValue() + "'";
                    }
    			}
   
    			j++;
    			
    		}
    		filter+=")";
		
		}
		filter+=")";
		
		//Add sort criterion
        String sortQuery = "";
        String sortVariable = "?sort0";
       
        
        if(sortCriterion!=null)
        {
            if(variableMap.containsKey(sortCriterion.getSortingCriterion()))
            {
                sortVariable = variableMap.get(sortCriterion.getSortingCriterion());
            }
            else
            {
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
                
               
            }
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
		String completeQuery = "SELECT DISTINCT ?s WHERE { ?s a <" + type + "> " + query + filter + " } " + sortQuery + limitString + " OFFSET " + offset;
			
		System.out.println("Created Query:\n"+completeQuery);
		return completeQuery;
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
		}
		int id = c.getCounter();
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
	
	protected abstract String getSpecificQuery() throws Exception;
	protected abstract String getSpecificFilter() throws Exception;
	
	
	
}
