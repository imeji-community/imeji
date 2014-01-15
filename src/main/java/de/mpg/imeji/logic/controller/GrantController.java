/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.controller;

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
        user.getGrants().addAll(g);
        UserController c = new UserController(currentUser);
        c.update(user, currentUser);
    }

    /**
     * Remove all {@link Grant} in the database (i.e for all {@link User})
     * 
     * @param uri
     * @throws Exception
     */
    public void removeGrants(String uri)
    {
        ImejiSPARQL.execUpdate(SPARQLQueries.updateRemoveGrantsFor(uri));
    }
}
