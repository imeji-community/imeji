/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.image;

import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.exceptions.NotFoundException;
import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.auth.util.AuthUtil;
import de.mpg.imeji.logic.concurrency.locks.Locks;
import de.mpg.imeji.logic.controller.AlbumController;
import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.search.SPARQLSearch;
import de.mpg.imeji.logic.search.vo.SearchIndex;
import de.mpg.imeji.logic.search.vo.SearchOperators;
import de.mpg.imeji.logic.search.vo.SearchPair;
import de.mpg.imeji.logic.search.vo.SearchQuery;
import de.mpg.imeji.logic.storage.StorageController;
import de.mpg.imeji.logic.storage.util.StorageUtils;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.util.StringHelper;
import de.mpg.imeji.logic.util.UrlHelper;
import de.mpg.imeji.logic.vo.*;
import de.mpg.imeji.presentation.beans.ConfigurationBean;
import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.beans.PropertyBean;
import de.mpg.imeji.presentation.lang.MetadataLabels;
import de.mpg.imeji.presentation.metadata.MetadataSetBean;
import de.mpg.imeji.presentation.metadata.SingleEditBean;
import de.mpg.imeji.presentation.metadata.extractors.TikaExtractor;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.session.SessionObjectsController;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.ObjectLoader;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Bean for a Single image
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class ItemBean {
	private String tab;
	private SessionBean sessionBean;
	private Item item;
	private String id;
	private boolean selected;
	private CollectionImeji collection;
	private List<String> techMd;
	protected Navigation navigation;
	private MetadataProfile profile;
	private SingleEditBean edit;
	protected String prettyLink;
	private MetadataLabels labels;
	private SingleItemBrowse browse = null;
	private MetadataSetBean mds;
	private List<Album> relatedAlbums;
	private String dateCreated;
	private String newFilename;
	private String stringContent = null;
	
	/**
	 * Construct a default {@link ItemBean}
	 * 
	 * @throws Exception
	 */
	public ItemBean() throws Exception {
		item = new Item();
		sessionBean = (SessionBean) BeanHelper
				.getSessionBean(SessionBean.class);
		navigation = (Navigation) BeanHelper
				.getApplicationBean(Navigation.class);
		prettyLink = sessionBean.getPrettySpacePage("pretty:editImage");
		labels = (MetadataLabels) BeanHelper
				.getSessionBean(MetadataLabels.class);
	}

	/**
	 * Initialize the {@link ItemBean}
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getInit() throws Exception {
		tab = UrlHelper.getParameterValue("tab");
		if ("".equals(tab))
			tab = null;
			try {
				loadImage();
			}
			catch (Exception e)
			{
				FacesContext.getCurrentInstance().getExternalContext()
				.responseSendError(404, "404_NOT_FOUND");

			}
			
			
		if (item != null) {
			if ("techmd".equals(tab)) {
				initViewTechnicalMetadata();
			} else if ("util".equals(tab)) {
				initUtilTab();
			} else {
				initViewMetadataTab();
			}
			initBrowsing();
			selected = sessionBean.getSelected().contains(
					item.getId().toString());
		} else {
			edit = null;
		}
		return "";
	}

	/**
	 * Initialize the util tab
	 * 
	 * @throws Exception
	 */
	private void initUtilTab() throws Exception {
		relatedAlbums = new ArrayList<Album>();
		AlbumController ac = new AlbumController();
		SearchQuery q = new SearchQuery();
		q.addPair(new SearchPair(SPARQLSearch.getIndex(SearchIndex.IndexNames.item),
				SearchOperators.EQUALS, getImage().getId().toString()));
		//TODO NB: check if related albums should be space restricted?
		relatedAlbums = (List<Album>) ac.loadAlbumsLazy(
				ac.search(q, sessionBean.getUser(), null, -1, 0, null).getResults(), sessionBean.getUser(),
				-1, 0);
	}

	/**
	 * Initialize the metadata information when the "view metadata" tab is
	 * called.
	 * 
	 * @throws Exception
	 */
	public void initViewMetadataTab() throws Exception {
		if (item != null) {
			User user = sessionBean.getUser();
			if (AuthUtil.canReadItemButNotCollection(user, item)) {
				// User has right to read the item, but not collection and the
				// profile
				user = Imeji.adminUser;
			}
			loadCollection(user);
			loadProfile(user);
			labels.init(profile);
			if (profile.getId() != null) {
				edit = new SingleEditBean(item, profile, getPageUrl());
				mds = edit.getEditor().getItems().get(0).getMds();
			}
		}
	}

	/**
	 * Initialize the technical metadata when the "technical metadata" tab is
	 * called
	 * 
	 * @throws Exception
	 */
	public void initViewTechnicalMetadata() throws Exception {
		try {
			techMd = new ArrayList<String>();
			techMd = TikaExtractor.extract(item);
			// techMd = BasicExtractor.extractTechMd(item);
		} catch (Exception e) {
			techMd = new ArrayList<String>();
			techMd.add(e.getMessage());
		}
	}

	/**
	 * Initiliaue the {@link SingleItemBrowse} for this {@link ItemBean}
	 * @throws Exception 
	 */
	public void initBrowsing() throws Exception {
		if (item != null)
			browse = new SingleItemBrowse(
					(ItemsBean) BeanHelper.getSessionBean(ItemsBean.class),
					item, "item", "");
	}

	/**
	 * Load the item according to the idntifier defined in the URL
	 * @throws Exception 
	 */
	public void loadImage() throws Exception {
		item = ObjectLoader.loadItem(ObjectHelper.getURI(Item.class, id),
				sessionBean.getUser());
		if (item == null ) {
			throw new NotFoundException("LoadImage: empty");
		}
	}

	/**
	 * Load the collection according to the identifier defined in the URL
	 */
	public void loadCollection(User user) {
		try {
			collection = ObjectLoader.loadCollectionLazy(item.getCollection(),
					user);
		} catch (Exception e) {
			BeanHelper.error(e.getMessage());
			collection = null;
		}
	}

	/**
	 * Load the {@link MetadataProfile} of the {@link Item}
	 */
	public void loadProfile(User user) {
		profile = ObjectLoader.loadProfile(item.getMetadataSet().getProfile(),
				user);
		if (profile == null) {
			profile = new MetadataProfile();
		}
	}

	public String getInitLabels() throws Exception {
		labels.init(profile);
		return "";
	}

	/**
	 * Return and URL encoded version of the filename
	 * 
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String getEncodedFileName() throws UnsupportedEncodingException {
		return URLEncoder.encode(item.getFilename(), "UTF-8");
	}

	public List<String> getTechMd() throws Exception {
		return techMd;
	}

	public void setTechMd(List<String> md) {
		this.techMd = md;
	}

	public String getPageUrl() {
		return navigation.getItemUrl() + id;
	}

	public String clearAll() {
		sessionBean.getSelected().clear();
		return "pretty:";
	}

	public CollectionImeji getCollection() {
		return collection;
	}

	public void setCollection(CollectionImeji collection) {
		this.collection = collection;
	}

	public void setImage(Item item) {
		this.item = item;
	}

	public Item getImage() {
		return item;
	}

	/**
	 * @param selected
	 *            the selected to set
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean getSelected() {
		return selected;
	}

	public String getThumbnailImageUrlAsString() {
		if (item.getThumbnailImageUrl() == null)
			return "/no_thumb";
		return item.getThumbnailImageUrl().toString();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTab() {
		return tab;
	}

	public void setTab(String tab) {
		this.tab = tab.toUpperCase();
	}

	public String getNavigationString() {
		return sessionBean.getPrettySpacePage("pretty:item");
	}

	public SessionBean getSessionBean() {
		return sessionBean;
	}

	public void setSessionBean(SessionBean sessionBean) {
		this.sessionBean = sessionBean;
	}

	@SuppressWarnings("unchecked")
	public void makePublic() throws Exception {
		ItemController c = new ItemController();
		c.release((List<Item>) c.toList(item), sessionBean.getUser());
	}

	@SuppressWarnings("unchecked")
	public void makePrivate() throws Exception {
		ItemController c = new ItemController();
		c.unRelease((List<Item>) c.toList(item), sessionBean.getUser());
	}

	/**
	 * Add the current {@link Item} to the active {@link Album}
	 * 
	 * @return
	 * @throws Exception
	 */
	public String addToActiveAlbum() throws Exception {
		SessionObjectsController soc = new SessionObjectsController();
		List<String> l = new ArrayList<String>();
		l.add(item.getId().toString());
		int sizeBeforeAdd = sessionBean.getActiveAlbumSize();
		soc.addToActiveAlbum(l);
		int sizeAfterAdd = sessionBean.getActiveAlbumSize();
		boolean added = sizeAfterAdd > sizeBeforeAdd;
		if (!added) {
			BeanHelper.error(((SessionBean) BeanHelper
					.getSessionBean(SessionBean.class)).getLabel("image")
					+ " "
					+ item.getFilename()
					+ " "
					+ ((SessionBean) BeanHelper
							.getSessionBean(SessionBean.class))
							.getMessage("already_in_active_album"));
		} else {
			BeanHelper.info(((SessionBean) BeanHelper
					.getSessionBean(SessionBean.class)).getLabel("image")
					+ " "
					+ item.getFilename()
					+ " "
					+ ((SessionBean) BeanHelper
							.getSessionBean(SessionBean.class))
							.getMessage("added_to_active_album"));
		}
		return "";
	}

	/**
	 * Remove the {@link Item} from the database. If the item was in the current
	 * {@link Album}, remove the {@link Item} from it
	 * 
	 * @throws Exception
	 */
	public void delete() throws Exception {
		if (getIsInActiveAlbum()) {
			removeFromActiveAlbum();
		}
		ItemController ic = new ItemController();
		List<Item> l = new ArrayList<Item>();
		l.add(item);
		ic.delete(l, sessionBean.getUser());
		SessionObjectsController soc = new SessionObjectsController();
		soc.unselectItem(item.getId().toString());
		BeanHelper.info(sessionBean.getLabel("image") + " "
				+ item.getFilename() + " "
				+ sessionBean.getMessage("success_collection_remove_from"));
		redirectToBrowsePage();
	}

	/**
	 * Remove the {@link Item} from the active {@link Album}
	 * 
	 * @return
	 * @throws Exception
	 */
	public String removeFromActiveAlbum() throws Exception {
		SessionObjectsController soc = new SessionObjectsController();
		List<String> l = new ArrayList<String>();
		l.add(item.getId().toString());
		soc.removeFromActiveAlbum(l);
		BeanHelper.info(sessionBean.getLabel("image") + " "
				+ item.getFilename() + " "
				+ sessionBean.getMessage("success_album_remove_from"));
		return "pretty:";
	}

	/**
	 * Return true if the {@link Item} is in the active {@link Album}
	 * 
	 * @return
	 */
	public boolean getIsInActiveAlbum() {
		if (sessionBean.getActiveAlbum() != null && item != null) {
			return sessionBean.getActiveAlbum().getImages()
					.contains(item.getId());
		}
		return false;
	}

	/**
	 * True if the current item page is part of the current album
	 * 
	 * @return
	 */
	public boolean isActiveAlbum() {
		return false;
	}

	/**
	 * Redirect to the browse page
	 * 
	 * @throws IOException
	 */
	public void redirectToBrowsePage() throws IOException {
		FacesContext.getCurrentInstance().getExternalContext()
				.redirect(navigation.getBrowseUrl());
	}

	/**
	 * Listener of the value of the select box
	 * 
	 * @param event
	 */
	public void selectedChanged(ValueChangeEvent event) {
		SessionObjectsController soc = new SessionObjectsController();
		if (event.getNewValue().toString().equals("true")) {
			setSelected(true);
			soc.selectItem(item.getId().toString());
		} else if (event.getNewValue().toString().equals("false")) {
			setSelected(false);
			soc.unselectItem(item.getId().toString());
		}
	}

	public MetadataProfile getProfile() {
		return profile;
	}

	public void setProfile(MetadataProfile profile) {
		this.profile = profile;
	}

	public List<SelectItem> getStatementMenu() {
		List<SelectItem> statementMenu = new ArrayList<SelectItem>();
		if (profile == null) {
			loadProfile(sessionBean.getUser());
		}
		for (Statement s : profile.getStatements()) {
			statementMenu.add(new SelectItem(s.getId(), s.getLabels()
					.iterator().next().toString()));
		}
		return statementMenu;
	}

	public SingleEditBean getEdit() {
		return edit;
	}

	public void setEdit(SingleEditBean edit) {
		this.edit = edit;
	}

	public boolean isLocked() {
		return Locks.isLocked(this.item.getId().toString(), sessionBean
				.getUser().getEmail());
	}

	public SingleItemBrowse getBrowse() {
		return browse;
	}

	public void setBrowse(SingleItemBrowse browse) {
		this.browse = browse;
	}

	public String getDescription() {
		for (Statement s : getProfile().getStatements()) {
			if (s.isDescription()) {
				for (Metadata md : this.getImage().getMetadataSet()
						.getMetadata()) {
					if (md.getStatement().equals(s.getId())) {
						return md.asFulltext();
					}
				}
			}
		}
		return item.getFilename();
	}
	
	/**
	 * Function to return the content of the item
	 * @return String
	 */
	public String getStringContent() throws ImejiException {
		StorageController sc = new StorageController();
		stringContent = sc.readFileStringContent(item.getFullImageUrl().toString());
        return stringContent;
	}
	
	/**
	 * Returns a list of all albums this image is added to.
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<Album> getRelatedAlbums() throws Exception {
		return relatedAlbums;
	}

	/**
	 * Return the {@link User} having uploaded the file for this item
	 * 
	 * @return
	 * @throws Exception
	 */
	public User getImageUploader() throws Exception {
		UserController uc = new UserController(Imeji.adminUser);
		User user = uc.retrieve(item.getCreatedBy());
		return user;
	}

	/**
	 * setter
	 * 
	 * @param mds
	 *            the mds to set
	 */
	public void setMds(MetadataSetBean mds) {
		this.mds = mds;
	}

	/**
	 * getter
	 * 
	 * @return the mds
	 */
	public MetadataSetBean getMds() {
		return mds;
	}

	/**
	 * getter
	 * 
	 * @return
	 */
	public String getItemStorageIdFilename() {
		return StringHelper.normalizeFilename(this.item.getFilename());
	}

	/**
	 * True if the current file is an image
	 * 
	 * @return
	 */
	public boolean isImageFile() {
		return StorageUtils.getMimeType(
				FilenameUtils.getExtension(item.getFilename())).contains(
				"image");
	}

	/**
	 * True if the data can be viewed in the data viewer (defined in the
	 * configuration)
	 * 
	 * @return
	 */
	public boolean isViewInDataViewer() {
		return ((ConfigurationBean) BeanHelper
				.getApplicationBean(ConfigurationBean.class))
				.isDataViewerSupportedFormats(FilenameUtils.getExtension(item
						.getFilename()));
	}

	/**
	 * View the File in Digilib (if Digilib is enabled)
	 * 
	 * @return
	 */
	public boolean isViewInDigilib() {
		return ((PropertyBean) BeanHelper
				.getApplicationBean(PropertyBean.class)).isDigilibEnabled()
				&& StorageUtils.getMimeType(
						FilenameUtils.getExtension(item.getFilename()))
						.contains("image")
				&& !"svg"
						.equals(FilenameUtils.getExtension(item.getFilename()));
	}

	/**
	 * True if the current file is a video
	 * 
	 * @return
	 */
	public boolean isVideoFile() {
		return StorageUtils.getMimeType(
				FilenameUtils.getExtension(item.getFilename())).contains(
				"video");
	}

	/**
	 * True if the File is a RAW file (a file which can not be viewed in any
	 * online tool)
	 * 
	 * @return
	 */
	public boolean isRawFile() {
		return !isAudioFile() && !isVideoFile() && !isImageFile()
				&& !isPdfFile();
	}

	/**
	 * True if the current file is a pdf
	 * 
	 * @return
	 */
	public boolean isPdfFile() {
		return StorageUtils.getMimeType(
				FilenameUtils.getExtension(item.getFilename())).contains(
				"application/pdf");
	}
	
	/**
	 * Function checks if the file ends with swc
	 */
	public boolean isSwcFile() {
		return item.getFullImageUrl().toString().endsWith(".swc");
	}
	
	/**
	 * True if the current file is an audio
	 * 
	 * @return
	 */
	public boolean isAudioFile() {
		return StorageUtils.getMimeType(
				FilenameUtils.getExtension(item.getFilename())).contains(
				"audio");
	}

	/**
	 * @return the dateCreated
	 */
	public String getDateCreated() {
		return dateCreated;
	}

	/**
	 * @param dateCreated
	 *            the dateCreated to set
	 */
	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}

	public String getNewFilename() {
		this.newFilename = getImage().getFilename();
		return newFilename;
	}

	public void setNewFilename(String newFilename) {
		if(!"".equals(newFilename))
			getImage().setFilename(newFilename);
	}
}
