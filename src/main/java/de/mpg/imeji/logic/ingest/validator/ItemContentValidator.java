package de.mpg.imeji.logic.ingest.validator;

import java.util.List;

import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.controller.resource.ProfileController;
import de.mpg.imeji.logic.controller.util.MetadataProfileUtil;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.logic.vo.predefinedMetadata.Metadata;

/**
 * @author hnguyen
 */
public class ItemContentValidator {
  /**
   * Validate the content of an {@link Item} according to its profile. Throw an exception if: <br/>
   * * the {@link Statement} of the {@link Metadata} is not found in the {@link MetadataProfile} of
   * the current {@link CollectionImeji} <br/>
   * * The value of the {@link Metadata} is not following the literals constraints (if define for
   * this {@link Statement}
   *
   * @throws ImejiException
   *
   * @param item
   */
  public static void validate(Item item) throws ImejiException {
    MetadataProfile profile =
        new ProfileController().retrieve(item.getMetadataSet().getProfile(), Imeji.adminUser);
    for (Metadata md : item.getMetadataSet().getMetadata()) {
      Statement st = MetadataProfileUtil.getStatement(md.getStatement(), profile);
      if (st == null) {
        throw new RuntimeException("Error Ingest: Statement " + md.getStatement()
            + " is not allowed in collection  " + item.getCollection());
      } else {
        if (!st.getLiteralConstraints().isEmpty()) {
          boolean constraintsFound = false;
          for (String s : st.getLiteralConstraints()) {
            constraintsFound = md.asFulltext().contains(s) || constraintsFound;
          }
          if (!constraintsFound) {
            throw new RuntimeException("Error Ingest: Found not allowed value in  metadata "
                + md.getId() + " Check restricted values in profile");
          }
        }
      }
    }
  }

  /**
   * @param items
   * @throws Exception
   */
  public static void validate(List<Item> items) throws Exception {
    for (Item item : items) {
      ItemContentValidator.validate(item);
    }
  }
}
