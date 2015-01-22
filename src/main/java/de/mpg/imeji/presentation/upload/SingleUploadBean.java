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
import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.search.SPARQLSearch;
import de.mpg.imeji.logic.search.SearchResult;
import de.mpg.imeji.logic.search.vo.SearchIndex;
import de.mpg.imeji.logic.search.vo.SearchOperators;
import de.mpg.imeji.logic.search.vo.SearchPair;
import de.mpg.imeji.logic.search.vo.SearchQuery;
import de.mpg.imeji.logic.search.vo.SortCriterion;
import de.mpg.imeji.logic.search.vo.SortCriterion.SortOrder;
import de.mpg.imeji.logic.storage.StorageController;
import de.mpg.imeji.logic.storage.util.StorageUtils;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Person;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.lang.MetadataLabels;
import de.mpg.imeji.presentation.metadata.MetadataSetBean;
import de.mpg.imeji.presentation.metadata.SingleEditBean;
import de.mpg.imeji.presentation.metadata.SuperMetadataBean;
import de.mpg.imeji.presentation.metadata.extractors.TikaExtractor;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.ObjectLoader;
import de.mpg.imeji.presentation.util.UrlHelper;

@ManagedBean(name = "SingleUploadBean")
@ViewScoped
public class SingleUploadBean implements Serializable{
	private static final long serialVersionUID = -2731118794797476328L;
	private static Logger logger = Logger.getLogger(SingleUploadBean.class);
	
	private Collection<CollectionImeji> collections = new ArrayList<CollectionImeji>();
	
	private List<SelectItem> collectionItems = new ArrayList<SelectItem>();	
	private String selectedCollectionItem;
	
	@ManagedProperty("#{SingleUploadSession}")
	private SingleUploadSession sus;
	
	@ManagedProperty(value = "#{SessionBean.user}")
	private User user;
	
	private IngestImage ingestImage;
	 

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
				sus.copyToTemp();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	    
	public String save(){
		try {        
			Item item = uploadFileToItem(getIngestImage().getFile(), getIngestImage().getName());
			SingleEditBean edit = new SingleEditBean(item, sus.getProfile(), "");
			MetadataSetBean mds = edit.getEditor().getItems().get(0).getMds();
			List <SuperMetadataBean> smdb1 = mds.getTree().getList();
			List <SuperMetadataBean> smdb2 = getSuperMetadataBeans();
			copyValueToItem(smdb1, smdb2);
			edit.save();
			sus.uploaded();
		} catch (Exception e) {
			BeanHelper.error(e.getMessage());
		}
		return "";
	} 
	  
	public void copyValueToItem(List <SuperMetadataBean> itemStatements, List <SuperMetadataBean> statements){
		for(SuperMetadataBean smdb1 : itemStatements)
		{
			for(SuperMetadataBean smdb2 : statements)
			{
				if(smdb1.getStatement().getId().equals(smdb2.getStatement().getId()))
				{
					smdb1.setText(smdb2.getText());
					smdb1.setDate(smdb2.getDate());
					smdb1.setCitation(smdb2.getCitation());
					smdb1.setConeId(smdb2.getConeId());
					smdb1.setPerson(smdb2.getPerson());
					smdb1.setName(smdb2.getName());
					smdb1.setLatitude(smdb2.getLatitude());
					smdb1.setLongitude(smdb2.getLongitude());
					smdb1.setLicense(smdb2.getLicense());
					smdb1.setExternalUri(smdb2.getExternalUri());
					smdb1.setLabel(smdb2.getLabel());
					smdb1.setNumber(smdb2.getNumber());
					smdb1.setExportFormat(smdb2.getExportFormat());
					smdb1.setCitation(smdb2.getCitation());
				}
			}
		}
		
	}
	    
	private Item uploadFileToItem(File file, String title) {
		try {    
			if (!StorageUtils.hasExtension(title))
				title += StorageUtils.guessExtension(file);
			validateName(file, title);
			Item item = null;
			ItemController controller = new ItemController();
			item = controller.createWithFile(null, file, title, getCollection(), user);
			sus.setUploadedItem(item);
			return item;
		} catch (Exception e) {	
			sus.setfFile(" File " + title + " not uploaded: " + e.getMessage());
			logger.error("Error uploading item: ", e);
			e.printStackTrace();
			return null;
		}
	}
	

	
    protected void addPositionToMetadata()
    {
            int pos = 0;
            for (SuperMetadataBean smb : sus.getSuperMdBeans())
            {
                smb.setPos(pos);
                pos++;
            }
    }
	
	/**
	 * Throws an {@link Exception} if the file ca be upload. Works only if the
	 * file has an extension (therefore, for file without extension, the
	 * validation will only occur when the file has been stored locally)
	 */
	private void validateName(File file, String title) {
		if (StorageUtils.hasExtension(title)) {
			StorageController sc = new StorageController();
			String guessedNotAllowedFormat = sc.guessNotAllowedFormat(file);
			if (guessedNotAllowedFormat != null) {
				SessionBean sessionBean = (SessionBean) BeanHelper
						.getSessionBean(SessionBean.class);
				throw new RuntimeException(
						sessionBean.getMessage("upload_format_not_allowed") + " (" + guessedNotAllowedFormat + ")");
			}
		}
	}

	
	public void upload() {  
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		List<String> techMd = new ArrayList<String>();
		try {
			this.ingestImage = getUploadedIngestFile(request);
			sus.setIngestImage(ingestImage);
			techMd = TikaExtractor.extractFromFile(ingestImage.getFile());
		} catch (Exception e) {
			techMd = new ArrayList<String>();
			techMd.add(e.getMessage());
			e.printStackTrace();
		}
		sus.setTechMD(techMd);
    }
	
	
	private IngestImage getUploadedIngestFile(HttpServletRequest request) throws FileUploadException{
		File tmp = null;
		boolean isMultipart=ServletFileUpload.isMultipartContent(request);
		IngestImage ii = new IngestImage();
		if (isMultipart) {
			ServletFileUpload upload=new ServletFileUpload();
			try {
				FileItemIterator iter = upload.getItemIterator(request);
				while (iter.hasNext()) {
					FileItemStream fis = iter.next();
					InputStream in = fis.openStream();
					tmp = File.createTempFile("singleupload", "." + FilenameUtils.getExtension(fis.getName()));
					FileOutputStream fos = new FileOutputStream(tmp);
					if(fis.getName() != null)
						ii.setName(fis.getName());
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
				ii.setFile(tmp);
			} catch (IOException | FileUploadException e) {
				e.printStackTrace();
			}
		}
		return ii;
	}

	public void colChangeListener(AjaxBehaviorEvent event){
		if(!"".equals(selectedCollectionItem))
		{  
			sus.setSelectedCollectionItem(selectedCollectionItem);
			try {    
				CollectionImeji collection = ObjectLoader.loadCollectionLazy(new URI(selectedCollectionItem), user);
				MetadataProfile profile = ObjectLoader.loadProfile(collection.getProfile(), user);
				List<SuperMetadataBean> sts = new ArrayList<SuperMetadataBean>();
				if(profile.getStatements().size() > 0){
					for(Statement st : profile.getStatements())
					{
 						SuperMetadataBean smb = new SuperMetadataBean(st);
						if("http://imeji.org/terms/metadata#conePerson".equals(st.getType().toString()))
						{
							Person person = new Person();
							smb.setPerson(person);
						}
						sts.add(smb);
					}  
				}
				MetadataLabels labels = (MetadataLabels) BeanHelper.getSessionBean(MetadataLabels.class);
				labels.init(profile);
				sus.setCollection(collection);
				sus.setProfile(profile);
				sus.setSuperMdBeans(sts);;
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

	public List<SuperMetadataBean> getSuperMetadataBeans() {
		return sus.getSuperMdBeans();
	}

	public List<String> getTechMd() {
		return sus.getTechMD();
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

	public IngestImage getIngestImage() {
		return sus.getIngestImage();
	}

	public String getfFile()
	{
		return sus.getfFile();
	}
	
	public Item getItem()
	{
		return sus.getUploadedItem();
	}
	
	public static String extractIDFromURI(URI uri) {
		return uri.getPath().substring(uri.getPath().lastIndexOf("/") + 1);
	}
	
	public boolean readyFotUploading(){
		return sus.isUploadFileToTemp() && sus.getCollection() != null;
	}
	
	

}

