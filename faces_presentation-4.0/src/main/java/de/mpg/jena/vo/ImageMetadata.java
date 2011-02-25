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
@RdfType("metadata")
@Embedded
public class ImageMetadata implements Serializable
{
    private String name;
    private ComplexType type;
    private String namespace;
    private String id = UUID.randomUUID().toString();
    private Collection<ImageMetadata> childs = new LinkedList<ImageMetadata>();
    private int pos = 0;
    
    public ImageMetadata(String name, ComplexType type)
    {
        this.type = type;
        this.name = name;
    }

    public ImageMetadata()
    {
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @RdfProperty("http://imeji.mpdl.mpg.de/metadata/name")
    public String getName()
    {
        return name;
    }

    public void setNamespace(String namespace)
    {
        this.namespace = namespace;
    }

    @RdfProperty("http://imeji.mpdl.mpg.de/metadata/elementNamespace")
    public String getNamespace()
    {
        return namespace;
    }

    @RdfProperty("http://imeji.mpdl.mpg.de/complexTypes")
    public ComplexType getType()
    {
        return type;
    }

    public void setType(ComplexType type)
    {
        this.type = type;
    }
    
    public void setId(String id)
    {
        this.id = id;
    }

    @Id
    public String getId()
    {
        return id;
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
