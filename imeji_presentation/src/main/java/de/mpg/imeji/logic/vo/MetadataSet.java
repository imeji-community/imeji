/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.vo;

import java.net.URI;
import java.util.Collection;
import java.util.LinkedList;

import de.mpg.j2j.annotations.j2jId;
import de.mpg.j2j.annotations.j2jLazyList;
import de.mpg.j2j.annotations.j2jList;
import de.mpg.j2j.annotations.j2jResource;

@j2jResource("http://imeji.org/terms/metadataSet")
@j2jId(getMethod = "getId", setMethod = "setId")
public class MetadataSet
{
    @j2jList("http://imeji.org/terms/metadata")
    private Collection<Metadata> metadata = new LinkedList<Metadata>();
    @j2jResource("http://imeji.org/terms/mdprofile")
    private URI profile;
    private URI id;

    public MetadataSet()
    {
    }

    public Collection<Metadata> getMetadata()
    {
        return metadata;
    }

    public void setMetadata(Collection<Metadata> metadata)
    {
        this.metadata = metadata;
    }

    public URI getProfile()
    {
        return profile;
    }

    public void setProfile(URI profile)
    {
        this.profile = profile;
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
