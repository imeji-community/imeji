/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.jena.export;


import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.apache.http.client.HttpResponseException;

import de.mpg.jena.controller.AlbumController;
import de.mpg.jena.controller.CollectionController;
import de.mpg.jena.controller.ImageController;
import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.search.SearchResult;
import de.mpg.jena.util.ObjectHelper;
import de.mpg.jena.vo.Album;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.User;

public class ExportManager 
{
	private OutputStream out;
	private Export export;
	private User user;
	
	public ExportManager(OutputStream out, User user, Map<String, String[]> params) throws HttpResponseException 
	{
		this.out = out;
		this.user = user;
		export = Export.factory(params);
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
	
	/**
	 * Implements search for export
	 * @param scList
	 * @param searchType
	 * @param collectionId
	 * @param albumId
	 * @param maximumNumberOfRecords
	 * @return
	 */
	public SearchResult search(List<SearchCriterion> scList)
	{
		String collectionId = export.getParam("col");
		String albumId = export.getParam("alb");
		String searchType = export.getParam("type");
		int maximumNumberOfRecords = 20;

		if ( export.getParam("n") != null)
		{
			maximumNumberOfRecords = Integer.parseInt(export.getParam("n"));
		}
		
		SearchResult result = null;
		
		if ("collection".equals(searchType))
		{
			CollectionController collectionController = new CollectionController(user);
			result = collectionController.search(scList, null, maximumNumberOfRecords, 0);
		}
		else if ("album".equals(searchType))
		{
			AlbumController albumController = new AlbumController(user);
			result = albumController.search(scList, null, maximumNumberOfRecords, 0);
		}
		else
		{
			ImageController imageController = new ImageController(user);
			
			if (collectionId != null)
			{
				result = imageController.searchImagesInContainer(ObjectHelper.getURI(CollectionImeji.class, collectionId), scList, null, maximumNumberOfRecords, 0);
			}
			else if (albumId != null)
			{
				result = imageController.searchImagesInContainer(ObjectHelper.getURI(Album.class, albumId), scList, null, maximumNumberOfRecords, 0);
			}
			else
			{
				result = imageController.searchImages(scList, null);
			}
		}

		if (result != null && result.getNumberOfRecords() > 0 && result.getNumberOfRecords() > maximumNumberOfRecords)
		{
			result.setResults(result.getResults().subList(0, maximumNumberOfRecords));
		}
		
		return result;
	}
	
	public String getContentType()
	{
		return export.getContentType();
	}
}
