/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.jena.vo;

import java.io.Serializable;
import java.net.URI;
import java.util.Collection;
import java.util.LinkedList;

import thewebsemantic.Id;
import thewebsemantic.LocalizedString;
import thewebsemantic.Namespace;
import thewebsemantic.RdfProperty;
import thewebsemantic.RdfType;

@Namespace("http://imeji.mpdl.mpg.de/mdprofile/")
@RdfType("statement")
public class Statement implements Serializable, Comparable<Statement>
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -6948096769263056023L;
	private URI type = URI.create("http://imeji.mpdl.mpg.de/ComplexTypes/text");
    private URI name;
    private Collection<LocalizedString> labels = new LinkedList<LocalizedString>();
    private URI vocabulary;
    private Collection<LocalizedString> literalConstraints = new LinkedList<LocalizedString>();
    private boolean isDescription = false;
    private String minOccurs = "0";
    private String maxOccurs = "1";
    private int pos = 0;

    public Statement() {
		// TODO Auto-generated constructor stub
	}
    
    @Id
    public URI getName()
    {
        return name;
    }

    public void setName(URI name)
    {
        this.name = name;
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

	public int getPos() {
		return pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}

	public int compareTo(Statement o) {
		if (o.getPos() > this.pos) return -1;
    	else if (o.getPos() == this.pos) return 0;
    	else return 1;
	}
	
	public boolean isDescription() {
		return isDescription;
	}
	
	public void setDescription(boolean isDescription) {
		this.isDescription = isDescription;
	}
	
}
