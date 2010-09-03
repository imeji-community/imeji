package de.mpg.jena.vo;

import java.net.URI;

import de.mpg.jena.util.ObjectHelper;
import de.mpg.jena.vo.ComplexType.AllowedTypes;
import thewebsemantic.Embedded;
import thewebsemantic.Namespace;
import thewebsemantic.RdfProperty;
import thewebsemantic.RdfType;

@Namespace("http://imeji.mpdl.mpg.de/image/")
@RdfType("metadata")
@Embedded
public class ImageMetadata
{
    private String elementNamespace;
    private String name;
    private String value;
    private URI type;

    public ImageMetadata(String ns, String name, String value)
    {
        this.elementNamespace = ns;
        this.name = name;
        this.value = value;
    }

    public ImageMetadata(URI type)
    {
        this.type = type;
        this.elementNamespace = ObjectHelper.getAllowedType(type).getNamespace();
        this.name = ObjectHelper.getAllowedType(type).getLabel();
        this.value = "";
    }

    public ImageMetadata()
    {
    }

    @RdfProperty("http://imeji.mpdl.mpg.de/image/metadata/value")
    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
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

    public void setElementNamespace(String elementNamespace)
    {
        this.elementNamespace = elementNamespace;
    }

    @RdfProperty("http://imeji.mpdl.mpg.de/image/metadata/elementNamespace")
    public String getElementNamespace()
    {
        return elementNamespace;
    }

    
    @RdfProperty("http://purl.org/dc/terms/type")
    public URI getType()
    {
        return type;
    }

    public void setType(URI type)
    {
        this.type = type;
    }
}
