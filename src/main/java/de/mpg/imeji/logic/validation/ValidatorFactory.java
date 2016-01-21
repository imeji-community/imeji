package de.mpg.imeji.logic.validation;

import de.mpg.imeji.logic.validation.impl.AlbumValidator;
import de.mpg.imeji.logic.validation.impl.CollectionValidator;
import de.mpg.imeji.logic.validation.impl.ItemValidator;
import de.mpg.imeji.logic.validation.impl.MetadataValidator;
import de.mpg.imeji.logic.validation.impl.ProfileValidator;
import de.mpg.imeji.logic.validation.impl.PseudoValidator;
import de.mpg.imeji.logic.validation.impl.SpaceValidator;
import de.mpg.imeji.logic.validation.impl.UserValidator;
import de.mpg.imeji.logic.vo.Album;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Space;
import de.mpg.imeji.logic.vo.User;

/**
 * Factory for {@link Validator}
 * 
 * @author saquet
 *
 */
public class ValidatorFactory {
  private static final PseudoValidator PSEUDO_VALIDATOR = new PseudoValidator();
  private static final ItemValidator ITEM_VALIDATOR = new ItemValidator();
  private static final MetadataValidator METADATA_VALIDATOR = new MetadataValidator();
  private static final CollectionValidator COLLECTION_VALIDATOR = new CollectionValidator();
  private static final AlbumValidator ALBUM_VALIDATOR = new AlbumValidator();
  private static final ProfileValidator PROFILE_VALIDATOR = new ProfileValidator();
  private static final UserValidator USER_VALIDATOR = new UserValidator();
  private static final SpaceValidator SPACE_VALIDATOR = new SpaceValidator();

  private ValidatorFactory() {

  }

  /**
   * Return a new {@link Validator} according to the object class
   * 
   * @param <T>
   * 
   * @param t
   * @return
   */
  public static Validator<?> newValidator(Object obj, Validator.Method method) {
    Validator<?> validator = PSEUDO_VALIDATOR;
    // For now, do not do anything with Delete, just a possiblity
    if (Validator.Method.DELETE.equals(method)) {
      return validator;
    }
    if (obj instanceof Item) {
      validator = ITEM_VALIDATOR;
    } else if (obj instanceof Metadata) {
      validator = METADATA_VALIDATOR;
    } else if (obj instanceof CollectionImeji) {
      validator = COLLECTION_VALIDATOR;
    } else if (obj instanceof Album) {
      validator = ALBUM_VALIDATOR;
    } else if (obj instanceof MetadataProfile) {
      validator = PROFILE_VALIDATOR;
    } else if (obj instanceof User) {
      validator = USER_VALIDATOR;
    } else if (obj instanceof Space) {
      validator = SPACE_VALIDATOR;
    }
    return validator;
  }
}
