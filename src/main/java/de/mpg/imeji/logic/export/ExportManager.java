/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.export;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpResponseException;

import de.mpg.imeji.logic.controller.AlbumController;
import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.controller.ProfileController;
import de.mpg.imeji.logic.search.SearchResult;
import de.mpg.imeji.logic.search.vo.SearchQuery;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.Album;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.search.URLQueryTransformer;

/**
 * Manage {@link Export}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class ExportManager {
	private OutputStream out;
	private Export export;
	private User user;

	/**
	 * Create a new {@link ExportManager} with url parameters, and perform the
	 * {@link Export} in the specified {@link OutputStream}
	 * 
	 * @param out
	 * @param user
	 * @param params
	 * @throws HttpResponseException
	 */
	public ExportManager(OutputStream out, User user,
			Map<String, String[]> params) throws HttpResponseException {
		this.out = out;
		this.user = user;
		export = Export.factory(params);
		export.setUser(user);
	}

	/**
	 * Write in {@link OutputStream} the export
	 * 
	 * @param sr
	 * 
	 */
	public void export(SearchResult sr) {
		if (export != null) {
			export.export(out, sr);
		} else {
			try {
				out.write("Unknown format".getBytes());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Search the element to export
	 * 
	 * @param searchQuery
	 * @return
	 * @throws IOException
	 */
	public SearchResult search() throws IOException {
		String collectionId = export.getParam("col");
		String albumId = export.getParam("album");
		String query = export.getParam("q");
		export.getParam("id");
		String searchType = export.getParam("type");
		int maximumNumberOfRecords = 100;
		SearchQuery searchQuery = URLQueryTransformer.parseStringQuery(query);
		if (export.getParam("n") != null) {
			maximumNumberOfRecords = Integer.parseInt(export.getParam("n"));
		}
		SearchResult result = null;
		if ("collection".equals(searchType) || "metadata".equals(searchType)) {
			CollectionController collectionController = new CollectionController();
			result = collectionController.search(searchQuery, null,
					maximumNumberOfRecords, 0, user);
		} else if ("album".equals(searchType)) {
			AlbumController albumController = new AlbumController();
			result = albumController.search(searchQuery, user, null,
					maximumNumberOfRecords, 0);
		} else if ("profile".equals(searchType)) {
			// List<String> uris = new ArrayList<String>();
			// result = new SearchResult(uris, new SortCriterion());
			ProfileController pc = new ProfileController();
			result = pc.search(searchQuery, user);
		} else if ("image".equals(searchType)) {
			ItemController itemController = new ItemController();
			if (collectionId != null) {
				result = itemController.search(ObjectHelper.getURI(
						CollectionImeji.class, collectionId), searchQuery,
						null, null, user);
			} else if (albumId != null) {
				result = itemController.search(
						ObjectHelper.getURI(Album.class, albumId), searchQuery,
						null, null, user);
			} else {
				result = itemController.search(null, searchQuery, null, null,
						user);
			}
		}
		if (result != null && result.getNumberOfRecords() > 0
				&& result.getNumberOfRecords() > maximumNumberOfRecords) {
			result.setResults(result.getResults().subList(0,
					maximumNumberOfRecords));
		}
		return result;
	}

	/**
	 * REturn the content type of the {@link HttpResponse}
	 * 
	 * @return
	 */
	public String getContentType() {
		return export.getContentType();
	}

}
