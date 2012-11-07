/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.vo.predefinedMetadata;

import java.net.URI;

import javax.xml.bind.annotation.XmlType;

import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.j2j.annotations.j2jDataType;
import de.mpg.j2j.annotations.j2jId;
import de.mpg.j2j.annotations.j2jLiteral;
import de.mpg.j2j.annotations.j2jResource;

@j2jResource("http://imeji.org/terms/metadata")
@j2jDataType("http://imeji.org/terms/metadata#text")
@j2jId(getMethod = "getId", setMethod = "setId")
@XmlType(name="text")
public class Text extends Metadata
{
    @j2jLiteral("http://imeji.org/terms/text")
    private String text;
    @j2jResource("http://imeji.org/terms/statement")
    private URI statement;

    public Text()
    {
    }

    public String getText()
    {
        return text;
    }

    public void setText(String str)
    {
        text = str;
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
    public void copy(Metadata metadata)
    {
        if (metadata instanceof Text)
        {
            this.text = ((Text)metadata).getText();
            this.statement = metadata.getStatement();
        }
    }

    @Override
    public String asFulltext()
    {
        return text;
    }
}
