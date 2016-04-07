package de.mpg.imeji.logic.validation.impl;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import de.mpg.imeji.exceptions.UnprocessableError;
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
public class AlbumValidator extends ContainerValidator implements Validator<Album> {
  private UnprocessableError exception = new UnprocessableError(new HashSet<String>());


  @Override
  public void validate(Album album, Method method) throws UnprocessableError {
    exception = new UnprocessableError(new HashSet<String>());
    setValidateForMethod(method);

    validateContainerMetadata(album);

    List<Person> pers = new ArrayList<Person>();

    for (Person c : album.getMetadata().getPersons()) {
      List<Organization> orgs = new ArrayList<Organization>();
      for (Organization o : c.getOrganizations()) {
        if (!isNullOrEmpty(o.getName().trim())) {
          orgs.add(o);
        } else {
          exception.getMessages().add("error_organization_need_name");
        }
      }

      if (!isNullOrEmpty(c.getFamilyName().trim())) {
        if (orgs.size() > 0) {
          pers.add(c);
        } else {
          exception.getMessages().add("error_author_need_one_organization");
        }
      } else {
        exception.getMessages().add("error_author_need_one_family_name");
      }
    }

    if (pers.isEmpty()) {
      exception.getMessages().add("error_album_need_one_author");
    }

    if (!exception.getMessages().isEmpty()) {
      throw exception;
    }

  }

  @Override
  public void validate(Album t, MetadataProfile p, Method method) throws UnprocessableError {
    validate(t, method);
  }

  @Override
  protected UnprocessableError getException() {
    return exception;
  }

}
