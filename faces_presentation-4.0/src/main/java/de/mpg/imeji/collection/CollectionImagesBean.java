package de.mpg.imeji.collection;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.mpg.imeji.beans.Navigation;
import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.facet.FacetsBean;
import de.mpg.imeji.filter.FiltersBean;
import de.mpg.imeji.image.ImageBean;
import de.mpg.imeji.image.ImagesBean;
import de.mpg.imeji.search.URLQueryTransformer;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.util.ImejiFactory;
import de.mpg.jena.controller.CollectionController;
import de.mpg.jena.controller.ImageController;
import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.controller.SearchCriterion.ImejiNamespaces;
import de.mpg.jena.controller.SortCriterion;
import de.mpg.jena.controller.SortCriterion.SortOrder;
import de.mpg.jena.util.ObjectHelper;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.Image;

public class CollectionImagesBean extends ImagesBean {
	private int totalNumberOfRecords;
	private String id = null;
	private URI uri;
	private SessionBean sb = null;
	private CollectionImeji collection;
	private Navigation navigation;
	private List<SearchCriterion> scList = new ArrayList<SearchCriterion>();

	public CollectionImagesBean() {
		super();
		sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
		this.navigation = (Navigation) BeanHelper.getApplicationBean(Navigation.class);
	} 

	public void init() {
		CollectionController cc = new CollectionController(sb.getUser());
		this.collection = cc.retrieve(id);
	}

	@Override
	public String getNavigationString() {
		if(sb.getSelectedImagesContext()!=null &&!(sb.getSelectedImagesContext().equals("pretty:collectionImages" + collection.getId().toString())))
			sb.getSelected().clear();
		sb.setSelectedImagesContext("pretty:collectionImages" + collection.getId().toString());
		return "pretty:collectionImages";
	}

	@Override
	public int getTotalNumberOfRecords() {
		return totalNumberOfRecords;
	}

	@Override
	public List<ImageBean> retrieveList(int offset, int limit) throws Exception 
	{	
		uri = ObjectHelper.getURI(CollectionImeji.class, id);
		Collection<Image> images = new ArrayList<Image>();
		SortCriterion sortCriterion = new SortCriterion();
		sortCriterion.setSortingCriterion(ImejiNamespaces.valueOf(getSelectedSortCriterion()));
		sortCriterion.setSortOrder(SortOrder.valueOf(getSelectedSortOrder()));
		
		try 
		{
			scList = URLQueryTransformer.transform2SCList(getQuery());
//			System.out.println("url:" + getQuery());
//	        System.out.println("parsed:" + URLQueryTransformer.transform2URL(scList));
		} 
		catch (Exception e) 
		{
			BeanHelper.error("Invalid search query!");
		}		
		ImageController controller = new ImageController(sb.getUser());
		totalNumberOfRecords = controller.getNumberOfResultsInContainer(uri, scList);
		images = controller.searchImageInContainer(uri, scList, null, limit, offset);
		super.setImages(images);
		filters = new FiltersBean(getQuery(), totalNumberOfRecords);
		labels.init((List<Image>) images);
		return ImejiFactory.imageListToBeanList(images);
	}
	
	@Override
	public String initFacets()
    {
		setFacets(new FacetsBean(collection, scList));
        return "pretty";
    }
	
	public String getBackUrl() {
		return navigation.getImagesUrl() + "/collection" + "/" + this.id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setCollection(CollectionImeji collection) {
		this.collection = collection;
	}

	public CollectionImeji getCollection() {
		return collection;
	}
}
