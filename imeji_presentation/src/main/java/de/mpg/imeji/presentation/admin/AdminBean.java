/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.admin;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.tdb.TDB;

import de.mpg.imeji.logic.ImejiBean2RDF;
import de.mpg.imeji.logic.ImejiJena;
import de.mpg.imeji.logic.controller.AlbumController;
import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.vo.Album;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Item;
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
        List<Object> toReindex = new ArrayList<Object>();
        ImejiBean2RDF imejiBean2RDF;
        // load items
        ItemController ic = new ItemController(sb.getUser());
        for (Item item : ic.retrieveAll())
        {
            item.indexFulltext();
            toReindex.add(item);
        }
        imejiBean2RDF = new ImejiBean2RDF(ImejiJena.imageModel);
        imejiBean2RDF.updateLazy(toReindex, sb.getUser());
        // Load collections
        toReindex = new ArrayList<Object>();
        CollectionController cc = new CollectionController(sb.getUser());
        for (CollectionImeji c : cc.retrieveAllCollections())
        {
            c.indexFulltext();
            toReindex.add(c);
        }
        imejiBean2RDF = new ImejiBean2RDF(ImejiJena.collectionModel);
        imejiBean2RDF.updateLazy(toReindex, sb.getUser());
        // Load albums
        toReindex = new ArrayList<Object>();
        AlbumController ac = new AlbumController(sb.getUser());
        for (Album a : ac.retrieveAll())
        {
            a.indexFulltext();
            toReindex.add(a);
        }
        imejiBean2RDF = new ImejiBean2RDF(ImejiJena.albumModel);
        imejiBean2RDF.updateLazy(toReindex, sb.getUser());
    }

    public void closeDataSet()
    {
        try
        {
            ImejiJena.imejiDataSet.close();
        }
        finally
        {
            ImejiJena.imejiDataSet.end();
        }
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
