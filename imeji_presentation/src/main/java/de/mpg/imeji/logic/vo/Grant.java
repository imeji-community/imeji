/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.vo;

import java.net.URI;
import java.util.UUID;

import de.mpg.j2j.annotations.j2jId;
import de.mpg.j2j.annotations.j2jLiteral;
import de.mpg.j2j.annotations.j2jResource;

@j2jResource("http://imeji.org/terms/grant")
@j2jId(getMethod = "getId", setMethod = "setId")
public class Grant
{
    public enum GrantType
    {
        SYSADMIN, CONTAINER_ADMIN, CONTAINER_EDITOR, IMAGE_UPLOADER, IMAGE_EDITOR, PRIVILEGED_VIEWER, PROFILE_ADMIN, PROFILE_EDITOR, PROFILE_VIEWER;
    }

    // @j2jLiteral("http://imeji.org/terms/grantType")
    // private GrantType grantType;
    @j2jResource("http://imeji.org/terms/grantType")
    private URI grantType;
    @j2jResource("http://imeji.org/terms/grantFor")
    private URI grantFor;
    private URI id = URI.create("http://imeji.org/grant/" + UUID.randomUUID());

    public Grant()
    {
    }

    public Grant(GrantType gt, URI gf)
    {
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
