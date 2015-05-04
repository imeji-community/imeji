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
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.beans.PropertyBean;
import de.mpg.imeji.presentation.user.ShareBean;

/**
 * JUnit tests for the {@link Authorization}
 * 
 * @author saquet
 *
 */
public class AuthorizationTest {

	public static final String testCollectionURI = "http://imeji.org/collection/test";
	public static final String testProfileURI = "http://imeji.org/profile/test";

	@BeforeClass
	public static void init() {
		new PropertyBean();
	}

	@Test
	public void readCollectionTest() {
		User user = getDefaultUser();
		List<String> roles = new ArrayList<String>();
		roles.add(ShareBean.ShareType.READ.toString());
		user.getGrants().addAll(
				ShareBean.getGrantsAccordingtoRoles(roles, testCollectionURI,
						testProfileURI));
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
		roles.add(ShareBean.ShareType.READ.toString());
		roles.add(ShareBean.ShareType.CREATE.toString());
		user.getGrants().addAll(
				ShareBean.getGrantsAccordingtoRoles(roles, testCollectionURI,
						testProfileURI));
		// Test if the user has grant to upload in the collection
		Assert.assertTrue(AuthUtil.staticAuth().create(user, getCollection()));
		// Test if the user has grant to upload the item
		Assert.assertTrue(AuthUtil.staticAuth().create(user, getItem()));
	}

	@Test
	public void updateCollectionTest() {
		User user = getDefaultUser();
		List<String> roles = new ArrayList<String>();
		roles.add(ShareBean.ShareType.READ.toString());
		roles.add(ShareBean.ShareType.EDIT_CONTAINER.toString());
		user.getGrants().addAll(
				ShareBean.getGrantsAccordingtoRoles(roles, testCollectionURI,
						testProfileURI));
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
		roles.add(ShareBean.ShareType.READ.toString());
		roles.add(ShareBean.ShareType.EDIT_ITEM.toString());
		user.getGrants().addAll(
				ShareBean.getGrantsAccordingtoRoles(roles, testCollectionURI,
						testProfileURI));
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
		roles.add(ShareBean.ShareType.READ.toString());
		roles.add(ShareBean.ShareType.ADMIN.toString());
		user.getGrants().addAll(
				ShareBean.getGrantsAccordingtoRoles(roles, testCollectionURI,
						testProfileURI));
		// Is allowed to delete the collection?
		Assert.assertTrue(AuthUtil.staticAuth().delete(user, getCollection()));
	}

	/**
	 * Test edit items grants
	 */
	@Test
	public void deleteCollectionItemTest() {
		User user = getDefaultUser();
		List<String> roles = new ArrayList<String>();
		roles.add(ShareBean.ShareType.READ.toString());
		roles.add(ShareBean.ShareType.DELETE_ITEM.toString());
		user.getGrants().addAll(
				ShareBean.getGrantsAccordingtoRoles(roles, testCollectionURI,
						testProfileURI));
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
		roles.add(ShareBean.ShareType.READ.toString());
		roles.add(ShareBean.ShareType.EDIT_PROFILE.toString());
		user.getGrants().addAll(
				ShareBean.getGrantsAccordingtoRoles(roles, testCollectionURI,
						testProfileURI));
		// Is allowed to edit the profile of the collection?
		Assert.assertTrue(AuthUtil.staticAuth().update(user,
				getCollection().getProfile()));
		// Is allowed to edit the profile?
		Assert.assertTrue(AuthUtil.staticAuth().update(user,
				getProfile()));
	}

	/**
	 * Test what URI is used for a {@link CollectionImeji} during the
	 * {@link Authorization}
	 */
	@Test
	public void getCollectionUri() {
		CollectionImeji c = getCollection();
		Assert.assertEquals(c.getId().toString(), AuthUtil.staticAuth()
				.getRelevantURIForSecurity(c, false, false));
		Assert.assertEquals(AuthorizationPredefinedRoles.IMEJI_GLOBAL_URI,
				AuthUtil.staticAuth().getRelevantURIForSecurity(c, true, false));

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
