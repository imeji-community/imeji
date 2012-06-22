/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.vo;

import java.net.URI;

import de.mpg.j2j.annotations.j2jId;
import de.mpg.j2j.annotations.j2jModel;
import de.mpg.j2j.annotations.j2jResource;

@j2jResource("http://imeji.org/terms/collection")
@j2jModel("collection")
@j2jId(getMethod = "getId", setMethod = "setId")
public class CollectionImeji extends Container
{
    @j2jResource("http://imeji.org/terms/mdprofile")
    private URI profile = null;
    @j2jResource("http://imeji.org/terms/metadataSet")
    private MetadataSet metadataSet = new MetadataSet();

    public URI getProfile()
    {
        return profile;
    }

    public void setProfile(URI profile)
    {
        this.profile = profile;
    }

    public MetadataSet getMetadataSet()
    {
        return metadataSet;
    }

    public void setMetadataSet(MetadataSet metadataSet)
    {
        this.metadataSet = metadataSet;
    }
}
