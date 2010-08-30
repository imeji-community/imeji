package de.mpg.imeji.vo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.mpg.escidoc.services.common.valueobjects.metadata.PersonVO;
import de.mpg.imeji.vo.util.ImejiFactory;
import de.mpg.jena.vo.ContainerMetadata;
import de.mpg.jena.vo.Organization;
import de.mpg.jena.vo.Person;

public class MdsContainerVO extends ContainerMetadata
{
    /*
    private List<PersonVO> personsVO;
    public MdsContainerVO()
    {
        super();
        this.setTitle("");
        this.setDescription("");
        personsVO = new ArrayList<PersonVO>();
    }
    
    private List<PersonVO> getPersonsVO()
    {
        for (Person p : persons)
        {
            personsVO.add((PersonVO)p);
        }
        return personsVO;
    }
    
    public void setPersons(List<PersonVO> list)
    {
        for (PersonVO pVO : list)
        {
            persons.add(pVO);
        }
    }
    

    
    public PersonVO getPerson(int pos)
    {
        return this.getPersonsVO().get(pos);
    }
    
    public void addPersonVO(int pos, PersonVO pers)
    {
        this.getPersonsVO().add(pos, pers);
        this.setPersons(this.getPersonsVO());
    }
    
    public void removePersonVO(int pos)
    {
        this.getPersonsVO().remove(pos);
        this.setPersons(this.getPersonsVO());
    }
*/
}
