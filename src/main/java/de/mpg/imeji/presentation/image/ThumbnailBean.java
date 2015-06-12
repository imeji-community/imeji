/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.image;

import java.net.URI;

import javax.faces.event.ValueChangeEvent;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.MetadataSet;
import de.mpg.imeji.logic.vo.Properties.Status;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.logic.vo.predefinedMetadata.Link;
import de.mpg.imeji.logic.vo.predefinedMetadata.Publication;
import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.metadata.MetadataSetBean;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.session.SessionObjectsController;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.CommonUtils;
import de.mpg.imeji.presentation.util.ObjectCachedLoader;

/**
 * Bean for Thumbnail list elements. Each element of a list with thumbnail is an
 * instance of a {@link ThumbnailBean}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class ThumbnailBean {
	private static Logger logger = Logger.getLogger(ThumbnailBean.class);
	private String link = "";
	private String filename = "";
	private String caption = "";
	private URI uri = null;
	private String id;
	private boolean selected = false;
	private boolean isInActiveAlbum = false;
	private MetadataSetBean mds;
	private MetadataProfile profile;
	private MetadataSet mdSet;
	private URI collectionUri;

	/**
	 * Emtpy {@link ThumbnailBean}
	 */
	public ThumbnailBean() {

	}

	/**
	 * Bean for Thumbnail list elements. Each element of a list with thumbnail
	 * is an instance of a {@link ThumbnailBean}
	 * 
	 * @param item
	 * @throws Exception
	 */
	public ThumbnailBean(Item item) throws Exception {
		SessionBean sessionBean = (SessionBean) BeanHelper
				.getSessionBean(SessionBean.class);
		Navigation navigation = (Navigation) BeanHelper
				.getApplicationBean(Navigation.class);
		this.uri = item.getId();
		this.collectionUri = item.getCollection();
		this.id = ObjectHelper.getId(getUri());
		this.link = Status.WITHDRAWN != item.getStatus() ? navigation
				.getFileUrl() + item.getThumbnailImageUrl().toString()
				: navigation.getApplicationUrl()
						+ "resources/icon/discarded.png";
		this.filename = item.getFilename();
		this.mdSet = item.getMetadataSet();
		this.profile = ObjectCachedLoader.loadProfileWithoutPrivs(this.mdSet
				.getProfile());
		this.caption = findCaption();
		this.selected = sessionBean.getSelected().contains(uri.toString());
		if (sessionBean.getActiveAlbum() != null) {
			this.isInActiveAlbum = sessionBean.getActiveAlbum().getImages()
					.contains(item.getId());
		}

	}

	/**
	 * Initialize the {@link MetadataSetBean} which is used in the Popup
	 */
	public void initPopup() {
		if (getMds() == null) {
			setMds(new MetadataSetBean(mdSet, getProfile(), false));
			// Commented out, statements have to be filled in for sake of
			// Caption
			// setStatements(loadStatements(getProfile().getId()));
		}
	}

	/**
	 * Find the caption for this {@link ThumbnailBean} as defined in the
	 * {@link MetadataProfile}. If none defined in the {@link MetadataProfile}
	 * return the filename
	 * 
	 * @return
	 */
	private String findCaption() {
		for (Statement s : profile.getStatements()) {
			if (s.isDescription()) {
				for (Metadata md : mdSet.getMetadata()) {
					if (md.getStatement().equals(s.getId())) {
						String str = "";
						if (md instanceof Link)
							str = ((Link) md).getLabel();
						else if (md instanceof Publication)
							str = CommonUtils.removeTags(((Publication) md)
									.getCitation());
						else
							str = md.asFulltext();
						if (!"".equals(str.trim()))
							return str;
					}
				}
			}
		}
		return getFilename();
	}

	/**
	 * Listener for the select box of this {@link ThumbnailBean}
	 * 
	 * @param event
	 */
	public void selectedChanged(ValueChangeEvent event) {
		SessionObjectsController soc = new SessionObjectsController();
		if (event.getNewValue().toString().equals("true")) {
			setSelected(true);
			soc.selectItem(getUri().toString());
		} else if (event.getNewValue().toString().equals("false")) {
			setSelected(false);
			soc.unselectItem(getUri().toString());
		}
	}

	/**
	 * getter
	 * 
	 * @return
	 */
	public String getLink() {
		return link;
	}

	/**
	 * setter
	 * 
	 * @param link
	 */
	public void setLink(String link) {
		this.link = link;
	}

	/**
	 * getter
	 * 
	 * @return
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * setter
	 * 
	 * @param filename
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	/**
	 * getter
	 * 
	 * @return
	 */
	public String getCaption() {
		return caption;
	}

	/**
	 * setter
	 * 
	 * @param caption
	 */
	public void setCaption(String caption) {
		this.caption = caption;
	}

	/**
	 * getter
	 * 
	 * @return
	 */
	public URI getUri() {
		return uri;
	}

	/**
	 * setter
	 * 
	 * @param id
	 */
	public void setUri(URI id) {
		this.uri = id;
	}

	/**
	 * getter
	 * 
	 * @return
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * setter
	 * 
	 * @param selected
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/**
	 * getter
	 * 
	 * @return
	 */
	public boolean isInActiveAlbum() {
		return isInActiveAlbum;
	}

	/**
	 * setter
	 * 
	 * @param isInActiveAlbum
	 */
	public void setInActiveAlbum(boolean isInActiveAlbum) {
		this.isInActiveAlbum = isInActiveAlbum;
	}

	/**
	 * getter
	 * 
	 * @return
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
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
	 * setter
	 * 
	 * @param mds
	 *            the mds to set
	 */
	public void setMds(MetadataSetBean mds) {
		this.mds = mds;
	}

	public URI getCollectionUri() {
		return collectionUri;
	}

	public void setCollectionUri(URI colUri) {
		this.collectionUri = colUri;
	}

	public MetadataProfile getProfile() {
		return profile;
	}

	public void setProfile(MetadataProfile profile) {
		this.profile = profile;
	}

}
