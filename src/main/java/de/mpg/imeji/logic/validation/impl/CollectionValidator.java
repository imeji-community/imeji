package de.mpg.imeji.logic.validation.impl;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import de.mpg.imeji.exceptions.UnprocessableError;
import de.mpg.imeji.logic.util.StringHelper;
import de.mpg.imeji.logic.validation.Validator;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Organization;
import de.mpg.imeji.logic.vo.Person;

/**
 * {@link Validator} for {@link CollectionImeji}
 * 
 * @author saquet
 *
 */
public class CollectionValidator extends ObjectValidator implements Validator<CollectionImeji> {

  private final UnprocessableError exception = new UnprocessableError("");
  private static final Pattern DOI_VALIDATION_PATTERN = Pattern.compile("10\\.\\d+\\/\\S+");

  /**
   * Create a validator for a {@link Collection}
   * 
   * @param method
   */
  public CollectionValidator(Validator.Method method) {
    super(method);
  }

  @Override
  public void validate(CollectionImeji collection) throws UnprocessableError {
    exception.getMessages().clear();
    if (isDelete()) {
      return;
    }
    if (StringHelper.hasInvalidTags(collection.getMetadata().getDescription())) {
      exception.getMessages().add("error_bad_format_description");
    }
    if (isNullOrEmpty(collection.getMetadata().getTitle().trim())) {
      exception.getMessages().add("error_collection_need_title");
    }
    validateCollectionPersons(collection);
    validateDOI(collection.getDoi());
    if (!exception.getMessages().isEmpty()) {
      throw exception;
    }
  }

  /**
   * Validate the Persons of a {@link CollectionImeji}
   * 
   * @param c
   */
  private void validateCollectionPersons(CollectionImeji c) {
    List<Person> validPersons = new ArrayList<Person>();
    for (Person p : c.getMetadata().getPersons()) {
      if (validatePerson(p)) {
        validPersons.add(p);
      }
    }
    if (validPersons.isEmpty()) {
      exception.getMessages().add("error_collection_need_one_author");
    }
  }

  /**
   * Validate a Person
   * 
   * @param p
   * @return
   */
  private boolean validatePerson(Person p) {
    p.setOrganizations(validateOrgsNameAndReturnValidOrgs(p.getOrganizations()));
    if (!isNullOrEmpty(p.getFamilyName().trim())) {
      if (!p.getOrganizations().isEmpty()) {
        return true;
      } else {
        exception.getMessages().add("error_author_need_one_organization");
      }
    } else {
      exception.getMessages().add("error_author_need_one_family_name");
    }
    return false;
  }

  /**
   * If at least 1 organization doesn't have a name, add an exception
   * 
   * @param organizations
   * @return the valid organizations
   */
  private Collection<Organization> validateOrgsNameAndReturnValidOrgs(
      Collection<Organization> organizations) {
    List<Organization> orgs = new ArrayList<Organization>();
    for (Organization o : organizations) {
      if (!isNullOrEmpty(o.getName().trim())) {
        orgs.add(o);
      } else {
        exception.getMessages().add("error_organization_need_name");
      }
    }
    return orgs;
  }

  /**
   * Valid a DOI according to predefined pattern. If not valid, add a message to the exception
   * 
   * @param doi
   */
  private void validateDOI(String doi) {
    if (!isNullOrEmpty(doi) && !DOI_VALIDATION_PATTERN.matcher(doi).find()) {
      exception.getMessages().add("error_doi_format");
    }

  }

  @Override
  public void validate(CollectionImeji t, MetadataProfile p) throws UnprocessableError {
    validate(t);
  }

}
