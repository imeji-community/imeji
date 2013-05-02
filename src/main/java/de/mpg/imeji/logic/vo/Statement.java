/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.vo;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

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
@XmlRootElement(name = "statement")
@XmlType(name = "statement")
public class Statement implements Comparable<Statement>
{
    // Id: creation to be changed with pretty ids
    private URI id = IdentifierUtil.newURI(Statement.class);
    @j2jResource("http://purl.org/dc/terms/type")
    private URI type = URI.create("http://imeji.org/terms/metadata#text");
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

    public URI getVocabulary()
    {
        return vocabulary;
    }

    public void setVocabulary(URI vocabulary)
    {
        this.vocabulary = vocabulary;
    }

    public Collection<String> getLiteralConstraints()
    {
        return literalConstraints;
    }

    public void setLiteralConstraints(Collection<String> literalConstraints)
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

    public URI getId()
    {
        return id;
    }

    public void setParent(URI parent)
    {
        this.parent = parent;
    }

    public URI getParent()
    {
        return parent;
    }

    public void setPreview(boolean isPreview)
    {
        this.isPreview = isPreview;
    }

    public boolean isPreview()
    {
        return isPreview;
    }

    public boolean isRestricted()
    {
        return restricted;
    }

    public void setRestricted(boolean restricted)
    {
        this.restricted = restricted;
    }
}
