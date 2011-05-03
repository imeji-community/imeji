package de.mpg.imeji.mdProfile;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import de.mpg.imeji.beans.Navigation;
import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.collection.CollectionSessionBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.util.UrlHelper;
import de.mpg.jena.controller.ProfileController;

public class EditMdProfileBean extends MdProfileBean
{
    private SessionBean session;
    private CollectionSessionBean collectionSession;
    private ProfileController profileController;
    private boolean init = false;
    private String colId = null;

    public EditMdProfileBean()
    {
        super();
        session = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        collectionSession = (CollectionSessionBean)BeanHelper.getSessionBean(CollectionSessionBean.class);
        profileController = new ProfileController(session.getUser());
    }

    public String getInit()
    {
    	String col = UrlHelper.getParameterValue("col");
    	if (col != null && !"".equals(col)) colId = col;
    	init = UrlHelper.getParameterBoolean("init");
    	if (init)  
        {
            if (this.getId() != null)
            {
                try
                {
                    this.setProfile(profileController.retrieve(this.getId()));
                    collectionSession.setProfile(this.getProfile());
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
            }
            else
            {
                BeanHelper.error("No profile Id found in URL");
            }
            init = false;
            setTemplate(null);
        }
        super.getInit();
        return "";
    }
    
    public String cancel() throws IOException
    {
    	Navigation navigation = (Navigation) BeanHelper.getApplicationBean(Navigation.class);
    	if (colId != null)
    		FacesContext.getCurrentInstance().getExternalContext().redirect(navigation.getApplicationUri() + "/collection/" + colId + "/details?init=1");
        return "";
    }
    
    public String save() throws IOException
    {
        if(validateProfile(this.getProfile()))
        {
        	try 
            {	
        		profileController.update(this.getProfile());
  			} 
            catch (Exception e) 
  			{
  				 BeanHelper.error("Error saving profile");
  			}
            BeanHelper.info("Metadata Profile updates successfully!");
            cancel();
        }
//        Navigation navigation = (Navigation) BeanHelper.getApplicationBean(Navigation.class);
//        FacesContext.getCurrentInstance().getExternalContext().redirect(navigation.getApplicationUri() + "/collection/" + colId + "/details?init=1");
        return "";
    }

    public void titleListener(ValueChangeEvent event)
    {
    	 if (event.getNewValue() != null && event.getNewValue() != event.getOldValue())
    	 {
    		 this.getProfile().setTitle(event.getNewValue().toString());
    	 }
    }
    
    @Override
    protected String getNavigationString()
    {
        return "pretty:editProfile";
    }

	public String getColId() {
		return colId;
	}

	public void setColId(String colId) {
		this.colId = colId;
	}
    
    
}
