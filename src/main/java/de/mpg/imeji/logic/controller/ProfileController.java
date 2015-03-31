/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import de.mpg.imeji.exceptions.BadRequestException;
import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.exceptions.NotFoundException;
import de.mpg.imeji.exceptions.UnprocessableError;
import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.ImejiSPARQL;
import de.mpg.imeji.logic.auth.authorization.AuthorizationPredefinedRoles;
import de.mpg.imeji.logic.jobs.CleanMetadataJob;
import de.mpg.imeji.logic.jobs.CleanMetadataProfileJob;
import de.mpg.imeji.logic.reader.ReaderFacade;
import de.mpg.imeji.logic.search.SPARQLSearch;
import de.mpg.imeji.logic.search.Search;
import de.mpg.imeji.logic.search.Search.SearchType;
import de.mpg.imeji.logic.search.SearchFactory;
import de.mpg.imeji.logic.search.SearchResult;
import de.mpg.imeji.logic.search.query.SPARQLQueries;
import de.mpg.imeji.logic.search.query.URLQueryTransformer;
import de.mpg.imeji.logic.search.vo.SearchIndex;
import de.mpg.imeji.logic.search.vo.SearchQuery;
import de.mpg.imeji.logic.search.vo.SortCriterion;
import de.mpg.imeji.logic.search.vo.SortCriterion.SortOrder;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.*;
import de.mpg.imeji.logic.vo.Properties.Status;
import de.mpg.imeji.logic.writer.WriterFacade;
import de.mpg.imeji.presentation.util.PropertyReader;
import de.mpg.imeji.rest.process.RestProcessUtils;
import de.mpg.imeji.rest.to.MetadataProfileTO;
import de.mpg.j2j.helper.DateHelper;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;
import static de.mpg.imeji.logic.util.ResourceHelper.getStringFromPath;
import static de.mpg.imeji.logic.util.StringHelper.isNullOrEmptyTrim;
import static de.mpg.imeji.rest.process.ReverseTransferObjectFactory.TRANSFER_MODE;
import static de.mpg.imeji.rest.process.ReverseTransferObjectFactory.transferMetadataProfile;

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
	public static final String DEFAULT_METADATA_PROFILE_PATH_PROPERTY = "imeji.default.metadata.profile.path";
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
		if (!user.isAdmin()) {
			GrantController gc = new GrantController();
			gc.addGrants(user, AuthorizationPredefinedRoles.admin(null, p
					.getId().toString()), user);
		}
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
	public MetadataProfile retrieve(URI uri, User user) throws ImejiException {
		MetadataProfile p = null;
		p = ((MetadataProfile) reader.read(uri.toString(), user,
				new MetadataProfile()));
		Collections.sort((List<Statement>) p.getStatements());
		return p;
	}

	/**
	 * Retrieve a {@link User} by its {@link URI}
	 *
	 * @param collectionId
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
			// e.printStackTrace();
			throw new UnprocessableError("Invalid collection: "
					+ e.getLocalizedMessage());
		}

		return retrieve(c.getProfile(), user);
	}

	/**
	 * Updates a collection -Logged in users: --User is collection owner --OR
	 * user is collection editor
	 * 
	 * @param mdp
	 * @param user
	 * @throws ImejiException
	 */
	public void update(MetadataProfile mdp, User user) throws ImejiException {
		writeUpdateProperties(mdp, user);
		writer.update(WriterFacade.toList(mdp), user);
		Imeji.executor.submit(new CleanMetadataJob(mdp));
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
	 * Release a {@link MetadataProfile}
	 *
	 * @param id
	 * @param user
	 * @throws ImejiException
	 */
	public void release(String id, User user) throws ImejiException {
		release(retrieve(id, user), user);
	}

	/**
	 * Delete a {@link MetadataProfile} from within a collection
	 * 
	 * @param mdp
	 * @param user
	 * @throws ImejiException
	 */
	public void delete(MetadataProfile mdp, User user, String collectionId)
			throws ImejiException {
		if ((isNullOrEmpty(collectionId) && isReferencedByAnyResources(mdp
				.getId().toString()))
				|| !isNullOrEmpty(collectionId)
				&& isReferencedByOtherResources(mdp.getId().toString(),
						collectionId)) {
			throw new UnprocessableError(
					"error_profile_is_referenced_cannot_be_deleted");
		} else if (mdp.getDefault()) {
			throw new UnprocessableError(
					"error_profile_is_default_cannot_be_deleted");
		}
		writer.delete(WriterFacade.toList(mdp), user);
		Imeji.executor.submit(new CleanMetadataProfileJob(true));
		Imeji.executor.submit(new CleanMetadataJob(mdp));
	}

	/**
	 * Delete a {@link MetadataProfile} , checks if there are any references in
	 * other collections before deletion
	 * 
	 * @param mdp
	 * @param user
	 * @throws ImejiException
	 */
	public void delete(MetadataProfile mdp, User user) throws ImejiException {
		this.delete(mdp, user, "");
	}

	/**
	 * Withdraw a {@link MetadataProfile}
	 * 
	 * @param mdp
	 * @param user
	 * @throws ImejiException
	 */
	public void withdraw(MetadataProfile mdp, User user) throws ImejiException {

		if (mdp.getDefault()) {
			throw new UnprocessableError(
					"error_profile_is_default_cannot_be_withdrawn");
		}
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
		// Automatically sort by profile title
		SortCriterion sortCri = new SortCriterion(
				SPARQLSearch.getIndex(SearchIndex.names.prof.name()),
				SortOrder.ASCENDING);
		SearchResult result = search.search(query, sortCri, user);
		return result;
	}

	/**
	 * Search all profile allowed for the current user. Sorted by profile name, query parameter possible.
	 * 
	 * @return
	 * @throws ImejiException
	 */
	public List<MetadataProfile> search(User user, String q) throws ImejiException {
		Search search = SearchFactory.create(SearchType.PROFILE);

		// Automatically sort by profile title
		SortCriterion sortCri = new SortCriterion(
				SPARQLSearch.getIndex(SearchIndex.names.prof.name()),
				SortOrder.ASCENDING);
		SearchResult result;
		List<MetadataProfile> l = new ArrayList<MetadataProfile>();
		try {
			result = search.search(!isNullOrEmptyTrim(q) ? URLQueryTransformer.parseStringQuery(q) : null, sortCri, user);
			for (String uri : result.getResults()) {
				l.add(retrieve(URI.create(uri), user));
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return l;
	}

	
	/**
	 * Search all profile allowed for the current user, Sorted by profile name, no query parameter
	 * 
	 * @return
	 * @throws ImejiException
	 */
	public List<MetadataProfile> search(User user) throws ImejiException {
		return search(user, "");
	}
	
	/**
	 * Find default profile.
	 *
	 * @return default metadata profile
	 * @throws ImejiException
	 */

	public MetadataProfile retrieveDefaultProfile() throws ImejiException {
		Search search = SearchFactory.create();
		List<String> uris = search.searchSimpleForQuery(
				SPARQLQueries.selectDefaultMetadataProfile()).getResults();
		if (uris.size() == 1) {
			return retrieve(URI.create(uris.get(0)), Imeji.adminUser);
		} else if (uris.size() > 1) {
			throw new ImejiException("Data inconsistency: " + uris.size()
					+ " + default metadata profile have been found.");
		} else {
			logger.info("Cannot find default metadata profile...");
		}
		return null;
	}

	/**
	 * Create default profile.
	 *
	 * @return default metadata profile
	 * @throws ImejiException
	 */

	public MetadataProfile initDefaultMetadataProfile() throws ImejiException {

		MetadataProfile mdpVO = retrieveDefaultProfile();

		if (mdpVO == null) {
			String path = null;
			String profileJSON = null;
			MetadataProfileTO mdpTO = null;
			try {
				path = PropertyReader
						.getProperty(DEFAULT_METADATA_PROFILE_PATH_PROPERTY);
				if (isNullOrEmpty(path)) {
					logger.info("There is no default metadata profile defined! This is not an error, Imeji will still work. Default metadata profile is a convenience for quick start!"
							+ "Check more about it at the IMEJI Documentation.");
					return null;
				}
				profileJSON = getStringFromPath(path);
				mdpTO = (MetadataProfileTO) RestProcessUtils.buildTOFromJSON(
						profileJSON, MetadataProfileTO.class);
			} catch (UnrecognizedPropertyException e) {
				throw new ImejiException("Error reading property "
						+ DEFAULT_METADATA_PROFILE_PATH_PROPERTY + ": " + e);
			} catch (JsonProcessingException e) {
				throw new ImejiException("Cannot process json: " + e);
			} catch (URISyntaxException | IOException e) {
				throw new ImejiException("Wrong path: " + e);
			}
			mdpVO = new MetadataProfile();
			transferMetadataProfile(mdpTO, mdpVO, TRANSFER_MODE.CREATE);
			mdpVO.setDefault(true);
			mdpVO = create(mdpVO, Imeji.adminUser);
			release(mdpVO, Imeji.adminUser);

		}
		return mdpVO;

	}

	/**
	 * Remove all the {@link Metadata} not having a {@link Statement}. This
	 * happens when a {@link Statement} has been removed from a
	 * {@link MetadataProfile}.
	 */
	public void removeMetadataWithoutStatement(MetadataProfile p) {
		String id = p != null ? p.getId().toString() : null;
		ImejiSPARQL.execUpdate(SPARQLQueries
				.updateRemoveAllMetadataWithoutStatement(id));
		ImejiSPARQL.execUpdate(SPARQLQueries.updateEmptyMetadata());
	}

	public void validateProfile(MetadataProfile profile, User u)
			throws ImejiException {
		// Copied from Collection Bean in presentation
		if (isNullOrEmpty(profile.getTitle())) {
			throw new BadRequestException("error_profile_need_title");
		}
	}

	public boolean isReferencedByOtherResources(String profileUri,
			String resourceUri) {
		Search s = new SPARQLSearch(SearchType.ALL, null);
		List<String> r = s.searchSimpleForQuery(
				SPARQLQueries.hasOtherMetadataProfileReferences(profileUri,
						resourceUri)).getResults();
		if (r.size() > 0) {
			return true;
		}
		return false;
	}

	public boolean isReferencedByAnyResources(String profileUri) {
		Search s = new SPARQLSearch(SearchType.ALL, null);
		List<String> r = s.searchSimpleForQuery(
				SPARQLQueries.hasMetadataProfileReferences(profileUri))
				.getResults();
		if (r.size() > 0) {
			return true;
		}
		return false;
	}
}
