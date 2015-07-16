package de.mpg.imeji.logic.validation.impl;

import static de.mpg.imeji.logic.util.StringHelper.isNullOrEmptyTrim;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import de.mpg.imeji.exceptions.UnprocessableError;
import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.ImejiSPARQL;
import de.mpg.imeji.logic.search.query.SPARQLQueries;
import de.mpg.imeji.logic.validation.Validator;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Space;

/**
 * {@link Validator} for a {@link Space}
 * 
 * @author saquet
 *
 */
public class SpaceValidator extends ObjectValidator implements Validator<Space> {

  public SpaceValidator(Validator.Method method) {
    super(method);
  }

  @Override
  public void validate(Space space) throws UnprocessableError {
    if (isDelete())
      return;

    if (isNullOrEmptyTrim(space.getTitle())) {
      throw new UnprocessableError("error_space_need_title");
    }

    if (isSpaceByLabel(space.getSlug(), space.getId())) {
      throw new UnprocessableError("error_there_is_another_space_with_same_slug");
    }

    if (isNullOrEmptyTrim(space.getSlug())) {
      throw new UnprocessableError("error_space_needs_slug");
    }
    try {
      new URI(space.getSlug());
      // above creation of URI in order to check if it is a syntactically
      // valid slug
    } catch (URISyntaxException e) {
      throw new UnprocessableError("error_space_invalid_slug");
    }

  }

  private boolean isSpaceByLabel(String spaceId, URI spaceUriId) {
    if (isNullOrEmptyTrim(spaceId))
      return false;

    List<String> spaceUrisFound =
        ImejiSPARQL.exec(SPARQLQueries.getSpaceByLabel(spaceId), Imeji.spaceModel);
    if (spaceUrisFound.size() == 0) {
      return false;
    } else {
      for (String spaceUri : spaceUrisFound) {
        if (!spaceUri.equals(spaceUriId.toString())) {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public void validate(Space t, MetadataProfile p) throws UnprocessableError {
    validate(t);
  }

}
