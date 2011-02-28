package de.mpg.jena.vo;

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
public class MetadataSet 
{
	private Collection<ImageMetadata> metadata =  new LinkedList<ImageMetadata>();
	
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

}
