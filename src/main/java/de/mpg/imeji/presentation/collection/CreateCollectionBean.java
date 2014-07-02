/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.collection;

import java.net.URI;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.controller.ProfileController;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.presentation.beans.Navigation;
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
@ManagedBean(name = "CreateCollectionBean")
@SessionScoped
public class CreateCollectionBean extends CollectionBean
{
    private static final long serialVersionUID = 1257698224590957642L;

    /**
     * Bean Constructor
     */
    public CreateCollectionBean()
    {
        super();
    }

    /**
     * Method called when paged is loaded (defined in pretty-config.xml)
     */
    public void init()
    {
        if (UrlHelper.getParameterBoolean("reset"))
            setCollection(ImejiFactory.newCollection());
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
            MetadataProfile mdp = new MetadataProfile();
            // Create profile
            ProfileController profileController = new ProfileController();
            mdp.setDescription(getCollection().getMetadata().getDescription());
            mdp.setTitle(getCollection().getMetadata().getTitle());
            URI profile = profileController.create(mdp, sessionBean.getUser());
            // Create collection
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
        this.init();
        return nav.getCollectionsUrl() + "?q=";
    }

    @Override
    protected String getNavigationString()
    {
        return "pretty:createCollection";
    }
}
