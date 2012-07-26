/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.query.Dataset;

import de.mpg.imeji.logic.security.Operations.OperationsType;
import de.mpg.imeji.logic.security.Security;
import de.mpg.imeji.logic.vo.Container;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.User;
import de.mpg.j2j.transaction.CRUDTransaction;
import de.mpg.j2j.transaction.Transaction;

/**
 * Interface for create/delete/update in Jena. Implements transactions and security.
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

    private void runTransaction(List<Object> objects, OperationsType type) throws Exception
    {
        Transaction transaction = new CRUDTransaction(objects, type, modelURI, false);
        transaction.start();
        transaction.waitForEnd();
        transaction.throwException();
    }

    public void create(List<Object> objects, User user) throws Exception
    {
        checkSecurity(objects, user, OperationsType.CREATE);
        runTransaction(objects, OperationsType.CREATE);
    }

    public void delete(List<Object> objects, User user) throws Exception
    {
        checkSecurity(objects, user, OperationsType.DELETE);
        runTransaction(objects, OperationsType.DELETE);
    }

    public void update(List<Object> objects, User user) throws Exception
    {
        checkSecurity(objects, user, OperationsType.UPDATE);
        runTransaction(objects, OperationsType.UPDATE);
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
    // ImejiController.writeUpdateProperties(((Item)o).getProperties(), user);
    // }
    // else if (o instanceof Container)
    // {
    // ImejiController.writeUpdateProperties(((Container)o).getProperties(), user);
    // }
    // }
    //
    // private Calendar getLastModificationDate(Object o)
    // {
    // if (o instanceof Item)
    // {
    // return ((Item)o).getProperties().getCreated();
    // }
    // else if (o instanceof Container)
    // {
    // return ((Container)o).getProperties().getCreated();
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
