package de.mpg.imeji.mdProfile;

import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.util.UrlHelper;
import de.mpg.jena.controller.ProfileController;

public class EditMdProfileBean extends MdProfileBean
{
    private SessionBean session;
    private ProfileController profileController;
    private boolean init = false;

    public EditMdProfileBean()
    {
        super();
        session = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        profileController = new ProfileController(session.getUser());
        init = UrlHelper.getParameterBoolean("init");
    }

    public void init()
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
        super.init();
    }

    public String save()
    {
        if(validateProfile())
        {
            profileController.update(this.getProfile());
            BeanHelper.info("Metadata Profile updates successfully!");
        }
        
        return "pretty:";
    }

    @Override
    protected String getNavigationString()
    {
        return "pretty:editProfile";
    }
}
