package de.mpg.imeji.album;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.beans.SuperContainerBean;
import de.mpg.imeji.collection.CollectionBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.util.ImejiFactory;
import de.mpg.jena.controller.AlbumController;
import de.mpg.jena.controller.CollectionController;
import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.controller.SortCriterion;
import de.mpg.jena.controller.SearchCriterion.ImejiNamespaces;
import de.mpg.jena.controller.SortCriterion.SortOrder;
import de.mpg.jena.vo.Album;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.Properties.Status;

public class AlbumsBean extends SuperContainerBean<AlbumBean>
{

    private int totalNumberOfRecords;
    private SessionBean sb;
  
	public AlbumsBean()
    {
        super();
        this.sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
      
    }

    @Override
    public String getNavigationString()
    {
        return "pretty:albums";
    }

    @Override
    public int getTotalNumberOfRecords()
    {
       return totalNumberOfRecords;
    }

    @Override
    public List<AlbumBean> retrieveList(int offset, int limit)
    {
        AlbumController controller = new AlbumController(sb.getUser());
        
        Collection<Album> albums = new ArrayList<Album>();

        try
        {
        
        SortCriterion sortCriterion = new SortCriterion();
        sortCriterion.setSortingCriterion(ImejiNamespaces.valueOf(getSelectedSortCriterion()));
        sortCriterion.setSortOrder(SortOrder.valueOf(getSelectedSortOrder()));
        
        totalNumberOfRecords = controller.search(new ArrayList<SearchCriterion>(), sortCriterion, limit, offset).size();
        
       
        albums = controller.search(new ArrayList<SearchCriterion>(), sortCriterion, limit, offset);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return ImejiFactory.albumListToBeanList(albums);
    }

    
    public SessionBean getSb() {
		return sb;
	}

	public void setSb(SessionBean sb) {
		this.sb = sb;
	}
	
	public String selectAll() {
		for(AlbumBean bean: getCurrentPartList()){
			if(bean.getAlbum().getProperties().getStatus() != Status.RELEASED){
				bean.setSelected(true);
				if(!(sb.getSelectedAlbums().contains(bean.getAlbum().getId())))
					sb.getSelectedAlbums().add(bean.getAlbum().getId());
			}
		}
		return "";
	}
	
	public String selectNone(){
		sb.getSelectedAlbums().clear();
		return "";
	}
	
	public String deleteAll() throws Exception{
		for(URI uri : sb.getSelectedAlbums()){
			AlbumController albumController = new AlbumController(sb.getUser());
			Album album = albumController.retrieve(uri);
			albumController.delete(album, sb.getUser());
		}
		sb.getSelectedAlbums().clear();
		return "pretty:albums";
	}

   
}
