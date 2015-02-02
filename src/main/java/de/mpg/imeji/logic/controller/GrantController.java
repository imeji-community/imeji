/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import de.escidoc.core.resources.aa.useraccount.Grants;
import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.ImejiSPARQL;
import de.mpg.imeji.logic.search.query.SPARQLQueries;
import de.mpg.imeji.logic.vo.Grant;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.logic.vo.UserGroup;
import de.mpg.imeji.logic.writer.WriterFacade;

/**
 * Controller for {@link Grant}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class GrantController extends ImejiController
{
    private static final WriterFacade writer = new WriterFacade(Imeji.userModel);
    private static Logger logger = Logger.getLogger(GrantController.class);

    /**
     * Add to the {@link User} the {@link List} of {@link Grant} and update the user in the database
     * 
     * @param user
     * @param g
     * @throws ImejiException
     */
    public void addGrants(User user, List<Grant> g, User currentUser) throws ImejiException
    {
        user.getGrants().addAll(getNewGrants(user.getGrants(), g));
        UserController c = new UserController(currentUser);
        c.update(user, currentUser);
    }

    /**
     * Add to the {@link UserGroup} the {@link List} of {@link Grant} and update the user in the database
     * 
     * @param user
     * @param g
     * @throws ImejiException
     */
    public void addGrants(UserGroup group, List<Grant> g, User currentUser) throws ImejiException
    {
        group.getGrants().addAll(getNewGrants(group.getGrants(), g));
        UserGroupController c = new UserGroupController();
        c.update(group, currentUser);
    }

    /**
     * Remove {@link List} of {@link Grant} from the {@link User} {@link Grant}
     * 
     * @param user
     * @param g
     * @param grantFor
     * @param currentUser
     * @throws ImejiException
     */
    public void removeGrants(User user, List<Grant> toRemove, User currentUser)
    {
        user.setGrants(getNotRemovedGrants(user.getGrants(), toRemove));
        UserController c = new UserController(currentUser);
        try
        {
            c.update(user, currentUser);
            writer.delete(new ArrayList<Object>(toRemove), currentUser);
        }
        catch (Exception e)
        {
            logger.error(e);
        }
    }

    /**
     * Remove {@link List} of {@link Grant} from the {@link User} {@link Grant}
     * 
     * @param user
     * @param g
     * @param grantFor
     * @param currentUser
     * @throws ImejiException
     */
    public void removeGrants(UserGroup group, List<Grant> toRemove, User currentUser)
    {
        group.setGrants(getNotRemovedGrants(group.getGrants(), toRemove));
        UserGroupController c = new UserGroupController();
        try
        {
            c.update(group, currentUser);
            writer.delete(new ArrayList<Object>(toRemove), currentUser);
        }
        catch (Exception e)
        {
            logger.error(e);
        }
    }

    /**
     * Return the {@link List} of {@link Grants} which are not to be removed
     * 
     * @param current
     * @param toRemove
     * @return
     */
    public List<Grant> getNotRemovedGrants(Collection<Grant> current, List<Grant> toRemove)
    {
        List<Grant> notRemovedGrants = new ArrayList<>();
        for (Grant g : current)
        {
            if (!toRemove.contains(g))
                notRemovedGrants.add(g);
        }
        return notRemovedGrants;
    }

    /**
     * Return the {@link Grant} which are new for the {@link User}
     * 
     * @param user
     * @param toAdd
     * @return
     */
    public List<Grant> getNewGrants(Collection<Grant> current, List<Grant> toAdd)
    {
        List<Grant> newGrants = new ArrayList<>();
        for (Grant g : toAdd)
        {
            if (!current.contains(g) && !newGrants.contains(g))
                newGrants.add(g);
        }
        return newGrants;
    }

    /**
     * Remove all {@link Grant} in the database (i.e for all {@link User})
     * 
     * @param uri
     * @throws ImejiException
     */
    public void removeAllGrants(String uri)
    {
        ImejiSPARQL.execUpdate(SPARQLQueries.updateRemoveGrantsFor(uri));
    }
}
