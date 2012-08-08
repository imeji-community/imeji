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
@j2jDataType("http://imeji.org/terms/metadata#number")
@j2jId(getMethod = "getId", setMethod = "setId")
public class Number extends Metadata
{
    @j2jLiteral("http://imeji.org/terms/number")
    private double number = Double.NaN;
    @j2jResource("http://imeji.org/terms/statement")
    private URI statement;

    public Number()
    {
    }

    public Number(double value)
    {
        setNumber(value);
    }

    public void setNumber(double number)
    {
        this.number = number;
    }

    public double getNumber()
    {
        return number;
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
        if (metadata instanceof Number)
        {
            this.number = ((Number)metadata).getNumber();
            this.statement = metadata.getStatement();
        }
    }

    @Override
    public void indexFulltext()
    {
        setFulltextIndex(Double.toString(number));
    }
}
