package de.mpg.jena.vo;

import java.io.Serializable;

import thewebsemantic.Embedded;
import thewebsemantic.Namespace;
import thewebsemantic.RdfProperty;
import thewebsemantic.RdfType;

@Namespace("http://imeji.mpdl.mpg.de/image/")
@RdfType("metadata")
@Embedded
public class ImageMetadata implements Serializable
{
    private String name;
    private ComplexType type;
    private String namespace;

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

    @RdfProperty("http://imeji.mpdl.mpg.de/image/metadata/name")
    public String getName()
    {
        return name;
    }

    public void setNamespace(String namespace)
    {
        this.namespace = namespace;
    }

    @RdfProperty("http://imeji.mpdl.mpg.de/image/metadata/elementNamespace")
    public String getNamespace()
    {
        return namespace;
    }

    @RdfProperty("http://purl.org/dc/terms/type")
    public ComplexType getType()
    {
        return type;
    }

    public void setType(ComplexType type)
    {
        this.type = type;
    }

    public int compareTo(ImageMetadata imd)
    {
        return this.compareTo((ImageMetadata)imd);
    }
}
