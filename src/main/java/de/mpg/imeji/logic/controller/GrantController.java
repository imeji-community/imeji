/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.controller;

import java.util.ArrayList;
import java.util.List;

import de.mpg.imeji.logic.ImejiSPARQL;
import de.mpg.imeji.logic.search.query.SPARQLQueries;
import de.mpg.imeji.logic.vo.Grant;
import de.mpg.imeji.logic.vo.User;

/**
 * Controller for {@link Grant}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class GrantController extends ImejiController
{
    /**
     * Add to the {@link User} the {@link List} of {@link Grant} and update the user in the database
     * 
     * @param user
     * @param g
     * @throws Exception
     */
    public void addGrants(User user, List<Grant> g, User currentUser) throws Exception
    {
        user.getGrants().addAll(getNewGrants(currentUser, g));
        UserController c = new UserController(currentUser);
        c.update(user, currentUser);
    }

    /**
     * Remove {@link List} of {@link Grant} from the {@link User} {@link Grant}
     * 
     * @param user
     * @param g
     * @param grantFor
     * @param currentUser
     * @throws Exception
     */
    public void removeGrants(User user, List<Grant> toRemove, User currentUser) throws Exception
    {
        List<Grant> notRemovedGrants = new ArrayList<>();
        for (Grant g : user.getGrants())
        {
            if (!toRemove.contains(g))
                notRemovedGrants.add(g);
        }
        user.setGrants(notRemovedGrants);
        UserController c = new UserController(currentUser);
        c.update(user, currentUser);
    }

    /**
     * Return the {@link Grant} which are new for the {@link User}
     * 
     * @param user
     * @param l
     * @return
     */
    public List<Grant> getNewGrants(User user, List<Grant> l)
    {
        List<Grant> newGrants = new ArrayList<>();
        for (Grant g : l)
        {
            if (!user.getGrants().contains(g))
                newGrants.add(g);
        }
        return newGrants;
    }

    /**
     * Remove all {@link Grant} in the database (i.e for all {@link User})
     * 
     * @param uri
     * @throws Exception
     */
    public void removeAllGrants(String uri)
    {
        ImejiSPARQL.execUpdate(SPARQLQueries.updateRemoveGrantsFor(uri));
    }
}
