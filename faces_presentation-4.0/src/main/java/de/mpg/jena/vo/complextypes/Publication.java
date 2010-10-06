package de.mpg.jena.vo.complextypes;

import java.io.Serializable;

import thewebsemantic.Embedded;
import thewebsemantic.Namespace;
import thewebsemantic.RdfType;

import de.mpg.jena.vo.ComplexType;

@Namespace("http://imeji.mpdl.mpg.de/metadata/")
@RdfType("publication")
@Embedded
public class Publication extends ComplexType implements Serializable
{
    private java.net.URI uri;
    private String exportFormat;
    private String citation;

    public Publication()
    {
        super(ComplexTypes.PUBLICATION);
    }

    public java.net.URI getUri()
    {
        return uri;
    }

    public void setUri(java.net.URI uri)
    {
        this.uri = uri;
    }

    public String getExportFormat()
    {
        return exportFormat;
    }

    public void setExportFormat(String exportFormat)
    {
        this.exportFormat = exportFormat;
    }

    public String getCitation()
    {
        return citation;
    }

    public void setCitation(String citation)
    {
        this.citation = citation;
    }
}
