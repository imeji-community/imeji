/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.vo;

import java.net.URI;
import java.util.Calendar;

import de.mpg.j2j.annotations.j2jId;
import de.mpg.j2j.annotations.j2jLiteral;
import de.mpg.j2j.annotations.j2jResource;

@j2jResource("http://imeji.org/terms/properties")
//@j2jId(getMethod = "getId", setMethod = "setId")
public class Properties 
{
    private URI id;
    @j2jResource("http://purl.org/dc/terms/creator")
    private URI createdBy;
    @j2jResource("http://imeji.org/terms/modifiedBy")
    private URI modifiedBy;
    @j2jLiteral("http://purl.org/dc/terms/created")
    private Calendar created;
    @j2jLiteral("http://purl.org/dc/terms/modified")
    private Calendar modified;
    @j2jLiteral("http://purl.org/dc/terms/issued")
    private Calendar versionDate;
    // @j2jLiteral("http://imeji.org/terms/status")
    // private Status status = Status.PENDING;
    @j2jResource("http://imeji.org/terms/status")
    private URI status = URI.create("http://imeji.org/terms/status#" + Status.PENDING.name());
    @j2jLiteral("http://imeji.org/terms/versionNumber")
    private int version = 0;
    @j2jLiteral("http://imeji.org/terms/discardComment")
    private String discardComment;

    public enum Status
    {
        PENDING, RELEASED, WITHDRAWN;
    }

    public Properties()
    {
        // TODO Auto-generated constructor stub
    }

    public void setCreatedBy(URI createdBy)
    {
        this.createdBy = createdBy;
    }

    public URI getCreatedBy()
    {
        return createdBy;
    }

    public void setModifiedBy(URI modifiedBy)
    {
        this.modifiedBy = modifiedBy;
    }

    public URI getModifiedBy()
    {
        return modifiedBy;
    }

    public void setStatus(Status status)
    {
        this.status = URI.create("http://imeji.org/status#" + status.name());
    }

    public Status getStatus()
    {
        return Status.valueOf(status.getFragment());
    }

    public void setVersion(int version)
    {
        this.version = version;
    }

    public int getVersion()
    {
        return version;
    }

    public String getDiscardComment()
    {
        return discardComment;
    }

    public void setDiscardComment(String discardComment)
    {
        this.discardComment = discardComment;
    }

    public void setId(URI id)
    {
        this.id = id;
    }

    public URI getId()
    {
        return id;
    }

    public Calendar getCreated()
    {
        return created;
    }

    public void setCreated(Calendar created)
    {
        this.created = created;
    }

    public Calendar getModified()
    {
        return modified;
    }

    public void setModified(Calendar modified)
    {
        this.modified = modified;
    }

    public Calendar getVersionDate()
    {
        return versionDate;
    }

    public void setVersionDate(Calendar versionDate)
    {
        this.versionDate = versionDate;
    }
}
