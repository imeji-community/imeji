/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.imeji.mdProfile;

import java.io.Serializable;

import org.apache.log4j.Logger;

import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.jena.controller.ProfileController;

public class ViewMdProfileBean extends MdProfileBean implements Serializable
{
    private SessionBean session;
    private ProfileController profileController;
    
    private static Logger logger = Logger.getLogger(ViewMdProfileBean.class);
    
    public ViewMdProfileBean()
    {
        super();
        session = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        profileController = new ProfileController(session.getUser());
    }

    public String getInit()
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
             BeanHelper.error(session.getLabel("error") + "  No profile Id found in URL");
         }
        super.getInit();
        return "";
    }

    public String save()
    {
    	try 
        {	
    		 profileController.update(this.getProfile());
    		 session.getProfileCached().clear();
    		 BeanHelper.info(session.getMessage("success_profile_save"));
		} 
    	catch (Exception e) 
		{
			 BeanHelper.error(session.getMessage("error_profile_save"));
			 logger.error(session.getMessage("error_profile_save"), e);
		}
        return "pretty:";
    }

    @Override
    protected String getNavigationString()
    {
        return "pretty:editProfile";
    }
}
