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
import com.hp.hpl.jena.rdf.model.RDFNode;
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
        Query q = QueryFactory.create(searchQuery, Syntax.syntaxARQ);
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
        RDFNode rdfNode = qs.get("sort0");
        if (rdfNode != null)
        {
            String sortValue = "";
            if (rdfNode.isLiteral())
            {
                sortValue = rdfNode.asLiteral().toString();
            }
            else if (rdfNode.isURIResource())
            {
                sortValue = rdfNode.asResource().getURI();
            }
            return SortHelper.addSortValue(qs.getResource("s").toString(), sortValue);
        }
        return qs.getResource("s").toString();
    }

    @Override
    protected ReadWrite getLockType()
    {
        return ReadWrite.READ;
    }
}
