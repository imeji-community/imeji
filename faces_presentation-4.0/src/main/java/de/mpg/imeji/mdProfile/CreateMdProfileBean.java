package de.mpg.imeji.mdProfile;

import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.util.UrlHelper;
import de.mpg.jena.controller.ProfileController;

public class CreateMdProfileBean extends MdProfileBean
{
    private SessionBean session;

    public CreateMdProfileBean()
    {
        super();
        session = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        if (UrlHelper.getParameterBoolean("reset"))
        {
            this.reset();
        }
    }

    public String save()
    {
        if (validateProfile(getProfile()))
        {
            ProfileController controller = new ProfileController(session.getUser());
            try 
            {	
				controller.update(getProfile());
			} 
            catch (Exception e) 
			{
				 BeanHelper.error("Error saving profile");
			}
            BeanHelper.info(session.getMessage("profile_save_success"));
        }
        return "pretty:";
    }

    @Override
    protected String getNavigationString()
    {
        return "pretty:createProfile";
    }
}
