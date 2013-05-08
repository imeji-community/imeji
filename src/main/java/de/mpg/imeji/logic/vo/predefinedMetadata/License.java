/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.vo.predefinedMetadata;

import java.net.URI;
import java.text.SimpleDateFormat;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.j2j.annotations.j2jDataType;
import de.mpg.j2j.annotations.j2jId;
import de.mpg.j2j.annotations.j2jLiteral;
import de.mpg.j2j.annotations.j2jResource;

/**
 * {@link Metadata} for license value
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
@j2jResource("http://imeji.org/terms/metadata")
@j2jDataType("http://imeji.org/terms/metadata#license")
@j2jId(getMethod = "getId", setMethod = "setId")
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "license", namespace = "http://imeji.org/terms/metadata")
public class License extends Metadata
{
    private SimpleDateFormat date;
    private String dateFormat = "dd/mm/yyyy";
    @j2jLiteral("http://imeji.org/terms/license")
    private String license = null;
    @j2jResource("http://imeji.org/terms/statement")
    private URI statement;
    @j2jResource("http://purl.org/dc/elements/1.1/identifier")
    private URI externalUri;

    public License()
    {
    }

    public License(SimpleDateFormat date)
    {
        this.date = date;
        date.applyPattern(dateFormat);
    }

    public String getDateString()
    {
        if (date == null || date.isLenient())
            return "";
        return date.format(date);
    }

    @XmlElement(name = "license", namespace="http://imeji.org/terms")
    public String getLicense()
    {
        return license;
    }

    public void setLicense(String str)
    {
        license = str;
    }

    @Override
    @XmlElement(name = "statement", namespace="http://imeji.org/terms")
    public URI getStatement()
    {
        return statement;
    }

    @Override
    public void setStatement(URI namespace)
    {
        this.statement = namespace;
    }

    /**
     * @return the externalUri
     */
    @XmlElement(name = "identifier", namespace="http://purl.org/dc/elements/1.1")
    public URI getExternalUri()
    {
        return externalUri;
    }

    /**
     * @param externalUri the externalUri to set
     */
    public void setExternalUri(URI externalUri)
    {
        this.externalUri = externalUri;
    }

    @Override
    public void copy(Metadata metadata)
    {
        if (metadata instanceof License)
        {
            this.license = ((License)metadata).getLicense();
            this.statement = metadata.getStatement();
            this.externalUri = ((License)metadata).getExternalUri();
        }
    }

    @Override
    public String asFulltext()
    {
        return license + " " + getDateString();
    }
}
