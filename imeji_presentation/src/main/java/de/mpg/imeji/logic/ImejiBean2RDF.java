/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

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
 * Interface for write operations (create/delete/update) in Jena. Implements transactions and security.
 * 
 * @author saquet
 */
public class ImejiBean2RDF
{
    private Security security;
    private static Logger logger = Logger.getLogger(ImejiBean2RDF.class);
    private String modelURI;
    public ImejiBean2RDF(String modelURI)
    {
        security = new Security();
        this.modelURI = modelURI;
    }

    private void runTransaction(List<Object> objects, OperationsType type, boolean lazy) throws Exception
    {
        Transaction t = new CRUDTransaction(objects, type, modelURI, lazy);
        // Write Transaction needs to be added in a new Thread
        ThreadedTransaction ts = new ThreadedTransaction(t);
        ts.start();
        ts.waitForEnd();
        ts.throwException();
    }

    public void create(List<Object> objects, User user) throws Exception
    {
        checkSecurity(objects, user, OperationsType.CREATE);
        runTransaction(objects, OperationsType.CREATE, false);
    }

    public void delete(List<Object> objects, User user) throws Exception
    {
        checkSecurity(objects, user, OperationsType.DELETE);
        runTransaction(objects, OperationsType.DELETE, false);
    }

    public void update(List<Object> objects, User user) throws Exception
    {
        checkSecurity(objects, user, OperationsType.UPDATE);
        runTransaction(objects, OperationsType.UPDATE, false);
    }
    
    public void updateLazy(List<Object> objects, User user) throws Exception
    {
        checkSecurity(objects, user, OperationsType.UPDATE);
        runTransaction(objects, OperationsType.UPDATE, true);
    }

    private void checkSecurity(List<Object> list, User user, OperationsType opType)
    {
        for (Object o : list)
        {
            if (!security.check(opType, user, o))
            {
                throw new RuntimeException("Imeji Security exception: " + user.getEmail() + " not allowed to "
                        + opType.name() + " " + extractID(o));
            }
        }
    }

    public List<Object> toList(Object o)
    {
        List<Object> list = new ArrayList<Object>();
        list.add(o);
        return list;
    }



    // private void setLastModificationDate(Object o, User user)
    // {
    // if (o instanceof Item)
    // {
    // ImejiController.writeUpdateProperties(((Item)o), user);
    // }
    // else if (o instanceof Container)
    // {
    // ImejiController.writeUpdateProperties(((Container)o), user);
    // }
    // }
    //
    // private Calendar getLastModificationDate(Object o)
    // {
    // if (o instanceof Item)
    // {
    // return ((Item)o).getCreated();
    // }
    // else if (o instanceof Container)
    // {
    // return ((Container)o).getCreated();
    // }
    // return null;
    // }
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
