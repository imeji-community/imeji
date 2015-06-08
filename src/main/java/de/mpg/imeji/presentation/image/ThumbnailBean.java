/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.image;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ValueChangeEvent;

import org.apache.log4j.Logger;

import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.auth.util.AuthUtil;
import de.mpg.imeji.logic.storage.StorageController;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.MetadataSet;
import de.mpg.imeji.logic.vo.Properties.Status;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.logic.vo.predefinedMetadata.Link;
import de.mpg.imeji.logic.vo.predefinedMetadata.Publication;
import de.mpg.imeji.presentation.beans.ContainerBean;
import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.metadata.MetadataSetBean;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.session.SessionObjectsController;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.CommonUtils;
import de.mpg.imeji.presentation.util.ImejiFactory;
import de.mpg.imeji.presentation.util.ObjectCachedLoader;
import de.mpg.imeji.presentation.util.ObjectLoader;

/**
 * Bean for Thumbnail list elements. Each element of a list with thumbnail is an
 * instance of a {@link ThumbnailBean}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class ThumbnailBean {
	private String link = "";
	private String filename = "";
	private String caption = "";
	private URI uri = null;
	private String id;
	private List<Metadata> metadata = new ArrayList<Metadata>();
	private List<Statement> statements = new ArrayList<Statement>();
	private boolean selected = false;
	private boolean isInActiveAlbum = false;
	private SessionBean sessionBean;
	private static Logger logger = Logger.getLogger(ThumbnailBean.class);
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
		this.sessionBean = (SessionBean) BeanHelper
				.getSessionBean(SessionBean.class);
		setUri(item.getId());
		Navigation navigation = (Navigation) BeanHelper
				.getApplicationBean(Navigation.class);
		setId(ObjectHelper.getId(getUri()));
		setLink(( Status.WITHDRAWN != item.getStatus()) ? 
				navigation.getFileUrl() + item.getThumbnailImageUrl().toString() :
				navigation.getApplicationUrl()+"resources/icon/discarded.png");
		setFilename(item.getFilename());
		setMdSet(item.getMetadataSet());
		setMetadata((List<Metadata>) item.getMetadataSet().getMetadata());
		setCaption(findCaption());
		setSelected(sessionBean.getSelected().contains(uri.toString()));
		if (sessionBean.getActiveAlbum() != null) {
			setInActiveAlbum(sessionBean.getActiveAlbum().getImages()
					.contains(item.getId()));
		}

		setCollectionUri(item.getCollection());

	}

	/**
	 * Inititialize the popup with the metadata for this image. The method is
	 * called directly from xhtml
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getInitPopup() throws Exception {
		//TODO: This method should be left ofer to execute ONLY after Hover from the presentation (or on explicit action) and not always for each ThumbnailBean!
		setProfile(ObjectCachedLoader.loadProfileWithoutPrivs(getMdSet().getProfile()));
		setMds(new MetadataSetBean(getMdSet(), getProfile(), false));
		setStatements(loadStatements(getProfile().getId()));
		return "";
	}

	/**
	 * Load the statements of a {@link MetadataProfile} according to its id (
	 * {@link URI} )
	 * 
	 * @param uri
	 * @return
	 */
	private List<Statement> loadStatements(URI uri) {
		try {
			if (getProfile() != null) {
				return (List<Statement>) getProfile().getStatements();
			}
		} catch (Exception e) {
			BeanHelper.error(sessionBean.getMessage("error_profile_load") + " "
					+ uri + "  " + sessionBean.getLabel("of") + " " + uri);
			// TODO
			logger.error("Error load profile " + uri + " of item " + uri, e);
		}
		return new ArrayList<Statement>();
	}

	/**
	 * Find the caption for this {@link ThumbnailBean} as defined in the
	 * {@link MetadataProfile}. If none defined in the {@link MetadataProfile}
	 * return the filename
	 * 
	 * @return
	 */
	private String findCaption() {
		for (Statement s : getStatements()) {
			if (s.isDescription()) {
				for (Metadata md : getMetadata()) {
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
	public List<Metadata> getMetadata() {
		return metadata;
	}

	/**
	 * setter
	 * 
	 * @param metadata
	 */
	public void setMetadata(List<Metadata> metadata) {
		this.metadata = metadata;
	}

	/**
	 * getter
	 * 
	 * @return
	 */
	public List<Statement> getStatements() {
		return statements;
	}

	/**
	 * setter
	 * 
	 * @param statements
	 */
	public void setStatements(List<Statement> statements) {
		this.statements = statements;
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
	
	/**
	 * @return the mdSet
	 */
	public MetadataSet getMdSet() {
		return mdSet;
	}

	/**
	 * @param mdSet the mdSet to set
	 */
	public void setMdSet(MetadataSet mdSet) {
		this.mdSet = mdSet;
	}

}
