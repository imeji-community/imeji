package de.mpg.imeji.logic.validation.impl;

import java.util.HashSet;

import de.mpg.imeji.exceptions.UnprocessableError;
import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.validation.Validator;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.User;

/**
 * {@link Validator} for {@link CollectionImeji}
 * 
 * @author saquet
 *
 */
public class UserValidator extends ObjectValidator implements Validator<User> {
  private UnprocessableError exception = new UnprocessableError(new HashSet<String>());

  @Override
  public void validate(User user, Method m) throws UnprocessableError {
    exception = new UnprocessableError(new HashSet<String>());
    setValidateForMethod(m);
    if (isDelete()) {
      return;
    }

    if (user.getEmail() == null || "".equals(user.getEmail().trim())) {
      exception.getMessages().add("error_user_email_unfilled");
    } else if (!isValidEmail(user.getEmail())) {
      exception.getMessages().add("error_user_email_not_valid");
    }

    if (emailAlreadyUsed(user)) {
      exception.getMessages().add("error_user_already_exists");
    }

    if (user.getPerson() == null || "".equals(user.getPerson().getFamilyName())
        || user.getPerson().getFamilyName() == null) {
      exception.getMessages().add("error_user_name_unfilled");
    }

    if (user.getPerson() != null && "".equals(user.getPerson().getOrganizationString())) {
      exception.getMessages().add("error_user_organization_unfilled");
    }

    if (!exception.getMessages().isEmpty()) {
      throw exception;
    }
  }


  /**
   * True if the {@link User} exists
   * 
   * @return
   * @throws Exception
   */
  private boolean emailAlreadyUsed(User user) {
    UserController uc = new UserController(Imeji.adminUser);
    return uc.existsUserWitheMail(user.getEmail(), user.getId().toString(),
        (Method.CREATE.equals(getValidateForMethod()) ? true : false));
  }

  /**
   * Is true if the Email is valid
   * 
   * @return
   */
  public static boolean isValidEmail(String email) {
    String regexEmailMatch = "([^.@]+)(\\.[^.@]+)*@([^.@]+\\.)+([^.@]+)";
    return email.matches(regexEmailMatch);
  }

  @Override
  public void validate(User t, MetadataProfile p, Method m) throws UnprocessableError {
    validate(t, m);
  }

}
