/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.mpg.imeji.logic.ImejiBean2RDF;
import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.ImejiRDF2Bean;
import de.mpg.imeji.logic.search.Search;
import de.mpg.imeji.logic.search.Search.SearchType;
import de.mpg.imeji.logic.search.query.SPARQLQueries;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.Grant;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.logic.vo.UserGroup;
import de.mpg.j2j.exceptions.NotFoundException;

/**
 * Controller for {@link User}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class UserController
{
    private static ImejiRDF2Bean imejiRDF2Bean = new ImejiRDF2Bean(Imeji.userModel);
    private static ImejiBean2RDF imejiBean2RDF = new ImejiBean2RDF(Imeji.userModel);
    private User user;

    /**
     * Constructor
     * 
     * @param user
     */
    public UserController(User user)
    {
        this.user = user;
    }

    /**
     * Create a new {@link User}
     * 
     * @param newUser
     * @throws Exception
     */
    public void create(User newUser) throws Exception
    {
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
        User u = (User)imejiRDF2Bean.load(ObjectHelper.getURI(User.class, email).toString(), user, new User());
        UserGroupController ugc = new UserGroupController();
        u.setGroups((List<UserGroup>)ugc.searchByUser(u, user));
        return u;
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
        User u = (User)imejiRDF2Bean.load(uri.toString(), user, new User());
        UserGroupController ugc = new UserGroupController();
        u.setGroups((List<UserGroup>)ugc.searchByUser(u, user));
        return u;
    }

    /**
     * Update a {@link User}
     * 
     * @param user
     * @throws Exception
     */
    public void update(User user) throws Exception
    {
        imejiBean2RDF.update(imejiBean2RDF.toList(user), this.user);
    }

    /**
     * Update a {@link User}
     * 
     * @param updatedUser : The user who is updated in the database
     * @param currentUSer : The user who does the update
     * @throws Exception
     */
    public void update(User updatedUser, User currentUser) throws Exception
    {
        imejiBean2RDF.update(imejiBean2RDF.toList(updatedUser), currentUser);
    }

    /**
     * Retrieve all {@link User} in imeji<br/>
     * Only allowed for System administrator
     * 
     * @return
     */
    public Collection<User> retrieveAll(String name)
    {
        Search search = new Search(SearchType.ALL, null);
        return loadUsers(search.searchSimpleForQuery(SPARQLQueries.selectUserAll(name), null));
    }
    

    public Collection<User> retrieveUserWithGrantFor(String grantFor)
    {
        Search search = new Search(SearchType.ALL, null);
        return loadUsers(search.searchSimpleForQuery(SPARQLQueries.selectUserWithGrantFor(grantFor), null));
    }

    /**
     * Load all {@link User}
     * 
     * @param uris
     * @return
     */
    public Collection<User> loadUsers(List<String> uris)
    {
        Collection<User> users = new ArrayList<User>();
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

    /**
     * This method checks if a admin user exists for this instance
     * 
     * @return true of no admin user exists, false otherwise
     */
    public static boolean adminUserExist()
    {
        boolean exist = false;
        Search search = new Search(SearchType.ALL, null);
        List<String> uris = search.searchSimpleForQuery(SPARQLQueries.selectUserSysAdmin(), null);
        if (uris != null && uris.size() > 0)
        {
            exist = true;
        }
        return exist;
    }
}
