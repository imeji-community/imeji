/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.vo;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

import de.mpg.imeji.logic.util.ObjectHelper;
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
public class User
{
    @j2jLiteral("http://xmlns.com/foaf/0.1/name")
    private String name;
    @j2jLiteral("http://xmlns.com/foaf/0.1/nickname")
    private String nick;
    @j2jLiteral("http://xmlns.com/foaf/0.1/email")
    private String email;
    @j2jLiteral("http://xmlns.com/foaf/0.1/password")
    private String encryptedPassword;
    @j2jList("http://imeji.org/terms/grant")
    private Collection<Grant> grants = new ArrayList<Grant>();
    private URI id;

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

    public void setId(URI id)
    {
        this.id = id;
    }

    public URI getId()
    {
        return id;
    }
}
