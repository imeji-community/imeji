package de.mpg.imeji.logic.validation.impl;

import de.mpg.imeji.exceptions.UnprocessableError;
import de.mpg.imeji.logic.util.StringHelper;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Organization;
import de.mpg.imeji.logic.vo.Person;

/**
 * Validator for person
 * 
 * @author bastiens
 *
 */
public class PersonValidator extends ObjectValidator implements Validator<Person> {

  @Override
  public void validate(Person p, de.mpg.imeji.logic.validation.impl.Validator.Method method)
      throws UnprocessableError {
    UnprocessableError e = new UnprocessableError();
    if (StringHelper.isNullOrEmptyTrim(p.getFamilyName())) {
      e = new UnprocessableError("error_author_need_one_family_name", e);
    }
    if (!hasAtLeastOneOrganisation(p)) {
      e = new UnprocessableError("error_author_need_one_organization", e);
    }
    for (Organization org : p.getOrganizations()) {
      if (!isValidOrganization(org)) {
        e = new UnprocessableError("error_organization_need_name", e);
        break;
      }
    }
    if (e.hasMessages()) {
      throw e;
    }
  }

  @Override
  public void validate(Person t, MetadataProfile p,
      de.mpg.imeji.logic.validation.impl.Validator.Method method) throws UnprocessableError {
    validate(t, method);
  }


  /**
   * True if the person has at least one valid org
   * 
   * @param p
   * @return
   */
  private boolean hasAtLeastOneOrganisation(Person p) {
    for (Organization org : p.getOrganizations()) {
      if (isValidOrganization(org)) {
        return true;
      }
    }
    return false;
  }

  /**
   * True if the organization has a name
   * 
   * @param o
   * @return
   */
  private boolean isValidOrganization(Organization o) {
    return !StringHelper.isNullOrEmptyTrim(o.getName());
  }

}
