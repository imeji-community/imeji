package de.mpg.escidoc.faces.metastore2.vo;


import thewebsemantic.Embedded;
import thewebsemantic.Id;
import thewebsemantic.Namespace;
import thewebsemantic.RdfProperty;
import thewebsemantic.RdfType;

@Namespace("http://imeji.mpdl.mpg.de/image/")
@RdfType("metadata")
@Embedded
public class ImageMetadata {

	
	private String elementNamespace;
	
	
	private String name;
	
	
	private String value;


	public ImageMetadata(String ns, String name, String value)
	{
		this.elementNamespace = ns;
		this.name = name;
		this.value = value;
	}
	
	public ImageMetadata()
	{
		
	}
	
	@RdfProperty("http://imeji.mpdl.mpg.de/image/metadata/value")
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}



	public void setName(String name) {
		this.name = name;
	}

	@RdfProperty("http://imeji.mpdl.mpg.de/image/metadata/name")
	public String getName() {
		return name;
	}

	public void setElementNamespace(String elementNamespace) {
		this.elementNamespace = elementNamespace;
	}

	@RdfProperty("http://imeji.mpdl.mpg.de/image/metadata/elementNamespace")
	public String getElementNamespace() {
		return elementNamespace;
	}

	

}
