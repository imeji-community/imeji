/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.mdProfile;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.controller.ProfileController;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.UrlHelper;

/**
 * Java Bean for {@link MetadataProfile} create page
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class CreateMdProfileBean extends MdProfileBean
{
    private SessionBean session;
    private static Logger logger = Logger.getLogger(CreateMdProfileBean.class);

    /**
     * Bean Constructor
     */
    public CreateMdProfileBean()
    {
        super();
        session = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        if (UrlHelper.getParameterBoolean("reset"))
        {
            this.reset();
        }
    }

    /**
     * Save Button to create the {@link MetadataProfile}
     * 
     * @return
     */
    public String save()
    {
        if (validateProfile(getProfile()))
        {
            try
            {
                ProfileController controller = new ProfileController();
                controller.update(getProfile(), session.getUser());
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
