package de.mpg.imeji.logic.validation.impl;

import static de.mpg.imeji.logic.util.StringHelper.isNullOrEmptyTrim;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import de.mpg.imeji.exceptions.UnprocessableError;
import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.ImejiSPARQL;
import de.mpg.imeji.logic.search.jenasearch.JenaCustomQueries;
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

  @Override
  public void validate(Space space, Method m) throws UnprocessableError {
    if (isDelete())
      return;

    boolean valid = true;
    String errorMessage = "";

    if (isNullOrEmptyTrim(space.getTitle())) {
      valid = false;
      errorMessage += "error_space_need_title;";
    }

    try {
      new URI(space.getSlug());
      // above creation of URI in order to check if it is a syntactically
      // valid slug
    } catch (URISyntaxException e) {
      valid = false;
      errorMessage += "error_space_invalid_slug;";
    }

    if (isSpaceByLabel(space.getSlug(), space.getId())) {
      valid = false;
      errorMessage += "error_there_is_another_space_with_same_slug;";
    }

    if (isNullOrEmptyTrim(space.getSlug())) {
      valid = false;
      errorMessage += "error_space_needs_slug";
    }

    if (!valid) {
      throw new UnprocessableError(errorMessage);
    }

  }

  private boolean isSpaceByLabel(String spaceId, URI spaceUriId) {
    if (isNullOrEmptyTrim(spaceId))
      return false;

    List<String> spaceUrisFound =
        ImejiSPARQL.exec(JenaCustomQueries.getSpaceByLabel(spaceId), Imeji.spaceModel);
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
  public void validate(Space t, MetadataProfile p, Method m) throws UnprocessableError {
    validate(t, m);
  }

}
