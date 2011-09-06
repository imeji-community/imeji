package de.mpg.imeji.album;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import thewebsemantic.NotFoundException;

import de.mpg.imeji.beans.Navigation;
import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.facet.FacetsBean;
import de.mpg.imeji.image.ImageBean;
import de.mpg.imeji.image.ImagesBean;
import de.mpg.imeji.image.SelectedBean;
import de.mpg.imeji.search.URLQueryTransformer;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.util.ImejiFactory;
import de.mpg.jena.controller.AlbumController;
import de.mpg.jena.controller.ImageController;
import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.controller.SortCriterion;
import de.mpg.jena.controller.SearchCriterion.ImejiNamespaces;
import de.mpg.jena.controller.SortCriterion.SortOrder;
import de.mpg.jena.security.Security;
import de.mpg.jena.security.Operations.OperationsType;
import de.mpg.jena.util.ObjectHelper;
import de.mpg.jena.vo.Album;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.Image;

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
    	if (album != null)
		{
			if(sb.getSelectedImagesContext()!=null && !(sb.getSelectedImagesContext().equals("pretty:albumImages" + album.getAlbum().getId().toString())))
			{
				sb.getSelected().clear();
			}
			sb.setSelectedImagesContext("pretty:albumImages" +  album.getAlbum().getId().toString());
		}
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
    	//if (reloadPage() || !uri.equals(ObjectHelper.getURI(Album.class, id)))
    	if (true)
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
			deleteNonExistingImages();
    	}
    	super.setImages(controller.loadImages(getSearchResult().getResults(), limit, offset));
        return ImejiFactory.imageListToBeanList(getImages());
    }
    
    /**
     * Since an album only reference images, this images could have been deleted in Jena but not in albums
     * @throws Exception 
     */
    public void deleteNonExistingImages() throws Exception
    {
    	if (album.getAlbum().getImages().size() != totalNumberOfRecords)
    	{
    		ImageController controller = new ImageController(sb.getUser());
    		for (int i=0 ; i < album.getAlbum().getImages().size(); i++)
    		{
    			try 
    			{
					controller.retrieve(((List<URI>) album.getAlbum().getImages()).get(i));
				} 
    			catch (NotFoundException e) 
				{
    				((List<URI>) album.getAlbum().getImages()).remove(i);
    				i--;
				}     			
    		}
    		AlbumController albumController = new AlbumController(sb.getUser());
    		albumController.update(album.getAlbum());
    	}
    	if (album.getAlbum().getImages().size() != totalNumberOfRecords)
    	{
    		BeanHelper.error(((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getMessage("album_not_allowed_see_all"));
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
        BeanHelper.info(album.getAlbum().getImages().size() + " " + sb.getMessage("success_album_remove_images"));
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
    
    public String release() 
    {
    	((AlbumBean)BeanHelper.getSessionBean(AlbumBean.class)).initView();
    	((AlbumBean)BeanHelper.getSessionBean(AlbumBean.class)).release();
        return "pretty:";
    }
    
    public String delete()
    {
    	((AlbumBean)BeanHelper.getSessionBean(AlbumBean.class)).initView();
    	((AlbumBean)BeanHelper.getSessionBean(AlbumBean.class)).delete();
    	return "pretty:albums";
    }
    
    public String withdraw() throws Exception
    {
    	((AlbumBean)BeanHelper.getSessionBean(AlbumBean.class)).initView();
    	((AlbumBean)BeanHelper.getSessionBean(AlbumBean.class)).withdraw();
    	return "pretty:";
    }
    
    public boolean isEditable()
    {
		Security security = new Security();
    	return security.check(OperationsType.UPDATE, sb.getUser(), album.getAlbum());
    }
    
    public boolean isDeletable() 
	{
		Security security = new Security();
		return security.check(OperationsType.DELETE, sb.getUser(), album.getAlbum());
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
