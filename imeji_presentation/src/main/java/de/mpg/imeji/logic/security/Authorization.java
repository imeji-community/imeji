/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.security;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.sparql.pfunction.library.container;

import de.mpg.imeji.logic.vo.Container;
import de.mpg.imeji.logic.vo.Grant;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.logic.vo.Grant.GrantType;

/**
 * imeji Authorization. Based on imeji Grants: <br/>
 * SYSADMIN, CONTAINER_ADMIN, CONTAINER_EDITOR, IMAGE_UPLOADER, IMAGE_EDITOR, PRIVILEGED_VIEWER <br/>
 * See: http://colab.mpdl.mpg.de/mediawiki/Imeji_User_Management
 * 
 * @author saquet
 */
public class Authorization
{
    /**
     * Generic Authorization
     * 
     * @param gt
     * @param user
     * @param uri
     * @return
     */
    public boolean is(GrantType gt, User user, URI uri)
    {
        for (Grant g : getGrantsForURI(user, uri))
        {
            if (gt.equals(g.asGrantType()))
                return true;
        }
        return false;
    }

    /**
     * True if {@link User} has system admin role
     * 
     * @param user
     * @return
     */
    public boolean isSysAdmin(User user)
    {
        if (user == null)
            return false;
        for (Grant g : user.getGrants())
        {
            if (GrantType.SYSADMIN.equals(g.asGrantType()))
                return true;
        }
        return false;
    }

    /**
     * True if {@link User} has admin role for this {@link container}
     * 
     * @param user
     * @param container
     * @return
     */
    public boolean isContainerAdmin(User user, Container container)
    {
        for (Grant g : getGrantsForObject(user, container))
        {
            if (GrantType.CONTAINER_ADMIN.equals(g.asGrantType()))
                return true;
        }
        return isSysAdmin(user);
    }

    /**
     * True if {@link User} has admin role for the {@link container} in which the {@link Item} is contained
     * 
     * @param user
     * @param item
     * @return
     */
    public boolean isContainerAdmin(User user, Item item)
    {
        for (Grant g : getGrantsForURI(user, item.getCollection()))
        {
            if (GrantType.CONTAINER_ADMIN.equals(g.asGrantType()))
                return true;
        }
        return isSysAdmin(user);
    }

    /**
     * True if {@link User} has Editor role for this {@link container}
     * 
     * @param user
     * @param container
     * @return
     */
    public boolean isContainerEditor(User user, Container container)
    {
        for (Grant g : getGrantsForObject(user, container))
        {
            if (GrantType.CONTAINER_EDITOR.equals(g.asGrantType()))
                return true;
        }
        return isSysAdmin(user);
    }

    /**
     * True if {@link User} has editor role for the {@link container} in which the {@link Item} is contained
     * 
     * @param user
     * @param item
     * @return
     */
    public boolean isContainerEditor(User user, Item item)
    {
        for (Grant g : getGrantsForURI(user, item.getCollection()))
        {
            if (GrantType.CONTAINER_EDITOR.equals(g.asGrantType()))
                return true;
        }
        return isSysAdmin(user);
    }

    /**
     * True if {@link User} has picture editor role for this {@link container}
     * 
     * @param user
     * @param container
     * @return
     */
    public boolean isPictureEditor(User user, Container container)
    {
        for (Grant g : getGrantsForObject(user, container))
        {
            if (GrantType.IMAGE_EDITOR.equals(g.asGrantType()))
                return true;
        }
        return isSysAdmin(user);
    }

    /**
     * True if {@link User} has picture editor role for the {@link container} in which the {@link Item} is contained
     * 
     * @param user
     * @param item
     * @return
     */
    public boolean isPictureEditor(User user, Item item)
    {
        for (Grant g : getGrantsForURI(user, item.getCollection()))
        {
            if (GrantType.IMAGE_EDITOR.equals(g.asGrantType()))
                return true;
        }
        return isSysAdmin(user);
    }

    /**
     * True if {@link User} has viewer role for this {@link container}
     * 
     * @param user
     * @param container
     * @return
     */
    public boolean isViewerFor(User user, Container container)
    {
        for (Grant g : getGrantsForObject(user, container))
        {
            if (GrantType.PRIVILEGED_VIEWER.equals(g.asGrantType()))
                return true;
        }
        return isSysAdmin(user);
    }

    /**
     * True if {@link User} has viewer role for the {@link container} in which the {@link Item} is contained
     * 
     * @param user
     * @param item
     * @return
     */
    public boolean isViewerFor(User user, Item item)
    {
        for (Grant g : getGrantsForURI(user, item.getCollection()))
        {
            if (GrantType.PRIVILEGED_VIEWER.equals(g.asGrantType()))
                return true;
        }
        return isSysAdmin(user);
    }

    /**
     * True if {@link User} has admin role for this {@link container}
     * 
     * @param user
     * @param container
     * @return
     */
    public boolean isUploaderFor(User user, Container container)
    {
        for (Grant g : getGrantsForObject(user, container))
        {
            if (GrantType.IMAGE_UPLOADER.equals(g.asGrantType()))
                return true;
        }
        return isSysAdmin(user);
    }

    /**
     * Get all grants of one {@link User} for one specific {@link container}
     * 
     * @param user
     * @param container
     * @return
     */
    private List<Grant> getGrantsForObject(User user, Container container)
    {
        if (user == null || container == null)
            return new ArrayList<Grant>();
        return getGrantsForURI(user, container.getId());
    }

    /**
     * Get all grants of one {@link User} for one specific object defined by its uri
     * 
     * @param user
     * @param uri
     * @return
     */
    private List<Grant> getGrantsForURI(User user, URI uri)
    {
        List<Grant> grants = new ArrayList<Grant>();
        if (user == null)
            return grants;
        for (Grant g : user.getGrants())
        {
            if (g.asGrantType().equals(GrantType.SYSADMIN) || g.getGrantFor().equals(uri))
            {
                grants.add(g);
            }
        }
        return grants;
    }
}
