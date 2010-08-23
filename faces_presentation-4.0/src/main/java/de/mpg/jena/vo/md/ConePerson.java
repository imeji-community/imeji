package de.mpg.jena.vo.md;

import java.net.URI;

import de.mpg.jena.vo.Person;


public class ConePerson extends ComplexType
{
    private Person person;
    private URI coneId;
    private String role = "author";
    
    public ConePerson()
    {
        super(AllowedTypes.CONE_AUTHOR);
    }
    
    public ConePerson(Person pers)
    {
        super(AllowedTypes.CONE_AUTHOR);
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
