package de.mpg.imeji.rest.api;

import static de.mpg.imeji.logic.Imeji.adminUser;

import java.net.URI;
import java.util.List;

import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.search.Search;
import de.mpg.imeji.logic.search.SearchFactory;
import de.mpg.imeji.logic.search.jenasearch.JenaCustomQueries;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.rest.to.UserTO;


public class UserService implements API<UserTO> {


  public User read(URI uri) throws ImejiException {
    return adminUser.getId().equals(uri) ? adminUser : new UserController(adminUser).retrieve(uri);
  }

  public User read(String email) throws ImejiException {
    return adminUser.getEmail().equals(email) ? adminUser
        : new UserController(adminUser).retrieve(email);
  }

  public String getCompleteName(URI uri) throws ImejiException {
    Search search = SearchFactory.create();
    List<String> results =
        search.searchString(JenaCustomQueries.selectUserCompleteName(uri), null, null, 0, -1)
            .getResults();
    return results.size() == 1 ? results.get(0) : null;
  }

  @Override
  public UserTO create(UserTO o, User u) throws ImejiException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object read(String id, User u) throws ImejiException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public UserTO update(UserTO userTO, User u) throws ImejiException {
    return null;
  }

  @Override
  public boolean delete(String i, User u) throws ImejiException {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public UserTO release(String i, User u) throws ImejiException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public UserTO withdraw(String i, User u, String discardComment) throws ImejiException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void share(String id, String userId, List<String> roles, User u) throws ImejiException {
    // TODO Auto-generated method stub

  }

  @Override
  public void unshare(String id, String userId, List<String> roles, User u) throws ImejiException {
    // TODO Auto-generated method stub

  }

  @Override
  public List<String> search(String q, User u) throws ImejiException {
    // TODO Auto-generated method stub
    return null;
  }

}
