/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.jena.vo.complextypes;

import java.io.Serializable;
import java.net.URI;

import thewebsemantic.Embedded;
import thewebsemantic.Namespace;
import thewebsemantic.RdfType;

import de.mpg.jena.vo.ComplexType;
import de.mpg.jena.vo.Organization;
import de.mpg.jena.vo.Person;

@Namespace("http://imeji.mpdl.mpg.de/metadata/")
@RdfType("person")
@Embedded
public class ConePerson extends ComplexType implements Serializable
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 5858573975732425415L;
	private Person person;
    private URI coneId;

    public ConePerson()
    {
        super(ComplexTypes.PERSON);
        person = new Person();
        Organization o = new Organization();
        person.getOrganizations().add(o);
    }

    public ConePerson(Person pers)
    {
        super(ComplexTypes.PERSON);
        this.person = pers;
    }

    public Person getPerson()
    {
        return person;
    }

    public void setPerson(Person person)
    {
        this.person = person;
    }

    public URI getConeId()
    {
        return coneId;
    }

    public void setConeId(URI coneId)
    {
        this.coneId = coneId;
    }
}
