package de.mpg.imeji.logic.validation.impl;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

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
    if (isDelete()) {
      return;
    }

    boolean valid = true;
    String errorMessage = "";
    
    if (StringHelper.hasInvalidTags(collection.getMetadata().getDescription())) {
      valid = false;
      errorMessage+=("error_bad_format_description;");
      //throw new UnprocessableError("error_bad_format_description");
    }

    if (isNullOrEmpty(collection.getMetadata().getTitle().trim())) {
      valid = false;
      errorMessage+= "error_collection_need_title;";
      //throw new UnprocessableError("error_collection_need_title");
    }

    List<Person> pers = new ArrayList<Person>();
    for (Person c : collection.getMetadata().getPersons()) {
      List<Organization> orgs = new ArrayList<Organization>();
      for (Organization o : c.getOrganizations()) {
        if (!isNullOrEmpty(o.getName().trim())) {
          orgs.add(o);
        } else {
          valid=false;
          errorMessage+="error_organization_need_name;";
          //throw new UnprocessableError("error_organization_need_name");
        }
      }

      if (!isNullOrEmpty(c.getFamilyName().trim())) {
        if (orgs.size() > 0) {
          pers.add(c);
        } else {
          valid=false;
          errorMessage+="error_author_need_one_organization;";
          //throw new UnprocessableError("error_author_need_one_organization");
        }
      } else {
        valid=false;
        errorMessage+="error_author_need_one_family_name;";
        //throw new UnprocessableError("error_author_need_one_family_name");
      }
    }

    if (pers.size() == 0 || pers == null || pers.isEmpty()) {
      valid=false;
      errorMessage+="error_collection_need_one_author;";
      //throw new UnprocessableError("error_collection_need_one_author");
    }
    
    if (!valid) {
      throw new UnprocessableError(errorMessage);
    }

  }

  @Override
  public void validate(CollectionImeji t, MetadataProfile p) throws UnprocessableError {
    validate(t);
  }

}
