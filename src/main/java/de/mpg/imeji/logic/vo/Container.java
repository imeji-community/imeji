/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.vo;

import java.io.Serializable;
import java.net.URI;
import java.util.Collection;

import javax.xml.bind.annotation.XmlElement;

import de.mpg.imeji.logic.search.FulltextIndex;
import de.mpg.j2j.annotations.j2jId;
import de.mpg.j2j.annotations.j2jLiteral;
import de.mpg.j2j.annotations.j2jResource;

/**
 * Super class for all imeji containers ({@link CollectionImeji} and {@link Album})
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
@j2jResource("http://imeji.org/terms/container")
@j2jId(getMethod = "getId", setMethod = "setId")
public abstract class Container extends Properties implements FulltextIndex, Serializable
{
    private static final long serialVersionUID = -5314334939747696614L;
    @j2jResource("http://imeji.org/terms/container/metadata")
    private ContainerMetadata metadata = new ContainerMetadata();
    @j2jLiteral("http://imeji.org/terms/fulltext")
    private String fulltext;

    //same property used as in Space
    //property is provided on Container level, in order to fit both Collection and Album
	@j2jResource("http://imeji.org/terms/logoUrl")
	private URI logoUrl;


    public void setMetadata(ContainerMetadata metadata)
    {
        this.metadata = metadata;
    }

    public ContainerMetadata getMetadata()
    {
        return metadata;
    }

    public abstract void setImages(Collection<URI> images);

    public abstract Collection<URI> getImages();

    @Override
    public void setFulltextIndex(String fulltext)
    {
        this.fulltext = fulltext;
    }

    @Override
    public String getFulltextIndex()
    {
        return fulltext;
    }

    @Override
    public void indexFulltext()
    {
        fulltext = metadata.getTitle();
        if (!"".equals(metadata.getDescription()))
        {
            fulltext += " " + metadata.getDescription();
        }
        for (Person p : metadata.getPersons())
        {
            fulltext += " " + p.AsFullText();
        }
        fulltext = fulltext.trim();
    }
    
	@XmlElement(name = "logoUrl", namespace = "http://imeji.org/terms/")
	public URI getLogoUrl() {
		return this.logoUrl;
	}

	public void setLogoUrl(URI logoUrl) {
		this.logoUrl = logoUrl;
	}
}
