package de.mpg.imeji.album;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import de.mpg.escidoc.services.search.query.SearchQuery.SortingOrder;
import de.mpg.imeji.beans.BasePaginatorListSessionBean;
import de.mpg.imeji.beans.SuperContainerBean;
import de.mpg.imeji.vo.AlbumVO;
import de.mpg.imeji.vo.CollectionVO;
import de.mpg.imeji.vo.util.ImejiFactory;
import de.mpg.jena.controller.AlbumController;
import de.mpg.jena.controller.CollectionController;
import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.controller.SortCriterion;
import de.mpg.jena.controller.SearchCriterion.ImejiNamespaces;
import de.mpg.jena.controller.SortCriterion.SortOrder;
import de.mpg.jena.vo.Album;
import de.mpg.jena.vo.CollectionImeji;

public class AlbumsBean extends SuperContainerBean<AlbumVO>
{
    private AlbumController controller;
    private int totalNumberOfRecords;
  
    

    public AlbumsBean()
    {
        super();
        this.controller = new AlbumController(null);
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
    public List<AlbumVO> retrieveList(int offset, int limit)
    {
        Collection<Album> albums = controller.retrieveAll();
        totalNumberOfRecords = albums.size();
        
        
        SortCriterion sortCriterion = new SortCriterion();
        sortCriterion.setSortingCriterion(ImejiNamespaces.valueOf(getSelectedSortCriterion()));
        sortCriterion.setSortOrder(SortOrder.valueOf(getSelectedSortOrder()));
        
        
        albums = controller.search(null, new ArrayList<SearchCriterion>(), sortCriterion, limit, offset);
        
        return ImejiFactory.newAlbumList(albums);
    }


   
}
