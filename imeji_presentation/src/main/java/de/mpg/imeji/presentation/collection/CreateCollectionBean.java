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
import de.mpg.imeji.presentation.beans.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.ImejiFactory;
import de.mpg.imeji.presentation.util.UrlHelper;

public class CreateCollectionBean extends CollectionBean
{
    private String reset;
    private CollectionController collectionController = null;
    private SessionBean sessionBean = null;
    private CollectionSessionBean collectionSession = null;

    public CreateCollectionBean()
    {
        super();
        collectionSession = (CollectionSessionBean)BeanHelper.getSessionBean(CollectionSessionBean.class);
        sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        collectionController = new CollectionController(sessionBean.getUser());
        super.setTab(TabType.COLLECTION);
        super.setCollection(collectionSession.getActive());
        super.getProfilesMenu().add(new SelectItem("sdsdss", "sdsad"));
        if (UrlHelper.getParameterBoolean("reset"))
        {
            this.reset();
        }
    }

    public String loadProfile()
    {
        return getNavigationString();
    }

    public String save() throws Exception
    {
        if (valid())
        {
            ProfileController profileController = new ProfileController(sessionBean.getUser());
            MetadataProfile mdp = new MetadataProfile();
            mdp.setDescription(getCollection().getMetadata().getDescription());
            mdp.setTitle(getCollection().getMetadata().getTitle());
            URI profile = profileController.create(mdp);
            collectionController.create(getCollection(), profile);
            BeanHelper.info(sessionBean.getMessage("success_collection_create"));
            FacesContext.getCurrentInstance().getExternalContext().redirect(((Navigation)BeanHelper.getApplicationBean(Navigation.class)).getApplicationUrl()+ "collections?q=");
            return "";
        }
        else return "";
       
    }

    public void reset()
    {
        super.setCollection(new CollectionImeji());
        super.getCollection().getMetadata().setTitle("");
        super.getCollection().getMetadata().setDescription("");
        super.getCollection().getMetadata().getPersons().clear();
        super.getCollection().getMetadata().getPersons().add(ImejiFactory.newPerson());
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
}
