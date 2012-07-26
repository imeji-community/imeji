/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.search;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.tdb.TDB;

import de.mpg.imeji.logic.ImejiJena;
import de.mpg.j2j.transaction.SearchTransaction;

public class ImejiSPARQL
{
    /**
     * Execute a query but doesn't load the object.
     * 
     * @param query
     * @param c
     * @return
     */
    public static List<String> exec(String query)
    {
        long before = System.currentTimeMillis();
        List<String> results = new ArrayList<String>(1000);
        SearchTransaction transaction = new SearchTransaction(query, results, false);
        transaction.start();
        transaction.waitForEnd();
        try
        {
            transaction.throwException();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        //System.out.println("SEARCH TRANSACTION: " + Long.valueOf(System.currentTimeMillis() - before));
        execCount(query);
        return results;
    }

    /**
     * Example: SELECT ?s count(DISTINCT ?s) WHERE { ?s a <http://imeji.org/terms/item>}
     * 
     * @param query
     * @param modelURI
     * @return
     */
    public static int execCount(String query)
    {
        //System.out.println("COUNT TRANSACTION...");
        long before = System.currentTimeMillis();
        query = query.replace("SELECT DISTINCT ?s WHERE ", "SELECT count(DISTINCT ?s) WHERE ");
        List<String> results = new ArrayList<String>(1);
        SearchTransaction transaction = new SearchTransaction(query, results, true);
        transaction.start();
        transaction.waitForEnd();
        try
        {
            transaction.throwException();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
       // System.out.println("COUNT TRANSACTION: " + Long.valueOf(System.currentTimeMillis() - before));
        return Integer.parseInt(results.get(0));
    }
}
