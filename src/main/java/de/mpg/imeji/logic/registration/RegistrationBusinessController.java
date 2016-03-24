package de.mpg.imeji.logic.registration;

import java.net.URI;
import java.util.Calendar;

import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.exceptions.NotFoundException;
import de.mpg.imeji.exceptions.UnprocessableError;
import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.auth.authorization.AuthorizationPredefinedRoles;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.search.Search;
import de.mpg.imeji.logic.search.SearchFactory;
import de.mpg.imeji.logic.search.SearchFactory.SEARCH_IMPLEMENTATIONS;
import de.mpg.imeji.logic.search.SearchResult;
import de.mpg.imeji.logic.search.jenasearch.JenaCustomQueries;
import de.mpg.imeji.logic.vo.User;
import de.mpg.j2j.helper.DateHelper;

/**
 * Business Controller for user registration
 * 
 * @author bastiens
 *
 */
public class RegistrationBusinessController {

  private UserController userController = new UserController(Imeji.adminUser);

  /**
   * Retrieve a {@link User} according to its email
   * 
   * @param registrationToken
   * @return
   * @throws ImejiException
   */
  public User retrieveRegisteredUser(String registrationToken) throws ImejiException {
    Search search = SearchFactory.create(SEARCH_IMPLEMENTATIONS.JENA);
    SearchResult result = search.searchString(
        JenaCustomQueries.selectUserByRegistrationToken(registrationToken), null, null, 0, -1);
    if (result.getNumberOfRecords() == 1) {
      String id = result.getResults().get(0);
      return userController.retrieve(URI.create(id));
    }
    throw new NotFoundException("Invalid registration token!");
  }

  /**
   * Activae a {@link User}
   * 
   * @param registrationToken
   * @throws ImejiException
   * @return
   */
  public User activate(String registrationToken) throws ImejiException {
    try {
      User activateUser = retrieveRegisteredUser(registrationToken);

      if (activateUser.isActive()) {
        throw new UnprocessableError("User is already activated!");
      }

      Calendar now = DateHelper.getCurrentDate();
      if (!(activateUser.getCreated().before(now))) {
        throw new UnprocessableError(
            "Registration date does not match, its bigger then the current date!");
      }

      Calendar validUntil = activateUser.getCreated();
      validUntil.add(Calendar.DAY_OF_MONTH,
          Integer.valueOf(Imeji.CONFIG.getRegistrationTokenExpiry()));

      if ((now.after(validUntil))) {
        throw new UnprocessableError("Activation period expired, user should be deleted!");
      }

      activateUser.setUserStatus(User.UserStatus.ACTIVE);

      activateUser.getGrants()
          .addAll(AuthorizationPredefinedRoles.defaultUser(activateUser.getId().toString()));
      userController.update(activateUser, Imeji.adminUser);
      return activateUser;
    } catch (NotFoundException e) {
      throw new NotFoundException("Invalid registration token!");
    }
  }

}
