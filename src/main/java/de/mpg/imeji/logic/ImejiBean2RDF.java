/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.Jena;
import com.hp.hpl.jena.rdf.model.Model;

import de.mpg.imeji.logic.search.FulltextIndex;
import de.mpg.imeji.logic.security.Operations.OperationsType;
import de.mpg.imeji.logic.security.Security;
import de.mpg.imeji.logic.vo.Container;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.User;
import de.mpg.j2j.transaction.CRUDTransaction;
import de.mpg.j2j.transaction.ThreadedTransaction;
import de.mpg.j2j.transaction.Transaction;

/**
 * imeji WRITE operations (create/delete/update) in {@link Jena} <br/>
 * - Use {@link Transaction} and {@link Security} <br/>
 * - For concurrency purpose, each write {@link Transaction} is made within a single {@link Thread}. Use
 * {@link ThreadedTransaction} <br/>
 * - for READ operations, uses {@link ImejiRDF2Bean}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class ImejiBean2RDF
{
    private Security security;
    private String modelURI;

    /**
     * Construct one {@link ImejiBean2RDF} for one {@link Model}
     * 
     * @param modelURI
     */
    public ImejiBean2RDF(String modelURI)
    {
        security = new Security();
        this.modelURI = modelURI;
    }

    /**
     * Create a {@link List} of {@link Object} in {@link Jena}
     * 
     * @param objects
     * @param user
     * @throws Exception
     */
    public void create(List<Object> objects, User user) throws Exception
    {
        checkSecurity(objects, user, OperationsType.CREATE);
        runTransaction(objects, OperationsType.CREATE, false);
    }

    /**
     * Delete a {@link List} of {@link Object} in {@link Jena}
     * 
     * @param objects
     * @param user
     * @throws Exception
     */
    public void delete(List<Object> objects, User user) throws Exception
    {
        checkSecurity(objects, user, OperationsType.DELETE);
        runTransaction(objects, OperationsType.DELETE, false);
    }

    /**
     * Update a {@link List} of {@link Object} in {@link Jena}
     * 
     * @param objects
     * @param user
     * @throws Exception
     */
    public void update(List<Object> objects, User user) throws Exception
    {
        checkSecurity(objects, user, OperationsType.UPDATE);
        runTransaction(objects, OperationsType.UPDATE, false);
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
    public void updateLazy(List<Object> objects, User user) throws Exception
    {
        checkSecurity(objects, user, OperationsType.UPDATE);
        runTransaction(objects, OperationsType.UPDATE, true);
    }

    /**
     * Run one WRITE operation in {@link Transaction} within a {@link ThreadedTransaction}
     * 
     * @param objects
     * @param type
     * @param lazy
     * @throws Exception
     */
    private void runTransaction(List<Object> objects, OperationsType type, boolean lazy) throws Exception 
    {
        index(objects);
        Transaction t = new CRUDTransaction(objects, type, modelURI, lazy);
        // Write Transaction needs to be added in a new Thread
        ThreadedTransaction ts = new ThreadedTransaction(t);
        ts.start();
        ts.waitForEnd();
        ts.throwException();
    }

    /**
     * Check {@link Security} for WRITE operations
     * 
     * @param list
     * @param user
     * @param opType
     */
    private void checkSecurity(List<Object> list, User user, OperationsType opType)
    {
        for (Object o : list)
        {
            if (!security.check(opType, user, o))
            {
                throw new RuntimeException("imeji Security exception: " + user.getEmail() + " not allowed to "
                        + opType.name() + " " + extractID(o));
            }
        }
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

    /**
     * Transform a single {@link Object} into a {@link List} with one {@link Object}
     * 
     * @param o
     * @return
     */
    public List<Object> toList(Object o)
    {
        List<Object> list = new ArrayList<Object>();
        list.add(o);
        return list;
    }

    /**
     * Extract the id (as {@link URI}) of an imeji {@link Object},
     * 
     * @param o
     * @return
     */
    private URI extractID(Object o)
    {
        if (o instanceof Item)
        {
            return ((Item)o).getId();
        }
        else if (o instanceof Container)
        {
            return ((Container)o).getId();
        }
        else if (o instanceof MetadataProfile)
        {
            return ((MetadataProfile)o).getId();
        }
        else if (o instanceof User)
        {
            return URI.create(((User)o).getEmail());
        }
        return null;
    }
}
