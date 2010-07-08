package de.mpg.escidoc.faces.pictures;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;


public class BrowseSession 
{
	private Browse browsePictures = null;
	private Browse browseAlbums = null;
	
	public BrowseSession() 
	{
    	 	HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        	browseAlbums = new Browse(request);
        	browsePictures = new Browse(request);
	}

	/**
	 * @return the browsePictures
	 */
	public Browse getBrowsePictures()
	{
	    return browsePictures;
	}

	/**
	 * @param browsePictures the browsePictures to set
	 */
	public void setBrowsePictures(Browse browsePictures)
	{
	    this.browsePictures = browsePictures;
	}

	/**
	 * @return the browseAlbums
	 */
	public Browse getBrowseAlbums()
	{
	    return browseAlbums;
	}

	/**
	 * @param browseAlbums the browseAlbums to set
	 */
	public void setBrowseAlbums(Browse browseAlbums)
	{
	    this.browseAlbums = browseAlbums;
	}
	
	
}
