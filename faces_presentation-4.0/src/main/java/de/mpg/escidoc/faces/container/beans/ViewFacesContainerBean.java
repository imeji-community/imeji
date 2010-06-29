package de.mpg.escidoc.faces.container.beans;

import de.mpg.escidoc.faces.container.FacesContainerVO;

/**
 * JSF Bean for {@link FacesContainerVO}
 * <br> This class is directly reused by:
 * <br> - {@link CollectionBean}
 * <br> - {@link ViewAlbumBean}
 * 
 * @author saquet
 *
 */
public class ViewFacesContainerBean 
{
	private FacesContainerVO facesContainerVO = null;
	
	public ViewFacesContainerBean() 
	{
		
	}
	
	public ViewFacesContainerBean(FacesContainerVO facesContainerVO)
	{
		this.facesContainerVO = facesContainerVO;
	}
	
	public FacesContainerVO getFacesContainerVO() 
	{
		return facesContainerVO;
	}

	public void setFacesContainerVO(FacesContainerVO facesContainerVO) 
	{
		this.facesContainerVO = facesContainerVO;
	}

}
