/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.vo;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

import de.mpg.j2j.annotations.j2jId;
import de.mpg.j2j.annotations.j2jList;
import de.mpg.j2j.annotations.j2jModel;
import de.mpg.j2j.annotations.j2jResource;

/**
 * imeji collection has one {@link MetadataProfile} and contains {@link Item}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
@j2jResource("http://imeji.org/terms/collection")
@j2jModel("collection")
@j2jId(getMethod = "getId", setMethod = "setId")
public class CollectionImeji extends Container
{
    @j2jResource("http://imeji.org/terms/mdprofile")
    private URI profile = null;
    @j2jResource("http://imeji.org/terms/metadataSet")
    private MetadataSet metadataSet = new MetadataSet();
    /**
     * Item list is not defined as a {@link j2jList}, i.e. there is no triple like "collection has item", but only
     * "item is in collection". This avoid performance issue when the collection is growing.
     */
    private Collection<URI> images = new ArrayList<URI>();

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

    @Override
    public void setImages(Collection<URI> images)
    {
        this.images = images;
    }

    @Override
    public Collection<URI> getImages()
    {
        return images;
    }
}
