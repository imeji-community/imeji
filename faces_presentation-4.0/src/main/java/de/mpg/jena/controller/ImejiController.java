package de.mpg.jena.controller;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.apache.log4j.Logger;

import tdb.tdbloader;
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
import com.hp.hpl.jena.rdf.model.Selector;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.shared.Lock;
import com.hp.hpl.jena.tdb.TDBLoader;
import com.hp.hpl.jena.vocabulary.RDF;

import de.mpg.imeji.util.PropertyReader;
import de.mpg.jena.ImejiBean2RDF;
import de.mpg.jena.ImejiJena;
import de.mpg.jena.ImejiRDF2Bean;
import de.mpg.jena.concurrency.locks.Locks;
import de.mpg.jena.controller.SearchCriterion.Filtertype;
import de.mpg.jena.controller.SearchCriterion.ImejiNamespaces;
import de.mpg.jena.controller.SortCriterion.SortOrder;
import de.mpg.jena.util.Counter;
import de.mpg.jena.util.ObjectHelper;
import de.mpg.jena.vo.Properties;
import de.mpg.jena.vo.User;
import de.mpg.jena.vo.Properties.Status;

public abstract class ImejiController
{
    private static Logger logger = Logger.getLogger(ImejiController.class);
    protected User user;
    private static Model base = null;
    static
    {
        try
        {
        	//ImejiJena.init();
        	
            //String tdbPath = PropertyReader.getProperty("imeji.tdb.path");
            //base = DataFactory.model(tdbPath);
            /*
             * IndexBuilderString larqBuilder = new IndexBuilderString() ;
             * larqBuilder.indexStatements(base.listStatements()) ; base.register(larqBuilder);
             * larqBuilder.closeWriter(); IndexLARQ index = larqBuilder.getIndex() ; LARQ.setDefaultIndex(index) ;
             */
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    /*
     * protected static Model base = null; protected static Store store = null; static{ StoreDesc storeDesc = new
     * StoreDesc(LayoutType.LayoutTripleNodesHash, DatabaseType.MySQL) ; JDBC.loadDriverMySQL(); String jdbcURL =
     * "jdbc:mysql://dev-faces.mpdl.mpg.de:5432/imeji"; SDBConnection conn = new SDBConnection(jdbcURL, "imeji",
     * "dev-faces") ; store = SDBFactory.connectStore(conn, storeDesc); store.getLoader().setChunkSize(5000000);
     * store.getLoader().setUseThreading(true); base = SDBFactory.connectDefaultModel(store); }
     */
    /*
     * static{ StoreDesc storeDesc = new StoreDesc(LayoutType.LayoutTripleNodesHash, DatabaseType.PostgreSQL) ;
     * JDBC.loadDriverPGSQL(); String jdbcURL = "jdbc:postgresql://localhost:5432/imeji"; SDBConnection conn = new
     * SDBConnection(jdbcURL, "postgres", "postgres") ; store = SDBFactory.connectStore(conn, storeDesc);
     * //store.getLoader().setChunkSize(50000); store.getLoader(). base = SDBFactory.connectDefaultModel(store); }
     */
    /*
     * static { try { String path = PropertyReader.getProperty("imeji.tdb.path"); 
     * base = DataFactory.model(path); } catch (Exception e) { } }
     */
    protected static Bean2RDF bean2RDF;// = new Bean2RDF(base);
    protected static RDF2Bean rdf2Bean ;//= new RDF2Bean(base);
    private static ImejiBean2RDF imejiBean2RDF;// = new ImejiBean2RDF(base);
    private static ImejiRDF2Bean imejiRDF2Bean;

    public ImejiController(User user2)
    {
        this.user = user2;
        bean2RDF = new Bean2RDF(ImejiJena.imejiDataSet.getDefaultModel());
        //base = getModel();
    }

    protected static void writeCreateProperties(Properties properties, User user)
    { 
        Date now = new Date();
        properties.setCreatedBy(ObjectHelper.getURI(User.class, user.getEmail()));
        properties.setModifiedBy(ObjectHelper.getURI(User.class, user.getEmail()));
        properties.setCreationDate(now);
        properties.setLastModificationDate(now);
        if (properties.getStatus() == null) properties.setStatus(Status.PENDING);
    }
    
    public static void writeUpdateProperties(Properties properties, User user)
    {
        Date now = new Date();
        properties.setModifiedBy(ObjectHelper.getURI(User.class, user.getEmail()));
        properties.setLastModificationDate(now);
    }

    public boolean hasImageLocked(Collection<URI> containersUri, User user)
	{
		for (URI u : containersUri)
		{
			if (Locks.isLocked(u.toString(), user.getEmail()))
			{
				return true;
			}
		}
		return false;
	}
    
    public static void deleteObjects(String uri)
    {
        Resource r = base.getResource(uri);
        StmtIterator it = r.listProperties(RDF.type);
        while (it.hasNext()) {
			Resource r1 = it.nextStatement().getResource();
        }

        if (base.containsResource(r))
        {
            logger.info("Forced Delete of " + uri);
            try{
	            Selector selector = new SimpleSelector(null, null, r);
	            StmtIterator iter = base.listStatements(selector);
	            while (iter.hasNext())
	            {
	                Statement mdToDelete = iter.nextStatement();
	                Resource sub = mdToDelete.getSubject();
	                Selector selector2 = new SimpleSelector(null, null, sub);
	                StmtIterator iter2 = base.listStatements(selector2);
	                while (iter2.hasNext())
	                {
	                    Statement imageWithMd = iter2.nextStatement();
	                    Selector selector3 = new SimpleSelector(null, imageWithMd.getPredicate(), (Resource)null);
	                    StmtIterator iter3 = base.listStatements(selector3);
	                    while (iter3.hasNext())
	                    {
	                        Statement statementToDelete = iter3.nextStatement();
	                        if (mdToDelete.getSubject().getId().equals(statementToDelete.getResource().getId()))
	                        {
	                            try
	                            {
	                            	base.remove(statementToDelete);
	                            }
	                            catch (Exception e) 
	                            {	
	                            	logger.warn("Error deleting object" + statementToDelete.getResource().getId());
	                            }
	                            iter3 = base.listStatements(selector3);
	                        }
	                    }
	                }
	                try
	                {
	                	base.remove(mdToDelete);
	                }
	                catch (Exception e) 
	                {	
	                	logger.warn("Error deleting object" + mdToDelete.getResource().getId());
	                }
	            }
            }
            catch (Exception e) {
				logger.error("PROBLEM by Forced delete!" + e.getMessage());
			}
        }
        else
        {
            logger.warn("Error forced Delete of " + uri + ". Resource was not found.");
        }
    }

  
    protected static int getUniqueId()
    {
        Counter c = new Counter();
        try
        {
            c = new RDF2Bean(ImejiJena.imejiDataSet.getDefaultModel()).load(Counter.class, 0);
        }
        catch (NotFoundException e)
        {
            
        	bean2RDF.save(c);
            logger.warn("New Counter created", e);
        }
        int id = c.getCounter();
        logger.info("Counter : Requested id : " + id);
        c.setCounter(c.getCounter() + 1);
        bean2RDF.save(c);
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

    /**
     * Removes lost, anonymous nodes from graph. They are produces during updates of lists/collections. Bug of JenaBean.
     */
//    public synchronized void cleanGraph()
//    {
//        if (base != null)
//        {
//	    	try
//	        {
//	            base.enterCriticalSection(Lock.WRITE);
//	            String q = "SELECT DISTINCT ?s WHERE { ?s ?p ?o . OPTIONAL {?s2 ?p2 ?s} . FILTER (isBlank(?s) && !bound(?s2))}";
//	            Query queryObject = QueryFactory.create(q);
//	            QueryExecution qe = QueryExecutionFactory.create(queryObject, base);
//	            ResultSet results = qe.execSelect();
//	            while (results.hasNext())
//	            {
//	                QuerySolution qs = results.next();
//	                Resource s = qs.getResource("?s");
//	                s.removeProperties();
//	            }
//	            qe.close();
//	        }
//	        finally
//	        {
//	            base.leaveCriticalSection();
//	        }
//        }
//    }

    @Deprecated
    public synchronized void cleanGraph(Model graph)
    {
//        try
//        {
//        	graph.enterCriticalSection(Lock.WRITE);
//            String q = "SELECT DISTINCT ?s WHERE { ?s ?p ?o . OPTIONAL {?s2 ?p2 ?s} . FILTER (isBlank(?s) && !bound(?s2))}";
//            Query queryObject = QueryFactory.create(q);
//            QueryExecution qe = QueryExecutionFactory.create(queryObject, graph);
//            ResultSet results = qe.execSelect();
//           
//            while (results.hasNext())
//        	{
//             	QuerySolution qs = results.next();
//                Resource s = qs.getResource("?s");
//                s.removeProperties();
//            }
//           
//            qe.close();
//        }
//        catch (Exception e) 
//        {
//			logger.error(e.getMessage());
//		}
//        finally
//        {
//        	graph.leaveCriticalSection();
//        }
        
    }
    
    protected abstract String getSpecificQuery() throws Exception;

    protected abstract String getSpecificFilter() throws Exception;

    private class VarCounter
    {
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
