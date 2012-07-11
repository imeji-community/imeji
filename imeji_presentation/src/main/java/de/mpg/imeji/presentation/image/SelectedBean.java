/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.imeji.presentation.image;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import com.ocpsoft.pretty.PrettyContext;

import de.mpg.imeji.logic.controller.AlbumController;
import de.mpg.imeji.logic.search.SearchResult;
import de.mpg.imeji.logic.search.vo.SearchQuery;
import de.mpg.imeji.logic.search.vo.SortCriterion;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.presentation.album.AlbumBean;
import de.mpg.imeji.presentation.album.AlbumImagesBean;
import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.beans.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.ImejiFactory;

public class SelectedBean extends ImagesBean  implements Serializable
{
	private int totalNumberOfRecords;
	private SessionBean sb;
	private URI currentCollection;
	private String backUrl = null;
	
	private String selectedImagesContext=null;
	
	public SelectedBean() 
	{
		super();
		this.sb = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
		Navigation navigation = (Navigation) BeanHelper.getApplicationBean(Navigation.class);
		backUrl = navigation.getImagesUrl();
	}
	
	public String getInitComment()
	{
		setDiscardComment("");
		return "";
	}

	@Override
	public String getNavigationString() {
		return "pretty:selected";
	}

	@Override
	public int getTotalNumberOfRecords() {
		return totalNumberOfRecords;
	}

	@Override
	public List<ThumbnailBean> retrieveList(int offset, int limit)
	{
		SearchResult results = search(new SearchQuery(), null);

		totalNumberOfRecords = results.getResults().size();
		
		return ImejiFactory.imageListToThumbList(loadImages(results));
	}
	
	public SearchResult search(SearchQuery searchQuery, SortCriterion sortCriterion)
	{
		return new SearchResult(getSelectedUris());
	}
	
	public List<String> getSelectedUris()
	{
		List<String> uris = new ArrayList<String>();
		
		for (URI uri : sb.getSelected()) 
		{
			uris.add(uri.toString());
		}
		
		return uris;
	}
	
	public String getSelectedImagesContext() 
	{
		return selectedImagesContext;
	}

	public void setSelectedImagesContext(String selectedImagesContext) 
	{
		if(selectedImagesContext.equals(sb.getSelectedImagesContext()))
		{
			this.selectedImagesContext = selectedImagesContext;
		}
		else
		{
			clearAll();
			this.selectedImagesContext = selectedImagesContext;
			sb.setSelectedImagesContext(selectedImagesContext);
		}
	}
	
	public String addToActiveAlbum() throws Exception
	{
		this.update();
		super.addToActiveAlbum();
		return "pretty:";
	}
	
	public String removeFromAlbum() throws Exception
    {
		this.update();
    	AlbumImagesBean bean = (AlbumImagesBean) BeanHelper.getSessionBean(AlbumImagesBean.class);
        AlbumController ac = new AlbumController(sb.getUser());
        
        SearchResult results = new SearchResult(getSelectedUris());
        Collection<Item> items = loadImages(results);
		
        int count =0;
        if (bean.getAlbum() != null && bean.getAlbum().getAlbum() != null)
        {
	        for (Item im : items)
	        {
	        	if (bean.getAlbum().getAlbum().getImages().contains(im.getId()))
	        	{
	        		bean.getAlbum().getAlbum().getImages().remove(im.getId());
	        		count++;
	        	}
	        }
	        if ( count >0 )
	        {
	        	BeanHelper.info(count + sb.getMessage("success_album_remove_images"));
	        }
	        ac.update(bean.getAlbum().getAlbum());
	        AlbumBean activeAlbum = sb.getActiveAlbum();
	        if (activeAlbum != null && activeAlbum.getAlbum().getId().toString().equals(bean.getAlbum().getAlbum().getId().toString()))
	        {
	        	sb.setActiveAlbum(bean.getAlbum());
	        }
	        clearAll();
        }
        return "pretty:";
    }

	public String clearAll() 
	{
		String prettyLink = PrettyContext.getCurrentInstance().getCurrentMapping().getId();
		sb.getSelected().clear();
		if (prettyLink.equalsIgnoreCase("selected"))
		{
			return "pretty:collectionImages";
		}
		else
		{
			return "pretty:";
		}
	}

	public String deleteAll() throws Exception
	{
		update();
		removeFromAlbum();
		super.deleteAll();
		clearAll();
		return "pretty:";
	}
	
	public String withdrawAll() throws Exception
	{
		update();
		removeFromAlbum();
		super.withdrawAll();
		clearAll();
		return "pretty:";
	}


	/**
	 * WORKAROUND!
	 */
	public String getBackUrl() {
		HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		if (req.getParameter("back") != null && !"".equals(req.getParameter("back"))) 
		{
			backUrl = req.getParameter("back");
		}
		return backUrl;
	}


	public void setCurrentCollection(URI currentCollection) {
		this.currentCollection = currentCollection;
	}

	public URI getCurrentCollection() {
		return currentCollection;
	}

	public SessionBean getSb() {
		return sb;
	}

	public void setSb(SessionBean sb) {
		this.sb = sb;
	}

	
}
