package de.mpg.escidoc.faces.metadata;

import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.PersonVO;

public class CreatorDisplayVO extends CreatorVO
{
    
    private String sup;

    public CreatorDisplayVO (CreatorVO creator, String sup)
    {
        this.sup = sup;
        PersonVO pers = new PersonVO();
        this.setPerson(pers);
        this.getPerson().setFamilyName(creator.getPerson().getFamilyName());
        this.getPerson().setGivenName(creator.getPerson().getGivenName());
        this.getPerson().getOrganizations().addAll(creator.getPerson().getOrganizations());       
    }
    
    public String getSup()
    {
        return sup;
    }

    public void setSup(String sup)
    {
        this.sup = sup;
    }
}
