/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.vo.predefinedMetadata;

import java.net.URI;

import javax.xml.bind.annotation.XmlType;

import de.mpg.imeji.logic.util.DateFormatter;
import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.j2j.annotations.j2jDataType;
import de.mpg.j2j.annotations.j2jId;
import de.mpg.j2j.annotations.j2jLiteral;
import de.mpg.j2j.annotations.j2jResource;
/**
 * 
 * The Date {@link Metadata}. Should be used for {@link Metadata} related to a date
 *
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
@j2jResource("http://imeji.org/terms/metadata")
@j2jDataType("http://imeji.org/terms/metadata#date")
@j2jId(getMethod = "getId", setMethod = "setId")
@XmlType(name="date")
public class Date extends Metadata
{
    @j2jLiteral("http://imeji.org/terms/date")
    private String date;
    @j2jLiteral("http://imeji.org/terms/time")
    private long time;
    @j2jResource("http://imeji.org/terms/statement")
    private URI statement;

    public Date()
    {
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        if (date != null && "".equals(date))
        {
            time = DateFormatter.getTime(date);
        }
        this.date = date;
    }

    public long getTime()
    {
        return time;
    }

    public void setTime(long dateTime)
    {
        this.time = dateTime;
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
        if (metadata instanceof Date)
        {
            this.date = ((Date)metadata).getDate();
            this.time = ((Date)metadata).getTime();
            this.statement = metadata.getStatement();
        }
    }

    @Override
    public String asFulltext()
    {
        return date;
    }
}
