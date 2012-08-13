package de.mpg.j2j.transaction;

import java.util.List;

import arq.arq;

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
import com.hp.hpl.jena.sparql.mgt.Explain.InfoLevel;
import com.hp.hpl.jena.tdb.TDB;

import de.mpg.imeji.logic.search.util.SortHelper;

public class SearchTransaction extends Transaction
{
    private String searchQuery;
    private List<String> results;
    private String modelName = null;
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
        long a = System.currentTimeMillis();
        // searchQuery += " OFFSET 29982 LIMIT 30000";
        //searchQuery = "PREFIX fn: <http://www.w3.org/2005/xpath-functions#> SELECT DISTINCT ?s  WHERE {?s <http://imeji.org/terms/metadataSet> ?mds .  exists{?mds <http://imeji.org/terms/metadata> ?md  .?md  <http://imeji.org/terms/statement> ?el  .FILTER(?el=<http://imeji.org/statement/43d6614e-c204-4f9d-8b67-1fa539f3f3a3>)}}";
        //searchQuery = "PREFIX fn: <http://www.w3.org/2005/xpath-functions#> SELECT DISTINCT ?s  WHERE {?s <http://imeji.org/terms/collection> <http://imeji.org/collection/37> . minus{ ?s <http://imeji.org/terms/metadataSet> ?mds . ?mds <http://imeji.org/terms/metadata> ?md  . ?md  <http://imeji.org/terms/statement> ?el  .FILTER(?el=<http://imeji.org/statement/43d6614e-c204-4f9d-8b67-1fa539f3f3a3>)}}";

        //System.out.println(searchQuery);
        Query q = QueryFactory.create(searchQuery, Syntax.syntaxARQ);
        // System.out.println(q.serialize(Syntax.syntaxSPARQL_11));
        //System.out.println(q.serialize(Syntax.defaultQuerySyntax));
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
            //System.out.println("SEARCH IN " + Long.valueOf(System.currentTimeMillis() - a));
            count = false;
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
            results.add(readResult(rs));
        }
    }

    private String readResult(ResultSet results)
    {
        QuerySolution qs = results.nextSolution();
        Literal l = qs.getLiteral("sort0");
        if (l != null)
        {
            return SortHelper.addSortValue(qs.getResource("s").toString(), qs.getLiteral("sort0").toString());
        }
        return qs.getResource("s").toString();
    }

    @Override
    protected ReadWrite getLockType()
    {
        return ReadWrite.READ;
    }
}
