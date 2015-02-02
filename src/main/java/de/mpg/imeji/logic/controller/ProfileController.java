/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.exceptions.NotFoundException;
import de.mpg.imeji.exceptions.UnprocessableError;
import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.ImejiSPARQL;
import de.mpg.imeji.logic.auth.authorization.AuthorizationPredefinedRoles;
import de.mpg.imeji.logic.reader.ReaderFacade;
import de.mpg.imeji.logic.search.Search;
import de.mpg.imeji.logic.search.Search.SearchType;
import de.mpg.imeji.logic.search.SearchFactory;
import de.mpg.imeji.logic.search.SearchResult;
import de.mpg.imeji.logic.search.query.SPARQLQueries;
import de.mpg.imeji.logic.search.vo.SearchQuery;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Properties.Status;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.logic.writer.WriterFacade;
import de.mpg.j2j.helper.DateHelper;

/**
 * Controller for {@link MetadataProfile}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class ProfileController extends ImejiController {
	private static final ReaderFacade reader = new ReaderFacade(
			Imeji.profileModel);
	private static final WriterFacade writer = new WriterFacade(
			Imeji.profileModel);
	private static Logger logger = Logger.getLogger(ProfileController.class);

	/**
	 * Default Constructor
	 */
	public ProfileController() {
		super();
	}

	/**
	 * Create a new Profile.
	 * 
	 * @param p
	 * @param user
	 * @return
	 * @throws ImejiException
	 */
	public MetadataProfile create(MetadataProfile p, User user)
			throws ImejiException {
		writeCreateProperties(p, user);
		p.setStatus(Status.PENDING);
		writer.create(WriterFacade.toList(p), user);
		GrantController gc = new GrantController();
		gc.addGrants(user,
				AuthorizationPredefinedRoles.admin(null, p.getId().toString()),
				user);
		return p;
	}

	/**
	 * Retrieve a {@link User} by its id
	 * 
	 * @param id
	 * @param user
	 * @return
	 * @throws ImejiException
	 */
	public MetadataProfile retrieve(String id, User user) throws ImejiException {
		return retrieve(ObjectHelper.getURI(MetadataProfile.class, id), user);
	}

	/**
	 * Retrieve a {@link User} by its {@link URI}
	 * 
	 * @param uri
	 * @param user
	 * @return
	 * @throws NotFoundException
	 * @throws ImejiException
	 */
	public MetadataProfile retrieve(URI uri, User user)
			throws ImejiException {
			MetadataProfile p = null;
			p = ((MetadataProfile) reader.read(uri.toString(), user,
					new MetadataProfile()));
		Collections.sort((List<Statement>) p.getStatements());
		return p;
	}

	/**
	 * Retrieve a {@link User} by its {@link URI}
	 *
	 * @param uri
	 * @param user
	 * @return
	 * @throws NotFoundException
	 * @throws ImejiException
	 */
	public MetadataProfile retrieveByCollectionId(URI collectionId, User user)
			throws ImejiException {

		CollectionController cc = new CollectionController();
		CollectionImeji c;
		try {
			c = cc.retrieve(collectionId, user);
		} catch (ImejiException e) {
			//e.printStackTrace();
			throw new UnprocessableError("Invalid collection: " + e.getLocalizedMessage());
		}

		return retrieve(c.getProfile(), user);
	}

	/**
	 * Updates a collection -Logged in users: --User is collection owner --OR
	 * user is collection editor
	 * 
	 * @param ic
	 * @param user
	 * @throws ImejiException
	 */
	public void update(MetadataProfile mdp, User user) throws ImejiException {
		writeUpdateProperties(mdp, user);
		writer.update(WriterFacade.toList(mdp), user);
	}

	/**
	 * Release a {@link MetadataProfile}
	 * 
	 * @param mdp
	 * @param user
	 * @throws ImejiException
	 */
	public void release(MetadataProfile mdp, User user) throws ImejiException {
		mdp.setStatus(Status.RELEASED);
		mdp.setVersionDate(DateHelper.getCurrentDate());
		update(mdp, user);
	}

	/**
	 * Delete a {@link MetadataProfile}
	 * 
	 * @param mdp
	 * @param user
	 * @throws ImejiException
	 */
	public void delete(MetadataProfile mdp, User user) throws ImejiException {
		writer.delete(WriterFacade.toList(mdp), user);
	}

	/**
	 * Withdraw a {@link MetadataProfile}
	 * 
	 * @param mdp
	 * @param user
	 * @throws ImejiException
	 */
	public void withdraw(MetadataProfile mdp, User user) throws ImejiException {
		mdp.setStatus(Status.WITHDRAWN);
		mdp.setVersionDate(DateHelper.getCurrentDate());
		update(mdp, user);
	}

	/**
	 * Search for a profile
	 * 
	 * @param query
	 * @param user
	 * @return
	 */
	public SearchResult search(SearchQuery query, User user) {
		Search search = SearchFactory.create(SearchType.PROFILE);
		SearchResult result = search.search(query, null, user);
		return result;
	}

	/**
	 * Search all profile allowed for the current user. Not sorted.
	 * 
	 * @return
	 * @throws ImejiException
	 */
	public List<MetadataProfile> search(User user) throws ImejiException {
		Search search = SearchFactory.create(SearchType.PROFILE);
		SearchResult result = search.search(new SearchQuery(), null, user);
		List<MetadataProfile> l = new ArrayList<MetadataProfile>();
		for (String uri : result.getResults()) {
			try {
				l.add(retrieve(URI.create(uri), user));
			} catch (Exception e) {
				logger.error(e);
			}
		}
		return l;
	}

	/**
	 * Remove all the {@link Metadata} not having a {@link Statement}. This
	 * happens when a {@link Statement} has been removed from a
	 * {@link MetadataProfile}.
	 */
	public void removeMetadataWithoutStatement(MetadataProfile p) {
		ImejiSPARQL
				.execUpdate(SPARQLQueries
						.updateRemoveAllMetadataWithoutStatement((p.getId()
								.toString())));
		ImejiSPARQL.execUpdate(SPARQLQueries.updateEmptyMetadata());
	}
}
