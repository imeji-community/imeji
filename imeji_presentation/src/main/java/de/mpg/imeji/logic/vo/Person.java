/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.vo;

import java.net.URI;
import java.util.Collection;
import java.util.LinkedList;
import java.util.UUID;

import de.mpg.j2j.annotations.j2jId;
import de.mpg.j2j.annotations.j2jList;
import de.mpg.j2j.annotations.j2jLiteral;
import de.mpg.j2j.annotations.j2jResource;

@j2jResource("http://xmlns.com/foaf/0.1/person")
@j2jId(getMethod = "getId", setMethod = "setId")
public class Person
{
    private URI id  = URI.create("http://imeji.org/person/" + UUID.randomUUID());
    @j2jLiteral("http://purl.org/escidoc/metadata/terms/0.1/family-name")
    private String familyName;
    @j2jLiteral("http://purl.org/escidoc/metadata/terms/0.1/given-name")
    private String givenName;
    @j2jLiteral("http://purl.org/escidoc/metadata/terms/0.1/complete-name")
    private String completeName;
    @j2jLiteral("http://purl.org/escidoc/metadata/terms/0.1/alternative-name")
    private String alternativeName;
    @j2jLiteral("http://purl.org/dc/elements/1.1/identifier")
    private String identifier;
    @j2jLiteral("http://purl.org/escidoc/metadata/terms/0.1/role")
    private URI role;
    private int pos = 0;
    @j2jList("http://imeji.org/terms/organization")
    protected Collection<Organization> organizations = new LinkedList<Organization>();

    public String getFamilyName()
    {
        return familyName;
    }

    public void setFamilyName(String familyName)
    {
        this.familyName = familyName;
    }

    public String getGivenName()
    {
        return givenName;
    }

    public void setGivenName(String givenName)
    {
        this.givenName = givenName;
    }

    public String getAlternativeName()
    {
        return alternativeName;
    }

    public void setAlternativeName(String alternativeName)
    {
        this.alternativeName = alternativeName;
    }

    public String getIdentifier()
    {
        return identifier;
    }

    public void setIdentifier(String identifier)
    {
        this.identifier = identifier;
    }

    public URI getRole()
    {
        return role;
    }

    public void setRole(URI role)
    {
        this.role = role;
    }

    public Collection<Organization> getOrganizations()
    {
        return organizations;
    }

    public void setOrganizations(Collection<Organization> organizations)
    {
        this.organizations = organizations;
    }

    public String getCompleteName()
    {
        return completeName;
    }

    public void setCompleteName(String completeName)
    {
        this.completeName = completeName;
    }

    public int getPos()
    {
        return pos;
    }

    public void setPos(int pos)
    {
        this.pos = pos;
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
