/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import de.escidoc.core.resources.aa.useraccount.Grants;
import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.ImejiSPARQL;
import de.mpg.imeji.logic.auth.authorization.AuthorizationPredefinedRoles;
import de.mpg.imeji.logic.auth.util.AuthUtil;
import de.mpg.imeji.logic.search.SPARQLSearch;
import de.mpg.imeji.logic.search.query.SPARQLQueries;
import de.mpg.imeji.logic.vo.Grant;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.logic.vo.UserGroup;
import de.mpg.imeji.logic.writer.WriterFacade;
import de.mpg.imeji.presentation.user.ShareBean.SharedObjectType;
import de.mpg.imeji.presentation.util.ObjectLoader;

/**
 * Controller for {@link Grant}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class ShareController extends ImejiController {
	private static final WriterFacade writer = new WriterFacade(Imeji.userModel);
	private static Logger logger = Logger.getLogger(ShareController.class);

	/**
	 * The Roles which can be shared to every object
	 * 
	 * @author saquet
	 *
	 */
	public enum ShareRoles {
		READ, CREATE, EDIT_ITEM, DELETE_ITEM, EDIT, ADMIN
	}

	/**
	 * Share an object (Item, Collection, Album) to a {@link User}
	 * 
	 * @param fromUser
	 *            - The User sharing the object
	 * @param toUser
	 *            - The user the object is shared to
	 * @param sharedObjectUri
	 *            - The uri of the shared object
	 * @param profileUri
	 *            - (only for collections) the uri of the profile of the
	 *            collection
	 * @param roles
	 *            - The roles given to the shared user
	 * @throws ImejiException
	 */
	public User share(User fromUser, User toUser, String sharedObjectUri,
			List<String> roles) throws ImejiException {
		if (toUser != null) {
			List<Grant> grants = transformRolesToGrants(roles, sharedObjectUri);
			toUser = shareGrants(fromUser, toUser, sharedObjectUri, grants);
		}
		return toUser;
	}

	/**
	 * Share an object (Item, Collection, Album) to a {@link User}
	 * 
	 * @param fromUser
	 *            - The User sharing the object
	 * @param toUser
	 *            - The user the object is shared to
	 * @param sharedObjectUri
	 *            - The uri of the shared object
	 * @param profileUri
	 *            - (only for collections) the uri of the profile of the
	 *            collection
	 * @param grants
	 *            - The grants given to the shared user
	 * @throws ImejiException
	 */
	private User shareGrants(User fromUser, User toUser,
			String sharedObjectUri, List<Grant> grants) throws ImejiException {
		if (toUser != null) {
			// UserController userController = new
			// UserController(Imeji.adminUser);
			// toUser = userController.retrieve(toUser.getEmail());
			toUser = removeGrants(toUser, AuthUtil.extractGrantsFor(
					(List<Grant>) toUser.getGrants(), sharedObjectUri),
					Imeji.adminUser);
			toUser = addGrants(toUser, grants, fromUser);
		}
		return toUser;
	}

	/**
	 * Share an object (Item, Collection, Album) to a {@link UserGroup}
	 * 
	 * @param fromUser
	 *            - The User sharing the object
	 * @param toGroup
	 *            - The group the object is shared to
	 * @param sharedObjectUri
	 *            - The uri of the shared object
	 * @param profileUri
	 *            - (only for collections) the uri of the profile of the
	 *            collection
	 * @param roles
	 *            - The roles given to the shared user
	 * @throws ImejiException
	 */
	public void shareWithGroup(User fromUser, UserGroup toGroup,
			String sharedObjectUri, List<String> roles) throws ImejiException {
		if (toGroup != null) {
			List<Grant> grants = transformRolesToGrants(roles, sharedObjectUri);
			shareGrantsWithGroup(fromUser, toGroup, sharedObjectUri, grants);
		}
	}

	/**
	 * Share an object (Item, Collection, Album) to a {@link UserGroup}
	 * 
	 * @param fromUser
	 *            - The User sharing the object
	 * @param toGroup
	 *            - The group the object is shared to
	 * @param sharedObjectUri
	 *            - The uri of the shared object
	 * @param profileUri
	 *            - (only for collections) the uri of the profile of the
	 *            collection
	 * @param grants
	 *            - The grants given to the shared user
	 * @throws ImejiException
	 */
	private void shareGrantsWithGroup(User fromUser, UserGroup toGroup,
			String sharedObjectUri, List<Grant> grants) throws ImejiException {
		if (toGroup != null) {
			removeGrants(toGroup, AuthUtil.extractGrantsFor(
					(List<Grant>) toGroup.getGrants(), sharedObjectUri),
					Imeji.adminUser);
			addGrants(toGroup, grants, Imeji.adminUser);
		}
	}

	/**
	 * Share an Object with its creator. Should be called when a user creates an
	 * object, to give him all the grants on the created object
	 * 
	 * @param creator
	 * @param sharedObjectUri
	 * @param profileUri
	 * @throws ImejiException
	 */
	public User shareWithCreator(User creator, String sharedObjectUri)
			throws ImejiException {
		return shareGrants(Imeji.adminUser, creator, sharedObjectUri,
				AuthorizationPredefinedRoles.admin(sharedObjectUri));
	}

	/**
	 * Transform a list of {@link ShareRoles} into a list of {@link Grant}
	 * 
	 * @param roles
	 * @param uri
	 * @param profileUri
	 * @return
	 */
	public static List<Grant> transformRolesToGrants(List<String> roles,
			String uri) {
		List<Grant> grants = new ArrayList<Grant>();
		for (String g : roles) {
			switch (g) {
			case "READ":
				grants.addAll(AuthorizationPredefinedRoles.read(uri));
				break;
			case "CREATE":
				grants.addAll(AuthorizationPredefinedRoles.upload(uri));
				break;
			case "EDIT_ITEM":
				grants.addAll(AuthorizationPredefinedRoles.editContent(uri));
				break;
			case "DELETE_ITEM":
				grants.addAll(AuthorizationPredefinedRoles.delete(uri));
				break;
			case "EDIT":
				grants.addAll(AuthorizationPredefinedRoles.edit(uri));
				break;
			case "ADMIN":
				grants.addAll(AuthorizationPredefinedRoles.admin(uri));
				break;
			}
		}
		return grants;
	}

	/**
	 * TRansform a list of Roles into a {@link List} of {@link String}
	 * 
	 * @param roles
	 * @return
	 */
	public static List<String> rolesAsList(ShareRoles... roles) {
		List<String> l = new ArrayList<String>();
		for (ShareRoles r : roles) {
			l.add(r.toString());
		}
		return l;
	}

	/**
	 * Transform a list of {@link Grant} into a list of {@link ShareRoles}
	 * 
	 * @param grants
	 * @param uri
	 * @param profileUri
	 * @param type
	 * @return
	 */
	public static List<String> transformGrantsToRoles(List<Grant> grants,
			String uri) {
		List<String> l = new ArrayList<>();
		if (hasReadGrants(grants, uri)) {
			l.add(ShareRoles.READ.toString());
		}
		if (hasUploadGrants(grants, uri)) {
			l.add(ShareRoles.CREATE.toString());
		}
		if (hasEditItemGrants(grants, uri)) {
			l.add(ShareRoles.EDIT_ITEM.toString());
		}
		if (hasDeleteItemGrants(grants, uri)) {
			l.add(ShareRoles.DELETE_ITEM.toString());
		}
		if (hasEditGrants(grants, uri)) {
			l.add(ShareRoles.EDIT.toString());
		}
		if (hasAdminGrants(grants, uri)) {
			l.add(ShareRoles.ADMIN.toString());
		}
		return l;
	}

	/**
	 * Add to the {@link User} the {@link List} of {@link Grant} and update the
	 * user in the database
	 * 
	 * @param user
	 * @param g
	 * @throws ImejiException
	 */
	private User addGrants(User user, List<Grant> grants, User currentUser)
			throws ImejiException {
		user.getGrants().addAll(
				filterNewGrants(user.getGrants(), grants, currentUser));
		UserController c = new UserController(currentUser);
		// Admin is the only User which can update other users
		c.update(user, Imeji.adminUser);
		return user;

	}

	/**
	 * Add to the {@link UserGroup} the {@link List} of {@link Grant} and update
	 * the user in the database
	 * 
	 * @param group
	 * @param g
	 * @param currentUser
	 * @throws ImejiException
	 */
	public void addGrants(UserGroup group, List<Grant> g, User currentUser)
			throws ImejiException {
		group.getGrants().addAll(
				filterNewGrants(group.getGrants(), g, currentUser));
		UserGroupController c = new UserGroupController();
		c.update(group, Imeji.adminUser);// Admin is the only User which can
											// update other users

	}

	/**
	 * Remove {@link List} of {@link Grant} from the {@link User} {@link Grant}
	 * 
	 * @param user
	 * @param toRemove
	 * @param currentUser
	 */
	public User removeGrants(User user, List<Grant> toRemove, User currentUser) {
		user.setGrants(getNotRemovedGrants(user.getGrants(), toRemove));
		UserController c = new UserController(currentUser);
		try {
			user = c.update(user, currentUser);
			writer.delete(new ArrayList<Object>(toRemove), currentUser);
		} catch (Exception e) {
			logger.error(e);
		}
		return user;
	}

	/**
	 * Remove {@link List} of {@link Grant} from the {@link User} {@link Grant}
	 * 
	 * @param group
	 * @param toRemove
	 * @param currentUser
	 */
	public void removeGrants(UserGroup group, List<Grant> toRemove,
			User currentUser) {
		group.setGrants(getNotRemovedGrants(group.getGrants(), toRemove));
		UserGroupController c = new UserGroupController();
		try {
			c.update(group, currentUser);
			writer.delete(new ArrayList<Object>(toRemove), currentUser);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	/**
	 * Return the {@link List} of {@link Grants} which are not to be removed
	 * 
	 * @param current
	 * @param toRemove
	 * @return
	 */
	private List<Grant> getNotRemovedGrants(Collection<Grant> current,
			List<Grant> toRemove) {
		List<Grant> notRemovedGrants = new ArrayList<>();
		for (Grant g : current) {
			if (!toRemove.contains(g))
				notRemovedGrants.add(g);
		}
		return notRemovedGrants;
	}

	/**
	 * Return the {@link Grant} which are new for the {@link User}
	 * 
	 * @param current
	 * @param toAdd
	 * @return
	 */
	private List<Grant> filterNewGrants(Collection<Grant> current,
			List<Grant> toAdd, User user) {
		List<Grant> newGrants = new ArrayList<>();
		for (Grant g : toAdd) {
			if (!current.contains(g) && !newGrants.contains(g)
					&& isAllowedToAddGrant(user, g)) {
				newGrants.add(g);
			} else if (!current.contains(g) && !newGrants.contains(g)
					&& isAllowedToAddGrant(user, g)) {
				logger.error(user.getPerson().getCompleteName()
						+ " NOT ALLOWED TO share " + g.getGrantFor());
			}
		}
		return newGrants;
	}

	/**
	 * True if the {@link User} is allowed to share the {@link Grant} to another
	 * {@link User}
	 * 
	 * @param user
	 * @param g
	 * @return
	 */
	private boolean isAllowedToAddGrant(User user, Grant g) {
		if (g.getGrantFor().toString().contains("/item/")) {
			// If the grantFor is an Item, the collection is needed to know if
			// the user can administrate it
			List<String> c = ImejiSPARQL
					.exec(SPARQLQueries.selectCollectionIdOfItem(g
							.getGrantFor().toString()), null);
			if (!c.isEmpty())
				return AuthUtil.staticAuth().administrate(user, c.get(0));

		}
		return AuthUtil.staticAuth().administrate(user, g.getGrantFor());
	}

	private static boolean hasReadGrants(List<Grant> userGrants, String uri) {
		List<Grant> grants = AuthorizationPredefinedRoles.read(uri);
		return !grantNotExist(userGrants, grants);
	}

	private static boolean hasUploadGrants(List<Grant> userGrants, String uri) {
		List<Grant> grants = AuthorizationPredefinedRoles.upload(uri);
		return !grantNotExist(userGrants, grants);
	}

	private static boolean hasEditItemGrants(List<Grant> userGrants, String uri) {
		List<Grant> grants = AuthorizationPredefinedRoles.editContent(uri);
		return !grantNotExist(userGrants, grants);
	}

	private static boolean hasDeleteItemGrants(List<Grant> userGrants,
			String uri) {
		List<Grant> grants = AuthorizationPredefinedRoles.delete(uri);
		return !grantNotExist(userGrants, grants);
	}

	private static boolean hasEditGrants(List<Grant> userGrants, String uri) {
		List<Grant> grants = AuthorizationPredefinedRoles.edit(uri);
		return !grantNotExist(userGrants, grants);
	}

	private static boolean hasAdminGrants(List<Grant> userGrants, String uri) {
		List<Grant> grants = AuthorizationPredefinedRoles.admin(uri);
		return !grantNotExist(userGrants, grants);
	}

	/**
	 * True if ???
	 * 
	 * @param userGrants
	 * @param grantList
	 * @return
	 */
	public static boolean grantNotExist(List<Grant> userGrants,
			List<Grant> grantList) {
		boolean b = false;
		for (Grant g : grantList)
			if (!userGrants.contains(g))
				b = true;
		return b;
	}
}
