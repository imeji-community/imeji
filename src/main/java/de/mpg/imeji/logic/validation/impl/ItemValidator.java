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
    Object[] itemMetadataList = item.getMetadataSet().getMetadata().toArray();
    
    //Validate that every child has its parent filled
    for (int i=0; i<itemMetadataList.length; i++) {
      Statement s = ProfileHelper.getStatement(((Metadata) itemMetadataList[i]).getStatement(), p);
      if(s.getParent()!=null){
        Statement parentStatement = ProfileHelper.getStatement(s.getParent(), p);
        //First element can not have a parent
        if(i==0){
          throw new UnprocessableError(parentStatement.getLabel() + " has to be filled");
        
        }else{
          Statement preStatement = ProfileHelper.getStatement(((Metadata) itemMetadataList[i-1]).getStatement(), p);
          if(!(parentStatement.getId().equals(preStatement.getId()))){
            throw new UnprocessableError(parentStatement.getLabel() + " has to be filled");
          }
        }

      }
    }
    
    for (Metadata md : item.getMetadataSet().getMetadata()) {
      Statement s = ProfileHelper.getStatement(md.getStatement(), p);
      try {
        mdValidator.validate(md, p);
      } catch (UnprocessableError e) {
        validationMap.put(md, e.getMessage());
      }


      
      
      boolean isMultiple = isMultipleStatement(s, p);
      if (!isMultiple) {
      //if (s.getMaxOccurs() == null || s.getMaxOccurs().equals("1")) {
          
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
      //List<String> errorMessages = new ArrayList<String>();

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
  
  /**
   * @param s {@link Statement}
   * @param p {@link MetadataProfile}
   * @return boolean
   * 
   * method is pretty dummy, it only finds out if the metadata statement can be multiple at any place
   * in general, method should find out if the metadata statement can be multiple in context with its parent
   * however now it makes no troubles during saving of data as previously
   */
  private boolean isMultipleStatement (Statement s, MetadataProfile p) {
          if (s.getParent() == null) {
             return (!(s.getMaxOccurs() == null || s.getMaxOccurs().equals("1")));
          }
          else
          {
             return ( (s.getMaxOccurs()!= null && !s.getMaxOccurs().equals("1"))
                      || isMultipleStatement(ProfileHelper.getStatement(s.getParent(), p), p) );
          }
  }

}
