package de.mpg.jena.vo;

import java.io.Serializable;
import java.net.URI;
import java.util.Collection;
import java.util.LinkedList;
import java.util.UUID;

import de.mpg.jena.vo.ComplexType.ComplexTypes;

import thewebsemantic.Embedded;
import thewebsemantic.Id;
import thewebsemantic.LocalizedString;
import thewebsemantic.Namespace;
import thewebsemantic.RdfProperty;
import thewebsemantic.RdfType;

@Namespace("http://imeji.mpdl.mpg.de/")
@RdfType("metadata")
@Embedded
public class ImageMetadata implements Serializable
{
    private URI namespace;
    private ComplexTypes type;
    private Collection<ImageMetadata> childs = new LinkedList<ImageMetadata>();
    private int pos = 0;
    
    private String id = UUID.randomUUID().toString();
    
    public ImageMetadata()
    {
    }
    
    public ImageMetadata(URI namespace)
    {
        this.namespace = namespace;
    }
    
    @RdfProperty("http://imeji.mpdl.mpg.de/complexTypes")
    public ComplexTypes getType() 
    {
		return type;
	}

	public void setType(ComplexTypes type) 
	{
		this.type = type;
	}
	
	@RdfProperty("http://imeji.mpdl.mpg.de/metadata/ns")
	public URI getNamespace() 
	{
		return namespace;
	}
	
	public void setNamespace(URI namespace)
	{
		this.namespace = namespace;
	}
	
	 @Id
    public String getId()
    {
        return id;
    }
	 
    public void setId(String id)
    {
        this.id = id;
    }

    public int compareTo(ImageMetadata imd)
    {
        return this.compareTo((ImageMetadata)imd);
    }

	public Collection<ImageMetadata> getChilds() {
		return childs;
	}

	public void setChilds(Collection<ImageMetadata> childs) {
		this.childs = childs;
	}

	public int getPos() {
		return pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}
	 
	
}
