package de.mpg.escidoc.faces.mdProfile.beans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import de.mpg.escidoc.faces.metadata.Metadata;
import de.mpg.escidoc.faces.util.BeanHelper;

public class MdProfileBean
{
    private List<Metadata> metadataList = new ArrayList<Metadata>();
    private List<SelectItem> metadataMenu = new ArrayList<SelectItem>();
    private List<MetadataBean> mdProfile = new ArrayList<MetadataBean>();
    private MdProfileSession session = null;
    
    /**
     * Bean for Metadata representation in MdProfile formular
     * @author saquet
     *
     */
    public class MetadataBean
    {
	private Metadata selected = null;
	private List<Metadata> metadataList = new ArrayList<Metadata>();
	
	/**
	 * Constructor for a {@link MetadataBean}
	 * @param list
	 */
	public MetadataBean(List<Metadata> list)
	{
	    metadataList.addAll(list);
	    if (selected == null)
	    {
    	    	selected = new Metadata(metadataList.get(0).getIndex(), metadataList.get(0).getLabel());
    	    	//Correct is
    	    	// selected = new Metadata(metadataList.get(0));
	    }
	}
	
	public void menuListener(ValueChangeEvent event)
	{
	    if (event != null 
		    && event.getOldValue() != event.getNewValue())
	    {
		for (Metadata m : metadataList)
		{
		    if (m.getIndex().equals(event.getNewValue().toString()))
		    {
			selected = new Metadata(m.getIndex(), m.getLabel());
			//correct is
			// select = new Metadata(m);
		    }
		}
	    }
	}
	
	public void valueListener(ValueChangeEvent event)
	{
	    if (event != null 
		    && event.getOldValue() != event.getNewValue())
	    {
		selected.setSimpleValue(event.getNewValue().toString());
	    }
	}

	public Metadata getSelected()
	{
	    return selected;
	}

	public void setSelected(Metadata selected)
	{
	    this.selected = selected;
	}

	public List<Metadata> getMetadataList()
	{
	    return metadataList;
	}

	public void setMetadataList(List<Metadata> metadataList)
	{
	    this.metadataList = metadataList;
	}
    }
    
    /**
     * Default Constructor
     */
    public MdProfileBean()
    {
	session = (MdProfileSession)BeanHelper.getSessionBean(MdProfileSession.class);
	metadataList = session.getMetadataList();
	mdProfile = session.getMdProfile();
	
	HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
	
	if ("reset".equalsIgnoreCase(request.getParameter("init")))		
	{
	    reset();
	}
	
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

	if (mdProfile.size() == 0)
	{
	    mdProfile.add(new MetadataBean(metadataList));
	}
    }
    
    public void reset()
    {
	mdProfile = new ArrayList<MetadataBean>();
	session.setProfileName("");
	session.setMdProfile(mdProfile);
    }
    
    public void save()
    {
	
    }
    
    public void addMetadata(ActionEvent event)
    {
	if (event != null)
	{
	    int position = Integer.parseInt(event.getComponent().getAttributes().get("position").toString());
	    mdProfile.add(position + 1, new MetadataBean(metadataList));
	    session.setMdProfile(mdProfile);
	}
	reloadPage();
    }
    
    public void removeMetadata(ActionEvent event)
    {
	if (event != null)
	{
	    int position = Integer.parseInt(event.getComponent().getAttributes().get("position").toString());
	    mdProfile.remove(position);
	    session.setMdProfile(mdProfile);
	}
	reloadPage();
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
     * @return the metadataList
     */
    public List<Metadata> getMetadataList()
    {
        return metadataList;
    }

    /**
     * @param metadataList the metadataList to set
     */
    public void setMetadataList(List<Metadata> metadataList)
    {
        this.metadataList = metadataList;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return session.getProfileName();
    }

    /**
     * @param name the name to set
     */
    public void setName(String name)
    {
        session.setProfileName(name);
    }
    
    public void nameListener(ValueChangeEvent event)
    {
	 if (event != null 
		    && event.getOldValue() != event.getNewValue())
	 {
	     session.setProfileName(event.getNewValue().toString());
	 }
    }

    /**
     * @return the mdProfile
     */
    public List<MetadataBean> getMdProfile()
    {
        return mdProfile;
    }

    /**
     * @param mdProfile the mdProfile to set
     */
    public void setMdProfile(List<MetadataBean> mdProfile)
    {
        this.mdProfile = mdProfile;
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
    
    
}
