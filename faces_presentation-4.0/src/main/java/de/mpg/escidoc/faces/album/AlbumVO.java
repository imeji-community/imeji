package de.mpg.escidoc.faces.album;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.escidoc.www.services.aa.UserAccountHandler;
import de.mpg.escidoc.faces.metadata.MdsAlbumVO;
import de.mpg.escidoc.services.common.referenceobjects.ContextRO;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.ContainerVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;
/**
 * 
 * @author saquet
 * @deprecated
 */
public class AlbumVO extends ContainerVO
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
    * The metadata record of an album 
    */    
    private MdsAlbumVO mdRecord = null;    
    /**
     * The list of IDs of all album's member
     */
    private List<String> membersId = null;
    /**
     * Parameter to check if user hast selected it.
     */
    private boolean selected = false;
    /**
     * Number of members of the album
     */
    private int size = 0;
    /**
     * The account of the creator of this album.
     */
    private String creator = null;
    
    /**
     * Default constructor
     */
    public AlbumVO()
    {
        super();
        init();
        mdRecord = new MdsAlbumVO();
    }
    
    /**
     * Constructor of an album from a {@link ContainerVO} 
     * @param ct
     */
    public AlbumVO(ContainerVO ct)
    {
        super(ct);
        
        if (ct.getStatusComment() != null) 
        {
        	this.setStatusComment(ct.getStatusComment());
		}
        
        try
        {
            mdRecord = new MdsAlbumVO((MdsPublicationVO)ct.getMetadataSets().get(0));
        }
        catch (Exception e)
        {
            mdRecord = new MdsAlbumVO();
            mdRecord.setTitle(ct.getMetadataSets().get(0).getTitle());
            mdRecord.getAbstracts().add(new TextVO("Album with old md-record schema."));
        }
    }
    
    private void init()
    {
        try
        {
            this.setContext(new ContextRO(PropertyReader.getProperty("escidoc.faces.context.id")));
            this.setContentModel(PropertyReader.getProperty("escidoc.faces.container.content-model.id"));
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public AlbumVO clone()
    {
        ContainerVO ct  = new ContainerVO(this);
        AlbumVO clone = new AlbumVO(ct);
        clone.setMdRecord(mdRecord.clone());
        
        return clone;
        
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

    public MdsAlbumVO getMdRecord()
    {
        return mdRecord;
    }

    public void setMdRecord(MdsAlbumVO mdRecord)
    {
        this.mdRecord = mdRecord;
    }

    public boolean isSelected()
    {
        return selected;
    }

    public void setSelected(boolean selected)
    {
        this.selected = selected;
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
  
}