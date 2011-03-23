package de.mpg.jena.sparql;

import java.util.Iterator;
import java.util.LinkedList;

import javax.faces.model.ResultSetDataModel;

import com.hp.hpl.jena.db.impl.ResultSetReifIterator;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.shared.Lock;
import com.hp.hpl.jena.tdb.TDB;

import de.mpg.jena.ImejiJena;
import de.mpg.jena.ImejiRDF2Bean;

public class ImejiSPARQL
{
	
	/**
	 * Exectute over the whole Imeji DataSet
	 * @param <T>
	 * @param query
	 * @param c
	 * @return
	 */
	public static <T> LinkedList<T> execAndLoad(String query, Class<T> c)
	{
		System.out.println("Search query: " + query);
		Query q = QueryFactory.create(query, Syntax.syntaxARQ);
		System.out.println("queryFactory ");
		QueryExecution qexec  = QueryExecutionFactory.create(q, ImejiJena.imejiDataSet);
		System.out.println("queryexecutionFactory ");
		qexec.getContext().set(TDB.symUnionDefaultGraph, true);
		Model m = ImejiJena.imejiDataSet.getNamedModel(ImejiJena.getModelName(c));
		System.out.println("model ");
		ImejiRDF2Bean reader = new ImejiRDF2Bean(m);
		System.out.println("reader ");
		LinkedList<T> beans = new LinkedList<T>();
        try 
        {
                m.enterCriticalSection(Lock.READ);
                System.out.println("Search exec");
                long before = System.currentTimeMillis();
                ResultSet results = qexec.execSelect();
                System.out.println("Search query done in " +  Long.valueOf(System.currentTimeMillis() - before));
               // ResultSetFormatter.out(System.out, results) ;
                before = System.currentTimeMillis();
               
                for (;results.hasNext();)beans.add(reader.load(c, resource(results).toString()));
                System.out.println("Object loaded in " + Long.valueOf(System.currentTimeMillis() - before) + " rownumber: " + results.getRowNumber());
                return beans;
        } finally 
        {
                m.leaveCriticalSection();
                qexec.close();
        }
	}
	
	/**
	 * Search only over a model
	 * @param <T>
	 * @param m
	 * @param query
	 * @param c
	 * @return
	 */
	public static <T> LinkedList<T> execAndLoad(Model m, String query, Class<T> c)
	{
		ImejiRDF2Bean reader = new ImejiRDF2Bean(m);
		Query q = QueryFactory.create(query, Syntax.syntaxARQ);
		QueryExecution qexec  = QueryExecutionFactory.create(q, m);
		LinkedList<T> beans = new LinkedList<T>();
        try 
        {
                m.enterCriticalSection(Lock.READ);
                ResultSet results = qexec.execSelect();
                for (;results.hasNext();) beans.add(reader.load(c, resource(results).toString()));
                return beans;
        } finally 
        {
                m.leaveCriticalSection();
                qexec.close();
        }
	}
	
	/**
	 * Execute a query but doesn't load the object.
	 * Very fast. To be tested
	 * @param query
	 * @param c
	 * @return
	 */
	public static LinkedList<String> exec(String query)
	{
		Query q = QueryFactory.create(query, Syntax.syntaxARQ);
		QueryExecution qexec  = QueryExecutionFactory.create(q, ImejiJena.imejiDataSet);
		qexec.getContext().set(TDB.symUnionDefaultGraph, true) ;
		LinkedList<String> resultList = new LinkedList<String>();
        try 
        {
                ResultSet results = qexec.execSelect();
                for (;results.hasNext();) 
                {
                	QuerySolution qs = results.nextSolution();
                	Resource r = qs.getResource("v2");
                	if (r != null) resultList.add(r.toString());
                }
                return resultList;
        } finally 
        {
                qexec.close();
        }
	}
	
	/**
	 * Example: SELECT ?s count(DISTINCT ?s) WHERE { ?s a <http://imeji.mpdl.mpg.de/image>}
	 * @param query
	 * @param model
	 * @return
	 */
	public static int execCount(String query)
	{
		System.out.println("Count query: " + query);
		Query q = QueryFactory.create(query, Syntax.syntaxARQ);
		QueryExecution qexec  = QueryExecutionFactory.create(q, ImejiJena.imejiDataSet);
		qexec.getContext().set(TDB.symUnionDefaultGraph, true) ;
		long before = System.currentTimeMillis();
		ResultSet rs = qexec.execSelect();
		System.out.println("Count query done in " +  Long.valueOf(System.currentTimeMillis() - before));
		if (rs.hasNext())
		{
			return rs.next().getLiteral("?.1").getInt();
		}
		return 0;
	}
	
	public static Model execConstruct(String query)
	{
		Query q = QueryFactory.create(query, Syntax.syntaxARQ);
		QueryExecution qexec  = QueryExecutionFactory.create(q, ImejiJena.imejiDataSet);
		qexec.getContext().set(TDB.symUnionDefaultGraph, true) ;
		Model mod = qexec.execConstruct();
		return mod;
	}
	
	private static Resource resource(ResultSet results) 
	{
        return results.nextSolution().getResource("s");
	}
}
