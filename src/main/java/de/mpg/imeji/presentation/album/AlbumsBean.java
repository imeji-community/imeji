/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.album;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.mpg.imeji.logic.controller.AlbumController;
import de.mpg.imeji.logic.search.SearchResult;
import de.mpg.imeji.logic.search.model.SearchQuery;
import de.mpg.imeji.logic.search.model.SortCriterion;
import de.mpg.imeji.logic.vo.Album;
import de.mpg.imeji.logic.vo.Properties.Status;
import de.mpg.imeji.presentation.beans.SuperContainerBean;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.ImejiFactory;

/**
 * Bean for the Albums page
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class AlbumsBean extends SuperContainerBean<AlbumBean> {

  /**
   * Bean for the Albums page
   */
  public AlbumsBean() {
    super();
    this.sb = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
  }

  @Override
  public String getNavigationString() {
    return sb.getPrettySpacePage("pretty:albums");
  }

  @Override
  public List<AlbumBean> retrieveList(int offset, int limit) throws Exception {
    AlbumController controller = new AlbumController();
    Collection<Album> albums = new ArrayList<Album>();
    search(offset, limit);
    setTotalNumberOfRecords(searchResult.getNumberOfRecords());
    albums = controller.retrieveBatchLazy(searchResult.getResults(), sb.getUser(), limit, offset);
    return ImejiFactory.albumListToBeanList(albums);
  }

  @Override
  public String selectAll() {
    for (AlbumBean bean : getCurrentPartList()) {
      if (bean.getAlbum().getStatus() == Status.PENDING) {
        bean.setSelected(true);
        if (!(sb.getSelectedAlbums().contains(bean.getAlbum().getId()))) {
          sb.getSelectedAlbums().add(bean.getAlbum().getId());
        }
      }
    }
    return "";
  }

  @Override
  public String selectNone() {
    sb.getSelectedAlbums().clear();
    return "";
  }

  public String deleteAll() {
    if (sb.getSelectedAlbums().size() == 0) {
      BeanHelper.warn(sb.getMessage("error_delete_no_albums_selected"));
      return sb.getPrettySpacePage("pretty:albums");
    }
    for (AlbumBean b : getCurrentPartList()) {
      if (b.getSelected()) {
        b.delete();
      }
    }
    sb.getSelectedAlbums().clear();
    return sb.getPrettySpacePage("pretty:albums");
  }


  @Override
  public String getType() {
    return PAGINATOR_TYPE.ALBUMS.name();
  }

  /*
   * Perform the {@link SPARQLSearch}
   * 
   * @param searchQuery
   * 
   * @param sortCriterion
   * 
   * @return
   * 
   * @see de.mpg.imeji.presentation.beans.SuperContainerBean#search(de.mpg.imeji.logic.search.vo.
   * SearchQuery , de.mpg.imeji.logic.search.vo.SortCriterion)
   */
  @Override
  public SearchResult search(SearchQuery searchQuery, SortCriterion sortCriterion, int offset,
      int limit) {
    AlbumController controller = new AlbumController();
    return controller.search(searchQuery, sb.getUser(), sortCriterion, limit, offset,
        sb.getSelectedSpaceString());
  }

  public String getTypeLabel() {
    return sb.getLabel("type_" + getType().toLowerCase());
  }
}
