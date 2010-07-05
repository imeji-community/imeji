package de.mpg.escidoc.faces.container.beans;

import de.mpg.escidoc.faces.album.list.util.AlbumListParameters.OrderParameterType;
import de.mpg.escidoc.faces.album.list.util.AlbumListParameters.SortParameterType;
import de.mpg.escidoc.faces.beans.Navigation;
import de.mpg.escidoc.faces.container.collection.CollectionListVO;
import de.mpg.escidoc.faces.container.collection.CollectionSession;
import de.mpg.escidoc.faces.container.list.FacesContainerListController;
import de.mpg.escidoc.faces.container.list.FacesContainerListVO.ViewType;
import de.mpg.escidoc.faces.util.BeanHelper;

public class CollectionListBean 
{
	private static final int DISPLAYED_PAGES = 5;
	 
	private CollectionListVO list = null;
	private FacesContainerListController controller = null;
	private CollectionSession session = null;
	private Navigation navigation = null;
	
	private String url = null;
	
	public CollectionListBean() 
	{
		list = new CollectionListVO();
		controller = new FacesContainerListController();
		
		session = (CollectionSession)BeanHelper.getSessionBean(CollectionSession.class);
		navigation = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
	}
	
	public String getInit()
	{
		 return "";
	}
	
	public String getListType()
	{
		return list.getHandler().toString();
	}

	/**
	 * @return the collectionListVO
	 */
	public CollectionListVO getList() 
	{
		return list;
	}

	/**
	 * @param collectionListVO the collectionListVO to set
	 */
	public void setList(CollectionListVO collectionListVO) 
	{
		this.list = collectionListVO;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) 
	{
		this.url = url;
	}
	
	
}
