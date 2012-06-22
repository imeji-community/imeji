/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.vo;

import java.net.URI;
import java.util.Collection;
import java.util.LinkedList;

import de.mpg.j2j.annotations.j2jId;
import de.mpg.j2j.annotations.j2jList;
import de.mpg.j2j.annotations.j2jLiteral;
import de.mpg.j2j.annotations.j2jModel;
import de.mpg.j2j.annotations.j2jResource;

@j2jResource("http://imeji.org/terms/mdprofile")
@j2jId(getMethod = "getId", setMethod = "setId")
@j2jModel("metadataProfile")
public class MetadataProfile
{
    private URI id;
    @j2jResource("http://imeji.org/terms/properties")
    private Properties properties = new Properties();
    @j2jLiteral("http://purl.org/dc/elements/1.1/title")
    private String title;
    @j2jLiteral("http://purl.org/dc/elements/1.1/description")
    private String description;
    @j2jList("http://imeji.org/terms/statement")
    private Collection<Statement> statements = new LinkedList<Statement>();

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

    public void setProperties(Properties properties)
    {
        this.properties = properties;
    }

    public Properties getProperties()
    {
        return properties;
    }
}
