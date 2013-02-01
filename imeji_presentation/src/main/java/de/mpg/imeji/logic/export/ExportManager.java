/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.export;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.client.HttpResponseException;

import de.mpg.imeji.logic.controller.AlbumController;
import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.search.SearchResult;
import de.mpg.imeji.logic.search.vo.SearchQuery;
import de.mpg.imeji.logic.search.vo.SortCriterion;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.Album;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.User;

/**
 * Manage {@link Export}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class ExportManager
{
    private OutputStream out;
    private Export export;
    private User user;

    /**
     * Create a new {@link ExportManager} with url parameters, and perform the {@link Export} in the specified
     * {@link OutputStream}
     * 
     * @param out
     * @param user
     * @param params
     * @throws HttpResponseException
     */
    public ExportManager(OutputStream out, User user, Map<String, String[]> params) throws HttpResponseException
    {
        this.out = out;
        this.user = user;
        export = Export.factory(params);
        export.setUser(user);
    }

    public void export(SearchResult sr)
    {
        if (export != null)
        {
            export.export(out, sr);
        }
        else
        {
            try
            {
                out.write("Unknown format".getBytes());
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    public SearchResult search(SearchQuery searchQuery)
    {
        String collectionId = export.getParam("col");
        String albumId = export.getParam("alb");
        String id = export.getParam("id");
        String searchType = export.getParam("type");
        int maximumNumberOfRecords = 20;
        if (export.getParam("n") != null)
        {
            maximumNumberOfRecords = Integer.parseInt(export.getParam("n"));
        }
        SearchResult result = null;
        if ("collection".equals(searchType))
        {
            CollectionController collectionController = new CollectionController(user);
            result = collectionController.search(searchQuery, null, maximumNumberOfRecords, 0);
        }
        else if ("album".equals(searchType))
        {
            AlbumController albumController = new AlbumController(user);
            result = albumController.search(searchQuery, null, maximumNumberOfRecords, 0);
        }
        else if ("profile".equals(searchType))
        {
            List<String> uris = new ArrayList<String>();
            uris.add(id);
            result = new SearchResult(uris, new SortCriterion());
        }
        else if ("image".equals(searchType))
        {
            ItemController itemController = new ItemController(user);
            if (collectionId != null)
            {
                result = itemController.search(ObjectHelper.getURI(CollectionImeji.class, collectionId), searchQuery,
                        null, null);
            }
            else if (albumId != null)
            {
                result = itemController.search(ObjectHelper.getURI(Album.class, albumId), searchQuery, null, null);
            }
            else
            {
                result = itemController.search(null, searchQuery, null, null);
            }
        }
        if (result != null && result.getNumberOfRecords() > 0 && result.getNumberOfRecords() > maximumNumberOfRecords)
        {
            result.setResults(result.getResults().subList(0, maximumNumberOfRecords));
        }
        return result;
    }

    /**
     * Implements search for export
     * 
     * @param scList
     * @param searchType
     * @param collectionId
     * @param albumId
     * @param maximumNumberOfRecords
     * @return
     */
    // public SearchResult search(List<SearchCriterion> scList)
    // {
    // String collectionId = export.getParam("col");
    // String albumId = export.getParam("alb");
    // String searchType = export.getParam("type");
    // int maximumNumberOfRecords = 20;
    //
    // if ( export.getParam("n") != null)
    // {
    // maximumNumberOfRecords = Integer.parseInt(export.getParam("n"));
    // }
    //
    // SearchResult result = null;
    //
    // if ("collection".equals(searchType))
    // {
    // CollectionController collectionController = new CollectionController(user);
    // result = collectionController.search(scList, null, maximumNumberOfRecords, 0);
    // }
    // else if ("album".equals(searchType))
    // {
    // AlbumController albumController = new AlbumController(user);
    // result = albumController.search(scList, null, maximumNumberOfRecords, 0);
    // }
    // else if ("profile".equals(searchType))
    // {
    // //TODO Change the search for profiles!!!!!!!!!!!!
    // ProfileController pc = new ProfileController(user);
    // List<String> uris = new ArrayList<String>();
    // for (MetadataProfile mdp : pc.search())
    // {
    // uris.add(mdp.getId().toString());
    // }
    // result = new SearchResult(uris);
    // }
    // else if ("image".equals(searchType))
    // {
    // ItemController itemController = new ItemController(user);
    //
    // if (collectionId != null)
    // {
    // result = itemController.searchImagesInContainer(ObjectHelper.getURI(CollectionImeji.class, collectionId), scList,
    // null, maximumNumberOfRecords, 0);
    // }
    // else if (albumId != null)
    // {
    // result = itemController.searchImagesInContainer(ObjectHelper.getURI(Album.class, albumId), scList, null,
    // maximumNumberOfRecords, 0);
    // }
    // else
    // {
    // result = itemController.searchImages(scList, null);
    // }
    // }
    //
    // if (result != null && result.getNumberOfRecords() > 0 && result.getNumberOfRecords() > maximumNumberOfRecords)
    // {
    // result.setResults(result.getResults().subList(0, maximumNumberOfRecords));
    // }
    //
    // return result;
    // }
    public String getContentType()
    {
        return export.getContentType();
    }
}
