package de.mpg.imeji.collection;

import de.mpg.imeji.beans.SessionBean;
import de.mpg.jena.controller.CollectionController;

public class ViewCollectionBean extends CollectionBean
{
    private CollectionController collectionController = null;
    private SessionBean sessionBean = null;

    public ViewCollectionBean()
    {
    }

    public void init()
    {
        collectionController = new CollectionController(sessionBean.getUser());
        //collectionController.search(sessionBean.getUser(), null);
        tab = TabType.HOME;
    }

    public void next()
    {
        switch (tab)
        {
            case HOME:
                tab = TabType.COLLECTION;
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
            case COLLECTION:
                tab = TabType.HOME;
                break;
            case PROFILE:
                tab = TabType.COLLECTION;
                break;
            default:
                break;
        }
    }
}
