package de.mpg.escidoc.faces.metadata;

import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO.CreatorRole;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO;

/**
 * 
 * @author saquet
 *
 */
public class MdsAlbumVO extends MdsPublicationVO
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MdsAlbumVO()
    {
        super();
    }
    
    public MdsAlbumVO(MdsPublicationVO md)
    {
        super(md);
        format();
    }
    
    public MdsAlbumVO clone()
    {
    	super.clone();
    	return new MdsAlbumVO(this);
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
