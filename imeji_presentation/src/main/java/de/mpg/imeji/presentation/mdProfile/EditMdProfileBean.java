package de.mpg.imeji.presentation.mdProfile;

import java.io.IOException;
import java.io.Serializable;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.controller.ProfileController;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.beans.SessionBean;
import de.mpg.imeji.presentation.collection.CollectionSessionBean;
import de.mpg.imeji.presentation.collection.ViewCollectionBean;
import de.mpg.imeji.presentation.mdProfile.wrapper.StatementWrapper;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.UrlHelper;

public class EditMdProfileBean extends MdProfileBean implements Serializable
{
    private SessionBean session;
    private CollectionSessionBean collectionSession;
    private ProfileController profileController;
    private boolean init = false;
    private String colId = null;
    private CollectionImeji collection = null;
    private static Logger logger = Logger.getLogger(EditMdProfileBean.class);

    public EditMdProfileBean()
    {
        super();
        session = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        collectionSession = (CollectionSessionBean)BeanHelper.getSessionBean(CollectionSessionBean.class);
        readUrl();
    }

    @Override
    public String getInit()
    {
        profileController = new ProfileController(session.getUser());
        try
        {
            readUrl();
            if (init)
            {
                if (this.getId() != null)
                {
                    try
                    {
                        ((ViewCollectionBean)BeanHelper.getSessionBean(ViewCollectionBean.class)).setId(getColId());
                        ((ViewCollectionBean)BeanHelper.getSessionBean(ViewCollectionBean.class)).init();
                        setProfile(((ViewCollectionBean)BeanHelper.getSessionBean(ViewCollectionBean.class))
                                .getProfile());
                        // this.setProfile(profileController.retrieve(this.getId()));
                        // collectionSession.setProfile(this.getProfile());
                        // collection = ObjectLoader.loadCollection(ObjectHelper.getURI(CollectionImeji.class, colId),
                        // session.getUser());
                    }
                    catch (Exception e)
                    {
                        throw new RuntimeException(e);
                    }
                }
                else
                {
                    BeanHelper.error(session.getLabel("error") + ": No profile Id found in URL");
                }
                init = false;
                setTemplate(null);
            }
            super.getInit();
        }
        catch (Exception e)
        {
            BeanHelper.error(e.getMessage());
            e.printStackTrace();
        }
        return "";
    }

    public void readUrl()
    {
        String col = UrlHelper.getParameterValue("col");
        if (col != null && !"".equals(col))
        {
            colId = col;
        }
        init = UrlHelper.getParameterBoolean("init");
    }

    public String cancel() throws IOException
    {
        Navigation navigation = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
        if (colId != null)
            FacesContext.getCurrentInstance().getExternalContext()
                    .redirect(navigation.getApplicationUri() + "/collection/" + colId + "/details?init=1");
        return "";
    }

    public String save() throws IOException
    {
        getProfile().setStatements(getUnwrappedStatements());
        if (validateProfile(getProfile()))
        {
            try
            {
//                getProfile().getStatements().clear();
//                for (StatementWrapper wrapper : getStatements())
//                {
//                    getProfile().getStatements().add(wrapper.getAsStatement());
//                }
                profileController.update(getProfile());
                session.getProfileCached().clear();
                BeanHelper.info(session.getMessage("success_profile_save"));
            }
            catch (Exception e)
            {
                BeanHelper.error(session.getMessage("error_profile_save"));
                logger.error(session.getMessage("error_profile_save"), e);
            }
            cancel();
        }
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

    public String getColId()
    {
        return colId;
    }

    public void setColId(String colId)
    {
        this.colId = colId;
    }
}
