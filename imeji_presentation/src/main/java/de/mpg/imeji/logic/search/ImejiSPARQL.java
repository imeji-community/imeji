/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.search;

import java.util.ArrayList;
import java.util.List;

import de.mpg.imeji.logic.search.vo.ComparableSearchResult;
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
    public static List<String> exec(String query, String modelName)
    {
        List<String> results = new ArrayList<String>(1000);
        SearchTransaction transaction = new SearchTransaction(modelName, query, results, false);
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
        return results;
    }

    /**
     * Example: SELECT ?s count(DISTINCT ?s) WHERE { ?s a <http://imeji.org/terms/item>}
     * 
     * @param query
     * @param modelURI
     * @return
     */
    public static int execCount(String query, String modelName)
    {
        query = query.replace("SELECT DISTINCT ?s WHERE ", "SELECT count(DISTINCT ?s) WHERE ");
        List<String> results = new ArrayList<String>(1);
        SearchTransaction transaction = new SearchTransaction(modelName, query, results, true);
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
        if (results.size() > 0)
        {
            return Integer.parseInt(results.get(0));
        }
        return 0;
    }
}
