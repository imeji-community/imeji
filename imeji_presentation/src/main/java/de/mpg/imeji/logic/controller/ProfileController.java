/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.mpg.imeji.logic.ImejiBean2RDF;
import de.mpg.imeji.logic.ImejiJena;
import de.mpg.imeji.logic.ImejiRDF2Bean;
import de.mpg.imeji.logic.search.Search;
import de.mpg.imeji.logic.search.Search.SearchType;
import de.mpg.imeji.logic.search.SearchResult;
import de.mpg.imeji.logic.search.vo.SearchQuery;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.Grant;
import de.mpg.imeji.logic.vo.Grant.GrantType;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Properties.Status;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.logic.vo.User;
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
    private static ImejiRDF2Bean imejiRDF2Bean = new ImejiRDF2Bean(ImejiJena.profileModel);
    private static ImejiBean2RDF imejiBean2RDF = new ImejiBean2RDF(ImejiJena.profileModel);

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
        imejiBean2RDF = new ImejiBean2RDF(ImejiJena.profileModel);
        writeCreateProperties(mdp, user);
        mdp.setStatus(Status.PENDING);
        imejiBean2RDF.create(imejiBean2RDF.toList(mdp), user);
        addCreatorGrant(mdp, user);
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
        imejiRDF2Bean = new ImejiRDF2Bean(ImejiJena.profileModel);
        return retrieve(ObjectHelper.getURI(MetadataProfile.class, id), user);
    }

    /**
     * Retrieve a {@link User} by its {@link URI}
     * 
     * @param uri
     * @param user
     * @return
     * @throws Exception
     */
    public MetadataProfile retrieve(URI uri, User user) throws Exception
    {
        imejiRDF2Bean = new ImejiRDF2Bean(ImejiJena.profileModel);
        MetadataProfile p = ((MetadataProfile)imejiRDF2Bean.load(uri.toString(), user, new MetadataProfile()));
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
        imejiBean2RDF = new ImejiBean2RDF(ImejiJena.profileModel);
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
        imejiBean2RDF = new ImejiBean2RDF(ImejiJena.profileModel);
        imejiBean2RDF.delete(imejiBean2RDF.toList(mdp), user);
        GrantController gc = new GrantController(user);
        gc.removeAllGrantsFor(user, mdp.getId());
    }

    public void withdraw(MetadataProfile mdp, User user) throws Exception
    {
        mdp.setStatus(Status.WITHDRAWN);
        mdp.setVersionDate(DateHelper.getCurrentDate());
        update(mdp, user);
    }

    /**
     * Add Creator {@link Grant} to a {@link User}
     * 
     * @param p
     * @param user
     * @return
     * @throws Exception
     */
    private User addCreatorGrant(MetadataProfile p, User user) throws Exception
    {
        GrantController gc = new GrantController(user);
        Grant grant = new Grant(GrantType.PROFILE_ADMIN, p.getId());
        gc.addGrant(user, grant);
        UserController uc = new UserController(user);
        return uc.retrieve(user.getEmail());
    }

    /**
     * Search all profile allowed for the current user. Not sorted.
     * 
     * @return
     * @throws Exception
     */
    public List<MetadataProfile> search(User user) throws Exception
    {
        Search search = new Search(SearchType.PROFILE, null);
        SearchResult result = search.search(new SearchQuery(), null, user);
        List<MetadataProfile> l = new ArrayList<MetadataProfile>();
        for (String uri : result.getResults())
        {
            l.add(retrieve(URI.create(uri), user));
        }
        return l;
    }
}
