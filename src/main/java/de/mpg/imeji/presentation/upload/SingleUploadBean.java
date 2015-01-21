package de.mpg.imeji.presentation.upload;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
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
import de.mpg.imeji.presentation.lang.MetadataLabels;
import de.mpg.imeji.presentation.mdProfile.SuperStatementBean;
import de.mpg.imeji.presentation.metadata.extractors.TikaExtractor;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.ObjectLoader;
import de.mpg.imeji.presentation.util.UrlHelper;

@ManagedBean(name = "SingleUploadBean")
@ViewScoped
public class SingleUploadBean implements Serializable{
	private static final long serialVersionUID = -2731118794797476328L;
	private static Logger logger = Logger.getLogger(SingleUploadBean.class);
	
	private Collection<CollectionImeji> collections = new ArrayList<CollectionImeji>();
	private CollectionImeji collection;
	private MetadataProfile profile = new MetadataProfile();
	private MetadataLabels labels;
	private List<SuperStatementBean> sts = new ArrayList<SuperStatementBean>();
	
	private List<SelectItem> collectionItems = new ArrayList<SelectItem>();	
	private String selectedCollectionItem;
	
	@ManagedProperty("#{SingleUploadSession}")
	private SingleUploadSession sus;
	
	@ManagedProperty(value = "#{SessionBean.user}")
	private User user;
	
	private File ingestFile;
	private List<String> techMd = new ArrayList<String>();
	 

	public SingleUploadBean(){
		
	}

	@PostConstruct
	public void init() {
		try {
			loadCollections(); 
			if (UrlHelper.getParameterBoolean("init")) {
				sus.reset();
				
			}else if(UrlHelper.getParameterBoolean("start")){
				upload();			
			}
			else if(UrlHelper.getParameterBoolean("done"))
			{
				sus.uploadedToTemp();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void upload() {  
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		try {
			this.ingestFile = getUploadedIngestFile(request);
			sus.setFile(ingestFile);
			this.techMd = TikaExtractor.extractFromFile(ingestFile);
			sus.setTechMD(techMd);
		} catch (Exception e) {
			techMd = new ArrayList<String>();
			techMd.add(e.getMessage());
			e.printStackTrace();
		}
    }
	
	private File getUploadedIngestFile(HttpServletRequest request) throws FileUploadException{
		File tmp = null;
		boolean isMultipart=ServletFileUpload.isMultipartContent(request);
		if (isMultipart) {
			ServletFileUpload upload=new ServletFileUpload();
			try {
				FileItemIterator iter = upload.getItemIterator(request);
				while (iter.hasNext()) {
					FileItemStream fis = iter.next();
					InputStream in = fis.openStream();
					tmp = File.createTempFile("singleupload", "." + FilenameUtils.getExtension(fis.getName()));
					FileOutputStream fos = new FileOutputStream(tmp);
					if(!fis.isFormField())
					{
						try {
							IOUtils.copy(in, fos);
						}finally{
							in.close();
							fos.close();
						}
					}
				}
			} catch (IOException | FileUploadException e) {
				e.printStackTrace();
			}
		}
		return tmp;
	}
	
	

	public void colChangeListener(AjaxBehaviorEvent event){
		if(!"".equals(selectedCollectionItem))
		{
			sus.setSelectedCollectionItem(selectedCollectionItem);
			try {    
				collection = ObjectLoader.loadCollectionLazy(new URI(selectedCollectionItem), user);
				profile = ObjectLoader.loadProfile(collection.getProfile(), user);
				if(profile.getStatements().size() == 0)
					sts.clear();
				for(Statement st : profile.getStatements())
				{
					SuperStatementBean smd = new SuperStatementBean(st);
					sts.add(smd);
				}  
				labels = (MetadataLabels) BeanHelper.getSessionBean(MetadataLabels.class);
				labels.init(profile);
				sus.setCollection(collection);
				sus.setSts(sts);
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
		return sus.getSelectedCollectionItem();
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
		return sus.getCollection();
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
		return sus.getSts();
	}

	public void setSts(List<SuperStatementBean> sts) {
		this.sts = sts;
	}

	public List<String> getTechMd() {
		return sus.getTechMD();
	}

	public void setTechMd(List<String> techMd) {
		this.techMd = techMd;
	}

	public SingleUploadSession getSus() {
		return sus;
	}

	public void setSus(SingleUploadSession sus) {
		this.sus = sus;
	}

	public MetadataLabels getLabels() {
		return sus.getLabels();
	}

	public void setLabels(MetadataLabels labels) {
		this.labels = labels;
	}

	public File getIngestFile() {
		return sus.getFile();
	}

	public void setIngestFile(File ingestFile) {
		this.ingestFile = ingestFile;
	}

	
	
	
	
	





}

