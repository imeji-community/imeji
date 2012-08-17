/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.mdProfile;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.controller.ProfileController;
import de.mpg.imeji.presentation.beans.SessionBean;
import de.mpg.imeji.presentation.mdProfile.wrapper.StatementWrapper;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.ImejiFactory;
import de.mpg.imeji.presentation.util.UrlHelper;

public class CreateMdProfileBean extends MdProfileBean
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
