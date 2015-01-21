/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.auth.authorization.AuthorizationPredefinedRoles;
import de.mpg.imeji.logic.reader.ReaderFacade;
import de.mpg.imeji.logic.search.SPARQLSearch;
import de.mpg.imeji.logic.search.Search;
import de.mpg.imeji.logic.search.Search.SearchType;
import de.mpg.imeji.logic.search.SearchFactory;
import de.mpg.imeji.logic.search.SearchResult;
import de.mpg.imeji.logic.search.query.SPARQLQueries;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Organization;
import de.mpg.imeji.logic.vo.Person;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.logic.vo.UserGroup;
import de.mpg.imeji.logic.writer.WriterFacade;
import de.mpg.j2j.exceptions.AlreadyExistsException;
import de.mpg.j2j.exceptions.NotFoundException;

/**
 * Controller for {@link User}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class UserController {
	private static final ReaderFacade reader = new ReaderFacade(Imeji.userModel);
	private static final WriterFacade writer = new WriterFacade(Imeji.userModel);
	private User user;

	/**
	 * User type (restricted: can not create collection)
	 * 
	 * @author saquet
	 * 
	 */
	public enum USER_TYPE {
		DEFAULT, ADMIN, RESTRICTED, COPY;
	}

	/**
	 * Constructor
	 * 
	 * @param user
	 */
	public UserController(User user) {
		this.user = user;
	}

	/**
	 * Create a new user in the database with predefined roles (ADMIN, DEFAULT
	 * or RESTRICTED)
	 * 
	 * @param newUser
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public User create(User u, USER_TYPE type) throws Exception {
		try {
			retrieve(u.getEmail());
			throw new AlreadyExistsException("Email" + u.getEmail()
					+ "already used by another user");
		} catch (NotFoundException e) {
			// fine, user can be created
		}
		switch (type) {
		case ADMIN:
			u.setGrants(AuthorizationPredefinedRoles.imejiAdministrator(u
					.getId().toString()));
			break;
		case RESTRICTED:
			u.setGrants(AuthorizationPredefinedRoles.restrictedUser(u.getId()
					.toString()));
			break;
		case DEFAULT:
			u.setGrants(AuthorizationPredefinedRoles.defaultUser(u.getId()
					.toString()));
		case COPY:
			// Don't change the grants of the user
			break;
		}
		u.setName(u.getPerson().getGivenName() + " "
				+ u.getPerson().getFamilyName());
		writer.create(WriterFacade.toList(u), user);
		return u;
	}

	/**
	 * Delete a {@link User}
	 * 
	 * @param user
	 * @throws Exception
	 */
	public void delete(User user) throws Exception {
		// remove user grant
		writer.delete(new ArrayList<Object>(user.getGrants()), this.user);
		// remove user
		writer.delete(WriterFacade.toList(user), this.user);
	}

	/**
	 * Retrieve a {@link User} according to its email
	 * 
	 * @param email
	 * @return
	 * @throws Exception
	 */
	public User retrieve(String email) throws Exception {
		Search search = SearchFactory.create();
		SearchResult result = search.searchSimpleForQuery(SPARQLQueries
				.selectUserByEmail(email));
		if (result.getNumberOfRecords() == 1) {
			String id = result.getResults().get(0);
			User u = (User) reader.read(id, user, new User());
			UserGroupController ugc = new UserGroupController();
			u.setGroups((List<UserGroup>) ugc.searchByUser(u, user));
			return u;
		}
		throw new NotFoundException("User with email " + email + " not found");
	}

	/**
	 * Retrieve a {@link User} according to its uri (id)
	 * 
	 * @param email
	 * @return
	 * @throws Exception
	 */
	public User retrieve(URI uri) throws Exception {
		User u = (User) reader.read(uri.toString(), user, new User());
		UserGroupController ugc = new UserGroupController();
		u.setGroups((List<UserGroup>) ugc.searchByUser(u, user));
		return u;
	}

	/**
	 * Update a {@link User}
	 * 
	 * @param updatedUser
	 *            : The user who is updated in the database
	 * @param currentUSer
	 *            : The user who does the update
	 * @throws Exception
	 */
	public void update(User updatedUser, User currentUser) throws Exception {
		try {
			retrieve(updatedUser.getEmail());
			throw new AlreadyExistsException("Email" + updatedUser.getEmail()
					+ "already used by another user");
		} catch (NotFoundException e) {
			// fine, user can be updated
		}
		updatedUser.setName(updatedUser.getPerson().getGivenName() + " "
				+ updatedUser.getPerson().getFamilyName());
		writer.update(WriterFacade.toList(updatedUser), currentUser);
	}

	/**
	 * Retrieve all {@link User} in imeji<br/>
	 * Only allowed for System administrator
	 * 
	 * @return
	 */
	public Collection<User> searchUserByName(String name) {
		Search search = SearchFactory.create();
		return loadUsers(search.searchSimpleForQuery(
				SPARQLQueries.selectUserAll(name)).getResults());
	}

	/**
	 * Search for all users having the grant for an object
	 * 
	 * @param grantFor
	 * @return
	 */
	public Collection<User> searchByGrantFor(String grantFor) {
		Search search = SearchFactory.create();
		return loadUsers(search.searchSimpleForQuery(
				SPARQLQueries.selectUserWithGrantFor(grantFor)).getResults());
	}

	/**
	 * Search for all {@link Person} by their names. The search looks within the
	 * {@link User} and the {@link Collection} what {@link Person} are already
	 * existing.
	 * 
	 * @param name
	 * @return
	 */
	public Collection<Person> searchPersonByName(String name) {

		return searchPersonByNameInUsers(name);
		// don't search (for now) for all persons, since it would get messy
		// (many duplicates)
		// l.addAll(searchPersonByNameInCollections(name));
		// Map<String, Person> map = new HashMap<>();
		// for (Person p : l) {
		// map.put(p.getIdentifier(), p);
		// }
		// return map.values();
	}

	/**
	 * Load a {@link User} by its uri
	 * 
	 * @param id
	 * @return
	 */
	public Person retrievePersonById(String id) {
		List<String> l = new ArrayList<String>();
		l.add(id);
		Collection<Person> c = new ArrayList<Person>();
		try {
			c = loadPersons(l, Imeji.userModel);
		} catch (Exception e) {
			c.addAll(loadPersons(l, Imeji.collectionModel));
		}
		return c.iterator().next();
	}

	/**
	 * Load an {@link Organization} by its uri
	 * 
	 * @param id
	 * @return
	 */
	public Organization retrieveOrganizationById(String id) {
		List<String> l = new ArrayList<String>();
		l.add(id);
		Collection<Organization> c = new ArrayList<Organization>();
		try {
			c = loadOrganizations(l, Imeji.userModel);
		} catch (Exception e) {
			c.addAll(loadOrganizations(l, Imeji.collectionModel));
		}
		return c.iterator().next();
	}

/**
	 * Search for all {@link Organization} in imeji, i.e. t The search looks within the
	 * {@link User} and the {@link Collection} what {@link Organization are already
	 * existing.
	 * @param name
	 * @return
	 */
	public Collection<Organization> searchOrganizationByName(String name) {
		Collection<Organization> l = searchOrganizationByNameInUsers(name);
		Map<String, Organization> map = new HashMap<>();
		for (Organization o : l) {
			map.put(o.getIdentifier(), o);
		}
		return map.values();
	}

	/**
	 * Search all {@link Person} which are defined in a {@link User}
	 * 
	 * @param name
	 * @return
	 */
	private Collection<Person> searchPersonByNameInUsers(String name) {
		Search search = SearchFactory.create(SearchType.USER);
		return loadPersons(
				search.searchSimpleForQuery(
						SPARQLQueries.selectPersonByName(name)).getResults(),
				Imeji.userModel);
	}

	/**
	 * Search all {@link Person} which are defined as person of a
	 * {@link CollectionImeji}
	 * 
	 * @param name
	 * @return
	 */
	private Collection<Person> searchPersonByNameInCollections(String name) {
		Search search = SearchFactory.create(SearchType.COLLECTION);
		return loadPersons(
				search.searchSimpleForQuery(
						SPARQLQueries.selectPersonByName(name)).getResults(),
				Imeji.collectionModel);
	}

	/**
	 * Search all {@link Organization} which are defined in a {@link User}
	 * 
	 * @param name
	 * @return
	 */
	private Collection<Organization> searchOrganizationByNameInUsers(String name) {
		Search search = SearchFactory.create(SearchType.USER);
		return loadOrganizations(
				search.searchSimpleForQuery(
						SPARQLQueries.selectOrganizationByName(name))
						.getResults(), Imeji.userModel);
	}

	/**
	 * Search all {@link Organization} which are defined as person of a
	 * {@link CollectionImeji}
	 * 
	 * @param name
	 * @return
	 */
	private Collection<Organization> searchOrganizationByNameInCollections(
			String name) {
		Search search = SearchFactory.create(SearchType.COLLECTION);
		return loadOrganizations(
				search.searchSimpleForQuery(
						SPARQLQueries.selectOrganizationByName(name))
						.getResults(), Imeji.collectionModel);
	}

	/**
	 * Load all {@link User}
	 * 
	 * @param uris
	 * @return
	 */
	public Collection<User> loadUsers(List<String> uris) {
		Collection<User> users = new ArrayList<User>();
		for (String uri : uris) {
			try {

				users.add((User) reader.read(uri, user, new User()));
			} catch (NotFoundException e) {
				throw new RuntimeException("User " + uri + " not found", e);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return users;
	}

	/**
	 * Load Organizations
	 * 
	 * @param uris
	 * @return
	 */
	public Collection<Organization> loadOrganizations(List<String> uris,
			String model) {
		Collection<Organization> orgs = new ArrayList<Organization>();
		for (String uri : uris) {
			try {
				ReaderFacade reader = new ReaderFacade(model);
				orgs.add((Organization) reader.read(uri, user,
						new Organization()));
			} catch (NotFoundException e) {
				throw new RuntimeException(
						"Organization " + uri + " not found", e);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return orgs;
	}

	/**
	 * Load Organizations
	 * 
	 * @param uris
	 * @return
	 */
	private Collection<Person> loadPersons(List<String> uris, String model) {
		Collection<Person> p = new ArrayList<Person>();
		for (String uri : uris) {
			try {
				ReaderFacade reader = new ReaderFacade(model);
				p.add((Person) reader.read(uri, user, new Person()));
			} catch (NotFoundException e) {
				throw new RuntimeException("Person " + uri + " not found", e);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return p;
	}

	/**
	 * This method checks if a admin user exists for this instance
	 * 
	 * @return true of no admin user exists, false otherwise
	 */
	public static boolean adminUserExist() {
		boolean exist = false;
		Search search = SearchFactory.create();
		List<String> uris = search.searchSimpleForQuery(
				SPARQLQueries.selectUserSysAdmin()).getResults();
		if (uris != null && uris.size() > 0) {
			exist = true;
		}
		return exist;
	}

	/**
	 * Retrieve all admin users
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<User> retrieveAllAdmins() {
		Search search = SearchFactory.create();
		List<String> uris = search.searchSimpleForQuery(
				SPARQLQueries.selectUserSysAdmin()).getResults();
		List<User> admins = new ArrayList<User>();
		for (String uri : uris) {
			try {
				admins.add(retrieve(URI.create(uri)));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return admins;
	}
}
