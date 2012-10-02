/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.history;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.history.Page.ImejiPages;
import de.mpg.imeji.presentation.util.BeanHelper;

public class PageURIHelper
{
    public static URI getPageURI(ImejiPages pageType, String q, String[] id) throws IOException, URISyntaxException
    {
        // String baseURL = PropertyReader.getProperty("escidoc.imeji.instance.url");
        Navigation navigation = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
        switch (pageType)
        {
            case IMAGES:
                return URI.create(navigation.getBrowseUrl() + "?h=");
            case COLLECTIONS:
                return URI.create(navigation.getCollectionsUrl() + "?h=");
            case COLLECTION_IMAGES:
                return URI.create(navigation.getCollectionUrl() + id[0] + "/" + navigation.getBrowsePath() + "?h=");
            case HOME:
                return URI.create(navigation.getHomeUrl() + "?h=");
            case SEARCH:
                return URI.create(navigation.getSearchUrl() + "?h=");
            case IMAGE:
                return URI.create(navigation.getItemUrl() + id[0] + "?h=");
            case COLLECTION_IMAGE:
                if (id.length == 2)
                    return URI.create(navigation.getCollectionUrl() + id[0] + "/" + navigation.getItemPath() + "/"
                            + id[1] + "?h=");
            case ALBUMS:
                return URI.create(navigation.getAlbumsUrl() + "?h=");
            case COLLECTION_HOME:
                return URI.create(navigation.getCollectionUrl() + id[0] + "?h=");
            case SEARCH_RESULTS_IMAGES:
                return URI.create(navigation.getBrowseUrl() + "?q=" + URLEncoder.encode(q, "UTF-8") + "&h=");
            case ALBUM_IMAGES:
                return URI.create(navigation.getAlbumUrl() + id[0] + "/" + navigation.getBrowsePath() + "?h=");
            case ALBUM_HOME:
                return URI.create(navigation.getAlbumUrl() + id[0] + "?h=");
            case ALBUM_IMAGE:
                if (id.length == 2)
                    return URI.create(navigation.getAlbumUrl() + id[0] + "/" + navigation.getItemPath() + "/" + id[1]
                            + "?h=");
            case COLLECTION_INFO:
                return URI.create(navigation.getCollectionUrl() + id[0] + "/" + navigation.getInfosPath() + "?h=");
            case UPLOAD:
                return URI.create(navigation.getCollectionUrl() + id[0] + "/" + navigation.getUploadPath() + "?h=");
            default:
                return URI.create(navigation.getHomeUrl());
        }
    }
}
