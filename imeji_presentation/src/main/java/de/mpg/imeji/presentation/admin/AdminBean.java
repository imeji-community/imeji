/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.admin;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.ImejiBean2RDF;
import de.mpg.imeji.logic.ImejiJena;
import de.mpg.imeji.logic.ImejiRDF2Bean;
import de.mpg.imeji.logic.controller.AlbumController;
import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.search.Search;
import de.mpg.imeji.logic.search.Search.SearchType;
import de.mpg.imeji.logic.search.vo.SortCriterion;
import de.mpg.imeji.logic.vo.Album;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Grant;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;

/**
 * Bean for the administration page. Methods working on data
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class AdminBean
{
    private SessionBean sb;
    private static Logger logger = Logger.getLogger(AdminBean.class);

    public AdminBean()
    {
        sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
    }

    /**
     * Initialize the full text index for all elements
     * 
     * @throws Exception
     */
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

    /**
     * Here are called all methods related to data cleaning
     * 
     * @throws Exception
     */
    public void clean() throws Exception
    {
        logger.warn("CLEAN DATA CALLED!!!");
        cleanGrants();
        logger.warn("CLEAN DATA DONE!!!");
    }

    /**
     * Clean grants which are not related to a user
     * 
     * @throws Exception
     */
    private void cleanGrants() throws Exception
    {
        logger.info("Searching not bounded grants...");
        Search search = new Search(SearchType.ALL, null);
        List<String> uris = search
                .searchSimpleForQuery(
                        "PREFIX fn: <http://www.w3.org/2005/xpath-functions#> SELECT DISTINCT ?s WHERE { ?s <http://imeji.org/terms/grantType> ?type . not exists{ ?user <http://imeji.org/terms/grant> ?s}}",
                        new SortCriterion());
        logger.info("...found " + uris.size());
        ImejiRDF2Bean reader = new ImejiRDF2Bean(ImejiJena.userModel);
        List<Object> l = new ArrayList<Object>();
        for (String uri : uris)
        {
            l.add(reader.load(uri, sb.getUser(), new Grant()));
        }
        logger.info("Removing the grants...");
        ImejiBean2RDF writer = new ImejiBean2RDF(ImejiJena.userModel);
        writer.delete(l, sb.getUser());
        logger.info("...done");
    }

    /**
     * return count of all {@link Album}
     * @return
     */
    public int getAllAlbumsSize()
    {
        AlbumController ac = new AlbumController(sb.getUser());
        return ac.countAllAlbums();
    }

    /**
     * return count of all {@link CollectionImeji}
     * @return
     */
    public int getAllCollectionsSize()
    {
        CollectionController cc = new CollectionController(sb.getUser());
        return cc.countAllCollections();
    }

    /**
     * return count of all {@link Item}
     * @return
     */
    public int getAllImagesSize()
    {
        ItemController ic = new ItemController(sb.getUser());
        return ic.allImagesSize();
    }

    /**
     * Return all {@link User}
     * @return
     */
    public List<User> getAllUsers()
    {
        UserController uc = new UserController(sb.getUser());
        return (List<User>)uc.retrieveAll();
    }

    /**
     * return count of all {@link User}
     * @return
     */
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
