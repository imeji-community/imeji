package de.mpg.jena.vo.complextypes;

import java.net.URI;

import thewebsemantic.Namespace;
import thewebsemantic.RdfType;

import de.mpg.jena.vo.ComplexType;
import de.mpg.jena.vo.Person;

@Namespace("http://imeji.mpdl.mpg.de/metadata/")
@RdfType("cone-person")
public class ConePerson extends ComplexType
{
    private Person person;
    private URI coneId;
    private String role = "author";
    
    public ConePerson()
    {
        super(ComplexTypes.CONE_AUTHOR);
    }
    
    public ConePerson(Person pers)
    {
        super(ComplexTypes.CONE_AUTHOR);
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

    public String getRole()
    {
        return role;
    }

    public void setRole(String role)
    {
        this.role = role;
    }
    
    
}
