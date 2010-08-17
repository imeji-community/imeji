package de.mpg.imeji.vo;

import java.util.List;

import de.mpg.escidoc.faces.metastore.vo.ContainerMetadata;
import de.mpg.escidoc.faces.metastore.vo.Organization;
import de.mpg.escidoc.faces.metastore.vo.Person;

public class MdsContainerVO extends ContainerMetadata
{
    
    public MdsContainerVO() 
    {
	super();
	this.setTitle("");
	this.setDescription("");
	this.getPersons().add(newCreator());
    }
    
    public static Person newCreator()
    {
	Person person = new Person();
	person.setFamilyName("");
	person.setGivenName("");
	person.getOrganizations().add(newOrganization());
	return person;
    }
    
    public static Organization newOrganization()
    {
	Organization orga = new Organization();
	orga.setName("");
	orga.setIdentifier("");
	return orga;
    }    
}
