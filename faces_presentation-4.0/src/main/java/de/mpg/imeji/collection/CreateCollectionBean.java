package de.mpg.imeji.collection;

import javax.faces.context.FacesContext;

import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.vo.CollectionVO;
import de.mpg.imeji.vo.util.ImejiFactory;

public class CreateCollectionBean extends CollectionBean
{
    private String reset;

    public CreateCollectionBean()
    {
        super();
        this.tab = TabType.COLLECTION;
        collection = collectionSession.getActive();
        if ("1".equals(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("reset")))
        {
            this.reset();
        }
    }

    public void save() throws Exception
    {
        if (valid())
        {
            collection.setProfile(mdProfileBean.getMdProfile());
            collection.getProfile().setName(collection.getMetadata().getTitle());
            collection.getProfile().setDescription(collection.getMetadata().getDescription());
            collectionController.create(collection);
            BeanHelper.info("collection_success_create");
        }
    }

    public void reset()
    {
        collection = new CollectionVO();
        collection.getMetadata().setTitle("");
        collection.getMetadata().setDescription("");
        collection.getMetadata().getPersons().clear();
        collection.addPerson(0, ImejiFactory.newPersonVO());
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
