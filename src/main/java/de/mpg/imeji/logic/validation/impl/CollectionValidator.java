package de.mpg.imeji.logic.validation.impl;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.util.ArrayList;
import java.util.List;

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

  public CollectionValidator(Validator.Method method) {
    super(method);
  }

  @Override
  public void validate(CollectionImeji collection) throws UnprocessableError {
    if (isDelete())
      return;

    if (StringHelper.hasInvalidTags(collection.getMetadata().getDescription())) {
      throw new UnprocessableError("error_bad_format_description");
    }

    if (isNullOrEmpty(collection.getMetadata().getTitle().trim())) {
      throw new UnprocessableError("error_collection_need_title");
    }
    List<Person> pers = new ArrayList<Person>();
    for (Person c : collection.getMetadata().getPersons()) {
      List<Organization> orgs = new ArrayList<Organization>();
      for (Organization o : c.getOrganizations()) {
        if (!isNullOrEmpty(o.getName().trim())) {
          orgs.add(o);
        } else {
          throw new UnprocessableError("error_organization_need_name");
        }
      }

      if (!isNullOrEmpty(c.getFamilyName().trim())) {
        if (orgs.size() > 0) {
          pers.add(c);
        } else {
          throw new UnprocessableError("error_author_need_one_organization");
        }
      } else {
        throw new UnprocessableError("error_author_need_one_family_name");
      }
    }

    if (pers.size() == 0 || pers == null || pers.isEmpty()) {
      throw new UnprocessableError("error_collection_need_one_author");
    }

  }

  @Override
  public void validate(CollectionImeji t, MetadataProfile p) throws UnprocessableError {
    validate(t);
  }

}
