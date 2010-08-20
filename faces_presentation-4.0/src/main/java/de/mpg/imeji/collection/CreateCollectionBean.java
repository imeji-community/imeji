package de.mpg.imeji.collection;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.vo.util.ImejiFactory;
import de.mpg.jena.vo.CollectionImeji;

public class CreateCollectionBean extends CollectionBean
{
    private String reset;

    public CreateCollectionBean()
    {
        super();
        this.tab = TabType.COLLECTION;
        collection = collectionSession.getActive();
        super.getProfilesMenu().add(new SelectItem("sdsdss", "sdsad"));
        if ("1".equals(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("reset")))
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
            collectionController.create(collection);
            BeanHelper.info("collection_success_create");
        }
        return "pretty:collections";
    }

    public void reset()
    {
        collection = new CollectionImeji();
        collection.getMetadata().setTitle("");
        collection.getMetadata().setDescription("");
        collection.getMetadata().getPersons().clear();
        collection.getMetadata().getPersons().add(ImejiFactory.newPerson());
        collectionSession.setActive(collection);
        reset = "0";
    }

    public void next()
    {
        switch (tab)
        {
            case PROFILE:
                tab = TabType.HOME;
                break;
            case COLLECTION:
                tab = TabType.PROFILE;
                break;
            default:
                break;
        }
    }

    public void back()
    {
        switch (tab)
        {
            case HOME:
                tab = TabType.PROFILE;
                break;
            case PROFILE:
                tab = TabType.COLLECTION;
                break;
            default:
                break;
        }
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
