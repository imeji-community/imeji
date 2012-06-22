/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic;

import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.query.ReadWrite;

import de.mpg.imeji.logic.concurrency.locks.Locks;
import de.mpg.imeji.logic.controller.ImejiController;
import de.mpg.imeji.logic.security.Operations.OperationsType;
import de.mpg.imeji.logic.security.Security;
import de.mpg.imeji.logic.vo.Container;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.User;
import de.mpg.j2j.controler.ResourceController;

/**
 * Interface for create/delete/update in Jena. Supports transactions, locking and security.
 * 
 * @author saquet
 */
public class ImejiBean2RDF
{
    private static ResourceController rc;
    private Security security;
    private static Logger logger = Logger.getLogger(ImejiBean2RDF.class);
    private String modelURI;

    public ImejiBean2RDF(String modelURI)
    {
        security = new Security();
        this.modelURI = modelURI;
    }

    public void create(List<Object> objects, User user) throws Exception
    {
        for (Object o : objects)
        {
            if (Locks.tryLock())
            {
                try
                {
                    beginTransaction(o, user, OperationsType.CREATE);
                    rc.create(o);
                    commitTransaction(o, user);
                }
                catch (Exception e)
                {
                    abortTransaction();
                    throw new RuntimeException("Fatal error! Transaction aborted", e);
                }
            }
        }
    }

    public void delete(List<Object> objects, User user) throws Exception
    {
        for (Object o : objects)
        {
            if (Locks.tryLock())
            {
                try
                {
                    beginTransaction(o, user, OperationsType.DELETE);
                    rc.delete(o);
                    commitTransaction(o, user);
                }
                catch (Exception e)
                {
                    abortTransaction();
                    throw new RuntimeException("Fatal error! Transaction aborted", e);
                }
            }
        }
    }

    public void update(List<Object> objects, User user) throws Exception
    {
        for (Object o : objects)
        {
            if (Locks.tryLock())
            {
                try
                {
                    beginTransaction(o, user, OperationsType.UPDATE);
                    rc.update(o);
                    commitTransaction(o, user);
                }
                catch (Exception e)
                {
                    abortTransaction();
                    throw new RuntimeException("Fatal error! Transaction aborted", e);
                }
            }
        }
    }

    /**
     * Begin an imeji transaction: <br/>
     * Check Security. <br/>
     * Check lockings (optimistic and pessimistic) <br/>
     * Lock bean
     * 
     * @param bean
     * @param user
     * @throws Exception
     */
    private void beginTransaction(Object bean, User user, OperationsType opType) throws Exception
    {
        ImejiJena.imejiDataSet.begin(ReadWrite.WRITE);
        try
        {
            checkSecurity(bean, user, opType);
            rc = new ResourceController(modelURI);
        }
        catch (Exception e)
        {
            abortTransaction();
            logger.error(e);
            throw e;
        }
    }

    private void abortTransaction()
    {
        ImejiJena.imejiDataSet.abort();
        Locks.releaseLockForWrite();
    }

    private void commitTransaction(Object bean, User user)
    {
        ImejiJena.imejiDataSet.commit();
        Locks.releaseLockForWrite();
    }

    private void checkSecurity(Object bean, User user, OperationsType opType)
    {
        if (!security.check(opType, user, bean))
        {
            throw new RuntimeException("Imeji Security exception: " + user.getEmail() + " not allowed to "
                    + opType.name() + " " + extractID(bean));
        }
    }

    public List<Object> toList(Object o)
    {
        List<Object> list = new ArrayList<Object>();
        list.add(o);
        return list;
    }

    private void setLastModificationDate(Object o, User user)
    {
        if (o instanceof Item)
        {
            ImejiController.writeUpdateProperties(((Item)o).getProperties(), user);
        }
        else if (o instanceof Container)
        {
            ImejiController.writeUpdateProperties(((Container)o).getProperties(), user);
        }
    }

    private Calendar getLastModificationDate(Object o)
    {
        if (o instanceof Item)
        {
            return ((Item)o).getProperties().getCreated();
        }
        else if (o instanceof Container)
        {
            return ((Container)o).getProperties().getCreated();
        }
        return null;
    }

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
