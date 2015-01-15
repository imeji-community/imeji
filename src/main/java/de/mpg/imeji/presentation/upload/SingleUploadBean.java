package de.mpg.imeji.presentation.upload;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.log4j.Logger;

import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.search.SPARQLSearch;
import de.mpg.imeji.logic.search.SearchResult;
import de.mpg.imeji.logic.search.vo.SearchIndex;
import de.mpg.imeji.logic.search.vo.SearchOperators;
import de.mpg.imeji.logic.search.vo.SearchPair;
import de.mpg.imeji.logic.search.vo.SearchQuery;
import de.mpg.imeji.logic.search.vo.SortCriterion;
import de.mpg.imeji.logic.search.vo.SortCriterion.SortOrder;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.mdProfile.SuperStatementBean;
import de.mpg.imeji.presentation.util.ObjectLoader;

@ManagedBean(name = "SingleUploadBean")
@RequestScoped
public class SingleUploadBean implements Serializable {
	private static final long serialVersionUID = -2731118794797476328L;
	private static Logger logger = Logger.getLogger(SingleUploadBean.class);
	
	private Collection<CollectionImeji> collections = new ArrayList<CollectionImeji>();
	private CollectionImeji collection;
	private MetadataProfile profile = new MetadataProfile();
	private List<SuperStatementBean> sts = new ArrayList<SuperStatementBean>();
	
	private List<SelectItem> collectionItems = new ArrayList<SelectItem>();	
	private String selectedCollectionItem;
	
	@ManagedProperty(value = "#{SessionBean.user}")
	private User user;
	
	  private Part file;
	  private String fileContent;
	 
	  public void upload() {

	  }
	 
	  public Part getFile() {
	    return file;
	  }
	 
	  public void setFile(Part file) {
	    this.file = file;
	  }
	
	public SingleUploadBean(){
		
	}

	@PostConstruct
	public void init() {
		try {
			loadCollections(); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void colChangeListener(AjaxBehaviorEvent event){
		if(!"".equals(selectedCollectionItem))
		{
			try {
				collection = ObjectLoader.loadCollectionLazy(new URI(selectedCollectionItem), user);
				profile = ObjectLoader.loadProfile(collection.getProfile(), user);
				for(Statement st : profile.getStatements())
				{
					SuperStatementBean smd = new SuperStatementBean(st);
					sts.add(smd);
				}
			} catch (URISyntaxException e) {
				e.printStackTrace();
			} 
		}
		else
		{
			
		}
		
	}

 
	/**
	 * Load the collection
	 */
	public void loadCollections() throws Exception{
		CollectionController cc = new CollectionController();

		SearchQuery sq = new SearchQuery();
		SearchPair sp = new SearchPair(SPARQLSearch.getIndex(SearchIndex.names.user), SearchOperators.EQUALS, ObjectHelper.getURI(User.class, user.getEmail()).toString());
		sq.addPair(sp);
        SortCriterion sortCriterion = new SortCriterion();
        sortCriterion.setIndex(SPARQLSearch.getIndex("user"));
        sortCriterion.setSortOrder(SortOrder.valueOf("DESCENDING"));
        SearchResult results = cc.search(sq, sortCriterion, -1, 0, user);
		collections = cc.retrieveLazy(results.getResults(), -1, 0, user);
		collectionItems.add(new SelectItem("", "-- select collection --"));
		for(CollectionImeji c : collections)
			collectionItems.add(new SelectItem(c.getId(), c.getMetadata().getTitle()));
	
	}


	public List<SelectItem> getCollectionItems() {
		return collectionItems;
	}

	public void setCollectionItems(List<SelectItem> collectionItems) {
		this.collectionItems = collectionItems;
	}

	public String getSelectedCollectionItem() {
		System.err.println("sel = " + selectedCollectionItem);
		return selectedCollectionItem;
	}

	public void setSelectedCollection(String selectedCollectionItem) {
		this.selectedCollectionItem = selectedCollectionItem;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}


	public Collection<CollectionImeji> getCollections() {
		return collections;
	}

	public void setCollections(Collection<CollectionImeji> collections) {
		this.collections = collections;
	}

	public void setSelectedCollectionItem(String selectedCollectionItem) {
		this.selectedCollectionItem = selectedCollectionItem;
	}

	public CollectionImeji getCollection() {
		return collection;
	}

	public void setCollection(CollectionImeji collection) {
		this.collection = collection;
	}

	public MetadataProfile getProfile() {
		return profile;
	}

	public void setProfile(MetadataProfile profile) {
		this.profile = profile;
	}

	public List<SuperStatementBean> getSts() {
		return sts;
	}

	public void setSts(List<SuperStatementBean> sts) {
		this.sts = sts;
	}


	
	
	
	
	





}

