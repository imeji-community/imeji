/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.album;

import java.util.List;

import de.mpg.imeji.logic.controller.AlbumController;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.search.SPARQLSearch;
import de.mpg.imeji.logic.search.SearchResult;
import de.mpg.imeji.logic.search.vo.SearchLogicalRelation.LOGICAL_RELATIONS;
import de.mpg.imeji.logic.search.vo.SearchQuery;
import de.mpg.imeji.logic.search.vo.SortCriterion;
import de.mpg.imeji.logic.search.vo.SortCriterion.SortOrder;
import de.mpg.imeji.logic.vo.Album;
import de.mpg.imeji.logic.vo.Properties.Status;
import de.mpg.imeji.presentation.beans.SuperContainerBean;
import de.mpg.imeji.presentation.search.URLQueryTransformer;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.ImejiFactory;
import de.mpg.imeji.presentation.util.UrlHelper;

/**
 * Bean for the Albums page
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class AlbumsBean extends SuperContainerBean<AlbumBean> {
	private int totalNumberOfRecords;
	private SessionBean sb;

	/**
	 * Bean for the Albums page
	 */
	public AlbumsBean() {
		super();
		sb = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
	}

	@Override
	public String getNavigationString() {
		return "pretty:albums";
	}

	@Override
	public int getTotalNumberOfRecords() {
		return totalNumberOfRecords;
	}

	@Override
	public List<AlbumBean> retrieveList(int offset, int limit) throws Exception {  
		UserController uc = new UserController(sb.getUser());
		if (sb.getUser() != null) {
			sb.setUser(uc.retrieve(sb.getUser().getEmail()));
		}
		AlbumController controller = new AlbumController();
		SortCriterion sortCriterion = new SortCriterion();
		sortCriterion.setIndex(SPARQLSearch
				.getIndex(getSelectedSortCriterion()));
		sortCriterion.setSortOrder(SortOrder.valueOf(getSelectedSortOrder()));
		SearchQuery searchQuery = new SearchQuery();
		query = UrlHelper.getParameterValue("q");
		if (query == null)
			query = "";
		if (!"".equals(query)) {
			searchQuery = URLQueryTransformer.parseStringQuery(query);
		}
		if (getFilter() != null) {
			searchQuery.addLogicalRelation(LOGICAL_RELATIONS.AND);
			searchQuery.addPair(getFilter());
		}
		SearchResult searchResult = controller.search(searchQuery,
				sb.getUser(), sortCriterion, limit, offset);
		totalNumberOfRecords = searchResult.getNumberOfRecords();
		return ImejiFactory.albumListToBeanList(controller.loadAlbumsLazy(
				searchResult.getResults(), sb.getUser(), limit, offset));
	}

	public SessionBean getSb() {
		return sb;
	}

	public void setSb(SessionBean sb) {
		this.sb = sb;
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
			return "pretty:albums";
		}
		for (AlbumBean b : getCurrentPartList()) {
			if (b.getSelected()) {
				b.delete();
			}
		}
		sb.getSelectedAlbums().clear();
		return "pretty:albums";
	}

	/**
	 * Collection search is always a simple search (needed for
	 * searchQueryDisplayArea.xhtml component)
	 * 
	 * @return
	 */
	public boolean isSimpleSearch() {
		return true;
	}

	/**
	 * needed for searchQueryDisplayArea.xhtml component
	 * 
	 * @return
	 */
	public String getSimpleQuery() {
		if (query != null) {
			return query;
		}
		return "";
	}
}
