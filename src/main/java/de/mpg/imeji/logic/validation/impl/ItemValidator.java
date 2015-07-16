package de.mpg.imeji.logic.validation.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.mpg.imeji.exceptions.UnprocessableError;
import de.mpg.imeji.logic.validation.Validator;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.presentation.util.ProfileHelper;

/**
 * {@link Validator} for an {@link Item}. Only working when {@link MetadataProfile} is passed
 * 
 * @author saquet
 *
 */
public class ItemValidator extends ObjectValidator implements Validator<Item> {

  public ItemValidator(Validator.Method method) {
    super(method);
  }

  @Override
  @Deprecated
  public void validate(Item t) throws UnprocessableError {

    throw new UnsupportedOperationException();

  }

  @Override
  public void validate(Item item, MetadataProfile p) throws UnprocessableError {

    if (isDelete())
      return;

    MetadataValidator mdValidator = new MetadataValidator(getValidateForMethod());
    // List of the statement which are not defined as Multiple
    List<String> nonMultipleStatement = new ArrayList<String>();
    Map<Metadata, String> validationMap = new HashMap<Metadata, String>();
    Map<Metadata, String> validationMapMultipleStatements = new HashMap<Metadata, String>();
    for (Metadata md : item.getMetadataSet().getMetadata()) {
      Statement s = ProfileHelper.getStatement(md.getStatement(), p);
      try {
        mdValidator.validate(md, p);
      } catch (UnprocessableError e) {
        validationMap.put(md, e.getMessage());
      }


      if (s.getMaxOccurs() == null || s.getMaxOccurs().equals("1")) {
        if (nonMultipleStatement.contains(s.getId().toString())) {
          validationMapMultipleStatements.put(md, "Multiple value not allowed for metadata "
              + s.getLabels().iterator().next().getValue() + "(ID: " + s.getId() + "");
        } else {
          // throw new UnprocessableError(
          // "Multiple value not allowed for metadata "
          // + s.getLabels().iterator().next()
          // .getValue() + "(ID: " + s.getId()
          // + "");
          nonMultipleStatement.add(s.getId().toString());
        }
      }
    }

    // checking Maps here
    if (!(validationMap.isEmpty() && validationMapMultipleStatements.isEmpty())) {
      List<String> errorMessages = new ArrayList<String>();

      StringBuilder builder = new StringBuilder();
      for (Metadata md : item.getMetadataSet().getMetadata()) {
        if (validationMap.containsKey(md))
          builder.append(validationMap.get(md) + ";");
        if (validationMapMultipleStatements.containsKey(md))
          builder.append(validationMapMultipleStatements.get(md) + ";");
      }

      throw new UnprocessableError(builder.toString());
    }

  }

}
