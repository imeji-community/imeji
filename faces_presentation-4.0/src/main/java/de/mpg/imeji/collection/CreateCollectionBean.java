package de.mpg.imeji.collection;

import de.mpg.imeji.util.BeanHelper;
import de.mpg.jena.vo.Person;

public class CreateCollectionBean extends CollectionBean
{
    public CreateCollectionBean()
    {
       super();
    }
    
    public void init()
    {
        
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
}
