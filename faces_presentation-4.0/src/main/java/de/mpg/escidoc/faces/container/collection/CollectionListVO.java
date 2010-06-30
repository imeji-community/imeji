package de.mpg.escidoc.faces.container.collection;

import java.util.ArrayList;
import java.util.List;

import de.mpg.escidoc.faces.album.list.util.AlbumListParameters;
import de.mpg.escidoc.faces.container.FacesContainerVO;
import de.mpg.escidoc.faces.container.list.FacesContainerListVO;
import de.mpg.escidoc.faces.container.list.FacesContainerListVO.HandlerType;

public class CollectionListVO extends FacesContainerListVO
{
    
	public CollectionListVO() 
	{
		super();
	}
	
	public CollectionListVO(List<FacesContainerVO> list, AlbumListParameters filter, HandlerType type)
	{
		super(list,  filter, type);
	}

	/**
	 * @return list of {@link CollectionVO}
	 */
	public List<CollectionVO> getCollectionVOList() 
	{
		List<CollectionVO> list = new ArrayList<CollectionVO>();
		
		for (FacesContainerVO fc : super.getList()) 
		{
			list.add((CollectionVO) fc);
		}
		
		return list;
	}

	/**
	 * @param list of {@link CollectionVO}
	 */
	public void setCollectionVOList(List<CollectionVO> list) 
	{
		super.getList().addAll(list);
	}
	
}
