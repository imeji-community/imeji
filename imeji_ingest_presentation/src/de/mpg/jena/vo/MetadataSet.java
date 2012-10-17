/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.jena.vo;

import java.io.Serializable;
import java.net.URI;
import java.util.Collection;
import java.util.LinkedList;
import java.util.UUID;

import thewebsemantic.Embedded;
import thewebsemantic.Id;
import thewebsemantic.Namespace;
import thewebsemantic.RdfProperty;
import thewebsemantic.RdfType;

@Namespace("http://imeji.mpdl.mpg.de/")
@RdfType("metadataSet")
@Embedded
public class MetadataSet implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2264637862976119121L;
	private Collection<ImageMetadata> metadata =  new LinkedList<ImageMetadata>();
	private URI profile;
	
	@SuppressWarnings("unused")
	@Id
	private String id = UUID.randomUUID().toString();
	
	
	public MetadataSet() {
		// TODO Auto-generated constructor stub
	}
	
	//@RdfProperty("http://imeji.mpdl.mpg.de/metadata")
	public Collection<ImageMetadata> getMetadata() {
		return metadata;
	}

	public void setMetadata(Collection<ImageMetadata> metadata) {
		this.metadata = metadata;
	}
	
	@RdfProperty("http://imeji.mpdl.mpg.de/mdprofile")
	public URI getProfile() {
		return profile;
	}

	public void setProfile(URI profile) {
		this.profile = profile;
	}

}
