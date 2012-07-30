/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.controller;

import java.net.URI;
import java.util.Calendar;
import java.util.Collection;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.ImejiBean2RDF;
import de.mpg.imeji.logic.ImejiJena;
import de.mpg.imeji.logic.ImejiRDF2Bean;
import de.mpg.imeji.logic.concurrency.locks.Locks;
import de.mpg.imeji.logic.util.Counter;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.Properties;
import de.mpg.imeji.logic.vo.Properties.Status;
import de.mpg.imeji.logic.vo.User;
import de.mpg.j2j.exceptions.NotFoundException;
import de.mpg.j2j.helper.DateHelper;

public abstract class ImejiController
{
    private static Logger logger = Logger.getLogger(ImejiController.class);
    protected User user;

    // private static Model base = null;
    public ImejiController(User user2)
    {
        this.user = user2;
    }

    protected static void writeCreateProperties(Properties properties, User user)
    {
        //properties.setId(ObjectHelper.getURI(Properties.class, Integer.toString(getUniqueId())));
        Calendar now = DateHelper.getCurrentDate();
        properties.setCreatedBy(ObjectHelper.getURI(User.class, user.getEmail()));
        properties.setModifiedBy(ObjectHelper.getURI(User.class, user.getEmail()));
        properties.setCreated(now);
        properties.setModified(now);
        if (properties.getStatus() == null)
            properties.setStatus(Status.PENDING);
    }

    public static void writeUpdateProperties(Properties properties, User user)
    {
        properties.setModifiedBy(ObjectHelper.getURI(User.class, user.getEmail()));
        properties.setModified(DateHelper.getCurrentDate());
    }

    public boolean hasImageLocked(Collection<URI> containersUri, User user)
    {
        for (URI u : containersUri)
        {
            if (Locks.isLocked(u.toString(), user.getEmail()))
            {
                return true;
            }
        }
        return false;
    }

    public synchronized static int getUniqueId()
    {
        Counter c = new Counter();
        if (Locks.tryLockCounter())
        {
            try
            {
                ImejiRDF2Bean rdf2Bean = new ImejiRDF2Bean(null);
                c = (Counter)rdf2Bean.load(c.getId().toString(), ImejiJena.adminUser, c);
                int id = c.getCounter();
                logger.info("Counter : Requested id : " + id);
                incrementCounter(c);
                return id;
            }
            catch (NotFoundException e)
            {
                throw new RuntimeException("Fatal error: Counter not found. Please restart your server. ", e);
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
            finally
            {
                Locks.releaseCounter();
            }
        }
        throw new RuntimeException("Counter locked, couldn't create new id");
    }

    private synchronized static void incrementCounter(Counter c)
    {
        try
        {
            c.setCounter(c.getCounter() + 1 );
            ImejiBean2RDF bean2rdf = new ImejiBean2RDF(null);
            bean2rdf.update(bean2rdf.toList(c), ImejiJena.adminUser);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Fatal error: Counter not found. Please restart your server. ", e);
        }
    }

    // protected Model getModel()
    // {
    // try
    // {
    // String tdbPath = PropertyReader.getProperty("imeji.tdb.path");
    // base = DataFactory.model(tdbPath);
    // return base;
    // }
    // catch (IOException e)
    // {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // catch (URISyntaxException e)
    // {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // return null;
    // }
    //
    // protected void closeModel()
    // {
    // base.close();
    // }
    protected abstract String getSpecificQuery() throws Exception;

    protected abstract String getSpecificFilter() throws Exception;
}
