package de.mpg.escidoc.faces.metadata;

import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.OrganizationVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.PersonVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO.CreatorRole;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO;

public class MdsFacesContainerVO  extends MdsPublicationVO
{	
    private static final long serialVersionUID = 1L;
	
    public MdsFacesContainerVO() 
    {
	super();
	this.setTitle(new TextVO(""));
	this.getAbstracts().add(new TextVO(""));
	this.getCreators().add(newCreator());
    }
	
    public MdsFacesContainerVO(MdsPublicationVO md)
    {
        super(md);
        format();
    }
    
    public MdsFacesContainerVO clone()
    {
    	super.clone();
    	return new MdsFacesContainerVO(this);
    }
    
    public static CreatorVO newCreator()
    {
	CreatorVO creator = new CreatorVO();
	PersonVO person = new PersonVO();
	person.setFamilyName("");
	person.setGivenName("");
	person.getOrganizations().add(newOrganization());
	creator.setPerson(person);
	return creator;
    }
    
    public static OrganizationVO newOrganization()
    {
	OrganizationVO orga = new OrganizationVO();
	orga.setName(new TextVO(""));
	orga.setIdentifier("");
	return orga;
    }
    
    public void format()
    {   
        // Clean all empty value for organization
        for (int i = this.getCreators().size() - 1; i >= 0; i--)
        {
            for (int j = this.getCreators().get(i).getPerson().getOrganizations().size() - 1; j >= 0; j--)
            {
                if (this.getCreators().get(i).getPerson().getOrganizations().get(j).getName() == null)
                {
                    this.getCreators().get(i).getPerson().getOrganizations().remove(j);
                }
            }
        }
        
        // All affiliations are set with the id of an external organization
        for (int i = 0; i <  this.getCreators().size(); i++)
        {
            for (int j = 0; j < this.getCreators().get(i).getPerson().getOrganizations().size(); j++)
            {
                this.getCreators().get(i).getPerson().getOrganizations().get(j).setIdentifier("escidoc:persistent22");
            }
        }
        
        // Creators are defined as author
        for (int i = 0; i <  this.getCreators().size(); i++)
        {
            this.getCreators().get(i).setRole(CreatorRole.AUTHOR);            
        }
    }
}
