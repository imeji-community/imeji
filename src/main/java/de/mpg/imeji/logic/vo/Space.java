/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.vo;

import de.mpg.j2j.annotations.j2jId;
import de.mpg.j2j.annotations.j2jLiteral;
import de.mpg.j2j.annotations.j2jModel;
import de.mpg.j2j.annotations.j2jResource;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

/**
 * imeji space
 * 
 * @author vmakarenko (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
@j2jResource("http://imeji.org/terms/space")
@j2jModel("space")
@j2jId(getMethod = "getId", setMethod = "setId")
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "space", namespace = "http://imeji.org/terms/")
public class Space extends Properties implements Serializable {

	private static final long serialVersionUID = 3531439142807036969L;

	@j2jLiteral("http://purl.org/dc/elements/1.1/title")
	private String title;
	@j2jLiteral("http://purl.org/dc/elements/1.1/description")
	private String description;
	@j2jResource("http://imeji.org/terms/visibility")
	private URI visibility = URI.create("http://imeji.org/terms/visibility#"
			+ Item.Visibility.PRIVATE.name());
	@j2jResource("http://imeji.org/terms/logoUrl")
	private URI logoUrl;
	@j2jResource("http://imeji.org/terms/slug")
	private URI slug;
	@j2jLiteral("http://imeji.org/terms/maxHeight")
	private int maxHeight = 0;
	@j2jLiteral("http://imeji.org/terms/maxWidth")
	private int maxWidth = 0;

//	@j2jList("http://imeji.org/terms/collection")
	private Collection<String> spaceCollections = new ArrayList<String>();


	@XmlElement(name = "logoUrl", namespace = "http://imeji.org/terms/")
	public URI getLogoUrl() {
		return logoUrl;
	}

	public void setLogoUrl(URI logoUrl) {
		this.logoUrl = logoUrl;
	}

	@XmlElement(name = "slug", namespace = "http://imeji.org/terms/")
	public URI getSlug() {
		return slug;
	}

	public void setSlug(URI slug) {
		this.slug = slug;
	}

	@XmlElement(name = "title", namespace = "http://imeji.org/terms/")
	public String getTitle() { return title; }

	public void setTitle(String title)
	{
		this.title = title;
	}

	@XmlElement(name = "description", namespace = "http://imeji.org/terms/")
	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public void setVisibility(Item.Visibility visibility) {
		this.visibility = URI.create("http://imeji.org/terms/visibility#"
				+ visibility.name());
	}

	@XmlElement(name = "visibility", namespace = "http://imeji.org/terms/")
	public Item.Visibility getVisibility() {
		return Item.Visibility.valueOf(visibility.getFragment());
	}


	@XmlElement(name = "maxHeight", namespace = "http://imeji.org/terms/")
	public int getMaxHeight() {
		return maxHeight;
	}

	public void setMaxHeight(int maxHeight) {
		this.maxHeight = maxHeight;
	}

	@XmlElement(name = "maxWidth", namespace = "http://imeji.org/terms/")
	public int getMaxWidth() {
		return maxWidth;
	}

	public void setMaxWidth(int maxWidth) {
		this.maxWidth = maxWidth;
	}


	public Collection<String> getSpaceCollections() {
		return spaceCollections;
	}

	public String addSpaceCollection(String id) {
		if (!this.spaceCollections.contains(id))
			this.spaceCollections.add(id);
		return id;
	}


	public void removeSpaceCollection(String id) {
		this.spaceCollections.remove(id);
	}


	public void setSpaceCollections(Collection<String> spaceCollections) {
		this.spaceCollections = spaceCollections;
	}

}
