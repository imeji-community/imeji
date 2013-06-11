/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.controller;

import java.net.URI;
import java.util.Calendar;
import java.util.List;
import org.apache.log4j.Logger;
import de.mpg.imeji.logic.ImejiBean2RDF;
import de.mpg.imeji.logic.ImejiJena;
import de.mpg.imeji.logic.ImejiRDF2Bean;
import de.mpg.imeji.logic.concurrency.locks.Locks;
import de.mpg.imeji.logic.util.Counter;
import de.mpg.imeji.logic.util.IdentifierUtil;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.Album;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Container;
import de.mpg.imeji.logic.vo.Grant;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.Grant.GrantType;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Properties;
import de.mpg.imeji.logic.vo.Properties.Status;
import de.mpg.imeji.logic.vo.User;
import de.mpg.j2j.exceptions.NotFoundException;
import de.mpg.j2j.helper.DateHelper;
import de.mpg.j2j.helper.J2JHelper;

/**
 * Abstract class for the controller in imeji dealing with imeji VO: {@link Item} {@link CollectionImeji} {@link Album}
 * {@link User} {@link MetadataProfile}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public abstract class ImejiController
{
    private static Logger logger = Logger.getLogger(ImejiController.class);
    protected User user;

    /**
     * Default constructor for {@link ImejiController}
     */
    public ImejiController()
    {
    }

    /**
     * Constructor with a user
     * 
     * @param user2
     * @deprecated use rather ImejiController() as constructor. User should be passed as parameter in the methods
     */
    @Deprecated
    public ImejiController(User user2)
    {
        this.user = user2;
    }

    /**
     * Add the {@link Properties} to an imeji object when it is created
     * 
     * @param properties
     * @param user
     */
    protected void writeCreateProperties(Properties properties, User user)
    {
        J2JHelper.setId(properties, IdentifierUtil.newURI(properties.getClass()));
        Calendar now = DateHelper.getCurrentDate();
        properties.setCreatedBy(ObjectHelper.getURI(User.class, user.getEmail()));
        properties.setModifiedBy(ObjectHelper.getURI(User.class, user.getEmail()));
        properties.setCreated(now);
        properties.setModified(now);
        if (properties.getStatus() == null)
            properties.setStatus(Status.PENDING);
    }

    /**
     * Add the {@link Properties} to an imeji object when it is updated
     * 
     * @param properties
     * @param user
     */
    protected void writeUpdateProperties(Properties properties, User user)
    {
        properties.setModifiedBy(ObjectHelper.getURI(User.class, user.getEmail()));
        properties.setModified(DateHelper.getCurrentDate());
    }

    /**
     * Add the {@link Properties} to an imeji object when it is released
     * 
     * @param properties
     * @param user
     */
    protected void writeReleaseProperty(Properties properties, User user)
    {
        properties.setVersion(1);
        properties.setVersionDate(DateHelper.getCurrentDate());
        properties.setStatus(Status.RELEASED);
    }

    /**
     * Add the {@link Properties} to an imeji object when it is withdrawn
     * 
     * @param properties
     * @param comment
     */
    protected void writeWithdrawProperties(Properties properties, String comment)
    {
        if (comment != null && !"".equals(comment))
        {
            properties.setDiscardComment(comment);
        }
        if (properties.getDiscardComment() == null || "".equals(properties.getDiscardComment()))
        {
            throw new RuntimeException("Discard error: A Discard comment is needed");
        }
        properties.setStatus(Status.WITHDRAWN);
    }

    public User addCreatorGrant(URI id, User user) throws Exception
    {
        GrantController gc = new GrantController(user);
        Grant grant = new Grant(GrantType.CONTAINER_ADMIN, id);
        gc.addGrant(user, grant);
        UserController uc = new UserController(user);
        return uc.retrieve(user.getEmail());
    }

    /**
     * load items of a container. Perform a search to load all items: is faster than to read the complete container
     * 
     * @param c
     * @param user
     */
    public Container loadContainerItems(Container c, User user, int limit, int offset)
    {
        ItemController ic = new ItemController(user);
        List<String> newUris = ic.search(c.getId(), null, null, null).getResults();
        c.getImages().clear();
        for (String s : newUris)
        {
            c.getImages().add(URI.create(s));
        }
        return c;
    }

    /**
     * True if at least one {@link Item} is locked by another {@link User}
     * 
     * @param uris
     * @param user
     * @return
     */
    public boolean hasImageLocked(List<String> uris, User user)
    {
        for (String uri : uris)
        {
            if (Locks.isLocked(uri.toString(), user.getEmail()))
            {
                return true;
            }
        }
        return false;
    }

    // /**
    // * Create universal unique id (no counter involved)
    // *
    // * @return
    // */
    // public static String getUniqueId()
    // {
    // return UUID.randomUUID().toString();
    // }
    /**
     * Create a unique id for this instance based on a counter
     * 
     * @return
     */
    public synchronized static int getUniqueIdOld()
    {
        Counter c = new Counter();
        if (Locks.tryLockCounter())
        {
            try
            {
                ImejiRDF2Bean rdf2Bean = new ImejiRDF2Bean(ImejiJena.counterModel);
                c = (Counter)rdf2Bean.load(c.getId().toString(), ImejiJena.adminUser, c);
                int id = c.getCounter();
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
            c.setCounter(c.getCounter() + 1);
            ImejiBean2RDF bean2rdf = new ImejiBean2RDF(ImejiJena.counterModel);
            bean2rdf.update(bean2rdf.toList(c), ImejiJena.adminUser);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Fatal error: Counter not found. Please restart your server. ", e);
        }
    }
}
