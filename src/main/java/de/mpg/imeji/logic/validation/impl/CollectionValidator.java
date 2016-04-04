package de.mpg.imeji.logic.validation.impl;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

import de.mpg.imeji.exceptions.UnprocessableError;
import de.mpg.imeji.logic.resource.vo.CollectionImeji;
import de.mpg.imeji.logic.resource.vo.MetadataProfile;
import de.mpg.imeji.logic.resource.vo.Organization;
import de.mpg.imeji.logic.resource.vo.Person;
import de.mpg.imeji.logic.validation.Validator;

/**
 * {@link Validator} for {@link CollectionImeji}
 * 
 * @author saquet
 *
 */
public class CollectionValidator extends ContainerValidator implements Validator<CollectionImeji> {

  private UnprocessableError exception = new UnprocessableError(new HashSet<String>());
  private static final Pattern DOI_VALIDATION_PATTERN = Pattern.compile("10\\.\\d+\\/\\S+");

  @Override
  protected UnprocessableError getException() {
    return exception;
  }

  @Override
  public void validate(CollectionImeji collection, Method m) throws UnprocessableError {
    exception = new UnprocessableError(new HashSet<String>());
    setValidateForMethod(m);
    validateContainerMetadata(collection);
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
    validateOrgsName(p.getOrganizations());
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
  private void validateOrgsName(Collection<Organization> organizations) {
    for (Organization o : organizations) {
      if (isNullOrEmpty(o.getName().trim())) {
        exception.getMessages().add("error_organization_need_name");
      }
    }
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
  public void validate(CollectionImeji t, MetadataProfile p, Method m) throws UnprocessableError {
    validate(t, m);
  }

}
