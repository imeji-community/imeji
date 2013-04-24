/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.vo;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.mpg.j2j.annotations.j2jId;
import de.mpg.j2j.annotations.j2jList;
import de.mpg.j2j.annotations.j2jLiteral;
import de.mpg.j2j.annotations.j2jModel;
import de.mpg.j2j.annotations.j2jResource;

/**
 * Profile where {@link Item} {@link Metadata} are defined
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
@j2jResource("http://imeji.org/terms/mdprofile")
@j2jId(getMethod = "getId", setMethod = "setId")
@j2jModel("metadataProfile")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
@XmlRootElement(name = "metadataProfile", namespace = "http://imeji.org/terms/mdprofile")
public class MetadataProfile extends Properties
{    
    @j2jLiteral("http://purl.org/dc/elements/1.1/title")
    private String title;
    @j2jLiteral("http://purl.org/dc/elements/1.1/description")
    private String description;
    @j2jList("http://imeji.org/terms/statement")
    private Collection<Statement> statements = new ArrayList<Statement>();

	@XmlElement(name = "title", namespace = "http://purl.org/dc/elements/1.1/title")
    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    @XmlElement(name = "description", namespace = "http://purl.org/dc/elements/1.1/description")
    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }
    
    @XmlElement(name = "statements", namespace = "http://imeji.org/terms/statement")
    public Collection<Statement> getStatements()
    {
        return statements;
    }

    public void setStatements(Collection<Statement> statements)
    {
        this.statements = statements;
    }
}
