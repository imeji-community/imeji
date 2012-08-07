/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.vo;

import java.net.URI;

import de.mpg.j2j.annotations.j2jId;
import de.mpg.j2j.annotations.j2jLiteral;
import de.mpg.j2j.annotations.j2jResource;

@j2jResource("http://purl.org/escidoc/metadata/profiles/0.1/organizationalunit")
@j2jId(getMethod = "getId", setMethod = "setId")
public class Organization
{
    private URI id;
    @j2jLiteral("http://purl.org/dc/terms/title")
    private String name;
    @j2jLiteral("http://purl.org/dc/terms/description")
    private String description;
    @j2jLiteral("http://purl.org/dc/terms/identifier")
    private String identifier;
    @j2jLiteral("http://purl.org/escidoc/metadata/terms/0.1/city")
    private String city;
    @j2jLiteral("http://purl.org/escidoc/metadata/terms/0.1/country")
    private String country;
    private int pos = 0;
    
    public Organization()
    {
        // TODO Auto-generated constructor stub
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getIdentifier()
    {
        return identifier;
    }

    public void setIdentifier(String identifier)
    {
        this.identifier = identifier;
    }

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public String getCountry()
    {
        return country;
    }

    public void setCountry(String country)
    {
        this.country = country;
    }

    public int getPos()
    {
        return pos;
    }

    public void setPos(int pos)
    {
        this.pos = pos;
    }

    public int compareTo(Organization o)
    {
        if (o.getPos() > this.pos)
            return -1;
        else if (o.getPos() == this.pos)
            return 0;
        else
            return 1;
    }

    public void setId(URI id)
    {
        this.id = id;
    }

    public URI getId()
    {
        return id;
    }
}
