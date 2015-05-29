package de.mpg.imeji.presentation.upload;

import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.exceptions.UnprocessableError;
import de.mpg.imeji.logic.auth.util.AuthUtil;
import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.controller.exceptions.TypeNotAllowedException;
import de.mpg.imeji.logic.search.SPARQLSearch;
import de.mpg.imeji.logic.search.SearchResult;
import de.mpg.imeji.logic.search.vo.*;
import de.mpg.imeji.logic.search.vo.SortCriterion.SortOrder;
import de.mpg.imeji.logic.storage.StorageController;
import de.mpg.imeji.logic.storage.util.StorageUtils;
import de.mpg.imeji.logic.util.UrlHelper;
import de.mpg.imeji.logic.vo.*;
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

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

	@PostConstruct
	public void init() {
		if (user != null) {
			try {
				loadCollections();
				if (UrlHelper.getParameterBoolean("init")) {
					sus.reset();

				} else if (UrlHelper.getParameterBoolean("start")) {
					upload();
				} else if (UrlHelper.getParameterBoolean("done")) {
					sus.copyToTemp();
				}
			} catch (Exception e) {
				logger.info("Some exception happened during initialization", e);
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

	/*
	 * public void copyValueToItem(List <SuperMetadataBean> itemStatements, List
	 * <SuperMetadataBean> statements){ for(SuperMetadataBean smdb1 :
	 * itemStatements) { for(SuperMetadataBean smdb2 : statements) {
	 * if(smdb1.getStatement().getId().equals(smdb2.getStatement().getId())) {
	 * smdb1.setText(smdb2.getText()); smdb1.setDate(smdb2.getDate());
	 * smdb1.setCitation(smdb2.getCitation());
	 * smdb1.setConeId(smdb2.getConeId()); smdb1.setPerson(smdb2.getPerson());
	 * smdb1.setName(smdb2.getName()); smdb1.setLatitude(smdb2.getLatitude());
	 * smdb1.setLongitude(smdb2.getLongitude());
	 * smdb1.setLicense(smdb2.getLicense());
	 * smdb1.setExternalUri(smdb2.getExternalUri());
	 * smdb1.setLabel(smdb2.getLabel()); smdb1.setNumber(smdb2.getNumber());
	 * smdb1.setExportFormat(smdb2.getExportFormat());
	 * smdb1.setCitation(smdb2.getCitation()); } } }
	 * 
	 * }
	 */
	// No throw Exception
	private Item uploadFileToItem(File file, String title) {
		try {
			Item item = ImejiFactory.newItem(getCollection());
			ItemController controller = new ItemController();
			item = controller.create(item, file, title, user, null, null);
			sus.setUploadedItem(item);
			return item;
		} catch (Exception e) {
			sus.setfFile(" File " + title + " not uploaded: " + e.getMessage());
			return null;
		}
	}

	/**
	 * Throws an {@link Exception} if the file ca be upload. Works only if the
	 * file has an extension (therefore, for file without extension, the
	 * validation will only occur when the file has been stored locally)
	 */
	// private void validateName(File file, String title) {
	// if (StorageUtils.hasExtension(title)) {
	// StorageController sc = new StorageController();
	// String guessedNotAllowedFormat = sc.guessNotAllowedFormat(file);
	// if (StorageUtils.BAD_FORMAT.equals(guessedNotAllowedFormat)) {
	// SessionBean sessionBean = (SessionBean) BeanHelper
	// .getSessionBean(SessionBean.class);
	// throw new RuntimeException(
	// sessionBean.getMessage("upload_format_not_allowed") + " (" +
	// guessedNotAllowedFormat + ")");
	// }
	// }
	// }

	public void upload() {
		HttpServletRequest request = (HttpServletRequest) FacesContext
				.getCurrentInstance().getExternalContext().getRequest();
		List<String> techMd = new ArrayList<String>();
		try {
			this.ingestImage = getUploadedIngestFile(request);
			sus.setIngestImage(ingestImage);
			techMd = TikaExtractor.extractFromFile(ingestImage.getFile());
			sus.setTechMD(techMd);
		} catch (FileUploadException | TypeNotAllowedException e) {
			BeanHelper.error(e.getMessage());
		}

	}

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
							StorageController sc = new StorageController();
							if (sc.guessNotAllowedFormat(tmp).equals(
									StorageUtils.BAD_FORMAT)) {
								throw new TypeNotAllowedException(
										sb.getMessage("single_upload_invalid_content_format"));
							}
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
