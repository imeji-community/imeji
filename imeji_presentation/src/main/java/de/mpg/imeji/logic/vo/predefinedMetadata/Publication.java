/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.vo.predefinedMetadata;

import java.net.URI;

import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.j2j.annotations.j2jDataType;
import de.mpg.j2j.annotations.j2jId;
import de.mpg.j2j.annotations.j2jLiteral;
import de.mpg.j2j.annotations.j2jResource;

@j2jResource("http://imeji.org/terms/metadata")
@j2jDataType("http://imeji.org/terms/metadata#publication")
@j2jId(getMethod = "getId", setMethod = "setId")
public class Publication extends Metadata
{
    @j2jLiteral("http://imeji.org/terms/uri")
    private URI uri;
    @j2jLiteral("http://imeji.org/terms/citationStyle")
    private String exportFormat;
    @j2jLiteral("http://imeji.org/terms/citation")
    private String citation;
    @j2jResource("http://imeji.org/terms/statement")
    private URI statement;

    public Publication()
    {
    }

    public java.net.URI getUri()
    {
        return uri;
    }

    public void setUri(URI uri)
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
        setSearchValue(citation + " " + uri.toString());
    }

    @Override
    public void copy(Metadata metadata)
    {
        if (metadata instanceof Publication)
        {
            this.citation = ((Publication)metadata).getCitation();
            this.exportFormat = ((Publication)metadata).getExportFormat();
            this.uri = ((Publication)metadata).getUri();
            this.statement = metadata.getStatement();
        }
    }
}
