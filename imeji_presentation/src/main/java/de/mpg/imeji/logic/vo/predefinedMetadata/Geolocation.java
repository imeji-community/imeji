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

/**
 * {@link Metadata} for geolocation data
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
@j2jResource("http://imeji.org/terms/metadata")
@j2jDataType("http://imeji.org/terms/metadata#geolocation")
@j2jId(getMethod = "getId", setMethod = "setId")
@XmlType(name = "geolocation")
public class Geolocation extends Metadata
{
    @j2jLiteral("http://imeji.org/terms/longitude")
    private double longitude = Double.NaN;
    @j2jLiteral("http://imeji.org/terms/latitude")
    private double latitude = Double.NaN;
    @j2jLiteral("http://purl.org/dc/terms/title")
    private String name;
    @j2jResource("http://imeji.org/terms/statement")
    private URI statement;

    public Geolocation()
    {
    }

    public Geolocation(String latitude, String longitude)
    {
    }

    public double getLongitude()
    {
        return longitude;
    }

    public void setLongitude(double longitude)
    {
        this.longitude = longitude;
    }

    public double getLatitude()
    {
        return latitude;
    }

    public void setLatitude(double latitude)
    {
        this.latitude = latitude;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
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
        if (metadata instanceof Geolocation)
        {
            this.latitude = ((Geolocation)metadata).getLatitude();
            this.longitude = ((Geolocation)metadata).getLongitude();
            this.name = ((Geolocation)metadata).getName();
            this.statement = metadata.getStatement();
        }
    }

    @Override
    public String asFulltext()
    {
        return name + " " + " lat:" + latitude + " long:" + longitude;
    }
}
