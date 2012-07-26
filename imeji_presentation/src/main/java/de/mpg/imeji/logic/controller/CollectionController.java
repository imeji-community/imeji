/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.controller;

import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.ImejiBean2RDF;
import de.mpg.imeji.logic.ImejiJena;
import de.mpg.imeji.logic.ImejiRDF2Bean;
import de.mpg.imeji.logic.search.ImejiSPARQL;
import de.mpg.imeji.logic.search.Search;
import de.mpg.imeji.logic.search.SearchResult;
import de.mpg.imeji.logic.search.vo.SearchQuery;
import de.mpg.imeji.logic.search.vo.SortCriterion;
import de.mpg.imeji.logic.security.Security;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Grant;
import de.mpg.imeji.logic.vo.Grant.GrantType;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.Properties.Status;
import de.mpg.imeji.logic.vo.User;
import de.mpg.j2j.exceptions.NotFoundException;
import de.mpg.j2j.helper.DateHelper;
import de.mpg.j2j.helper.J2JHelper;

public class CollectionController extends ImejiController
{
    private static ImejiRDF2Bean imejiRDF2Bean = null;
    private static ImejiBean2RDF imejiBean2RDF = null;
    private static Logger logger = Logger.getLogger(CollectionController.class);

    public CollectionController(User user)
    {
        super(user);
        imejiBean2RDF = new ImejiBean2RDF(ImejiJena.collectionModel);
        imejiRDF2Bean = new ImejiRDF2Bean(ImejiJena.collectionModel);
    }

    /**
     * Creates a new collection. - Add a unique id - Write user properties
     * 
     * @param ic
     * @param user
     */
    public URI create(CollectionImeji ic, URI profile) throws Exception
    {
        ProfileController pc = new ProfileController(user);
        pc.retrieve(profile); // If doesn't exists, throw not found exception
        writeCreateProperties(ic.getProperties(), user);
        ic.getProperties().setStatus(Status.PENDING);
        ic.setId(ObjectHelper.getURI(CollectionImeji.class, Integer.toString(getUniqueId())));
        ic.setProfile(profile);
        imejiBean2RDF = new ImejiBean2RDF(ImejiJena.collectionModel);
        imejiBean2RDF.create(imejiBean2RDF.toList(ic), user);
        user = addCreatorGrant(ic, user);
        return ic.getId();
    }

    private User addCreatorGrant(CollectionImeji c, User user) throws Exception
    {
        GrantController gc = new GrantController(user);
        Grant grant = new Grant(GrantType.CONTAINER_ADMIN, c.getId());
        gc.addGrant(user, grant);
        UserController uc = new UserController(user);
        return uc.retrieve(user.getEmail());
    }

    /**
     * Updates a collection
     * 
     * @param ic
     * @param user
     */
    public void update(CollectionImeji ic) throws Exception
    {
        writeUpdateProperties(ic.getProperties(), user);
        imejiBean2RDF = new ImejiBean2RDF(ImejiJena.collectionModel);
        imejiBean2RDF.update(imejiBean2RDF.toList(ic), user);
    }

    public void release(CollectionImeji ic) throws Exception
    {
        if (hasImageLocked(ic.getImages(), user))
        {
            throw new RuntimeException("Collection has at least one image locked by another user.");
        }
        else
        {
            ic.getProperties().setStatus(Status.RELEASED);
            ic.getProperties().setVersionDate(DateHelper.getCurrentDate());
            ItemController itemController = new ItemController(user);
            for (URI uri : ic.getImages())
            {
                try
                {
                    itemController.release(itemController.retrieve(uri));
                }
                catch (NotFoundException e)
                {
                    logger.error("Release image error: " + uri + " could not be found");
                }
            }
            update(ic);
            ProfileController pc = new ProfileController(user);
            pc.retrieve(ic.getProfile());
            pc.release(pc.retrieve(ic.getProfile()));
        }
    }

    public void delete(CollectionImeji collection, User user) throws Exception
    {
        if (hasImageLocked(collection.getImages(), user))
        {
            throw new RuntimeException("Collection has at least one image locked by another user.");
        }
        else
        {
            ItemController itemController = new ItemController(user);
            for (URI uri : collection.getImages())
            {
                try
                {
                    itemController.delete(itemController.retrieve(uri), user);
                }
                catch (NotFoundException e)
                {
                    logger.error("Delete image error: " + uri + " could not be found");
                }
            }
            ProfileController pc = new ProfileController(user);
            try
            {
                pc.delete(pc.retrieve(collection.getProfile()), user);
            }
            catch (Exception e)
            {
                logger.warn("Profile " + collection.getProfile() + " could not be deleted!", e);
            }
            imejiBean2RDF = new ImejiBean2RDF(ImejiJena.collectionModel);
            imejiBean2RDF.delete(imejiBean2RDF.toList(collection), user);
            GrantController gc = new GrantController(user);
            gc.removeAllGrantsFor(user, collection.getId());
        }
    }

    public void withdraw(CollectionImeji ic) throws Exception
    {
        if (hasImageLocked(ic.getImages(), user))
        {
            throw new RuntimeException("Collection has at least one image locked by another user.");
        }
        else
        {
            // Withdraw images
            ItemController itemController = new ItemController(user);
            for (URI uri : ic.getImages())
            {
                try
                {
                    Item im = itemController.retrieve(uri);
                    if (!Status.WITHDRAWN.equals(im.getProperties().getStatus()))
                    {
                        im.getProperties().setDiscardComment(ic.getProperties().getDiscardComment());
                        itemController.withdraw(im);
                    }
                }
                catch (NotFoundException e)
                {
                    logger.error("Withdraw image error: " + uri + " could not be found");
                }
            }
            // Withdraw collection
            ic.getProperties().setStatus(Status.WITHDRAWN);
            ic.getProperties().setVersionDate(DateHelper.getCurrentDate());
            this.update(ic);
            // Withdraw profile
            ProfileController pc = new ProfileController(user);
            pc.retrieve(ic.getProfile());
            pc.withdraw(pc.retrieve(ic.getProfile()), user);
            // Remove Grants (which are not useful anymore)
            // GrantController gc = new GrantController(user);
            // gc.removeAllGrantsFor(user, ic.getId());
        }
    }

    public CollectionImeji retrieve(URI uri) throws Exception
    {
        imejiRDF2Bean = new ImejiRDF2Bean(ImejiJena.collectionModel);
        return (CollectionImeji)imejiRDF2Bean.load(uri.toString(), user, new CollectionImeji());
    }

    public CollectionImeji retrieveLazy(URI uri) throws Exception
    {
        imejiRDF2Bean = new ImejiRDF2Bean(ImejiJena.collectionModel);
        return (CollectionImeji)imejiRDF2Bean.loadLazy(uri.toString(), user, new CollectionImeji());
    }

    public int countAllCollections()
    {
        return ImejiSPARQL.execCount("SELECT count(DISTINCT ?s) WHERE { ?s a <http://imeji.org/terms/collection>}");
    }

    public int getCollectionSize(String uri)
    {
        String query = "SELECT ?s count(DISTINCT ?s) WHERE { ?s a <http://imeji.org/terms/item> .<" + uri
                + "> <http://imeji.org/terms/item> ?s }";
        return ImejiSPARQL.execCount(query);
    }

    public SearchResult getCollectionItems(String uri)
    {
        String query = "SELECT ?s count(DISTINCT ?s) WHERE { ?s a <http://imeji.org/terms/item> .<" + uri
                + "> <http://imeji.org/terms/item> ?s }";
        return new SearchResult(ImejiSPARQL.exec(query));
    }

    /**
     * @deprecated
     * @return
     */
    public Collection<CollectionImeji> retrieveAll()
    {
        Security security = new Security();
        // rdf2Bean = new RDF2Bean(ImejiJena.collectionModel);
        // if (security.isSysAdmin(user))
        // {
        // return rdf2Bean.load(CollectionImeji.class);
        // }
        return new ArrayList<CollectionImeji>();
    }

    /**
     * Search for collections - Logged-out user: --Collection must be released -Logged-in users --Collection is released
     * --OR Collection is pending AND user is owner --OR Collection is withdrawn AND user is owner --OR Collection is
     * pending AND user has grant "Container Editor" for it.
     * 
     * @param user
     * @param scList
     * @return
     */
    // public SearchResult search(List<SearchCriterion> scList, SortCriterion sortCri, int limit, int offset)
    // {
    // Search search = new Search("http://imeji.org/terms/collection", null);
    // return search.search(scList, sortCri, simplifyUser());
    // }
    public SearchResult search(SearchQuery searchQuery, SortCriterion sortCri, int limit, int offset)
    {
        Search search = new Search("http://imeji.org/terms/collection", null);
        return search.search(searchQuery, sortCri, simplifyUser());
    }

    /**
     * Increase performance by restricting grants to the only grants needed
     * 
     * @param user
     * @return
     */
    public User simplifyUser()
    {
        if (user == null)
        {
            return null;
        }
        User simplifiedUser = new User();
        for (Grant g : user.getGrants())
        {
            if (GrantType.SYSADMIN.equals(g.asGrantType()))
            {
                simplifiedUser.getGrants().add(g);
            }
            else if (g.getGrantFor() != null && g.getGrantFor().toString().contains("collection"))
            {
                simplifiedUser.getGrants().add(g);
            }
        }
        return simplifiedUser;
    }

    public Collection<CollectionImeji> loadLazy(List<String> uris, int limit, int offset)
    {
        LinkedList<CollectionImeji> cols = new LinkedList<CollectionImeji>();
        ImejiRDF2Bean reader = new ImejiRDF2Bean(ImejiJena.collectionModel);
        int counter = 0;
        for (String s : uris)
        {
            if (offset <= counter && (counter < (limit + offset) || limit == -1))
            {
                try
                {
                    cols.add((CollectionImeji)J2JHelper.setId(new CollectionImeji(), URI.create(s)));
                }
                catch (Exception e)
                {
                    logger.error("Error loading image " + s, e);
                }
            }
            counter++;
        }
        try
        {
            reader.loadLazy(J2JHelper.cast2ObjectList(cols), user);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return cols;
    }

    @Override
    protected String getSpecificQuery() throws Exception
    {
        return " . ?s <http://imeji.org/terms/properties> ?props . ?props <http://imeji.org/terms/createdBy> ?createdBy . ?props <http://imeji.org/terms/status> ?status";
    }

    @Override
    protected String getSpecificFilter() throws Exception
    {
        // Add filters for user management
        String filter = "(";
        Security security = new Security();
        if (user == null)
        {
            filter += "?status = <<http://imeji.org/terms/status#RELEASED>";
        }
        else if (security.isSysAdmin(user))
        {
            filter += "?status = <<http://imeji.org/terms/status#RELEASED> || ?status = <<http://imeji.org/terms/status#PENDING>";
        }
        else
        {
            String userUri = "http://xmlns.com/foaf/0.1/Person/" + URLEncoder.encode(user.getEmail(), "UTF-8");
            filter += "?status = <<http://imeji.org/terms/status#RELEASED> || ?createdBy=<" + userUri + ">";
            for (Grant grant : user.getGrants())
            {
                switch (grant.asGrantType())
                {
                    case CONTAINER_ADMIN: // Add specifics here
                        break;
                    default:
                        filter += " || ?s=<" + grant.getGrantFor().toString() + ">";
                        break;
                }
            }
        }
        filter += ")";
        return filter;
    }
}
