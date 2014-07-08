/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic;

import java.net.URI;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.Jena;
import com.hp.hpl.jena.rdf.model.Model;

import de.mpg.imeji.logic.auth.exception.NotAllowedError;
import de.mpg.imeji.logic.auth.util.AuthUtil;
import de.mpg.imeji.logic.search.FulltextIndex;
import de.mpg.imeji.logic.search.query.SPARQLQueries;
import de.mpg.imeji.logic.vo.Container;
import de.mpg.imeji.logic.vo.Grant.GrantType;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.logic.vo.UserGroup;
import de.mpg.j2j.transaction.CRUDTransaction;
import de.mpg.j2j.transaction.ThreadedTransaction;
import de.mpg.j2j.transaction.Transaction;

/**
 * imeji WRITE operations (create/delete/update) in {@link Jena} <br/>
 * - Use {@link Transaction} and {@link Security} <br/>
 * - For concurrency purpose, each write {@link Transaction} is made within a single {@link Thread}. Use
 * {@link ThreadedTransaction} <br/>
 * - for READ operations, uses {@link ImejiReader}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class ImejiWriter
{
    private String modelURI;
    private static Logger logger = Logger.getLogger(ImejiWriter.class);

    /**
     * Construct one {@link ImejiWriter} for one {@link Model}
     * 
     * @param modelURI
     */
    public ImejiWriter(String modelURI)
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
    public void create(List<Object> objects, User user) throws Exception
    {
        checkSecurity(objects, user, GrantType.CREATE);
        runTransaction(objects, GrantType.CREATE, false);
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
        checkSecurity(objects, user, GrantType.DELETE);
        runTransaction(objects, GrantType.DELETE, false);
        for (Object o : objects)
        {
            URI uri = extractID(o);
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
    public void update(List<Object> objects, User user) throws Exception
    {
        checkSecurity(objects, user, GrantType.UPDATE);
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
    public void updateLazy(List<Object> objects, User user) throws Exception
    {
        checkSecurity(objects, user, GrantType.UPDATE);
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
    private void runTransaction(List<Object> objects, GrantType type, boolean lazy) throws Exception
    {
        index(objects);
        Transaction t = new CRUDTransaction(objects, type, modelURI, lazy);
        // Write Transaction needs to be added in a new Thread
        ThreadedTransaction.run(new ThreadedTransaction(t));
    }

    /**
     * Check {@link Security} for WRITE operations
     * 
     * @param list
     * @param user
     * @param opType
     * @throws NotAllowedError
     */
    private void checkSecurity(List<Object> list, User user, GrantType gt) throws NotAllowedError
    {
        for (Object o : list)
        {
            switch (gt)
            {
                case CREATE:
                    throwAuthorizationException(AuthUtil.staticAuth().createNew(user, o), user.getEmail()
                            + " not allowed to create " + extractID(o));
                    break;
                case DELETE:
                    throwAuthorizationException(AuthUtil.staticAuth().delete(user, o), user.getEmail()
                            + " not allowed to delete " + extractID(o));
                    break;
                case UPDATE:
                    throwAuthorizationException(AuthUtil.staticAuth().update(user, o), user.getEmail()
                            + " not allowed to update " + extractID(o));
                    break;
            }
        }
    }

    /**
     * If false, throw a {@link NotAllowedError}
     * 
     * @param b
     * @param message
     * @throws NotAllowedError
     */
    private void throwAuthorizationException(boolean allowed, String message) throws NotAllowedError
    {
        if (!allowed)
        {
            NotAllowedError e = new NotAllowedError(message);
            logger.error(e);
            throw e;
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
        else if (o instanceof UserGroup)
        {
            return ((UserGroup)o).getId();
        }
        return null;
    }
}
