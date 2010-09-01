package de.mpg.imeji.album;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.beans.SuperContainerBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.jena.controller.AlbumController;
import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.controller.SortCriterion;
import de.mpg.jena.controller.SearchCriterion.ImejiNamespaces;
import de.mpg.jena.controller.SortCriterion.SortOrder;
import de.mpg.jena.vo.Album;

public class AlbumsBean extends SuperContainerBean<Album>
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
    public List<Album> retrieveList(int offset, int limit)
    {
        AlbumController controller = new AlbumController(sb.getUser());
        
        Collection<Album> albums = controller.retrieveAll();
        totalNumberOfRecords = albums.size();
        
        
        SortCriterion sortCriterion = new SortCriterion();
        sortCriterion.setSortingCriterion(ImejiNamespaces.valueOf(getSelectedSortCriterion()));
        sortCriterion.setSortOrder(SortOrder.valueOf(getSelectedSortOrder()));
        
        
        try
        {
            albums = controller.search(null, new ArrayList<SearchCriterion>(), sortCriterion, limit, offset);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return (List<Album>)albums;
    }


   
}
