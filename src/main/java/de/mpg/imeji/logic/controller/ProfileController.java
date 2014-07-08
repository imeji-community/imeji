/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.ImejiWriter;
import de.mpg.imeji.logic.ImejiReader;
import de.mpg.imeji.logic.ImejiSPARQL;
import de.mpg.imeji.logic.search.SPARQLSearch;
import de.mpg.imeji.logic.search.Search;
import de.mpg.imeji.logic.search.Search.SearchType;
import de.mpg.imeji.logic.search.SearchFactory;
import de.mpg.imeji.logic.search.SearchResult;
import de.mpg.imeji.logic.search.query.SPARQLQueries;
import de.mpg.imeji.logic.search.vo.SearchQuery;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Properties.Status;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.logic.vo.User;
import de.mpg.j2j.exceptions.NotFoundException;
import de.mpg.j2j.helper.DateHelper;

/**
 * Controller for {@link MetadataProfile}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class ProfileController extends ImejiController
{
    private static ImejiReader imejiRDF2Bean = new ImejiReader(Imeji.profileModel);
    private static ImejiWriter imejiBean2RDF = new ImejiWriter(Imeji.profileModel);
    private static Logger logger = Logger.getLogger(ProfileController.class);

    /**
     * Default Constructor
     */
    public ProfileController()
    {
        super();
    }

    /**
     * Creates a new collection. - Add a unique id - Write user properties
     * 
     * @param ic
     * @param user
     */
    public URI create(MetadataProfile mdp, User user) throws Exception
    {
        imejiBean2RDF = new ImejiWriter(Imeji.profileModel);
        writeCreateProperties(mdp, user);
        mdp.setStatus(Status.PENDING);
        imejiBean2RDF.create(imejiBean2RDF.toList(mdp), user);
        return mdp.getId();
    }

    /**
     * Retrieve a {@link User} by its id
     * 
     * @param id
     * @param user
     * @return
     * @throws Exception
     */
    public MetadataProfile retrieve(String id, User user) throws Exception
    {
        imejiRDF2Bean = new ImejiReader(Imeji.profileModel);
        return retrieve(ObjectHelper.getURI(MetadataProfile.class, id), user);
    }

    /**
     * Retrieve a {@link User} by its {@link URI}
     * 
     * @param uri
     * @param user
     * @return
     * @throws NotFoundException
     * @throws Exception
     */
    public MetadataProfile retrieve(URI uri, User user) throws NotFoundException
    {
        imejiRDF2Bean = new ImejiReader(Imeji.profileModel);
        MetadataProfile p;
        try
        {
            p = ((MetadataProfile)imejiRDF2Bean.load(uri.toString(), user, new MetadataProfile()));
        }
        catch (Exception e)
        {
            throw new NotFoundException("Profile (URL: " + uri + " ) not found.");
        }
        Collections.sort((List<Statement>)p.getStatements());
        return p;
    }

    /**
     * Updates a collection -Logged in users: --User is collection owner --OR user is collection editor
     * 
     * @param ic
     * @param user
     * @throws Exception
     */
    public void update(MetadataProfile mdp, User user) throws Exception
    {
        imejiBean2RDF = new ImejiWriter(Imeji.profileModel);
        writeUpdateProperties(mdp, user);
        imejiBean2RDF.update(imejiBean2RDF.toList(mdp), user);
    }

    /**
     * Release a {@link MetadataProfile}
     * 
     * @param mdp
     * @param user
     * @throws Exception
     */
    public void release(MetadataProfile mdp, User user) throws Exception
    {
        mdp.setStatus(Status.RELEASED);
        mdp.setVersionDate(DateHelper.getCurrentDate());
        update(mdp, user);
    }

    /**
     * Delete a {@link MetadataProfile}
     * 
     * @param mdp
     * @param user
     * @throws Exception
     */
    public void delete(MetadataProfile mdp, User user) throws Exception
    {
        imejiBean2RDF = new ImejiWriter(Imeji.profileModel);
        imejiBean2RDF.delete(imejiBean2RDF.toList(mdp), user);
    }

    /**
     * Withdraw a {@link MetadataProfile}
     * 
     * @param mdp
     * @param user
     * @throws Exception
     */
    public void withdraw(MetadataProfile mdp, User user) throws Exception
    {
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
    public SearchResult search(SearchQuery query, User user)
    {
        Search search = SearchFactory.create(SearchType.PROFILE);
        SearchResult result = search.search(query, null, user);
        return result;
    }

    /**
     * Search all profile allowed for the current user. Not sorted.
     * 
     * @return
     * @throws Exception
     */
    public List<MetadataProfile> search(User user) throws Exception
    {
        Search search = SearchFactory.create(SearchType.PROFILE);
        SearchResult result = search.search(new SearchQuery(), null, user);
        List<MetadataProfile> l = new ArrayList<MetadataProfile>();
        for (String uri : result.getResults())
        {
            try
            {
                l.add(retrieve(URI.create(uri), user));
            }
            catch (Exception e)
            {
                logger.error(e);
            }
        }
        return l;
    }

    /**
     * Remove all the {@link Metadata} not having a {@link Statement}. This happens when a {@link Statement} has been
     * removed from a {@link MetadataProfile}.
     */
    public void removeMetadataWithoutStatement(MetadataProfile p)
    {
        ImejiSPARQL.execUpdate(SPARQLQueries.updateRemoveAllMetadataWithoutStatement((p.getId().toString())));
        ImejiSPARQL.execUpdate(SPARQLQueries.updateEmptyMetadata());
    }
}
