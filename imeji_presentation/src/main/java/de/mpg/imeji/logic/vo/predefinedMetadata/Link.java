/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.vo.predefinedMetadata;

import java.net.URI;

import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.j2j.annotations.j2jDataType;
import de.mpg.j2j.annotations.j2jId;
import de.mpg.j2j.annotations.j2jResource;

@j2jResource("http://imeji.org/terms/metadata")
@j2jDataType("http://imeji.org/terms/metadata#link")
@j2jId(getMethod = "getId", setMethod = "setId")
public class Link extends Metadata
{
    @j2jResource("http://imeji.org/terms/uri")
    private URI uri;
    @j2jResource("http://imeji.org/terms/label")
    private String label;
    @j2jResource("http://imeji.org/terms/statement")
    private URI statement;

    public Link()
    {
    }

    public java.net.URI getUri()
    {
        return uri;
    }

    public void setUri(java.net.URI uri)
    {
        this.uri = uri;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public String getLabel()
    {
        return label;
    }

    @Override
    public URI getStatement()
    {
        return statement;
    }

    @Override
    public void setStatement(URI namespace)
    {
        this.statement = namespace;
    }

    @Override
    public void init()
    {
        setSearchValue(label + " " + uri.toString());
    }

    @Override
    public void copy(Metadata metadata)
    {
        if (metadata instanceof Link)
        {
            this.label = ((Link)metadata).getLabel();
            this.uri = ((Link)metadata).getUri();
            this.statement = metadata.getStatement();
        }
    }
}
