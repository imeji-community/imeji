/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.imeji.mdProfile;

import java.io.Serializable;

import org.apache.log4j.Logger;

import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.util.UrlHelper;
import de.mpg.jena.controller.ProfileController;

public class CreateMdProfileBean extends MdProfileBean implements Serializable
{
    private SessionBean session;
    private static Logger logger = Logger.getLogger(CreateMdProfileBean.class);

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
				BeanHelper.info(session.getMessage("success_profile_save"));
			} 
            catch (Exception e) 
			{
				 BeanHelper.error(session.getMessage("error_profile_save"));
				 logger.error(session.getMessage("error_profile_save"), e);
			}
        }
        return "pretty:";
    }

    @Override
    protected String getNavigationString()
    {
        return "pretty:createProfile";
    }
}
