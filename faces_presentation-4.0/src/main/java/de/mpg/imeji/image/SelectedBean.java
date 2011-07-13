package de.mpg.imeji.image;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import com.ocpsoft.pretty.PrettyContext;

import de.mpg.imeji.album.AlbumBean;
import de.mpg.imeji.album.AlbumImagesBean;
import de.mpg.imeji.beans.Navigation;
import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.history.HistorySession;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.util.ImejiFactory;
import de.mpg.jena.controller.AlbumController;
import de.mpg.jena.controller.ImageController;
import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.controller.SearchCriterion.Filtertype;
import de.mpg.jena.controller.SearchCriterion.ImejiNamespaces;
import de.mpg.jena.vo.Image;

public class SelectedBean extends ImagesBean {
	private int totalNumberOfRecords;
	private SessionBean sb;
//	private String mdEdited;
	private URI currentCollection;
	private String backUrl = null;
	private HistorySession historySession;
	
	private String selectedImagesContext=null;
	

	public String getSelectedImagesContext() {
		return selectedImagesContext;
	}

	public void setSelectedImagesContext(String selectedImagesContext) {
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

	public SelectedBean() {
		super();
		this.sb = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
		this.historySession = (HistorySession)BeanHelper.getSessionBean(HistorySession.class);
		Navigation navigation = (Navigation) BeanHelper.getApplicationBean(Navigation.class);
		backUrl = navigation.getImagesUrl();
		
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
	public List<ImageBean> retrieveList(int offset, int limit) throws Exception 
	{
		ImageController controller = new ImageController(sb.getUser());
		super.setImages(new ArrayList<Image>());
		//List<SearchCriterion> uris = new ArrayList<SearchCriterion>();
		List<String> uris = new ArrayList<String>();
		for (URI uri : sb.getSelected()) 
		{
			//uris.add(new SearchCriterion(SearchCriterion.Operator.OR,
				//	ImejiNamespaces.ID_URI, uri.toString(), Filtertype.URI));
			uris.add(uri.toString());
		}
		if (uris.size() != 0) 
		{
			totalNumberOfRecords = uris.size();
			super.setImages(controller.loadImages(uris, limit, offset));
		}
		return ImejiFactory.imageListToBeanList(super.getImages());
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
        int count =0;
        if (bean.getAlbum() != null && bean.getAlbum().getAlbum() != null)
        {
	        for (Image im : getImages())
	        {
	        	if (bean.getAlbum().getAlbum().getImages().contains(im.getId()))
	        	{
	        		bean.getAlbum().getAlbum().getImages().remove(im.getId());
	        		count++;
	        	}
	        }
	        if ( count >0 )BeanHelper.info(count + " images removed from album");
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

	/**
	 * WORKAROUND!
	 */
	public String getBackUrl() {
		HttpServletRequest req = (HttpServletRequest) FacesContext
				.getCurrentInstance().getExternalContext().getRequest();
		if (req.getParameter("back") != null
				&& !"".equals(req.getParameter("back"))) {
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
