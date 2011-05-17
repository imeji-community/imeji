package de.mpg.imeji.album;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import de.mpg.imeji.beans.Navigation;
import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.image.ImageBean;
import de.mpg.imeji.image.ImagesBean;
import de.mpg.imeji.image.SelectedBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.util.ImejiFactory;
import de.mpg.jena.controller.AlbumController;
import de.mpg.jena.controller.ImageController;
import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.controller.SortCriterion;
import de.mpg.jena.controller.SearchCriterion.ImejiNamespaces;
import de.mpg.jena.controller.SortCriterion.SortOrder;
import de.mpg.jena.util.ObjectHelper;
import de.mpg.jena.vo.Album;
import de.mpg.jena.vo.CollectionImeji;

public class AlbumImagesBean extends ImagesBean
{
    private int totalNumberOfRecords;
    private String id = null;
    private AlbumBean album;
    private URI uri;
    private SessionBean sb;
    private CollectionImeji collection;
    private Navigation navigation;

    public AlbumImagesBean()
    {
        super();
        this.sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        this.navigation = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
    }

    public void init()
    {
        AlbumController ac = new AlbumController(sb.getUser());
        try 
        {
        	 this.setAlbum(new AlbumBean(ac.retrieve(id)));
		} 
        catch (Exception e) 
        {
			BeanHelper.error(e.getMessage());
		}
       
    }

    @Override
    public String getNavigationString()
    {
        return "pretty:albumImages";
    }

    @Override
    public int getTotalNumberOfRecords()
    {
        return totalNumberOfRecords;
    }

    @Override
    public List<ImageBean> retrieveList(int offset, int limit) throws Exception
    {
        ImageController controller = new ImageController(sb.getUser());
    	if (reloadPage() || !uri.equals(ObjectHelper.getURI(Album.class, id)))
    	{
    		uri = ObjectHelper.getURI(Album.class, id);
    		SortCriterion sortCriterion = new SortCriterion();
 	        sortCriterion.setSortingCriterion(ImejiNamespaces.valueOf(getSelectedSortCriterion()));
 	        sortCriterion.setSortOrder(SortOrder.valueOf(getSelectedSortOrder()));
 	        List<SearchCriterion> scList = new ArrayList<SearchCriterion>();
 	        setSearchResult(controller.searchImagesInContainer(uri, scList, sortCriterion, limit, offset));
           	totalNumberOfRecords = getSearchResult().getNumberOfRecords();
           	getSearchResult().setQuery(getQuery());
			getSearchResult().setSort(sortCriterion);
    	}
    	super.setImages(controller.loadImages(getSearchResult().getResults(), limit, offset));
        return ImejiFactory.imageListToBeanList(getImages());
    }
    
    public String removeFromAlbum() throws Exception
    {
        AlbumController ac = new AlbumController(sb.getUser());
        BeanHelper.info(album.getAlbum().getImages().size() + "Images removed from album");
        album.getAlbum().getImages().clear();
        ac.update(album.getAlbum());
        AlbumBean activeAlbum = sb.getActiveAlbum();
        if (activeAlbum != null && activeAlbum.getAlbum().getId().toString().equals(album.getAlbum().getId().toString()))
        {
        	sb.setActiveAlbum(album);
        }
        SelectedBean sb = (SelectedBean) BeanHelper.getSessionBean(SelectedBean.class);
        sb.clearAll();
        return "pretty:";
    }
    
    public String getImageBaseUrl()
    {
		if (album.getAlbum() == null) return "";
        return navigation.getApplicationUri() + album.getAlbum().getId().getPath();
    }

    
    public String getBackUrl() 
    {
		return navigation.getImagesUrl() + "/album" + "/" + this.id;
	}
    
    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public void setCollection(CollectionImeji collection)
    {
        this.collection = collection;
    }

    public CollectionImeji getCollection()
    {
        return collection;
    }

    public void setAlbum(AlbumBean album)
    {
        this.album = album;
    }

    public AlbumBean getAlbum()
    {
        return album;
    }
}
