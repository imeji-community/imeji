package de.mpg.imeji.mdProfile;

import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.util.UrlHelper;
import de.mpg.jena.controller.ProfileController;

public class ViewMdProfileBean extends MdProfileBean
{
    private SessionBean session;
    private ProfileController profileController;
    private boolean init = false;

    public ViewMdProfileBean()
    {
        super();
        session = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        profileController = new ProfileController(session.getUser());
        init = UrlHelper.getParameterBoolean("init");

    }

    public String getInit()
    {
        if (init)
        {
            if (this.getId() != null)
            {
                try
                {
                    this.setProfile(profileController.retrieve(this.getId()));
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
        }
        super.getInit();
        return "";
    }

    public String save()
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
        return "pretty:";
    }

    @Override
    protected String getNavigationString()
    {
        return "pretty:editProfile";
    }
}
