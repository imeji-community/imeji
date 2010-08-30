package de.mpg.imeji.mdProfile;

import java.util.ArrayList;
import java.util.List;

import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.mdProfile.wrapper.StatementWrapper;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.util.UrlHelper;
import de.mpg.jena.controller.ProfileController;
import de.mpg.jena.vo.MetadataProfile;
import de.mpg.jena.vo.Statement;

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
                this.setProfile(profileController.retrieve(this.getId()));
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
        for (Statement st : this.getProfile().getStatements())
        {
            System.out.println(st.getType());
            
        }
        profileController.update(this.getProfile());
        return null;
    }

    @Override
    protected String getNavigationString()
    {
        return "pretty:editProfile";
    }
}
