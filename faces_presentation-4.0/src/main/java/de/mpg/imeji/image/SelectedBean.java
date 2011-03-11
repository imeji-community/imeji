package de.mpg.imeji.image;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import com.ocpsoft.pretty.PrettyContext;

import de.mpg.imeji.beans.Navigation;
import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.history.HistorySession;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.util.ImejiFactory;
import de.mpg.jena.controller.ImageController;
import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.controller.SearchCriterion.Filtertype;
import de.mpg.jena.controller.SearchCriterion.ImejiNamespaces;
import de.mpg.jena.sparql.ImejiSPARQL;
import de.mpg.jena.vo.Image;
import de.mpg.jena.vo.Properties.Status;

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
		List<SearchCriterion> uris = new ArrayList<SearchCriterion>();
		//List<String> uris = new ArrayList<String>();
		for (URI uri : sb.getSelected()) 
		{
			uris.add(new SearchCriterion(SearchCriterion.Operator.OR,
					ImejiNamespaces.ID_URI, uri.toString(), Filtertype.URI));
		}
		if (uris.size() != 0) 
		{
			totalNumberOfRecords = controller.getNumberOfResults(uris);
			super.setImages(controller.search(uris, null, limit, offset));
		}
		return ImejiFactory.imageListToBeanList(super.getImages());
	}

	public String clearAll() {
		String prettyLink = PrettyContext.getCurrentInstance().getCurrentMapping().getId();
		sb.getSelected().clear();
		if (prettyLink.equalsIgnoreCase("selected"))
			return "pretty:collectionImages";
		else
			return "pretty:";

	}

	public String deleteAll() throws Exception {
		List<URI> selectedList = new ArrayList<URI>();
		for (URI uri : sb.getSelected()) {
			selectedList.add(uri);
		}
		for (URI uri : selectedList) {
			ImageController imageController = new ImageController(sb.getUser());
			Image img = imageController.retrieve(uri);
			if (img.getProperties().getStatus() != Status.RELEASED) {
				imageController.delete(img, sb.getUser());
				sb.getSelected().remove(uri);
			}
		}
		if (sb.getSelected().size() == 0) {
			BeanHelper.info(sb.getMessage("success_delete"));
			return "pretty:";
		} else {
			BeanHelper.info(sb.getMessage("released_item_delete_error"));
			return "pretty:";
		}
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
