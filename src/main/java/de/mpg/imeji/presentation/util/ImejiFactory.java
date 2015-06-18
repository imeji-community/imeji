/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.util;

import de.mpg.imeji.logic.vo.*;
import de.mpg.imeji.logic.vo.Item.Visibility;
import de.mpg.imeji.logic.vo.Properties.Status;
import de.mpg.imeji.presentation.album.AlbumBean;
import de.mpg.imeji.presentation.collection.CollectionListItem;
import de.mpg.imeji.presentation.image.ThumbnailBean;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.j2j.misc.LocalizedString;
import org.apache.log4j.Logger;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Create objects ready to be displayed in JSF
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class ImejiFactory {
	private static Logger logger = Logger.getLogger(ImejiFactory.class);

	public static Album newAlbum() {
		Album album = new Album();
		album.setMetadata(newContainerMetadata());
		return album;
	}

	public static CollectionImeji newCollection() {
		CollectionImeji coll = new CollectionImeji();
		coll.setMetadata(newContainerMetadata());
		return coll;
	}

	public static CollectionImeji newCollection(String title,
			String firstAuthorFamilyName, String firstAuthorGivenName,
			String firstAuthorOrganization) {
		CollectionImeji coll = new CollectionImeji();
		coll.setMetadata(newContainerMetadata(title, firstAuthorFamilyName,
				firstAuthorGivenName, firstAuthorOrganization));
		return coll;
	}

	public static MetadataProfile newProfile() {
		MetadataProfile p = new MetadataProfile();
		return p;
	}

	public static ContainerMetadata newContainerMetadata() {
		ContainerMetadata cm = new ContainerMetadata();
		cm.getPersons().add(newPerson());
		return cm;
	}

	public static ContainerMetadata newContainerMetadata(String title,
			String firstAuthorFamilyName, String firstAuthorGivenName,
			String firstAuthorOrganization) {
		ContainerMetadata cm = new ContainerMetadata();
		cm.setTitle(title);
		cm.getPersons().add(
				newPerson(firstAuthorFamilyName, firstAuthorGivenName,
						firstAuthorOrganization));
		return cm;
	}

	public static Space newSpace() {
		Space s = new Space();
		return s;
	}

	/**
	 * Crate a new emtpy {@link Statement}
	 * 
	 * @return
	 */
	public static Statement newStatement() {
		Statement s = new Statement();
		s.getLabels().add(new LocalizedString("", null));
		return s;
	}

	/**
	 * Create an emtpy {@link Statement} as a child of another {@link Statement}
	 * 
	 * @param parent
	 * @return
	 */
	public static Statement newStatement(URI parent) {
		Statement s = newStatement();
		s.setParent(parent);
		return s;
	}

	public static Person newPerson() {
		Person pers = new Person();
		pers.setAlternativeName("");
		pers.setFamilyName("");
		pers.setGivenName("");
		pers.getOrganizations().add(newOrganization());
		return pers;
	}

	public static Person newPerson(String familyName, String givenName,
			String firstOrganization) {
		Person pers = new Person();
		pers.setAlternativeName("");
		pers.setFamilyName(familyName);
		pers.setGivenName(givenName);
		pers.getOrganizations().add(newOrganization(firstOrganization));
		return pers;
	}

	public static Organization newOrganization() {
		Organization org = new Organization();
		org.setName("");
		return org;
	}

	public static Organization newOrganization(String name) {
		Organization org = new Organization();
		org.setName(name);
		return org;
	}

	public static MetadataSet newMetadataSet(URI profile) {
		MetadataSet mds = new MetadataSet();
		mds.setProfile(profile);
		return mds;
	}

	/**
	 * Create a new emtpy {@link Item}
	 * 
	 * @param collection
	 * @return
	 */
	public static Item newItem(CollectionImeji collection) {
		Item item = new Item();
		if (collection == null || collection.getId() == null) {
			throw new RuntimeException(
					"Can not create item with a collection null");
		}
		item.setCollection(collection.getId());
		item.getMetadataSets().add(newMetadataSet(collection.getProfile()));
		return item;
	}

	/**
	 * Factory Method used during the upload
	 * 
	 * @param collection
	 * @param user
	 * @param storageId
	 * @param title
	 * @param fullImageURI
	 * @param thumbnailURI
	 * @param webURI
	 * @return
	 */
	public static Item newItem(CollectionImeji collection, User user,
			String storageId, String title, URI fullImageURI, URI thumbnailURI,
			URI webURI, String filetype) {
		Item item = ImejiFactory.newItem(collection);
		return newItem(item, collection, user, storageId, title, fullImageURI,
				thumbnailURI, webURI, filetype);
	}

	/**
	 * Copy the params into the item
	 * 
	 * @param item
	 * @param collection
	 * @param user
	 * @param storageId
	 * @param title
	 * @param fullImageURI
	 * @param thumbnailURI
	 * @param webURI
	 * @param filetype
	 * @return
	 */
	public static Item newItem(Item item, CollectionImeji collection,
			User user, String storageId, String title, URI fullImageURI,
			URI thumbnailURI, URI webURI, String filetype) {
		item.setFullImageUrl(fullImageURI);
		item.setThumbnailImageUrl(thumbnailURI);
		item.setWebImageUrl(webURI);
		item.setVisibility(Visibility.PUBLIC);
		item.setFilename(title);
		item.setFiletype(filetype);
		if (storageId != null) {
			item.setStorageId(storageId);
		}
		if (collection.getStatus() == Status.RELEASED) {
			item.setStatus(Status.RELEASED);
		}
		return item;
	}

	/**
	 * Transform a {@link List} of {@link CollectionImeji} to a {@link List} of
	 * {@link CollectionListItem}
	 * 
	 * @param collList
	 * @param user
	 * @return
	 */
	public static List<CollectionListItem> collectionListToListItem(
			Collection<CollectionImeji> collList, User user) {
		List<CollectionListItem> l = new ArrayList<CollectionListItem>();
		for (CollectionImeji c : collList) {
			l.add(new CollectionListItem(c, user));
		}
		return l;
	}

	/**
	 * Transform a {@link List} of {@link Album} to a {@link List} of
	 * {@link AlbumBean}
	 * 
	 * @param albumList
	 * @return
	 * @throws Exception
	 */
	public static List<AlbumBean> albumListToBeanList(
			Collection<Album> albumList) throws Exception {
		List<AlbumBean> beanList = new ArrayList<AlbumBean>();
		for (Album album : albumList) {
			beanList.add(new AlbumBean(album));
		}
		return beanList;
	}

	/**
	 * Transform a {@link List} of {@link Item} to a {@link List} of
	 * {@link ThumbnailBean}
	 * 
	 * @param itemList
	 * @return
	 */
	public static List<ThumbnailBean> imageListToThumbList(
			Collection<Item> itemList) {
		List<ThumbnailBean> beanList = new ArrayList<ThumbnailBean>();
		try {
			((SessionBean) BeanHelper.getSessionBean(SessionBean.class))
					.setProfileCached(ProfileHelper
							.loadProfiles(new ArrayList<Item>(itemList)));
		} catch (Exception e) {
			logger.error("Error loading profiles", e);
		}
		for (Item img : itemList) {
			try {
				beanList.add(new ThumbnailBean(img));
			} catch (Exception e) {
				logger.error("Error creating ThumbnailBean list", e);
			}
		}
		return beanList;
	}
}
