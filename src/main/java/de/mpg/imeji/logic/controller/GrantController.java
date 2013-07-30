/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
    public GrantController(User user)
    {
        super(user);
    }

    /**
     * Add a {@link Grant} to a {@link User}
     * 
     * @param user
     * @param grant
     * @return
     * @throws Exception
     */
    public User addGrant(User user, Grant grant) throws Exception
    {
        if (!isValid(grant))
        {
            throw new RuntimeException("Grant: " + grant.getGrantType() + " for " + grant.getGrantFor() + " not valid");
        }
        if (!hasGrant(user, grant))
        {
            user.getGrants().add(grant);
            saveUser(user);
        }
        else
            throw new RuntimeException("User " + user.getEmail() + " is already " + grant.getGrantType() + " for "
                    + grant.getGrantFor());
        return user;
    }

    /**
     * Replace the grant of one user for one object with the new Grant. It means only one grant for one object pro user.
     * 
     * @param user
     * @param grant
     * @throws Exception
     */
    public User updateGrant(User user, Grant grant) throws Exception
    {
        if (!isValid(grant))
        {
            throw new RuntimeException("Grant: " + grant.getGrantType() + " for " + grant.getGrantFor() + " not valid");
        }
        Collection<Grant> newGrants = new ArrayList<Grant>();
        newGrants.add(grant);
        for (Grant g : user.getGrants())
        {
            if (!g.getGrantFor().equals(grant.getGrantFor()))
            {
                newGrants.add(g);
            }
        }
        user.setGrants(newGrants);
        saveUser(user);
        return user;
    }

    public User removeGrant(User user, Grant grant) throws Exception
    {
        if (hasGrant(user, grant))
        {
            user.getGrants().remove(grant);
            saveUser(user);
        }
        return user;
    }

    public User removeAllGrantsFor(User user, URI uri) throws Exception
    {
        for (int i = 0; i < user.getGrants().size(); i++)
        {
            if (uri != null && uri.equals(((List<Grant>)user.getGrants()).get(i).getGrantFor()))
            {
                ((List<Grant>)user.getGrants()).remove(i);
            }
        }
        saveUser(user);
        return user;
    }

    public boolean hasGrant(User user, Grant grant)
    {
        for (Grant g : user.getGrants())
        {
            if (compare(g, grant))
            {
                return true;
            }
        }
        return false;
    }

    public boolean compare(Grant grant1, Grant grant2)
    {
        if (isValid(grant1) && isValid(grant2))
        {
            return (grant1.getGrantFor().equals(grant2.getGrantFor()) && grant1.getGrantType().equals(
                    grant2.getGrantType()));
        }
        return false;
    }

    public boolean isValid(Grant grant)
    {
        return (grant != null && grant.getGrantFor() != null && grant.asGrantType() != null);
    }

    private void saveUser(User user) throws Exception
    {
        UserController uc = new UserController(user);
        uc.update(user);
    }
}
