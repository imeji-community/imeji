package de.mpg.jena.vo;

import java.net.URI;
import java.util.Collection;
import java.util.LinkedList;

import thewebsemantic.Generated;
import thewebsemantic.Id;
import thewebsemantic.Namespace;
import thewebsemantic.RdfProperty;
import thewebsemantic.RdfType;
import thewebsemantic.custom_datatypes.XmlLiteral;



public class Container{

	
	private URI id;
	
	
	private Properties properties = new Properties();

	private Collection<URI> images = new LinkedList<URI>();

	
	private ContainerMetadata metadata = new ContainerMetadata();

	private XmlLiteral metadataSchema;
	
	private XmlLiteral metadataDSP;
	

	public void setId(URI id) {
		this.id = id;
	}


	@Id
	public URI getId() {
		return id;
	}

	public void setMetadata(ContainerMetadata metadata) {
		this.metadata = metadata;
	}

	@RdfProperty("http://imeji.mpdl.mpg.de/container/metadata")
	public ContainerMetadata getMetadata() {
		return metadata;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	@RdfProperty("http://imeji.mpdl.mpg.de/properties")
	public Properties getProperties() {
		return properties;
	}


	public void setImages(Collection<URI> images) {
		this.images = images;
	}


	public Collection<URI> getImages() {
		return images;
	}


	public void setMetadataSchema(XmlLiteral metadataSchema) {
		this.metadataSchema = metadataSchema;
	}


	public XmlLiteral getMetadataSchema() {
		return metadataSchema;
	}


	public void setMetadataDSP(XmlLiteral metadataDSP) {
		this.metadataDSP = metadataDSP;
	}


	public XmlLiteral getMetadataDSP() {
		return metadataDSP;
	}
	
	


	



	
	
	

	

}
