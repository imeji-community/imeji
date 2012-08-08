/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.admin;

import java.util.List;

import de.mpg.imeji.logic.controller.AlbumController;
import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.beans.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;

public class AdminBean
{
    private SessionBean sb;

    public AdminBean()
    {
        sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
    }

    public void reIndex() throws Exception
    {
        ItemController ic = new ItemController(sb.getUser());
        List<Item> items = (List<Item>)ic.retrieveAll();
        for(Item item : items)
        {
            for (Metadata md : item.getMetadataSet().getMetadata())
            {
                md.indexFulltext();
            }
        }
        System.out.println("Start update");
        ic.update(items);
    }

    public int getAllAlbumsSize()
    {
        AlbumController ac = new AlbumController(sb.getUser());
        return ac.countAllAlbums();
    }

    public int getAllCollectionsSize()
    {
        CollectionController cc = new CollectionController(sb.getUser());
        return cc.countAllCollections();
    }

    public int getAllImagesSize()
    {
        ItemController ic = new ItemController(sb.getUser());
        return ic.allImagesSize();
    }

    public List<User> getAllUsers()
    {
        UserController uc = new UserController(sb.getUser());
        return (List<User>)uc.retrieveAll();
    }

    public int getAllUsersSize()
    {
        try
        {
            return this.getAllUsers().size();
        }
        catch (Exception e)
        {
            return 0;
        }
    }
}
