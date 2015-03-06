/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.writer;

import java.net.URI;
import java.util.List;

import com.hp.hpl.jena.Jena;
import com.hp.hpl.jena.rdf.model.Model;

import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.logic.ImejiSPARQL;
import de.mpg.imeji.logic.reader.JenaReader;
import de.mpg.imeji.logic.search.FulltextIndex;
import de.mpg.imeji.logic.search.query.SPARQLQueries;
import de.mpg.imeji.logic.vo.Grant.GrantType;
import de.mpg.imeji.logic.vo.User;
import de.mpg.j2j.transaction.CRUDTransaction;
import de.mpg.j2j.transaction.ThreadedTransaction;
import de.mpg.j2j.transaction.Transaction;

/**
 * imeji WRITE operations (create/delete/update) in {@link Jena} <br/>
 * - Use {@link Transaction} <br/>
 * - For concurrency purpose, each write {@link Transaction} is made within a single {@link Thread}. Use
 * {@link ThreadedTransaction} <br/>
 * - for READ operations, uses {@link JenaReader}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class JenaWriter implements Writer
{
    private String modelURI;

    /**
     * Construct one {@link JenaWriter} for one {@link Model}
     * 
     * @param modelURI
     */
    public JenaWriter(String modelURI)
    {
        this.modelURI = modelURI;
    }

    /**
     * Create a {@link List} of {@link Object} in {@link Jena}
     * 
     * @param objects
     * @param user
     * @throws Exception
     */
    public void create(List<Object> objects, User user) throws ImejiException
    {
        runTransaction(objects, GrantType.CREATE, false);
    }

    /**
     * Delete a {@link List} of {@link Object} in {@link Jena}
     * 
     * @param objects
     * @param user
     * @throws Exception
     */
    public void delete(List<Object> objects, User user) throws ImejiException
    {
        runTransaction(objects, GrantType.DELETE, false);
        for (Object o : objects)
        {
            URI uri = WriterFacade.extractID(o);
            if (uri != null)
                ImejiSPARQL.execUpdate(SPARQLQueries.updateRemoveGrantsFor(uri.toString()));
        }
    }

    /**
     * Update a {@link List} of {@link Object} in {@link Jena}
     * 
     * @param objects
     * @param user
     * @throws Exception
     */
    public void update(List<Object> objects, User user) throws ImejiException
    {
        runTransaction(objects, GrantType.UPDATE, false);
    }

    /**
     * Update LAZY a {@link List} of {@link Object} in {@link Jena}<br/>
     * - {@link List} contained within the {@link Object} are not updated: faster performance, especially for objects
     * with huge {@link List}
     * 
     * @param objects
     * @param user
     * @throws Exception
     */
    public void updateLazy(List<Object> objects, User user) throws ImejiException
    {
        runTransaction(objects, GrantType.UPDATE, true);
    }

    /**
     * Run one WRITE operation in {@link Transaction} within a {@link ThreadedTransaction}
     * 
     * @param objects
     * @param type
     * @param lazy
     * @throws Exception
     */
    private void runTransaction(List<Object> objects, GrantType type, boolean lazy) throws ImejiException
    {
        index(objects);
        Transaction t = new CRUDTransaction(objects, type, modelURI, lazy);
        // Write Transaction needs to be added in a new Thread
        ThreadedTransaction.run(new ThreadedTransaction(t));
    }

    /**
     * Set the fulltext value of each imeji object (based on the metadata values, the author, etc.)
     * 
     * @param l
     */
    private void index(List<Object> l)
    {
        for (Object o : l)
        {
            if (o instanceof FulltextIndex)
            {
                ((FulltextIndex)o).indexFulltext();
            }
        }
    }
}
