package de.mpg.escidoc.faces.container;

import java.util.ArrayList;
import java.util.List;

import de.mpg.escidoc.faces.album.AlbumVO;
import de.mpg.escidoc.faces.metadata.MdsAlbumVO;
import de.mpg.escidoc.faces.metadata.MdsFacesContainerVO;
import de.mpg.escidoc.services.common.valueobjects.ContainerVO;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO;

public class FacesContainerVO extends ContainerVO
{
	
	/**
    * The metadata record of an album 
    */    
    private MdsFacesContainerVO mdRecord = null;
    /**
     * The list of IDs of all album's member
     */
    private List<String> membersId = null;
	
    private boolean selected = false;
    
	public FacesContainerVO() 
	{
		super();
        mdRecord = new MdsFacesContainerVO();
	}

	public FacesContainerVO(ContainerVO ct) 
	{
		 super(ct);
	        
        if (ct.getStatusComment() != null) 
        {
        	this.setStatusComment(ct.getStatusComment());
		}
        
        try
        {
            mdRecord = new MdsFacesContainerVO((MdsPublicationVO)ct.getMetadataSets().get(0));
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error creating FacesContainerVO", e);
        }
	}
	
	@Override
	public FacesContainerVO clone()
    {
		FacesContainerVO clone = new FacesContainerVO(this);
        clone.setMdRecord(mdRecord.clone());
        return clone;
    }
	        
	
	public MdsFacesContainerVO getMdRecord()
    {
        return mdRecord;
    }

    public void setMdRecord(MdsFacesContainerVO mdRecord)
    {
        this.mdRecord = mdRecord;
    }
    
    /**
     * Returns a list of the id of the members of the album.
     * @return
     */
    public List<String> getMembersId()
    {
        membersId = new ArrayList<String>();
        for (int i = 0; i < this.getMembers().size(); i++)
        {
            membersId.add(this.getMembers().get(i).getObjectId());
        }
        return membersId;
    }

    public void setMembersId(List<String> membersId)
    {
        this.membersId = membersId;
    }
    
    public boolean isSelected()
    {
        return selected;
    }

    public void setSelected(boolean selected)
    {
        this.selected = selected;
    }
}
