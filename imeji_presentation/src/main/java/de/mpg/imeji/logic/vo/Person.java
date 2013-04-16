/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.vo;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import de.mpg.imeji.logic.util.IdentifierUtil;
import de.mpg.imeji.logic.vo.predefinedMetadata.ConePerson;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.j2j.annotations.j2jId;
import de.mpg.j2j.annotations.j2jList;
import de.mpg.j2j.annotations.j2jLiteral;
import de.mpg.j2j.annotations.j2jResource;

/**
 * a foaf person
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
@j2jResource("http://xmlns.com/foaf/0.1/person")
@j2jId(getMethod = "getId", setMethod = "setId")
@XmlRootElement(name = "person", namespace = "http://xmlns.com/foaf/0.1/person")
public class Person
{
    private URI id = IdentifierUtil.newURI(Person.class);
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
    @j2jList("http://purl.org/escidoc/metadata/profiles/0.1/organizationalunit")
    protected Collection<Organization> organizations = new ArrayList<Organization>();

    public Person()
    {
    }

    @XmlElement(name = "familyName", namespace = "http://purl.org/escidoc/metadata/terms/0.1/family-name", type = String.class)
    public String getFamilyName()
    {
        return familyName;
    }

    public void setFamilyName(String familyName)
    {
        this.familyName = familyName;
    }

    @XmlElement(name = "givenName", namespace = "http://purl.org/escidoc/metadata/terms/0.1/given-name", type = String.class)
    public String getGivenName()
    {
        return givenName;
    }

    public void setGivenName(String givenName)
    {
        this.givenName = givenName;
    }

    @XmlElement(name = "alternativeName", namespace = "http://purl.org/escidoc/metadata/terms/0.1/alternative-name", type = String.class)
    public String getAlternativeName()
    {
        return alternativeName;
    }

    public void setAlternativeName(String alternativeName)
    {
        this.alternativeName = alternativeName;
    }

    @XmlElement(name = "identifier", namespace = "http://purl.org/dc/elements/1.1/identifier", type = String.class)
    public String getIdentifier()
    {
        return identifier;
    }

    public void setIdentifier(String identifier)
    {
        this.identifier = identifier;
    }

    @XmlElement(name = "role", namespace = "http://purl.org/escidoc/metadata/terms/0.1/role", type = URI.class)
    public URI getRole()
    {
        return role;
    }

    public void setRole(URI role)
    {
        this.role = role;
    }
    
    @XmlElements(value=@XmlElement(name = "organizations", namespace = "http://purl.org/escidoc/metadata/profiles/0.1/organizationalunit", type = Organization.class))
    public Collection<Organization> getOrganizations()
    {
        return organizations;
    }

    public void setOrganizations(Collection<Organization> organizations)
    {
        this.organizations = organizations;
    }

    @XmlElement(name = "completeName", namespace = "http://purl.org/escidoc/metadata/terms/0.1/complete-name", type = String.class)
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

    public String getOrganizationString()
    {
        String s = "";
        for (Organization o : organizations)
        {
            if (!"".equals(s))
                s += " ,";
            s += o.getName();
        }
        return s;
    }

    public String AsFullText()
    {
        String str = givenName + " " + familyName + " " + alternativeName;
        for (Organization org : organizations)
        {
            str += " " + org.getName();
        }
        return str.trim();
    }
}
