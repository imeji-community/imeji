package de.mpg.imeji.logic.validation.impl;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

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
public class AlbumValidator extends ObjectValidator implements Validator<Album> {

  public AlbumValidator(Validator.Method method) {
    super(method);
  }

  @Override
  public void validate(Album album) throws UnprocessableError {
    if (isDelete())
      return;
    
    //clean untrusted HTML
    String safeDescription = Jsoup.clean(album.getMetadata().getDescription(), Whitelist.relaxed());
    album.getMetadata().setDescription(safeDescription);   

    if (isNullOrEmpty(album.getMetadata().getTitle().trim())) {
      throw new UnprocessableError("error_album_need_title");
    }
    List<Person> pers = new ArrayList<Person>();

    for (Person c : album.getMetadata().getPersons()) {
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
      throw new UnprocessableError("error_album_need_one_author");
    }

  }

  @Override
  public void validate(Album t, MetadataProfile p) throws UnprocessableError {
    validate(t);
  }

}
