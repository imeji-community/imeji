package de.mpg.escidoc.faces.container.collection;

import java.util.ArrayList;
import java.util.List;

import de.mpg.escidoc.faces.container.FacesContainerVO;
import de.mpg.escidoc.faces.container.list.FacesContainerListParameters;
import de.mpg.escidoc.faces.container.list.FacesContainerListVO;

public class CollectionListVO extends FacesContainerListVO
{
	public CollectionListVO() 
	{
		super();
	}
	
	public CollectionListVO(List<FacesContainerVO> list, FacesContainerListParameters filter, HandlerType type)
	{
		super(list,  filter, type);
		this.setName("Collections");
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
