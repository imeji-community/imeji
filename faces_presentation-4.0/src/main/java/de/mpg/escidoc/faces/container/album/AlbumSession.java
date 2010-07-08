package de.mpg.escidoc.faces.container.album;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.servlet.http.HttpServletRequest;

import de.mpg.escidoc.faces.album.ExportManager;
import de.mpg.escidoc.faces.album.ExportParameters.ExportType;
import de.mpg.escidoc.faces.beans.Navigation;
import de.mpg.escidoc.faces.beans.SessionBean;
import de.mpg.escidoc.faces.container.FacesContainerVO;
import de.mpg.escidoc.faces.container.list.FacesContainerListController;
import de.mpg.escidoc.faces.container.list.FacesContainerListParameters;
import de.mpg.escidoc.faces.container.list.FacesContainerListParameters.OrderParameterType;
import de.mpg.escidoc.faces.container.list.FacesContainerListParameters.SortParameterType;
import de.mpg.escidoc.faces.container.list.FacesContainerListVO.HandlerType;
import de.mpg.escidoc.faces.metadata.CreatorDisplayVO;
import de.mpg.escidoc.faces.util.BeanHelper;
import de.mpg.escidoc.services.common.valueobjects.ContainerVO.State;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.OrganizationVO;

/**
 * Session bean for all albums values.
 * @author saquet
 *
 */
public class AlbumSession
{
    /**
     * The active album.
     */
    private AlbumVO active = null;
    /**
     * The album currently browse (id in url)
     */
    private AlbumVO current = null;
    /**
     * The list of albums of current user.
     */
    private AlbumListVO myAlbums = null;
    /**
     * The published albums of instance.
     */
    private AlbumListVO published = null;
    /**
     * List of the ids of the selected albums
     */
    private List<String> selectedAlbums = null;
    /**
     * The {@link ExportManager} object
     */
    private ExportManager export = null;
    
    private String selectedMenu = "SORTING";
    private String filter = "all";
    private FacesContainerListController controller = null;
    private AlbumController albumController = null;
    private SessionBean sessionBean = null;    
    
    /**
     * Default Constructor
     */
    public AlbumSession()
    {
    	active = new AlbumVO();
        current = new AlbumVO();
        myAlbums = new AlbumListVO(new ArrayList<FacesContainerVO>(), new FacesContainerListParameters(null, SortParameterType.LAST_MODIFICATION_DATE, OrderParameterType.DESCENDING, 10, 1, null, null), HandlerType.FILTER);
        published = new AlbumListVO(new ArrayList<FacesContainerVO>(), new FacesContainerListParameters(null, SortParameterType.LAST_MODIFICATION_DATE,  OrderParameterType.DESCENDING, 10, 1, null, null), HandlerType.SEARCH);
        controller = new FacesContainerListController();
        albumController = new AlbumController();
        selectedAlbums = new ArrayList<String>();
        sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        export = new ExportManager();
    }

	/**
     * Retrieve the lists.
     * @throws Exception
     */
    public void getInit() throws Exception
    {
        myAlbums = (AlbumListVO) controller.retrieve(myAlbums, sessionBean.getUserHandle());
        published = (AlbumListVO) controller.retrieve(published, sessionBean.getUserHandle());
    }
    
    /**
     * Retrieve the current album
     */
    public String getInitCurrentAlbum()
    {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        
        if (request.getParameter("album") != null 
                && !"".equals(request.getParameter("album")))
        {
            try 
            {
		current = (AlbumVO) albumController.retrieve((String)request.getParameter("album"), sessionBean.getUserHandle());
            } 
            catch (Exception e) 
            {
            	sessionBean.setMessage((String)request.getParameter("album") + " not found!");
            }
        }
        else 
        {
            current = new AlbumVO();
        }
        
        return "";
    }
    
    /**
     * Retrieve the active album
     */
    public String getInitActiveAlbum()
    {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        
        if (request.getParameter("selection") != null 
                && !"".equals(request.getParameter("selection")))
        {
            try 
            {
				active = (AlbumVO) albumController.retrieve((String)request.getParameter("selection"), sessionBean.getUserHandle());
			} 
            catch (Exception e) 
            {
            	sessionBean.setMessage((String)request.getParameter("selection") + " not found!");
            	active = new AlbumVO();
			}
        }
        else if (request.getParameter("selection") == null 
        		&& active != null
                && active.getVersion().getObjectId() != null
                && active.getState().equals(State.PENDING))
        {
            // Update the current active album
            try 
            {
				active = (AlbumVO) albumController.retrieve(active.getVersion().getObjectId(), sessionBean.getUserHandle());
			} 
            catch (Exception e) 
            {
            	sessionBean.setMessage(active.getVersion().getObjectId() + " not found!");
            	active = new AlbumVO();
			}
        }
        else if (active != null &&
        			active.getVersion().getObjectId() != null && 
        			!active.getState().equals(State.PENDING)) 
        {
        	active = new AlbumVO();
		}
            // There isn't the possibilty to unactivate albums
            //active = new AlbumVO();
        
        return "";
    }
    
    
    /**
     * Returns the number of albums selected in the current album list.
     * @return
     */
    public int getNumberOfAlbumsSelected()
    {
        int total= 0;
        
        for (int i = 0; i < myAlbums.getList().size(); i++)
        {
            if (myAlbums.getList().get(i).isSelected())
            {
                total++;
            }
        }
        
        return total;
    }
    
    /**
     * JSF listener for the export format value.
     * @param event
     */
    public void exportFormatListener(ValueChangeEvent event)
    {
    	if (event.getNewValue() != null && !event.getNewValue().equals(event.getOldValue())) 
    	{
			for (int i = 0; i < ExportType.values().length; i++) 
			{
				if (ExportType.values()[i].toString().equals(event.getNewValue())) 
				{
					export.getParameters().setExportFormat(ExportType.values()[i]);
				}
			}
		}
    }
    
    /**
     * JSF listener for the thumbnails resolution value
     * @param event
     */
    public void thumbnailsResolutionListener(ValueChangeEvent event)
    {
    	if (event.getNewValue() != null && !event.getNewValue().equals(event.getOldValue())) 
    	{
    		export.getParameters().setThumbnails(Boolean.parseBoolean(event.getNewValue().toString()));
		}
    }
    
    /**
     * JSF listener for the web resolution value
     * @param event
     */
    public void webResolutionListener(ValueChangeEvent event)
    {
    	if (event.getNewValue() != null && !event.getNewValue().equals(event.getOldValue())) 
    	{
    		export.getParameters().setWeb(Boolean.parseBoolean(event.getNewValue().toString()));
		}
    }
    
    /**
     * JSF listener for the original resolution value
     * @param event
     */
    public void originalListener(ValueChangeEvent event)
    {
    	if (event.getNewValue() != null && !event.getNewValue().equals(event.getOldValue())) 
    	{
    		export.getParameters().setOrignal(Boolean.parseBoolean(event.getNewValue().toString()));
		}
    }
    
    /**
     * Submit the formular of the export.
     */
    public void submitExportFormular()
    {
    	Navigation navigation = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
    	try 
    	{
    	    if (this.export.getParameters().getExportFormat().equals(ExportType.CSV) || 
    	            this.export.getParameters().getExportFormat().equals(ExportType.XML))
    	    {
    	        sessionBean.setAgreement(true);
//    	        FacesContext.getCurrentInstance().getExternalContext().redirect(
//    	                navigation.getConfirmationUrl() + "/export/" + current.getVersion().getObjectId()+"?agree=1");
    	        export.setUserHandle(sessionBean.getUserHandle());
    	        // TODO
    	        //export.doExport(current);
    	        
    	    }
    	    else
    	    {
    	        FacesContext.getCurrentInstance().getExternalContext().redirect(
    	                navigation.getConfirmationUrl() + "/export/" + current.getVersion().getObjectId());
    	    }
		} 
    	catch (Exception e) 
		{
			throw new RuntimeException(e);
		}
    }
    
    public boolean isExport()
    {
        return false;
    }

    public AlbumVO getActive()
    {
        return active;
    }

    public void setActive(AlbumVO active)
    {
        this.active = active;
    }

    public AlbumVO getCurrent()
    {
        return current;
    }

    public void setCurrent(AlbumVO current)
    {
        this.current = current;
    }

    public AlbumListVO getMyAlbums()
    {
        return myAlbums;
    }

    public void setMyAlbums(AlbumListVO myAlbums)
    {
        this.myAlbums = myAlbums;
    }

    public AlbumListVO getPublished()
    {
        return published;
    }

    public void setPublished(AlbumListVO published)
    {
        this.published = published;
    }

    public String getSelectedMenu()
    {
        return selectedMenu;
    }

    public void setSelectedMenu(String selectedMenu)
    {
        this.selectedMenu = selectedMenu;
    }

	public String getFilter() 
	{
		return filter;
	}

	public void setFilter(String filter) 
	{
		this.filter = filter;
	}
	
	public List<AlbumVO> getSelectedAlbums() 
	{
	    ArrayList <AlbumVO> selectedAlbums = new ArrayList<AlbumVO>();
        
        for (int i = 0; i < myAlbums.getList().size(); i++)
        {
            if (myAlbums.getList().get(i).isSelected())
            {
                selectedAlbums.add((AlbumVO) myAlbums.getList().get(i));
            }
        }
        
        return selectedAlbums;
	}

	public void setSelectedAlbums(List<String> selectedAlbums) 
	{
		this.selectedAlbums = selectedAlbums;
	}

	public ExportManager getExportManager() 
	{
		if ("true".equals(this.getRequest().getParameter("init"))) 
		{
			export.getParameters().setExportFormat(ExportType.XML);
			export.getParameters().setWeb(true);
			export.getParameters().setThumbnails(false);
			export.getParameters().setOrignal(false);
		}
		
		return export;
	}

	public void setExport(ExportManager export) 
	{
		this.export = export;
	}
	
	/**
	 * Override selecbooleancheckBox inability
	 * @return
	 */
	public Boolean getThumbnails()
	{
		return export.getParameters().getThumbnails();
	}
	
	/**
	 * Override selecbooleancheckBox inability
	 * @return
	 */
	public Boolean getWeb()
	{
		return export.getParameters().getWeb();
	}
	
	/**
	 * Returns the current {@link HttpServletRequest}.
	 * @return
	 */
	private HttpServletRequest getRequest()
	{
		return (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
	}
	
	/**
	 * This method returns a list of all affiliations of an album,
	 * without duplicates.
	 * @return List of affiliations as String
	 */
	public List<OrganizationVO> getAllAffiliations()
	{
	    List <OrganizationVO> affiliations = new ArrayList<OrganizationVO>();
	    List <CreatorVO> creators = this.getCurrent().getMdRecord().getCreators();
	    
	    for (int i = 0; i< creators.size(); i++)
	    {
	        CreatorVO creator = creators.get(i);
	        for (int x = 0; x < creator.getPerson().getOrganizationsSize(); x++)
	        {
	            OrganizationVO org = creator.getPerson().getOrganizations().get(x);
	            boolean contains = false;
	            for (int y = 0; y < affiliations.size(); y++)
	            {
	                if (affiliations.get(y).getName().getValue().equalsIgnoreCase(org.getName().getValue()))
	                {
	                    contains = true;
	                }
	            }
                if (! contains)
                {
                    affiliations.add(org);
                }
	        }	        
	    }	    
	    return affiliations;
	}
	
	public List <CreatorDisplayVO> getCreatorsForDisplay ()
	{
	    List <CreatorDisplayVO> creatorsDisplay = new ArrayList<CreatorDisplayVO>();
	    List <CreatorVO> creators = this.getCurrent().getMdRecord().getCreators();
	    for (int i = 0; i < creators.size(); i++)
	    {
	        CreatorVO creator = creators.get(i);
	        List <OrganizationVO> allOrgs = this.getAllAffiliations();
	        String sup = "<sup>";
	        for (int x = 0; x < allOrgs.size(); x++)
	        {
	            List <OrganizationVO> creatorOrgs = creator.getPerson().getOrganizations();
	            for (int y = 0; y < creatorOrgs.size(); y++)
	            {
    	            if (creatorOrgs.get(y).getName().getValue()
    	                    .equalsIgnoreCase(allOrgs.get(x).getName().getValue()))
    	            {  	                 
    	                sup += x+1;  
                        sup += ", ";
    	            }
	            }
	        }
	        //Delete last comma of string sequence
	        sup = sup.substring(0, sup.length()-2);
	        sup += "</sup>";
	        CreatorDisplayVO creatorDisplay = new CreatorDisplayVO(creator, sup);
	        creatorsDisplay.add(creatorDisplay);
	    }
	    
	    return creatorsDisplay;
	}
}
