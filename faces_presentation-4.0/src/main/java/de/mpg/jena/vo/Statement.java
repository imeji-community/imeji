package de.mpg.jena.vo;

import java.io.Serializable;
import java.net.URI;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import thewebsemantic.Embedded;
import thewebsemantic.Id;
import thewebsemantic.LocalizedString;
import thewebsemantic.Namespace;
import thewebsemantic.RdfProperty;
import thewebsemantic.RdfType;

@Namespace("http://imeji.mpdl.mpg.de/mdprofile/")
@RdfType("statement")
@Embedded
public class Statement implements Serializable
{
    private URI type = URI.create("http://imeji.mpdl.mpg.de/text");
    private String name;
    private Collection<LocalizedString> labels = new LinkedList<LocalizedString>();
    private URI vocabulary;
    private Collection<LocalizedString> literalConstraints = new LinkedList<LocalizedString>();
    private String minOccurs = "0";
    private String maxOccurs = "1";

    @RdfProperty("http://purl.org/dc/terms/type")
    public URI getType()
    {
        return type;
    }

    public void setType(URI type)
    {
        this.type = type;
    }

    public Collection<LocalizedString> getLabels()
    {
        return labels;
    }

    public void setLabels(Collection<LocalizedString> labels)
    {
        this.labels = labels;
    }

    @RdfProperty("http://purl.org/dc/dcam/VocabularyEncodingScheme")
    public URI getVocabulary()
    {
        return vocabulary;
    }

    public void setVocabulary(URI vocabulary)
    {
        this.vocabulary = vocabulary;
    }

    public Collection<LocalizedString> getLiteralConstraints()
    {
        return literalConstraints;
    }

    public void setLiteralConstraints(Collection<LocalizedString> literalConstraints)
    {
        this.literalConstraints = literalConstraints;
    }

    public String getMinOccurs()
    {
        return minOccurs;
    }

    public void setMinOccurs(String minOccurs)
    {
        this.minOccurs = minOccurs;
    }

    public String getMaxOccurs()
    {
        return maxOccurs;
    }

    public void setMaxOccurs(String maxOccurs)
    {
        this.maxOccurs = maxOccurs;
    }

    @RdfProperty("http://purl.org/dc/elements/1.1/title")
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}
