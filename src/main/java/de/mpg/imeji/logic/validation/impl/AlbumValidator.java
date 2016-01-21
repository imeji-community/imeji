package de.mpg.imeji.logic.validation.impl;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.util.ArrayList;
import java.util.List;

import de.mpg.imeji.exceptions.UnprocessableError;
import de.mpg.imeji.logic.util.StringHelper;
import de.mpg.imeji.logic.validation.Validator;
import de.mpg.imeji.logic.vo.Album;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Organization;
import de.mpg.imeji.logic.vo.Person;

/**
 * {@link Validator} for {@link Album}
 * 
 * @author saquet
 *
 */
public class AlbumValidator extends ObjectValidator implements Validator<Album> {


  @Override
  public void validate(Album album, Method method) throws UnprocessableError {
    setValidateForMethod(method);
    if (isDelete()) {
      return;
    }

    boolean valid = true;
    String errorMessage = "";

    if (StringHelper.hasInvalidTags(album.getMetadata().getDescription())) {
      valid = false;
      errorMessage += "error_bad_format_description;";
      // throw new UnprocessableError("error_bad_format_description");
    }

    if (isNullOrEmpty(album.getMetadata().getTitle().trim())) {
      valid = false;
      errorMessage += "error_album_need_title;";
    }

    List<Person> pers = new ArrayList<Person>();

    for (Person c : album.getMetadata().getPersons()) {
      List<Organization> orgs = new ArrayList<Organization>();
      for (Organization o : c.getOrganizations()) {
        if (!isNullOrEmpty(o.getName().trim())) {
          orgs.add(o);
        } else {
          valid = false;
          errorMessage += "error_organization_need_name;";
        }
      }

      if (!isNullOrEmpty(c.getFamilyName().trim())) {
        if (orgs.size() > 0) {
          pers.add(c);
        } else {
          valid = false;
          errorMessage += "error_author_need_one_organization;";
        }
      } else {
        valid = false;
        errorMessage += "error_author_need_one_family_name;";
      }
    }

    if (pers.size() == 0 || pers == null || pers.isEmpty()) {
      valid = false;
      errorMessage += "error_album_need_one_author;";
    }

    if (!valid) {
      throw new UnprocessableError(errorMessage);
    }

  }

  @Override
  public void validate(Album t, MetadataProfile p, Method method) throws UnprocessableError {
    validate(t, method);
  }

}
