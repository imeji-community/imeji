package de.mpg.escidoc.faces.mdProfile.beans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import de.mpg.escidoc.faces.beans.SessionBean;
import de.mpg.escidoc.faces.container.beans.MdProfileSession;
import de.mpg.escidoc.faces.mdProfile.MdProfileController;
import de.mpg.escidoc.faces.mdProfile.MdProfileVO;
import de.mpg.escidoc.faces.metadata.Metadata;
import de.mpg.escidoc.faces.metadata.MetadataBean;
import de.mpg.escidoc.faces.metadata.MetadataBean.ConstraintBean;
import de.mpg.escidoc.faces.util.BeanHelper;

public class MdProfileBean
{
    public enum PageType
    {
    	CREATE, EDIT, VIEW;
    }
    
    private List<Metadata> metadataList = new ArrayList<Metadata>();
    private List<SelectItem> metadataMenu = new ArrayList<SelectItem>();
    private List<MetadataBean> metadataBeanList = new ArrayList<MetadataBean>();
    private MdProfileSession session = null;
    private PageType type = PageType.CREATE;
    private MdProfileController controller = null;
    private SessionBean sessionBean = null;
    private int profilePosition;
    
  
    /**
     * Default Constructor
     */
    public MdProfileBean()
    {
	session = (MdProfileSession) BeanHelper.getSessionBean(MdProfileSession.class);
	sessionBean = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
	controller = new MdProfileController();
	
	metadataList = session.getMetadataList();
	metadataBeanList = session.getMetadataBeanList();
	
	initRequest();
	init();
    }
    
    public void init()
    {
	for (Metadata m : metadataList)
	{
	    if (m.getSimpleValue() == null)
	    {
		m.setSimpleValue("");
	    }
	    metadataMenu.add(new SelectItem(m.getIndex(), m.getLabel()));
	}

	if (metadataBeanList.size() == 0)
	{
	    metadataBeanList.add(new MetadataBean(metadataList));
	}
	
	if (PageType.EDIT.equals(type))
	{
	    //do what sould be done
	}
	
    }
    
    /**
     * Read Http request and initialize parameters
     */
    public void initRequest()
    {
	HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
	
	if ("reset".equalsIgnoreCase(request.getParameter("init")))		
	{
	    reset();
	}
	
	if (request.getParameter("type") != null)
	{
	    for (PageType p : PageType.values())
	    {
		if (p.name().equalsIgnoreCase(request.getParameter("type")))
		{
		    type = p;
		}
	    }
	}
    }
    
    public void reset()
    {
	//TODO
	metadataBeanList = new ArrayList<MetadataBean>();
	session.setProfileName("");
	
	session.setMdProfile(new MdProfileVO());
	session.getMetadataBeanList().clear();
    }
    
    /**
     * JSF Method of save button:
     * <br> Create if profile is new
     * <br> Edit if profile already exists
     */
    public void save()
    {
	MdProfileVO profile = new MdProfileVO();
	int i = 0;
	for (MetadataBean m : session.getMetadataBeanList())
	{
	   // profile.getMetadataList().add(new Metadata(m.getCurrent().getName(), m.getCurrent().getIndex(), m.getCurrent().getNamespace()));
	    profile.getMetadataList().add(new Metadata(m.getCurrent()));
	    profile.getMetadataList().get(i).getConstraint().clear();
	    
	    for (ConstraintBean c : m.getConstraints())
	    {
		if (!"".equals(c.getValue()))
		{
		    profile.getMetadataList().get(i).getConstraint().add(c.getValue());
		} 
	    }
	    i++;
	}
	session.getMdProfile().setMetadataList(profile.getMetadataList());
	
	switch (type)
	{
	case CREATE:
	    controller.create(session.getMdProfile(), sessionBean.getUserHandle());
	    sessionBean.setInformation("Metadata Profile  created!");
	    break;
	
	case EDIT:
	    
	    break;

	default:
	    break;
	}
    }
    
    /**
     * Returns the list of {@link Metadata} selected by the user.
     * @return
     */
    public List<Metadata> getSelectedMetadataList()
    {
	 List<Metadata> list = new ArrayList<Metadata>();
	    
	 for (MetadataBean mBean : metadataBeanList)
	 {
	     list.add(mBean.getCurrent());
	 }
	    
	 return list; 
    }
    
    public String addMetadata()
    {
	
	   
	    metadataBeanList.add(getProfilePosition() + 1, new MetadataBean(metadataList));
	    session.setMetadataBeanList(metadataBeanList);
	
		return "";
	}
    
    public String removeMetadata()
    {
	    metadataBeanList.remove(getProfilePosition());
	    session.setMetadataBeanList(metadataBeanList);
		return "";
		
    }
    
    public void reloadPage() 
    {
	try
	{
	    FacesContext.getCurrentInstance().getExternalContext().redirect("http://localhost:8080/faces/jsf/MdProfile.xhtml");
	} 
	catch (IOException e)
	{
	    throw new RuntimeException("Error reloading page: "+ e);
	}
    }

    


    /**
     * @return the mdProfile
     */
    public List<MetadataBean> getMetadataBeanList()
    {
        return metadataBeanList;
    }

    /**
     * @param mdProfile the mdProfile to set
     */
    public void setMetadataBeanList(List<MetadataBean> metadataBeanList)
    {
        this.metadataBeanList = metadataBeanList;
    }

    /**
     * @return the metadataMenu
     */
    public List<SelectItem> getMetadataMenu()
    {
        return metadataMenu;
    }

    /**
     * @param metadataMenu the metadataMenu to set
     */
    public void setMetadataMenu(List<SelectItem> metadataMenu)
    {
        this.metadataMenu = metadataMenu;
    }

	public void setProfilePosition(int profilePosition) {
		this.profilePosition = profilePosition;
	}

	public int getProfilePosition() {
		return profilePosition;
	}
    
    
}
