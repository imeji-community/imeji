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
  /**
   * Return a new {@link Validator} according to the object class
   * 
   * @param <T>
   * 
   * @param t
   * @return
   */
  public static Validator<?> newValidator(Object obj, Validator.Method method) {
    // For now, do not do anything with Delete, just a possiblity
    if (Validator.Method.DELETE.equals(method))
      return new PseudoValidator(method);

    if (obj instanceof Item) {
      return new ItemValidator(method);
    } else if (obj instanceof Metadata) {
      return new MetadataValidator(method);
    } else if (obj instanceof CollectionImeji) {
      return new CollectionValidator(method);
    } else if (obj instanceof Album) {
      return new AlbumValidator(method);
    } else if (obj instanceof MetadataProfile) {
      return new ProfileValidator(method);
    } else if (obj instanceof User) {
      return new UserValidator(method);
    } else if (obj instanceof Space) {
      return new SpaceValidator(method);
    }
    return new PseudoValidator(method);
  }
}
