/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.vo;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.mpg.imeji.logic.util.IdentifierUtil;
import de.mpg.j2j.annotations.j2jId;
import de.mpg.j2j.annotations.j2jList;
import de.mpg.j2j.annotations.j2jLiteral;
import de.mpg.j2j.annotations.j2jResource;
import de.mpg.j2j.misc.LocalizedString;

/**
 * Define the properties of a {@link Metadata}. {@link Statement} are defined in a {@link MetadataProfile}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
@j2jResource("http://imeji.org/terms/statement")
@j2jId(getMethod = "getId", setMethod = "setId")
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "statement", namespace = "http://imeji.org/terms")
public class Statement implements Comparable<Statement>
{
    // Id: creation to be changed with pretty ids
    private URI id = IdentifierUtil.newURI(Statement.class);
    @j2jResource("http://purl.org/dc/terms/type")
    private URI type = URI.create("http://imeji.org/terms/metadata#text");
    // @j2jList("http://imeji.org/terms/label")
    @j2jList("http://www.w3.org/2000/01/rdf-schema#label")
    private Collection<LocalizedString> labels = new ArrayList<LocalizedString>();
    @j2jResource("http://purl.org/dc/dcam/VocabularyEncodingScheme")
    private URI vocabulary;
    @j2jList("http://imeji.org/terms/literalConstraint")
    private Collection<String> literalConstraints = new ArrayList<String>();
    @j2jLiteral("http://imeji.org/terms/isDescription")
    private boolean isDescription = false;
    @j2jLiteral("http://imeji.org/terms/minOccurs")
    private String minOccurs = "0";
    @j2jLiteral("http://imeji.org/terms/maxOccurs")
    private String maxOccurs = "1";
    @j2jResource("http://imeji.org/terms/parent")
    private URI parent = null;
    @j2jLiteral("http://imeji.org/terms/isPreview")
    private boolean isPreview = true;
    @j2jLiteral("http://imeji.org/terms/position")
    private int pos = 0;
    @j2jLiteral("http://imeji.org/terms/restricted")
    private boolean restricted = false;

    public Statement()
    {
    }

    @XmlElement(name = "type", namespace = "http://purl.org/dc/terms")
    public URI getType()
    {
        return type;
    }

    public void setType(URI type)
    {
        this.type = type;
    }

    @XmlElement(name = "label", namespace = "http://www.w3.org/2000/01/rdf-schema#")
    public Collection<LocalizedString> getLabels()
    {
        return labels;
    }

    public void setLabels(Collection<LocalizedString> labels)
    {
        this.labels = labels;
    }

    @XmlElement(name = "VocabularyEncodingScheme", namespace = "http://purl.org/dc/dcam")
    public URI getVocabulary()
    {
        return vocabulary;
    }

    public void setVocabulary(URI vocabulary)
    {
        this.vocabulary = vocabulary;
    }

    @XmlElement(name = "literalConstraint", namespace = "http://imeji.org/terms")
    public Collection<String> getLiteralConstraints()
    {
        return literalConstraints;
    }

    public void setLiteralConstraints(Collection<String> literalConstraints)
    {
        this.literalConstraints = literalConstraints;
    }

    @XmlElement(name = "minOccurs", namespace = "http://imeji.org/terms")
    public String getMinOccurs()
    {
        return minOccurs;
    }

    public void setMinOccurs(String minOccurs)
    {
        this.minOccurs = minOccurs;
    }

    @XmlElement(name = "maxOccurs", namespace = "http://imeji.org/terms")
    public String getMaxOccurs()
    {
        return maxOccurs;
    }

    public void setMaxOccurs(String maxOccurs)
    {
        this.maxOccurs = maxOccurs;
    }

    @XmlElement(name = "position", namespace = "http://imeji.org/terms")
    public int getPos()
    {
        return pos;
    }

    public void setPos(int pos)
    {
        this.pos = pos;
    }

    @Override
    public int compareTo(Statement o)
    {
        if (o.getPos() > this.pos)
            return -1;
        else if (o.getPos() == this.pos)
            return 0;
        else
            return 1;
    }

    @XmlElement(name = "isDescription", namespace = "http://imeji.org/terms")
    public boolean isDescription()
    {
        return isDescription;
    }

    public void setDescription(boolean isDescription)
    {
        this.isDescription = isDescription;
    }

    public void setId(URI id)
    {
        this.id = id;
    }

    @XmlAttribute(name = "id")
    public URI getId()
    {
        return id;
    }

    public void setParent(URI parent)
    {
        this.parent = parent;
    }

    @XmlElement(name = "parent", namespace = "http://imeji.org/terms")
    public URI getParent()
    {
        return parent;
    }

    public void setPreview(boolean isPreview)
    {
        this.isPreview = isPreview;
    }

    @XmlElement(name = "isPreview", namespace = "http://imeji.org/terms")
    public boolean isPreview()
    {
        return isPreview;
    }

    @XmlElement(name = "restricted", namespace = "http://imeji.org/terms")
    public boolean isRestricted()
    {
        return restricted;
    }

    public void setRestricted(boolean restricted)
    {
        this.restricted = restricted;
    }
}
