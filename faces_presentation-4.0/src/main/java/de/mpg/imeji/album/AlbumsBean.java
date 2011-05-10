package de.mpg.imeji.album;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.beans.SuperContainerBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.util.ImejiFactory;
import de.mpg.jena.controller.AlbumController;
import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.controller.SearchCriterion.ImejiNamespaces;
import de.mpg.jena.controller.SortCriterion;
import de.mpg.jena.controller.SortCriterion.SortOrder;
import de.mpg.jena.controller.UserController;
import de.mpg.jena.vo.Album;
import de.mpg.jena.vo.Properties.Status;

public class AlbumsBean extends SuperContainerBean<AlbumBean>
{
    private int totalNumberOfRecords;
    private SessionBean sb;
  
	public AlbumsBean()
    {
        super();
        sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
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
    public List<AlbumBean> retrieveList(int offset, int limit) throws Exception
    {
    	UserController uc = new UserController(sb.getUser());
    	
        if (sb.getUser() != null) 
        {
        	sb.setUser(uc.retrieve(sb.getUser().getEmail()));
        }
        
        AlbumController controller = new AlbumController(sb.getUser());
        Collection<Album> albums = new ArrayList<Album>();

        SortCriterion sortCriterion = new SortCriterion();
        sortCriterion.setSortingCriterion(ImejiNamespaces.valueOf(getSelectedSortCriterion()));
        sortCriterion.setSortOrder(SortOrder.valueOf(getSelectedSortOrder()));
        
        List<SearchCriterion> scList = new ArrayList<SearchCriterion>();
        List<SearchCriterion> scList1 = new ArrayList<SearchCriterion>();
        
        if (getFilter() != null)
        {
        	scList.add(getFilter());
        	scList1.add(getFilter());
        }
        
        totalNumberOfRecords = controller.getNumberOfResults(scList);	        
        albums = controller.search(scList1, sortCriterion, limit, offset);
        
        return ImejiFactory.albumListToBeanList(albums);
    }

    
    public SessionBean getSb() {
		return sb;
	}

	public void setSb(SessionBean sb) {
		this.sb = sb;
	}
	
	public String selectAll() 
	{
		for(AlbumBean bean: getCurrentPartList())
		{
			if(bean.getAlbum().getProperties().getStatus() != Status.RELEASED)
			{
				bean.setSelected(true);
				if(!(sb.getSelectedAlbums().contains(bean.getAlbum().getId())))
				{
					sb.getSelectedAlbums().add(bean.getAlbum().getId());
				}
			}
		}
		return "";
	}
	
	public String selectNone(){
		sb.getSelectedAlbums().clear();
		return "";
	}
	
	public String deleteAll() throws Exception
	{
		for(URI uri : sb.getSelectedAlbums()){
			AlbumController albumController = new AlbumController(sb.getUser());
			Album album = albumController.retrieve(uri);
			albumController.delete(album, sb.getUser());
		}
		sb.getSelectedAlbums().clear();
		return "pretty:albums";
	}
}
