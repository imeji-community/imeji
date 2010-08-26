package de.mpg.imeji.mdProfile;

import javax.faces.context.FacesContext;

import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.jena.controller.ProfileController;

public class CreateMdProfileBean extends MdProfileBean
{   
    private SessionBean session;
    public CreateMdProfileBean()
    {
       super();
       session = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
       if ("1".equals(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("reset")))
       {
           this.reset();
       }
    }
    
    public String save()
    {
        ProfileController controller = new ProfileController(session.getUser());
        controller.update(this.getProfile());
        BeanHelper.info(session.getMessage("profile_save_success"));
        return "pretty:";
    }
}
