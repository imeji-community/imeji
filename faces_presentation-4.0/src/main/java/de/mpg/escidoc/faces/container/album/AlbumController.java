package de.mpg.escidoc.faces.container.album;

import de.mpg.escidoc.faces.container.FacesContainerController;
import de.mpg.escidoc.faces.container.FacesContainerVO;

public class AlbumController extends FacesContainerController
{
	public AlbumController() 
	{
		super();
	}
	
	public AlbumVO retrieve(String id, String userHandle) throws Exception
	{
	    FacesContainerVO containerVO = super.retrieve(id, userHandle);
	    
	    return new AlbumVO(containerVO);
	}
}
	