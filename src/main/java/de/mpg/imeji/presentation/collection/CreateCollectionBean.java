/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.collection;

import java.net.URI;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.controller.ProfileController;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.ImejiFactory;
import de.mpg.imeji.presentation.util.UrlHelper;

/**
 * Java Bean for the create Collection Page
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class CreateCollectionBean extends CollectionBean
{
    private String reset;
    private SessionBean sessionBean = null;
    private CollectionSessionBean collectionSession = null;

    /**
     * Bean Constructor
     */
    public CreateCollectionBean()
    {
        super();
        collectionSession = (CollectionSessionBean)BeanHelper.getSessionBean(CollectionSessionBean.class);
        sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        super.setTab(TabType.COLLECTION);
        super.setCollection(collectionSession.getActive());
        super.getProfilesMenu().add(new SelectItem("sdsdss", "sdsad"));
        if (UrlHelper.getParameterBoolean("reset"))
        {
            this.reset();
        }
    }

    /**
     * Load the Profile TODO check if this is used...
     * 
     * @return
     */
    public String loadProfile()
    {
        return getNavigationString();
    }

    /**
     * Method for save button. Create the {@link CollectionImeji} according to the form
     * 
     * @return
     * @throws Exception
     */
    public String save() throws Exception
    {
        if (valid())
        {
            ProfileController profileController = new ProfileController();
            MetadataProfile mdp = new MetadataProfile();
            mdp.setDescription(getCollection().getMetadata().getDescription());
            mdp.setTitle(getCollection().getMetadata().getTitle());
            URI profile = profileController.create(mdp, sessionBean.getUser());
            CollectionController collectionController = new CollectionController();
            collectionController.create(getCollection(), profile, sessionBean.getUser());
            BeanHelper.info(sessionBean.getMessage("success_collection_create"));
            FacesContext
                    .getCurrentInstance()
                    .getExternalContext()
                    .redirect(
                            ((Navigation)BeanHelper.getApplicationBean(Navigation.class)).getApplicationUrl()
                                    + "collections?q=");
            return "";
        }
        else
            return "";
    }

    /**
     * Return the link for the Cancel button
     * 
     * @return
     */
    public String getCancel()
    {
        Navigation nav = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
        this.reset();
        return nav.getCollectionsUrl() + "?q=";
    }

    /**
     * Method for Rest button. Reset all form value to empty value
     */
    public void reset()
    {
        setCollection(ImejiFactory.newCollection());
        collectionSession.setActive(super.getCollection());
        reset = "0";
    }

    @Override
    protected String getNavigationString()
    {
        return "pretty:createCollection";
    }

    /**
     * @return the reset
     */
    public String getReset()
    {
        return reset;
    }

    /**
     * @param reset the reset to set
     */
    public void setReset(String reset)
    {
        this.reset = reset;
    }

    /*
     * (non-Javadoc)
     * @see de.mpg.imeji.presentation.collection.CollectionBean#isVisible()
     */
    @Override
    public boolean isVisible()
    {
        return true;
    }
}
