package de.mpg.imeji.presentation.mdProfile;

import java.io.IOException;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.controller.ProfileController;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.collection.ViewCollectionBean;
import de.mpg.imeji.presentation.mdProfile.wrapper.StatementWrapper;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.ImejiFactory;
import de.mpg.imeji.presentation.util.UrlHelper;

/**
 * Java Bean for the edit metadata Profile page
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class EditMdProfileBean extends MdProfileBean
{
    private SessionBean session;
    private boolean init = false;
    private String colId = null;
    private static Logger logger = Logger.getLogger(EditMdProfileBean.class);

    /**
     * Constructor
     */
    public EditMdProfileBean()
    {
        super();
        session = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        readUrl();
    }

    @Override
    public String getInit()
    {
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
            logger.error("Error Initializing profile editor", e);
        }
        return "";
    }

    /**
     * Parse the url parameters
     */
    public void readUrl()
    {
        String col = UrlHelper.getParameterValue("col");
        if (col != null && !"".equals(col))
        {
            colId = col;
        }
        init = UrlHelper.getParameterBoolean("init");
    }

    /**
     * Method when cancel button is clicked
     * 
     * @return
     * @throws IOException
     */
    public String cancel() throws IOException
    {
        Navigation navigation = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
        if (colId != null)
            FacesContext.getCurrentInstance().getExternalContext()
                    .redirect(navigation.getCollectionUrl() + colId + "/" + navigation.getInfosPath() + "?init=1");
        return "";
    }

    /**
     * Method when save button is clicked
     * 
     * @return
     * @throws IOException
     */
    public String save() throws IOException
    {
        getProfile().setStatements(getUnwrappedStatements());
        int pos = 0;
        // Set the position of the statement (used for the sorting later)
        for (Statement st : getProfile().getStatements())
        {
            st.setPos(pos);
            pos++;
        }
        if (validateProfile(getProfile()))
        {
            try
            {
                ProfileController profileController = new ProfileController();
                profileController.update(getProfile(), session.getUser());
                if (cleanMetadata)
                    profileController.removeMetadataWithoutStatement();
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

    /**
     * Listener for the title input
     * 
     * @param event
     */
    public void titleListener(ValueChangeEvent event)
    {
        if (event.getNewValue() != null && event.getNewValue() != event.getOldValue())
        {
            this.getProfile().setTitle(event.getNewValue().toString());
        }
    }

    /**
     * Method when button addfirstStatement
     * 
     * @return
     */
    public String addFirstStatement()
    {
        Statement firstStatement = ImejiFactory.newStatement();
        getWrappers().add(new StatementWrapper(firstStatement, getProfile().getId(), getLevel(firstStatement)));
        return getNavigationString();
    }

    @Override
    protected String getNavigationString()
    {
        return "pretty:editProfile";
    }

    /**
     * getter
     * 
     * @return
     */
    public String getColId()
    {
        return colId;
    }

    /**
     * setter
     * 
     * @param colId
     */
    public void setColId(String colId)
    {
        this.colId = colId;
    }
}
