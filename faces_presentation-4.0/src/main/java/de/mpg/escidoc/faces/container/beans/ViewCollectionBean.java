package de.mpg.escidoc.faces.container.beans;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import de.mpg.escidoc.faces.beans.SessionBean;
import de.mpg.escidoc.faces.container.collection.CollectionController;
import de.mpg.escidoc.faces.container.collection.CollectionSession;
import de.mpg.escidoc.faces.container.collection.CollectionVO;
import de.mpg.escidoc.faces.metadata.ScreenConfigurationSession;
import de.mpg.escidoc.faces.metadata.ScreenManager;
import de.mpg.escidoc.faces.util.BeanHelper;
import de.mpg.escidoc.services.common.referenceobjects.ContextRO;

/**
 * JSF bean for {@link CollectionVO}
 * 
 * @author saquet
 *
 */
public class ViewCollectionBean 
{
	private CollectionVO collectionVO = null;
	private CollectionController collectionController = null;
	private SessionBean sessionBean = null;
	private CollectionSession collectionSession = null;
	private ScreenManager screenManager = null;
	private List<SelectItem> screenManagerMenu = null;
	private ScreenConfigurationSession screenConfigurationSession = null;

	public ViewCollectionBean() 
	{
		collectionController = new CollectionController();
		sessionBean = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
		screenConfigurationSession = (ScreenConfigurationSession) BeanHelper.getSessionBean(ScreenConfigurationSession.class);
		collectionSession = (CollectionSession) BeanHelper.getSessionBean(CollectionSession.class);
		screenManagerMenu = new ArrayList<SelectItem>();
		init();
	}
	
	public void init()
	{
		collectionVO = collectionSession.getCurrentCollection();
		screenManagerMenu.add(new SelectItem("escidoc:40", "Faces"));
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void createCollection() throws Exception
	{
		collectionController.create(collectionVO, sessionBean.getUserHandle());
	}
	
	public CollectionVO getCollectionVO() 
	{
		return collectionVO;
	}

	public void setCollectionVO(CollectionVO collectionVO) 
	{
		this.collectionVO = collectionVO;
	}

	public ScreenManager getScreenManager() 
	{
		return screenManager;
	}

	public void setScreenManager(ScreenManager screenManager) 
	{
		this.screenManager = screenManager;
	}

	public List<SelectItem> getScreenManagerMenu() 
	{
		return screenManagerMenu;
	}

	public void setScreenManagerMenu(List<SelectItem> screenManagerMenu) 
	{
		this.screenManagerMenu = screenManagerMenu;
	}
	
}
