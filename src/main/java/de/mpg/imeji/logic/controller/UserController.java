/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.mpg.imeji.logic.ImejiBean2RDF;
import de.mpg.imeji.logic.ImejiJena;
import de.mpg.imeji.logic.ImejiRDF2Bean;
import de.mpg.imeji.logic.search.Search;
import de.mpg.imeji.logic.search.Search.SearchType;
import de.mpg.imeji.logic.search.query.SPARQLQueries;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.User;
import de.mpg.j2j.exceptions.NotFoundException;

/**
 * Controller for {@link User}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class UserController extends ImejiController
{
    private static ImejiRDF2Bean imejiRDF2Bean = new ImejiRDF2Bean(ImejiJena.userModel);
    private static ImejiBean2RDF imejiBean2RDF = new ImejiBean2RDF(ImejiJena.userModel);

    /**
     * Default constructor
     */
    public UserController()
    {
        super();
    }

    /**
     * TODO remove this constructor and add to all methods of controller the user as parameter
     * 
     * @deprecated
     * @param user
     */
    @Deprecated
    public UserController(User user)
    {
        super(user);
    }

    /**
     * Create a new {@link User}
     * 
     * @param newUser
     * @throws Exception
     */
    public void create(User newUser) throws Exception
    {
        imejiBean2RDF = new ImejiBean2RDF(ImejiJena.userModel);
        imejiBean2RDF.create(imejiBean2RDF.toList(newUser), user);
    }

    /**
     * Delete a {@link User}
     * 
     * @param user
     * @throws Exception
     */
    public void delete(User user) throws Exception
    {
        imejiBean2RDF = new ImejiBean2RDF(ImejiJena.userModel);
        // remove user grant
        imejiBean2RDF.delete(new ArrayList<Object>(user.getGrants()), this.user);
        // remove user
        imejiBean2RDF.delete(imejiBean2RDF.toList(user), this.user);
    }

    /**
     * Retrieve a {@link User} according to its email
     * 
     * @param email
     * @return
     * @throws Exception
     */
    public User retrieve(String email) throws Exception
    {
        imejiRDF2Bean = new ImejiRDF2Bean(ImejiJena.userModel);
        User loadedUser = (User)imejiRDF2Bean.load(ObjectHelper.getURI(User.class, email).toString(), user, new User());
        return loadedUser;
    }

    /**
     * Retrieve a {@link User} according to its uri (id)
     * 
     * @param email
     * @return
     * @throws Exception
     */
    public User retrieve(URI uri) throws Exception
    {
        imejiRDF2Bean = new ImejiRDF2Bean(ImejiJena.userModel);
        User loadedUser = (User)imejiRDF2Bean.load(uri.toString(), user, new User());
        return loadedUser;
    }

    /**
     * Update a {@link User}
     * 
     * @param user
     * @throws Exception
     */
    public void update(User user) throws Exception
    {
        imejiBean2RDF = new ImejiBean2RDF(ImejiJena.userModel);
        imejiBean2RDF.update(imejiBean2RDF.toList(user), this.user);
    }

    /**
     * Retrieve all {@link User} in imeji<br/>
     * Only allowed for System administrator
     * 
     * @return
     */
    public Collection<User> retrieveAll()
    {
        imejiRDF2Bean = new ImejiRDF2Bean(ImejiJena.userModel);
        Collection<User> users = new ArrayList<User>();
        Search search = new Search(SearchType.ALL, null);
        List<String> uris = search.searchSimpleForQuery(SPARQLQueries.selectUserAll(), null);
        for (String uri : uris)
        {
            try
            {
                users.add((User)imejiRDF2Bean.load(uri, user, new User()));
            }
            catch (NotFoundException e)
            {
                throw new RuntimeException("User " + uri + " not found", e);
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
        return users;
    }
}
