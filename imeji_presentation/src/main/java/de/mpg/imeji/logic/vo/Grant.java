/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.vo;

import java.net.URI;

import de.mpg.imeji.logic.util.IdentifierUtil;
import de.mpg.j2j.annotations.j2jId;
import de.mpg.j2j.annotations.j2jResource;

/**
 * Grant of one {@link GrantType} for one {@link User} used for imeji authorization
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
@j2jResource("http://imeji.org/terms/grant")
@j2jId(getMethod = "getId", setMethod = "setId")
public class Grant
{
    /**
     * The types of possible {@link Grant} in imeji
     * 
     * @author saquet (initial creation)
     * @author $Author$ (last modification)
     * @version $Revision$ $LastChangedDate$
     */
    public enum GrantType
    {
        SYSADMIN, CONTAINER_ADMIN, CONTAINER_EDITOR, IMAGE_UPLOADER, IMAGE_EDITOR, VIEWER, PRIVILEGED_VIEWER, PROFILE_ADMIN, PROFILE_EDITOR, PROFILE_VIEWER;
    }

    @j2jResource("http://imeji.org/terms/grantType")
    private URI grantType;
    @j2jResource("http://imeji.org/terms/grantFor")
    private URI grantFor;
    private URI id;

    /**
     * Constructor: no ids is created with this constructor
     */
    public Grant()
    {
    }

    /**
     * Create a {@link Grant} of type {@link GrantType} for the object with the {@link URI} grantfor. Define the id
     * 
     * @param gt
     * @param gf
     */
    public Grant(GrantType gt, URI gf)
    {
        id = IdentifierUtil.newURI(Grant.class);
        if (gt == null || gf == null)
        {
            throw new NullPointerException("Impossible to created a grant with a null value! Granttype: " + gt
                    + " , and GrantFor: " + gf);
        }
        this.setGrantType(URI.create("http://imeji.org/terms/grantType#" + gt.name()));
        this.grantFor = gf;
    }

    public GrantType asGrantType()
    {
        return GrantType.valueOf(grantType.getFragment());
    }

    public void setGrantFor(URI grantFor)
    {
        this.grantFor = grantFor;
    }

    public URI getGrantFor()
    {
        return grantFor;
    }

    public void setId(URI id)
    {
        this.id = id;
    }

    public URI getId()
    {
        return id;
    }

    public void setGrantType(URI grantType)
    {
        this.grantType = grantType;
    }

    public URI getGrantType()
    {
        return grantType;
    }
}
