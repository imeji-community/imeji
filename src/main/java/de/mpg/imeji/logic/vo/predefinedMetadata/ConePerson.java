/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.vo.predefinedMetadata;

import java.net.URI;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.imeji.logic.vo.Person;
import de.mpg.j2j.annotations.j2jDataType;
import de.mpg.j2j.annotations.j2jId;
import de.mpg.j2j.annotations.j2jResource;

/**
 * Metadata for {@link Person}. Can have a CoNe identifier
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
@j2jResource("http://imeji.org/terms/metadata")
@j2jDataType("http://imeji.org/terms/metadata#conePerson")
@j2jId(getMethod = "getId", setMethod = "setId")
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "conePerson", namespace = "http://imeji.org/terms/metadata")
public class ConePerson extends Metadata
{
    @j2jResource("http://xmlns.com/foaf/0.1/person")
    private Person person;
    @j2jResource("http://imeji.org/terms/coneId")
    private URI coneId;
    @j2jResource("http://imeji.org/terms/statement")
    private URI statement;

    public ConePerson()
    {
    }

    public ConePerson(Person pers)
    {
        this.person = pers;
    }

    @XmlElement(name = "person", namespace = "http://xmlns.com/foaf/0.1")
    public Person getPerson()
    {
        return person;
    }

    public void setPerson(Person person)
    {
        this.person = person;
    }

    @XmlElement(name = "coneId", namespace = "http://imeji.org/terms")
    public URI getConeId()
    {
        return coneId;
    }

    public void setConeId(URI coneId)
    {
        this.coneId = coneId;
    }

    @Override
    @XmlElement(name = "statement", namespace = "http://imeji.org/terms")
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
        if (metadata instanceof ConePerson)
        {
            this.person = ((ConePerson)metadata).getPerson();
            this.coneId = ((ConePerson)metadata).getConeId();
            this.statement = metadata.getStatement();
        }
    }

    @Override
    public String asFulltext()
    {
        return person.AsFullText();
    }
}
