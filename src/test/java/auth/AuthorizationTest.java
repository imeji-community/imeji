package auth;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.mpg.imeji.logic.auth.Authorization;
import de.mpg.imeji.logic.auth.authorization.AuthorizationPredefinedRoles;
import de.mpg.imeji.logic.auth.util.AuthUtil;
import de.mpg.imeji.logic.controller.ShareController;
import de.mpg.imeji.logic.controller.ShareController.ShareRoles;
import de.mpg.imeji.logic.vo.Album;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Properties.Status;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.beans.PropertyBean;

/**
 * JUnit tests for the {@link Authorization}
 * 
 * @author saquet
 *
 */
public class AuthorizationTest {

	public static final String testCollectionURI = "http://imeji.org/collection/test";
	public static final String testProfileURI = "http://imeji.org/profile/test";
	public static final String testAlbumURI = "http://imeji.org/album/test";
	public static final String testItemURI = "http://imeji.org/item/test";

	@BeforeClass
	public static void init() {
		new PropertyBean();
	}

	@Test
	public void readCollectionTest() {
		User user = getDefaultUser();
		List<String> roles = new ArrayList<String>();
		roles.add(ShareRoles.READ.toString());
		user.getGrants().addAll(
				ShareController.transformRolesToGrants(roles,
						testCollectionURI));
		Assert.assertTrue(AuthUtil.staticAuth().read(user, getCollection()));
	}

	@Test
	public void createCollectionTest() {
		User user = getDefaultUser();
		Assert.assertTrue(AuthUtil.isAllowedToCreateCollection(user));
		Assert.assertTrue(AuthUtil.staticAuth().create(user, getCollection()));
	}

	@Test
	public void createCollectionTestRestrictedUSer() {
		User user = getRestrictedUser();
		Assert.assertFalse(AuthUtil.isAllowedToCreateCollection(user));
		Assert.assertFalse(AuthUtil.staticAuth().create(user, getCollection()));
	}

	@Test
	public void uploadItem() {
		User user = getDefaultUser();
		List<String> roles = new ArrayList<String>();
		roles.add(ShareRoles.READ.toString());
		roles.add(ShareRoles.CREATE.toString());
		user.getGrants().addAll(
				ShareController.transformRolesToGrants(roles,
						testCollectionURI));
		// Test if the user has grant to upload in the collection
		Assert.assertTrue(AuthUtil.staticAuth().createContent(user,
				getCollection()));
		// Test if the user has grant to upload the item
		Assert.assertTrue(AuthUtil.staticAuth().create(user, getItem()));
		// Test if a user without grant can upload in the collection
		Assert.assertFalse(AuthUtil.staticAuth().createContent(
				getDefaultUser(), getCollection()));
		// Test if a user without grant can upload the item
		Assert.assertFalse(AuthUtil.staticAuth().create(getDefaultUser(),
				getItem()));
	}

	@Test
	public void uploadItemDiscardedCollection() {
		User user = getDefaultUser();
		List<String> roles = new ArrayList<String>();
		roles.add(ShareRoles.READ.toString());
		roles.add(ShareRoles.CREATE.toString());
		user.getGrants().addAll(
				ShareController.transformRolesToGrants(roles,
						testCollectionURI));
		// Discard the collection
		CollectionImeji c = getCollection();
		c.setStatus(Status.WITHDRAWN);
		// Test if the user has grant to upload in the collection
		Assert.assertFalse(AuthUtil.staticAuth().create(user, c));
	}

	@Test
	public void updateCollectionTest() {
		User user = getDefaultUser();
		List<String> roles = new ArrayList<String>();
		roles.add(ShareRoles.READ.toString());
		roles.add(ShareRoles.EDIT.toString());
		user.getGrants().addAll(
				ShareController.transformRolesToGrants(roles,
						testCollectionURI));
		// is allowed to update the collection?
		Assert.assertTrue(AuthUtil.staticAuth().update(user, getCollection()));
		// is NOT allowed to update the items of the collection?
		Assert.assertFalse(AuthUtil.staticAuth().updateContent(user,
				getCollection()));
		Assert.assertFalse(AuthUtil.staticAuth().update(user, getItem()));
	}

	@Test
	public void updateCollectionItemsTest() {
		User user = getDefaultUser();
		List<String> roles = new ArrayList<String>();
		roles.add(ShareRoles.READ.toString());
		roles.add(ShareRoles.EDIT_ITEM.toString());
		user.getGrants().addAll(
				ShareController.transformRolesToGrants(roles,
						testCollectionURI));
		// is allowed to update the items of the collection?
		Assert.assertTrue(AuthUtil.staticAuth().updateContent(user,
				getCollection()));
		Assert.assertTrue(AuthUtil.staticAuth().update(user, getItem()));
		// is NOT allowed to update the collection?
		Assert.assertFalse(AuthUtil.staticAuth().update(user, getCollection()));
	}

	/**
	 * Only collection admin can delete a collection
	 */
	@Test
	public void deleteCollectionTest() {
		User user = getDefaultUser();
		List<String> roles = new ArrayList<String>();
		roles.add(ShareRoles.READ.toString());
		roles.add(ShareRoles.ADMIN.toString());
		user.getGrants().addAll(
				ShareController.transformRolesToGrants(roles,
						testCollectionURI));
		// Is allowed to delete the collection?
		Assert.assertTrue(AuthUtil.staticAuth().delete(user, getCollection()));
	}

	/**
	 * Only collection admin can delete a collection
	 */
	@Test
	public void deleteCollectionReleasedTest() {
		User user = getDefaultUser();
		List<String> roles = new ArrayList<String>();
		roles.add(ShareRoles.READ.toString());
		roles.add(ShareRoles.ADMIN.toString());
		user.getGrants().addAll(
				ShareController.transformRolesToGrants(roles,
						testCollectionURI));
		// Release collection
		CollectionImeji c = getCollection();
		c.setStatus(Status.RELEASED);
		// Is allowed to delete the collection?
		Assert.assertFalse(AuthUtil.staticAuth().delete(user, c));
	}

	/**
	 * Test edit items grants
	 */
	@Test
	public void deleteCollectionItemTest() {
		User user = getDefaultUser();
		List<String> roles = new ArrayList<String>();
		roles.add(ShareRoles.READ.toString());
		roles.add(ShareRoles.DELETE_ITEM.toString());
		user.getGrants().addAll(
				ShareController.transformRolesToGrants(roles,
						testCollectionURI));
		// Is allowed to delete the items collection?
		Assert.assertTrue(AuthUtil.staticAuth().deleteContent(user,
				getCollection()));
		Assert.assertTrue(AuthUtil.staticAuth().delete(user, getItem()));
		// Is NOT allowed to delete the collection?
		Assert.assertFalse(AuthUtil.staticAuth().delete(user, getCollection()));
	}

	/**
	 * Test if user can edit the profile of a collection
	 */
	@Test
	public void editCollectionProfileTest() {
		User user = getDefaultUser();
		List<String> roles = new ArrayList<String>();
		roles.add(ShareRoles.READ.toString());
		roles.add(ShareRoles.EDIT.toString());
		user.getGrants().addAll(
				ShareController.transformRolesToGrants(roles,
						testProfileURI ));
		// Is allowed to edit the profile of the collection?
		Assert.assertTrue(AuthUtil.staticAuth().update(user,
				getCollection().getProfile()));
		// Is allowed to edit the profile?
		Assert.assertTrue(AuthUtil.staticAuth().update(user, getProfile()));
	}

	@Test
	public void createAlbumForRestrictedUser() {
		User user = getRestrictedUser();
		Assert.assertTrue(AuthUtil.staticAuth().create(user, getAlbum()));
	}

	@Test
	public void viewAlbumTest() {
		User user = getDefaultUser();
		List<String> roles = new ArrayList<String>();
		roles.add(ShareRoles.READ.toString());
		Album album = getAlbum();
		user.getGrants().addAll(
				ShareController.transformRolesToGrants(roles, album.getId()
						.toString()));
		// Is allowed to view the album?
		Assert.assertTrue(AuthUtil.staticAuth().read(user, album));
	}

	@Test
	public void addRemoveItemToAlbumTest() {
		User user = getDefaultUser();
		List<String> roles = new ArrayList<String>();
		roles.add(ShareRoles.CREATE.toString());
		Album album = getAlbum();
		user.getGrants().addAll(
				ShareController.transformRolesToGrants(roles, album.getId()
						.toString()));
		// Is allowed to add an Item to the album
		Assert.assertTrue(AuthUtil.staticAuth().createContent(user, album));
		// Is not allowed to add/remove an item
		Assert.assertTrue(!AuthUtil.staticAuth().createContent(
				getDefaultUser(), album));
	}

	@Test
	public void editAlbumTest() {
		User user = getDefaultUser();
		List<String> roles = new ArrayList<String>();
		roles.add(ShareRoles.EDIT.toString());
		Album album = getAlbum();
		user.getGrants().addAll(
				ShareController.transformRolesToGrants(roles, album.getId()
						.toString()));
		// Is allowed to edit an album
		Assert.assertTrue(AuthUtil.staticAuth().update(user, album));
	}

	/**
	 * Test when User as only a grant to view an item but not its collection
	 */
	@Test
	public void readItemTest() {
		User user = getDefaultUser();
		List<String> roles = new ArrayList<String>();
		roles.add(ShareRoles.READ.toString());
		user.getGrants().addAll(
				ShareController.transformRolesToGrants(roles, getItem().getId()
						.toString()));
		// Is allowed to view the item?
		Assert.assertTrue(AuthUtil.staticAuth().read(user, getItem()));
		// Is allowed to view the collection?
		Assert.assertFalse(AuthUtil.staticAuth().read(user, getCollection()));
	}

	/**
	 * Get a {@link CollectionImeji} for these tests
	 * 
	 * @return
	 */
	public CollectionImeji getCollection() {
		CollectionImeji c = new CollectionImeji();
		c.setId(URI.create(testCollectionURI));
		c.setProfile(URI.create(testProfileURI));
		return c;
	}

	/**
	 * Get a {@link CollectionImeji} for these tests
	 * 
	 * @return
	 */
	public Album getAlbum() {
		Album a = new Album();
		a.setId(URI.create(testAlbumURI));
		return a;
	}

	/**
	 * Get a {@link MetadataProfile} for these tests
	 * 
	 * @return
	 */
	public MetadataProfile getProfile() {
		MetadataProfile p = new MetadataProfile();
		p.setId(URI.create(testProfileURI));
		return p;
	}

	/**
	 * Get an {@link Item} for these tests
	 * 
	 * @return
	 */
	public Item getItem() {
		Item item = new Item();
		item.setId(URI.create(testItemURI));
		item.setCollection(getCollection().getId());
		return item;
	}

	/**
	 * A user with grant to create a collection in imeji
	 * 
	 * @return
	 */
	private User getDefaultUser() {
		User user = new User();
		user.setGrants(AuthorizationPredefinedRoles.defaultUser(user.getId()
				.toString()));
		return user;
	}

	/**
	 * A user without grant to create a collection in imeji
	 * 
	 * @return
	 */
	private User getRestrictedUser() {
		User user = new User();
		user.setGrants(AuthorizationPredefinedRoles.restrictedUser(user.getId()
				.toString()));
		return user;
	}

}
