package de.mpg.imeji.history;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.imeji.history.Page.ImejiPages;

public class PageURIHelper 
{
	public static URI getPageURI(ImejiPages pageType, String q, String[] id) throws IOException, URISyntaxException
	{
		String baseURL = PropertyReader.getProperty("escidoc.faces.instance.url");
		switch (pageType) 
		{
			case IMAGES: return URI.create(baseURL + "images?");
			case COLLECTIONS: return URI.create(baseURL + "collections?");
			case COLLECTION_IMAGES: return URI.create(baseURL + "images/collection/" + id[0] + "?");
			case HOME : return URI.create(baseURL + "?");
			case SEARCH : return URI.create(baseURL + "search" + "?");
			case IMAGE : return URI.create(baseURL + "image/" + id[0] + "/view?");
			case COLLECTION_IMAGE : return URI.create(baseURL + "collection/" + id[0] + "/image/" + id[1] + "/view?");
			case ALBUMS : return URI.create(baseURL + "albums" + "?");
			case COLLECTION_HOME : return URI.create(baseURL + "collection/" + id[0] + "?");
			case SEARCH_RESULTS_IMAGES : return URI.create(baseURL + "images" + "?q=" + URLEncoder.encode(q, "UTF-8"));
			default: return URI.create(baseURL);
		}
	}
}
