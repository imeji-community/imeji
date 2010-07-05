package de.mpg.escidoc.faces.container;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.mpg.escidoc.faces.album.AlbumVO;
import de.mpg.escidoc.faces.metadata.MdsAlbumVO;
import de.mpg.escidoc.faces.metadata.MdsFacesContainerVO;
import de.mpg.escidoc.faces.metadata.ScreenConfiguration;
import de.mpg.escidoc.services.common.valueobjects.ContainerVO;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO;

public class FacesContainerVO extends ContainerVO
{  
    private MdsFacesContainerVO mdRecord = null;
    /**
     * The list of IDs of all album's member
     */
    private List<String> membersId = null;
    /**
     * Check if selected by user
     */
    private boolean selected = false;
    
    private String creator = null;
    
    private int size = 0;
    
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
    
    /**
     * Returns a shorter description for albums list menu.
     * @return
     */
    public String getShortDescription()
    {
  	  if (this.getMdRecord().getAbstracts().get(0) != null
  			  && this.getMdRecord().getAbstracts().get(0).getValue().length() > 50) 
  	  {
  		  return this.getMdRecord().getAbstracts().get(0).getValue().substring(0, 50) + "...";
  	  }
  	  else if (this.getMdRecord().getAbstracts().get(0) != null)
  	  {
  		  return this.getMdRecord().getAbstracts().get(0).getValue() ;
  	  }
  	  return null;
    }

    public String getCreator() 
    {
  	  creator = this.getMdRecord().getCreators().get(0).getPerson().getFamilyName() 
    				+ ", " 
    				+ this.getMdRecord().getCreators().get(0).getPerson().getGivenName();
  	  return creator;
    }

    public void setCreator(String creator) 
    {	
  	  this.creator = creator;
    }
    
    public int getSize()
    {
        return this.getMembers().size();
    }

    public void setSize(int size)
    {
        this.size = size;
    }
    
    /**
   * Return date of creation in faces format
   * @return
   */
  public String getDateofCreation()
  {
      if (this.getCreationDate() != null)
      {
          return this.formatDate(this.getCreationDate());
      }
      else
      {
          return null;
      }
  }
  
  /**
   * return date of last modification in Faces Format
   * @return
   */
  public String getDateOfModification()
  {
      if (this.getModificationDate() != null)
      {
          return this.formatDate(this.getModificationDate());
      }
      else
      {
          return null;
      }
  }

  /**
   * Return the date of publication in Faces format
   * @return
   */
  public String getDateOfPublication()
  {
      if (this.getLatestRelease().getModificationDate() != null)
      {
          return this.formatDate(this.getLatestRelease().getModificationDate());
      }
      else
      {
          return null;
      }
     
  }
  
  /**
   * Format the date to display it according to faces specifications
   * @param date
   * @return
   */
  private String formatDate(Date date)
  {
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
      String dateString = simpleDateFormat.format(date);
      return dateString;
  }
  
}
