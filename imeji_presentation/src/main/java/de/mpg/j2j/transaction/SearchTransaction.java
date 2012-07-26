package de.mpg.j2j.transaction;

import java.util.List;

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
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.tdb.TDB;

public class SearchTransaction extends Transaction
{
    private String searchQuery;
    private List<String> results;
    private boolean count = false;

    public SearchTransaction(String searchQuery, List<String> results, boolean count)
    {
        super(null);
        this.searchQuery = searchQuery;
        this.results = results;
        this.count = count;
    }

    @Override
    protected void execute(Dataset ds) throws Exception
    {
        long before = System.currentTimeMillis();
        Query q = QueryFactory.create(searchQuery, Syntax.syntaxARQ);
        QueryExecution qexec = QueryExecutionFactory.create(q, ds);
        qexec.getContext().set(TDB.symUnionDefaultGraph, true);
        try
        {
            ResultSet rs = qexec.execSelect();
            setResults(rs);
        }
        finally
        {
            qexec.close();
        }
       // System.out.println("SEARCH EXEC: " + Long.valueOf(System.currentTimeMillis() - before));
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
        return results.nextSolution().getResource("s");
    }

    @Override
    protected ReadWrite getLockType()
    {
        return ReadWrite.READ;
    }
}
