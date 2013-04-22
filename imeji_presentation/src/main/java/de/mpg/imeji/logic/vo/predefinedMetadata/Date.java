/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.vo.predefinedMetadata;

import java.net.URI;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.mpg.imeji.logic.util.DateFormatter;
import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.j2j.annotations.j2jDataType;
import de.mpg.j2j.annotations.j2jId;
import de.mpg.j2j.annotations.j2jLiteral;
import de.mpg.j2j.annotations.j2jResource;

/**
 * The Date {@link Metadata}. Should be used for {@link Metadata} related to a date
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
@j2jResource("http://imeji.org/terms/metadata")
@j2jDataType("http://imeji.org/terms/metadata#date")
@j2jId(getMethod = "getId", setMethod = "setId")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
@XmlRootElement(name = "date", namespace = "http://imeji.org/terms/metadata#date")
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

    @XmlElement(name = "date", namespace="http://imeji.org/terms/date")
    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        if (date != null && !"".equals(date))
        {
            time = DateFormatter.getTime(date);
            this.date = date;
        }
    }

    @XmlElement(name = "time", namespace="http://imeji.org/terms/time")
    public long getTime()
    {
        return time;
    }

    public void setTime(long dateTime)
    {
        this.time = dateTime;
    }

    @Override
    @XmlElement(name = "statement", namespace="http://imeji.org/terms/statement")
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
            setDate(((Date)metadata).getDate());
            this.statement = metadata.getStatement();
        }
    }

    @Override
    public String asFulltext()
    {
        return date;
    }
}
