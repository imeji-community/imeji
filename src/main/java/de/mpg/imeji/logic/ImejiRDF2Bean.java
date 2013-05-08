/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.Jena;
import com.hp.hpl.jena.rdf.model.Model;

import de.mpg.imeji.logic.security.Operations.OperationsType;
import de.mpg.imeji.logic.security.Security;
import de.mpg.imeji.logic.vo.User;
import de.mpg.j2j.helper.J2JHelper;
import de.mpg.j2j.transaction.CRUDTransaction;
import de.mpg.j2j.transaction.Transaction;

/**
 * imeji READ operations in {@link Jena} <br/>
 * - Use {@link CRUDTransaction} to load objects <br/>
 * - Check objects visibility via {@link Security}<br/>
 * - Implements lazy loading ({@link List} contained in objects are then no loaded), for faster load<br/>
 * - For WRITE operations, uses {@link ImejiBean2RDF}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class ImejiRDF2Bean
{
    private String modelURI;
    private boolean lazy = false;
    private Security security = null;
    private static Logger logger = Logger.getLogger(ImejiRDF2Bean.class);

    /**
     * imeji object loader for one {@link Model}
     * 
     * @param modelURI
     */
    public ImejiRDF2Bean(String modelURI)
    {
        this.modelURI = modelURI;
        security = new Security();
    }

    /**
     * Load lazy one {@link Object} according to its uri <br/>
     * Faster than load method, but contained {@link List} are skipped for loading
     * 
     * @param uri
     * @param user
     * @param o
     * @return
     * @throws Exception
     */
    public Object loadLazy(String uri, User user, Object o) throws Exception
    {
        this.lazy = true;
        return load(uri, user, o);
    }

    /**
     * Load a object from {@link Jena} within one {@link CRUDTransaction}
     * 
     * @param uri
     * @param user
     * @param o
     * @return
     * @throws Exception
     */
    public Object load(String uri, User user, Object o) throws Exception
    {
        J2JHelper.setId(o, URI.create(uri));
        List<Object> objects = new ArrayList<Object>();
        objects.add(o);
        List<Object> l = load(objects, user);
        lazy = false;
        if (l.size() > 0)
            return l.get(0);
        return null;
    }

    /**
     * Load a list of objects within one {@link CRUDTransaction}
     * 
     * @param objects
     * @param user
     * @return
     * @throws Exception
     */
    public List<Object> load(List<Object> objects, User user) throws Exception
    {
        Transaction t = new CRUDTransaction(objects, OperationsType.READ, modelURI, lazy);
        t.start();
        t.throwException();
        checkSecurity(objects, user, OperationsType.READ);
        return objects;
    }

    /**
     * Load a {@link List} of {@link Object} within one {@link CRUDTransaction} <br/>
     * Faster than load method, but contained {@link List} are skipped for loading
     * 
     * @param objects
     * @param user
     * @return
     * @throws Exception
     */
    public List<Object> loadLazy(List<Object> objects, User user) throws Exception
    {
        this.lazy = true;
        return load(objects, user);
    }

    /**
     * Check the {@link Security} of loaded {@link Object}
     * 
     * @param list
     * @param user
     * @param opType
     */
    private void checkSecurity(List<Object> list, User user, OperationsType opType)
    {
        for (int i = 0; i < list.size(); i++)
        {
            if (!security.check(opType, user, list.get(i)))
            {
                String id = J2JHelper.getId(list.get(i)).toString();
                String email = "Not logged in";
                if (user != null)
                    email = user.getEmail();
                logger.error("imeji Security exception: " + email + " not allowed to " + opType.name() + " " + id);
                // throw new RuntimeException("imeji Security exception: " + email + " not allowed to " + opType.name()
                // + " " + id);
            }
        }
    }
}
