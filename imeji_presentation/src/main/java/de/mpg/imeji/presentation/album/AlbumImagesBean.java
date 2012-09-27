/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.album;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;

import de.mpg.imeji.logic.controller.AlbumController;
import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.search.SearchResult;
import de.mpg.imeji.logic.search.vo.SearchQuery;
import de.mpg.imeji.logic.search.vo.SortCriterion;
import de.mpg.imeji.logic.security.Operations.OperationsType;
import de.mpg.imeji.logic.security.Security;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.Album;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.beans.SessionBean;
import de.mpg.imeji.presentation.image.ImagesBean;
import de.mpg.imeji.presentation.image.ThumbnailBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.ImejiFactory;
import de.mpg.imeji.presentation.util.ObjectLoader;

public class AlbumImagesBean extends ImagesBean
{
    private int totalNumberOfRecords;
    private String id = null;
    private Album album;
    private URI uri;
    private SessionBean sb;
    private CollectionImeji collection;
    private Navigation navigation;
    private List<String> itemsUris = new ArrayList<String>();

    public AlbumImagesBean()
    {
        super();
        this.sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        this.navigation = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
    }

    public String getInit()
    {
        readUrl();
        loadAlbum();
        return "";
    }

    @Override
    public String getNavigationString()
    {
        if (album != null && album.getId() != null)
        {
            if (sb.getSelectedImagesContext() != null
                    && !(sb.getSelectedImagesContext().equals("pretty:albumBrowse" + album.getId().toString())))
            {
                sb.getSelected().clear();
            }
            sb.setSelectedImagesContext("pretty:albumBrowse" + album.getId().toString());
        }
        return "pretty:albumBrowse";
    }

    @Override
    public int getTotalNumberOfRecords()
    {
        return totalNumberOfRecords;
    }

    @Override
    public List<ThumbnailBean> retrieveList(int offset, int limit)
    {
        // readUrl();
        // loadAlbum();
        SortCriterion sortCriterion = initSortCriterion();
        ItemController controller = new ItemController(sb.getUser());
        SearchResult result = controller.searchImagesInContainer(uri, new SearchQuery(), sortCriterion, limit, offset);
        setAlbumItems(result.getResults());
        totalNumberOfRecords = result.getNumberOfRecords();
        itemsUris = result.getResults();
        result.setQuery(getQuery());
        result.setSort(sortCriterion);
        return ImejiFactory.imageListToThumbList(loadImages(result.getResults()));
    }

    public void readUrl()
    {
        uri = ObjectHelper.getURI(Album.class, id);
    }

    public void loadAlbum()
    {
        album = ObjectLoader.loadAlbumLazy(uri, sb.getUser());
    }

    public void setAlbumItems(List<String> uris)
    {
        for (String uri : uris)
        {
            album.getImages().add(URI.create(uri));
        }
    }

    @Override
    public String initFacets() throws Exception
    {
        // NO FACETs FOR ALBUMS
        return "pretty";
    }

    public String removeFromAlbum() throws Exception
    {
        AlbumController ac = new AlbumController(sb.getUser());
        int deletedCount = ac.removeFromAlbum(this.album, sb.getSelected(), sb.getUser());
        sb.getSelected().clear();
        BeanHelper.info(deletedCount + " " + sb.getMessage("success_album_remove_images"));
        return "pretty:";
    }

    public String removeFromActiveAlbum() throws Exception
    {
        AlbumController ac = new AlbumController(sb.getUser());
        int deletedCount = ac.removeFromAlbum(sb.getActiveAlbum(), sb.getSelected(), sb.getUser());
        sb.getSelected().clear();
        BeanHelper.info(deletedCount + " " + sb.getMessage("success_album_remove_images"));
        return "pretty:";
    }

    public String removeAllFromAlbum() throws Exception
    {
        AlbumController ac = new AlbumController(sb.getUser());
        int deletedCount = ac.removeFromAlbum(this.album, itemsUris, sb.getUser());
        sb.getSelected().clear();
        BeanHelper.info(deletedCount + " " + sb.getMessage("success_album_remove_images"));
        return "pretty:";
    }

    public String removeAllFromActiveAlbum() throws Exception
    {
        AlbumController ac = new AlbumController(sb.getUser());
        int deletedCount = ac.removeFromAlbum(sb.getActiveAlbum(), itemsUris, sb.getUser());
        sb.getSelected().clear();
        BeanHelper.info(deletedCount + " " + sb.getMessage("success_album_remove_images"));
        return "pretty:";
    }

    public String getImageBaseUrl()
    {
        if (album == null || album.getId() == null)
            return "";
        return navigation.getApplicationUri() + album.getId().getPath();
    }

    public String getBackUrl()
    {
        return navigation.getBrowseUrl() + "/album" + "/" + this.id;
    }

    public String release()
    {
        ((AlbumBean)BeanHelper.getSessionBean(AlbumBean.class)).setId(id);
        ((AlbumBean)BeanHelper.getSessionBean(AlbumBean.class)).initView();
        ((AlbumBean)BeanHelper.getSessionBean(AlbumBean.class)).release();
        return "pretty:";
    }

    public String delete()
    {
        ((AlbumBean)BeanHelper.getSessionBean(AlbumBean.class)).setId(id);
        ((AlbumBean)BeanHelper.getSessionBean(AlbumBean.class)).initView();
        ((AlbumBean)BeanHelper.getSessionBean(AlbumBean.class)).delete();
        return "pretty:albums";
    }

    public String withdraw() throws Exception
    {
        ((AlbumBean)BeanHelper.getSessionBean(AlbumBean.class)).setId(id);
        ((AlbumBean)BeanHelper.getSessionBean(AlbumBean.class)).initView();
        String dc = album.getDiscardComment();
        ((AlbumBean)BeanHelper.getSessionBean(AlbumBean.class)).getAlbum().setDiscardComment(dc);
        ((AlbumBean)BeanHelper.getSessionBean(AlbumBean.class)).withdraw();
        return "pretty:";
    }

    public boolean isEditable()
    {
        Security security = new Security();
        return security.check(OperationsType.UPDATE, sb.getUser(), album);
    }

    public boolean isDeletable()
    {
        Security security = new Security();
        return security.check(OperationsType.DELETE, sb.getUser(), album);
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
        //@Ye set session value to share with AlbumImageBean, another way is via injection
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("AlbumImagesBean.id", id);
    }

    public void setCollection(CollectionImeji collection)
    {
        this.collection = collection;
    }

    public CollectionImeji getCollection()
    {
        return collection;
    }

    public void setAlbum(Album album)
    {
        this.album = album;
    }

    public Album getAlbum()
    {
        return album;
    }
}
