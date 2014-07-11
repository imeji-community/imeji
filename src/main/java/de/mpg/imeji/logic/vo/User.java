/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.vo;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.auth.util.AuthUtil;
import de.mpg.imeji.logic.controller.UserGroupController;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.presentation.util.ImejiFactory;
import de.mpg.j2j.annotations.j2jId;
import de.mpg.j2j.annotations.j2jList;
import de.mpg.j2j.annotations.j2jLiteral;
import de.mpg.j2j.annotations.j2jModel;
import de.mpg.j2j.annotations.j2jResource;

/**
 * imeji user
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
@j2jResource("http://imeji.org/terms/user")
@j2jModel("user")
@j2jId(getMethod = "getId", setMethod = "setId")
public class User implements Serializable
{
    private static final long serialVersionUID = -8961821901552709120L;
    @j2jLiteral("http://xmlns.com/foaf/0.1/name")
    private String name;
    @j2jLiteral("http://xmlns.com/foaf/0.1/nickname")
    private String nick;
    @j2jLiteral("http://xmlns.com/foaf/0.1/email")
    private String email;
    @j2jLiteral("http://xmlns.com/foaf/0.1/password")
    private String encryptedPassword;
    @j2jLiteral("http://xmlns.com/foaf/0.1/person")
    private Person person;
    @j2jList("http://imeji.org/terms/grant")
    private Collection<Grant> grants = new ArrayList<Grant>();
    private URI id;
    private List<UserGroup> groups = new ArrayList<>();

    /**
     * 
     */
    public User()
    {
        this.person = ImejiFactory.newPerson();
    }

    /**
     * Return a clone of this user, with a new email
     * 
     * @param email
     * @return
     */
    public User clone(String email)
    {
        User clone = new User();
        clone.setEmail(email);
        clone.encryptedPassword = encryptedPassword;
        clone.grants = new ArrayList<Grant>();
        for (Grant g : grants)
        {
            if (g.asGrantType() != null && g.getGrantFor() != null)
            {
                if (g.getGrantFor().toString().equals(this.getId().toString()))
                    clone.grants.add(new Grant(g.asGrantType(), clone.getId()));
                else
                    clone.grants.add(new Grant(g.asGrantType(), g.getGrantFor()));
            }
        }
        clone.name = name;
        clone.nick = nick;
        // Updates group references
        for (UserGroup group : groups)
        {
            UserGroupController c = new UserGroupController();
            group.getUsers().remove(this.getId());
            group.getUsers().add(clone.getId());
            try
            {
                c.update(group, Imeji.adminUser);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        clone.person = person;
        return clone;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
        this.id = ObjectHelper.getURI(User.class, this.email);
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
        this.getPerson().setCompleteName(name);
    }

    public String getNick()
    {
        return nick;
    }

    public void setNick(String nick)
    {
        this.nick = nick;
    }

    public void setEncryptedPassword(String encryptedPassword)
    {
        this.encryptedPassword = encryptedPassword;
    }

    public String getEncryptedPassword()
    {
        return encryptedPassword;
    }

    public void setGrants(Collection<Grant> grants)
    {
        this.grants = grants;
    }

    public Collection<Grant> getGrants()
    {
        return grants;
    }

    public Collection<Grant> getGrantsWithoutUser()
    {
        Collection<Grant> grantsWithoutUser = new ArrayList<Grant>();
        for (Grant g : grants)
        {
            if (!g.getGrantFor().getPath().contains("user"))
            {
                grantsWithoutUser.add(new Grant(g.asGrantType(), g.getGrantFor()));
            }
        }
        return grantsWithoutUser;
    }

    public void setId(URI id)
    {
        this.id = id;
    }

    public URI getId()
    {
        return id;
    }

    /**
     * @return the groups
     */
    public List<UserGroup> getGroups()
    {
        return groups;
    }

    /**
     * @param groups the groups to set
     */
    public void setGroups(List<UserGroup> groups)
    {
        this.groups = groups;
    }

    /**
     * True if the current user is the system administrator
     * 
     * @return
     */
    public boolean isAdmin()
    {
        return AuthUtil.isSysAdmin(this);
    }

    public void setAdmin(boolean b)
    {
        // dummy method for jsf
    }

    public boolean isAllowedToCreateCollection()
    {
        return AuthUtil.isAllowedToCreateCollection(this);
    }

    /**
     * @return the person
     */
    public Person getPerson()
    {
        return person;
    }

    /**
     * @param person the person to set
     */
    public void setPerson(Person person)
    {
        this.person = person;
    }
}
