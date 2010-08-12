package de.mpg.escidoc.faces.metastore.vo;

import java.net.URI;
import java.util.Collection;
import java.util.LinkedList;

import thewebsemantic.Id;
import thewebsemantic.Namespace;
import thewebsemantic.RdfProperty;
import thewebsemantic.RdfType;


@Namespace("http://imeji.mpdl.mpg.de/")
@RdfType("image")
public class Image{
	
	@Namespace("http://imeji.mpdl.mpg.de/image/")
	@RdfType("visibility")
	public enum Visibility
	{
		PUBLIC, PRIVATE;
	}
	
	
	
	
	private URI id;
	
	
	private Properties properties = new Properties();
	
	private URI webImageUrl;

	private URI thumbnailImageUrl;
	
	private URI fullImageUrl;
	
	private Visibility visibility;
	
	private URI collection;
	
	
	private Collection<ImageMetadata> metadata = new LinkedList<ImageMetadata>();
	
	

	public URI getWebImageUrl() {
		return webImageUrl;
	}


	public void setWebImageUrl(URI webImageUrl) {
		this.webImageUrl = webImageUrl;
	}


	public URI getThumbnailImageUrl() {
		return thumbnailImageUrl;
	}


	public void setThumbnailImageUrl(URI thumbnailImageUrl) {
		this.thumbnailImageUrl = thumbnailImageUrl;
	}


	public URI getFullImageUrl() {
		return fullImageUrl;
	}


	public void setFullImageUrl(URI fullImageUrl) {
		this.fullImageUrl = fullImageUrl;
	}
	
	

	/*
	public void setProperties(ImejiProperties properties) {
		this.properties = properties;
	}

	public ImejiProperties getProperties() {
		return properties;
	}
	 */
	
	


	public void setVisibility(Visibility visibility) {
		this.visibility = visibility;
	}


	public Visibility getVisibility() {
		return visibility;
	}


	public void setMetadata(Collection<ImageMetadata> metadata) {
		this.metadata = metadata;
	}


	@RdfProperty("http://imeji.mpdl.mpg.de/image/metadata")
	public Collection<ImageMetadata> getMetadata() {
		return metadata;
	}


	public void setId(URI id) {
		this.id = id;
	}

	@Id
	public URI getId() {
		return id;
	}


	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	@RdfProperty("http://imeji.mpdl.mpg.de/properties")
	public Properties getProperties() {
		return properties;
	}


	public void setCollection(URI collection) {
		this.collection = collection;
	}


	public URI getCollection() {
		return collection;
	}


	
	
	
	
	
	
	
	

	

}
