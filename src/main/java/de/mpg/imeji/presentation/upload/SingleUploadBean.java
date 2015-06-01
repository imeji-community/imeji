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

import de.mpg.imeji.logic.auth.util.AuthUtil;
import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.controller.exceptions.TypeNotAllowedException;
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
import de.mpg.imeji.logic.util.UrlHelper;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.MetadataSet;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.lang.MetadataLabels;
import de.mpg.imeji.presentation.metadata.MetadataSetBean;
import de.mpg.imeji.presentation.metadata.SingleEditBean;
import de.mpg.imeji.presentation.metadata.SuperMetadataBean;
import de.mpg.imeji.presentation.metadata.extractors.TikaExtractor;
import de.mpg.imeji.presentation.metadata.util.SuggestBean;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.ImejiFactory;
import de.mpg.imeji.presentation.util.ObjectLoader;

@ManagedBean(name = "SingleUploadBean")
@ViewScoped
public class SingleUploadBean implements Serializable {
	private static final long serialVersionUID = -2731118794797476328L;
	private static Logger logger = Logger.getLogger(SingleUploadBean.class);

	private Collection<CollectionImeji> collections = new ArrayList<CollectionImeji>();

	private List<SelectItem> collectionItems = new ArrayList<SelectItem>();
	private String selectedCollectionItem;

	@ManagedProperty("#{SingleUploadSession}")
	private SingleUploadSession sus;

	@ManagedProperty("#{SessionBean}")
	private SessionBean sb;

	@ManagedProperty(value = "#{SessionBean.user}")
	private User user;

	private IngestImage ingestImage;

	public SingleUploadBean() {
	}

	public void init() {
		if (user != null) {
			try {
				if (UrlHelper.getParameterBoolean("init")) {
					sus.reset();
				} else if (UrlHelper.getParameterBoolean("start")) {
					upload();
				} else if (UrlHelper.getParameterBoolean("done")) {
					prepareEditor();
				}
			} catch (Exception e) {
				BeanHelper.error(e.getMessage());
			}
		}
	}

	public String save() {
		try {
			Item item = uploadFileToItem(getIngestImage().getFile(),
					getIngestImage().getName());
			SingleEditBean edit = new SingleEditBean(item, sus.getProfile(), "");
			MetadataSetBean newSet = getMdSetBean();
			edit.getEditor().getItems().get(0).setMds(newSet);
			edit.save();
			sus.uploaded();
		} catch (Exception e) {
			BeanHelper.error(e.getMessage());
		}
		return "";
	}

	/**
	 * After the file has been uploaded
	 * 
	 * @throws Exception
	 */
	private void prepareEditor() throws Exception {
		StorageController sc = new StorageController();
		if (sc.guessNotAllowedFormat(sus.getIngestImage().getFile()).equals(
				StorageUtils.BAD_FORMAT)) {
			sus.reset();
			throw new TypeNotAllowedException(
					sb.getMessage("single_upload_invalid_content_format"));
		}
		loadCollections();
		sus.copyToTemp();
	}

	// No throw Exception
	private Item uploadFileToItem(File file, String title) {
		try {
			Item item = ImejiFactory.newItem(getCollection());
			ItemController controller = new ItemController();
			item = controller.create(item, file, title, user, null, null);
			sus.setUploadedItem(item);
			return item;
		} catch (Exception e) {
			// sus.setfFile(" File " + title + " not uploaded: " +
			// e.getMessage());
			BeanHelper.error(e.getMessage());
			return null;
		}
	}

	/**
	 * Upload the file and read the technical Metadata
	 * 
	 * @throws FileUploadException
	 * @throws TypeNotAllowedException
	 */
	public void upload() throws FileUploadException, TypeNotAllowedException {
		HttpServletRequest request = (HttpServletRequest) FacesContext
				.getCurrentInstance().getExternalContext().getRequest();
		List<String> techMd = new ArrayList<String>();
		this.ingestImage = getUploadedIngestFile(request);
		sus.setIngestImage(ingestImage);
		techMd = TikaExtractor.extractFromFile(ingestImage.getFile());
		sus.setTechMD(techMd);

	}

	/**
	 * Upload the file
	 * 
	 * @param request
	 * @return
	 * @throws FileUploadException
	 * @throws TypeNotAllowedException
	 */
	private IngestImage getUploadedIngestFile(HttpServletRequest request)
			throws FileUploadException, TypeNotAllowedException {
		File tmp = null;
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		IngestImage ii = new IngestImage();
		if (isMultipart) {
			ServletFileUpload upload = new ServletFileUpload();
			try {
				FileItemIterator iter = upload.getItemIterator(request);
				while (iter.hasNext()) {
					FileItemStream fis = iter.next();
					String filename = fis.getName();
					InputStream in = fis.openStream();
					tmp = File.createTempFile("singleupload", "."
							+ FilenameUtils.getExtension(filename));
					FileOutputStream fos = new FileOutputStream(tmp);
					if (!fis.isFormField()) {
						try {
							IOUtils.copy(in, fos);
						} finally {
							in.close();
							fos.close();
							ii.setName(filename);
						}

					}
				}
				ii.setFile(tmp);
			} catch (IOException | FileUploadException e) {
				logger.info("Could not get uploaded ingest file", e);
			}
		}
		return ii;
	}

	public void colChangeListener(AjaxBehaviorEvent event) throws Exception {
		if (!"".equals(selectedCollectionItem)) {
			sus.setSelectedCollectionItem(selectedCollectionItem);
			try {
				CollectionImeji collection = ObjectLoader.loadCollectionLazy(
						new URI(selectedCollectionItem), user);
				MetadataProfile profile = ObjectLoader.loadProfile(
						collection.getProfile(), user);
				((SuggestBean) BeanHelper.getSessionBean(SuggestBean.class))
						.init(profile);
				MetadataSet mdSet = ImejiFactory
						.newMetadataSet(profile.getId());
				MetadataSetBean mdSetBean = new MetadataSetBean(mdSet, profile,
						true);

				MetadataLabels labels = (MetadataLabels) BeanHelper
						.getSessionBean(MetadataLabels.class);
				labels.init(profile);
				sus.setCollection(collection);
				sus.setProfile(profile);
				sus.setMdSetBean(mdSetBean);
			} catch (URISyntaxException e) {
				logger.info("Pure URI Syntax issue ", e);
			}
		} else {

		}

	}

	/**
	 * Add a Metadata of the same type as the passed metadata
	 */
	public void addMetadata(SuperMetadataBean smb) {
		SuperMetadataBean newMd = smb.copyEmpty();
		newMd.addEmtpyChilds(sus.getProfile());
		sus.getMdSetBean().getTree().add(newMd);
	}

	/**
	 * Remove the active metadata
	 */
	public void removeMetadata(SuperMetadataBean smb) {
		sus.getMdSetBean().getTree().remove(smb);
		sus.getMdSetBean().addEmtpyValues();
	}

	/**
	 * Load the collection
	 */
	public void loadCollections() throws Exception {

		CollectionController cc = new CollectionController();
		SearchQuery sq = new SearchQuery();
		SearchPair sp = new SearchPair(
				SPARQLSearch.getIndex(SearchIndex.IndexNames.user),
				SearchOperators.EQUALS, user.getId().toString());
		sq.addPair(sp);
		SortCriterion sortCriterion = new SortCriterion();
		sortCriterion.setIndex(SPARQLSearch.getIndex("user"));
		sortCriterion.setSortOrder(SortOrder.valueOf("DESCENDING"));
		// TODO: check if here space restriction is needed
		SearchResult results = cc.search(sq, sortCriterion, -1, 0, user, null);
		collections = cc.retrieveLazy(results.getResults(), -1, 0, user);
		collectionItems.add(new SelectItem("", "-- select collection --"));
		for (CollectionImeji c : collections) {
			if (AuthUtil.staticAuth().create(user, c))
				collectionItems.add(new SelectItem(c.getId(), c.getMetadata()
						.getTitle()));
		}
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

	public MetadataSetBean getMdSetBean() {
		return sus.getMdSetBean();
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

	public String getfFile() {
		return sus.getfFile();
	}

	public Item getItem() {
		return sus.getUploadedItem();
	}

	public SessionBean getSb() {
		return sb;
	}

	public void setSb(SessionBean sb) {
		this.sb = sb;
	}

	public static String extractIDFromURI(URI uri) {
		return uri.getPath().substring(uri.getPath().lastIndexOf("/") + 1);
	}

	public boolean readyForUploading() {
		return sus.isUploadFileToTemp() && sus.getCollection() != null;
	}

}
