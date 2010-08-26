package de.mpg.imeji.collection;

import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.jena.controller.CollectionController;

public class EditCollectionBean extends CollectionBean
{
    private CollectionController collectionController;
    private SessionBean sessionBean;

    public EditCollectionBean()
    {
        super();
        sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        collectionController = new CollectionController(sessionBean.getUser());
    }

    public void init()
    {
    }

    public void save() throws Exception
    {
        if (valid())
        {
            collectionController.update(super.getCollection());
            BeanHelper.info("collection_success_create");
        }
    }

    @Override
    protected String getNavigationString()
    {
        return "pretty:editCollection";
    }
}
