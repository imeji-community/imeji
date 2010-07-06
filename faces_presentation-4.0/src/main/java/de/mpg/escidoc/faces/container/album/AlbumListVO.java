/**
 * 
 */
package de.mpg.escidoc.faces.container.album;

import java.util.List;

import de.mpg.escidoc.faces.container.FacesContainerVO;
import de.mpg.escidoc.faces.container.list.FacesContainerListParameters;
import de.mpg.escidoc.faces.container.list.FacesContainerListVO;

/**
 * @author saquet
 *
 */
public class AlbumListVO extends FacesContainerListVO
{
	public AlbumListVO() 
	{
		super();
	}
	
	public AlbumListVO(List<FacesContainerVO> list, FacesContainerListParameters filter, HandlerType type)
	{
		super(list,  filter, type);
		this.setName("Albums");
	}
}
