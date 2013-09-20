/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic;

import java.util.ArrayList;
import java.util.List;

import de.mpg.j2j.transaction.SPARQLUpdateTransaction;
import de.mpg.j2j.transaction.SearchTransaction;
import de.mpg.j2j.transaction.ThreadedTransaction;

/**
 * Manage search (sparql) transaction
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class ImejiSPARQL
{
    /**
     * Execute a sparql query and return {@link List} of uri
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

    /**
     * execute a sparql update
     * 
     * @param query
     */
    public static void execUpdate(String query)
    {
        try
        {
            ThreadedTransaction.run(new ThreadedTransaction(new SPARQLUpdateTransaction(null, query)));
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
