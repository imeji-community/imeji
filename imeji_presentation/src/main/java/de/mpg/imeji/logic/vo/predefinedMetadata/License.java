/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.vo.predefinedMetadata;

import java.net.URI;
import java.text.SimpleDateFormat;

import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.j2j.annotations.j2jId;
import de.mpg.j2j.annotations.j2jLiteral;
import de.mpg.j2j.annotations.j2jResource;

@j2jResource("http://imeji.org/terms/metadata#license")
@j2jId(getMethod = "getId", setMethod = "setId")
public class License extends Metadata
{
    private SimpleDateFormat date;
    private String dateFormat = "dd/mm/yyyy";
    @j2jLiteral("http://imeji.org/terms/license")
    private String license = null;
    @j2jResource("http://imeji.org/terms/statement")
    private URI statement;

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
        return date.format(date);
    }

    public String getLicense()
    {
        return license;
    }

    public void setLicense(String str)
    {
        license = str;
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
        setSearchValue(license + " " + getDateString());
    }

    @Override
    public void copy(Metadata metadata)
    {
        if (metadata instanceof License)
        {
            this.license = ((License)metadata).getLicense();
            copyMetadata(metadata);
        }
    }
}
