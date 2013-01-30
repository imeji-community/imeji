/**
 * License: src/main/resources/license/escidoc.license
 */


package de.mpg.imeji.logic.vo;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import de.mpg.j2j.annotations.j2jId;
import de.mpg.j2j.annotations.j2jList;
import de.mpg.j2j.annotations.j2jLiteral;
import de.mpg.j2j.annotations.j2jModel;
import de.mpg.j2j.annotations.j2jResource;

/**
 * 
 * Profile where {@link Item} {@link Metadata} are defined 
 *
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
@j2jResource("http://imeji.org/terms/mdprofile")
@j2jId(getMethod = "getId", setMethod = "setId")
@j2jModel("metadataProfile")
@XmlRootElement(name="metadataProfile")
@XmlType(name="metadataProfile")
public class MetadataProfile extends Properties
{
    private URI id;
    @j2jLiteral("http://purl.org/dc/elements/1.1/title")
    private String title;
    @j2jLiteral("http://purl.org/dc/elements/1.1/description")
    private String description;
    @j2jList("http://imeji.org/terms/statement")
    private Collection<Statement> statements = new ArrayList<Statement>();
    
    public URI getId()
    {
        return id;
    }

    public void setId(URI id)
    {
        this.id = id;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public Collection<Statement> getStatements()
    {
        return statements;
    }

    public void setStatements(Collection<Statement> statements)
    {
        this.statements = statements;
    }

//    public void setProperties(Properties properties)
//    {
//        this.properties = properties;
//    }
//
//    public Properties getProperties()
//    {
//        return properties;
//    }
}
