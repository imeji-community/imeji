/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.imeji.album;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import thewebsemantic.NotFoundException;
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
import de.mpg.jena.search.SearchResult;
import de.mpg.jena.vo.Album;
import de.mpg.jena.vo.Properties.Status;

public class AlbumsBean extends SuperContainerBean<AlbumBean> implements Serializable
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
        SortCriterion sortCriterion = new SortCriterion();
        sortCriterion.setSortingCriterion(ImejiNamespaces.valueOf(getSelectedSortCriterion()));
        sortCriterion.setSortOrder(SortOrder.valueOf(getSelectedSortOrder()));
        
        List<SearchCriterion> scList = new ArrayList<SearchCriterion>();
        
        if (getFilter() != null)
        {
        	scList.add(getFilter());
        }
              
        SearchResult searchResult = controller.search(scList, sortCriterion, limit, offset);
        totalNumberOfRecords = searchResult.getNumberOfRecords();
        return ImejiFactory.albumListToBeanList(controller.load(searchResult.getResults(), limit, offset));
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
			if(bean.getAlbum().getProperties().getStatus() == Status.PENDING)
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
	
	public String selectNone()
	{
		sb.getSelectedAlbums().clear();
		return "";
	}
	
	public String deleteAll() 
	{
		if (sb.getSelectedAlbums().size() == 0) 
		{
			BeanHelper.warn(sb.getMessage("error_delete_no_albums_selected"));
			return "pretty:albums";
		}
		
		for (AlbumBean b : getCurrentPartList())
		{
			if (b.getSelected())
			{
				b.delete();
			}
		}
		sb.getSelectedAlbums().clear();
		return "pretty:albums";
	}
}
