package de.mpg.j2j.transaction;

import java.util.Iterator;
import java.util.List;

import tdb.tdbstats;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.ARQ;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.mgt.Explain.InfoLevel;
import com.hp.hpl.jena.tdb.TDB;

import de.mpg.imeji.logic.ImejiJena;

public class SearchTransaction extends Transaction
{
    private String searchQuery;
    private List<String> results;
    private int numberOfResults = 0;
    private String modelName =null;
    private boolean count = false;

    public SearchTransaction(String modelName, String searchQuery, List<String> results, boolean count)
    {
        super(null);
        this.searchQuery = searchQuery;
        this.results = results;
        this.count = count;
        this.modelName = modelName;
    }

    @Override
    protected void execute(Dataset ds) throws Exception
    {
        long startSearch = System.currentTimeMillis();
        // searchQuery += " LIMIT 938  ";
        ARQ.setExecutionLogging(InfoLevel.NONE);
        System.out.println(searchQuery);
        //ImejiJena.printModel(ImejiJena.collectionModel);
        //searchQuery ="PREFIX fn: <http://www.w3.org/2005/xpath-functions#> SELECT DISTINCT ?s WHERE {?s <http://imeji.org/terms/collection> <http://imeji.org/collection/2016>}";
       //searchQuery = "PREFIX fn: <http://www.w3.org/2005/xpath-functions#> SELECT ?s ?sort0 WHERE {?s <http://imeji.org/terms/collection> <http://imeji.org/collection/2016> . ?s <http://imeji.org/terms/properties> ?props . ?props <http://imeji.org/terms/status> ?status . ?s <http://imeji.org/terms/collection> ?c   .FILTER(?status!=<http://imeji.org/terms/status#WITHDRAWN> && ( (?status=<http://imeji.org/terms/status#RELEASED> || ?c=<http://imeji.org/collection/12> || ?c=<http://imeji.org/collection/2016>)))} ";
        
        Query q = QueryFactory.create(searchQuery + " LIMIT 11000", Syntax.syntaxARQ);
        QueryExecution qexec = initQueryExecution(ds, q);
        qexec.getContext().set(TDB.symUnionDefaultGraph, true);
        qexec.setTimeout(20000);
        try
        {
           
            ResultSet rs = qexec.execSelect();
            setResults(rs);
            count = true;
        }
        finally
        {
            qexec.close();
            count = false;
            System.out.println(results.size() + " items found  done in " + Long.valueOf(System.currentTimeMillis() - startSearch));
        }
    }
    
    private QueryExecution initQueryExecution(Dataset ds, Query q)
    {
        if (modelName != null)
        {
            return QueryExecutionFactory.create(q, ds.getNamedModel(modelName));
        }
        return QueryExecutionFactory.create(q, ds);
    }

    private void setNumberOfResults(ResultSet rs)
    {
        if (rs.hasNext())
        {
            QuerySolution qs = rs.next();
            Literal l = qs.getLiteral("?.1");
            numberOfResults = l.getInt();
        }
    }

    private void setResults(ResultSet rs)
    {
        if (count)
        {
            setCountResults(rs);
        }
        else
        {
            setExecResults(rs);
        }
    }

    private void setCountResults(ResultSet rs)
    {
        if (rs.hasNext())
        {
            QuerySolution qs = rs.next();
            Literal l = qs.getLiteral("?.1");
            int c = l.getInt();
            results.add(Integer.toString(c));
        }
    }

    private void setExecResults(ResultSet rs)
    {
        for (; rs.hasNext();)
        {
            results.add(resource(rs).toString());
        }
    }

    private Resource resource(ResultSet results)
    {
        QuerySolution qs = results.nextSolution();
        // qs.getLiteral("sort0");
        return qs.getResource("s");
    }

    @Override
    protected ReadWrite getLockType()
    {
        return ReadWrite.READ;
    }
}
