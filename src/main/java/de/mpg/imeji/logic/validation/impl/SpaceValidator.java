package de.mpg.imeji.logic.validation.impl;

import static de.mpg.imeji.logic.util.StringHelper.isNullOrEmptyTrim;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;

import de.mpg.imeji.exceptions.UnprocessableError;
import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.resource.vo.MetadataProfile;
import de.mpg.imeji.logic.resource.vo.Space;
import de.mpg.imeji.logic.search.jenasearch.ImejiSPARQL;
import de.mpg.imeji.logic.search.jenasearch.JenaCustomQueries;
import de.mpg.imeji.logic.validation.Validator;

/**
 * {@link Validator} for a {@link Space}
 * 
 * @author saquet
 *
 */
public class SpaceValidator extends ObjectValidator implements Validator<Space> {
  private UnprocessableError exception = new UnprocessableError(new HashSet<String>());

  @Override
  public void validate(Space space, Method m) throws UnprocessableError {
    exception = new UnprocessableError(new HashSet<String>());
    setValidateForMethod(m);
    if (isDelete()) {
      return;
    }

    if (isNullOrEmptyTrim(space.getTitle())) {
      exception.getMessages().add("error_space_need_title");
    }

    try {
      // creation of URI in order to check if it is a syntactically valid slug
      new URI(space.getSlug());
    } catch (URISyntaxException e) {
      exception.getMessages().add("error_space_invalid_slug");
    }

    if (isSpaceByLabel(space.getSlug(), space.getId())) {
      exception.getMessages().add("error_there_is_another_space_with_same_slug");
    }

    if (isNullOrEmptyTrim(space.getSlug())) {
      exception.getMessages().add("error_space_needs_slug");
    }

    if (!exception.getMessages().isEmpty()) {
      throw exception;
    }
  }

  private boolean isSpaceByLabel(String spaceId, URI spaceUriId) {
    if (isNullOrEmptyTrim(spaceId)) {
      return false;
    }

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
