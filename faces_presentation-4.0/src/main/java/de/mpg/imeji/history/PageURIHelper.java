package de.mpg.imeji.history;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import de.mpg.imeji.history.Page.ImejiPages;
import de.mpg.imeji.util.PropertyReader;

public class PageURIHelper 
{
	public static URI getPageURI(ImejiPages pageType, String q, String[] id) throws IOException, URISyntaxException
	{
		String baseURL = PropertyReader.getProperty("escidoc.imeji.instance.url");
		switch (pageType) 
		{
		case IMAGES: return URI.create(baseURL + "images?h=");
		case COLLECTIONS: return URI.create(baseURL + "collections?h=");
		case COLLECTION_IMAGES: return URI.create(baseURL + "images/collection/" + id[0] + "?h=");
		case HOME : return URI.create(baseURL + "?h=");
		case SEARCH : return URI.create(baseURL + "search" + "?h=");
		case IMAGE : return URI.create(baseURL + "image/" + id[0] + "/view?h=");
		case COLLECTION_IMAGE : if (id.length == 2) return URI.create(baseURL + "collection/" + id[0] + "/image/" + id[1] + "/view?h=");
		case ALBUMS : return URI.create(baseURL + "albums" + "?h=");
		case COLLECTION_HOME : return URI.create(baseURL + "collection/" + id[0] + "?h=");
		case SEARCH_RESULTS_IMAGES : return URI.create(baseURL + "images" + "?q=" + URLEncoder.encode(q, "UTF-8") + "&h=");
		case ALBUM_IMAGES : return URI.create(baseURL + "images/album/" + id[0] + "?h=");
		case ALBUM_HOME : return URI.create(baseURL + "album/" + id[0] + "?h=");
		case ALBUM_IMAGE : if (id.length == 2) return URI.create(baseURL + "album/" + id[0] + "/image/" + id[1] + "/view?h=");
		case COLLECTION_INFO : return  URI.create(baseURL + "collection/" + id[0] + "/details?h=");
		case UPLOAD : return  URI.create(baseURL + "upload/collection/" + id[0] + "?h=");
		default: return URI.create(baseURL);
		}
	}
}
