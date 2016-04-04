package de.mpg.imeji.logic.ingest.validator;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;

import de.mpg.imeji.logic.resource.util.MetadataTypesHelper;
import de.mpg.imeji.logic.resource.vo.Item;
import de.mpg.imeji.logic.resource.vo.Metadata;
import de.mpg.imeji.logic.resource.vo.metadata.ConePerson;
import de.mpg.imeji.logic.resource.vo.metadata.Date;
import de.mpg.imeji.logic.resource.vo.metadata.Geolocation;
import de.mpg.imeji.logic.resource.vo.metadata.License;
import de.mpg.imeji.logic.resource.vo.metadata.Link;
import de.mpg.imeji.logic.resource.vo.metadata.Number;
import de.mpg.imeji.logic.resource.vo.metadata.Publication;
import de.mpg.imeji.logic.resource.vo.metadata.Text;

/**
 * @author hnguyen
 */
public class MetadataContentValidator {
  private static final ArrayList<String> notRequiredList = new ArrayList<String>();

  /**
   * @param metadata
   * @param types
   * @throws Exception
   * @throws IntrospectionException
   */
  public static void validate(Metadata metadata, Metadata.Types types, Item item) throws Exception,
      IntrospectionException {
    if (metadata == null)
      throw new Exception(new Throwable("metadata is null"));
    switch (types) {
      case TEXT:
        for (PropertyDescriptor propertyDescriptor : Introspector.getBeanInfo(Text.class)
            .getPropertyDescriptors()) {
          if (propertyDescriptor.getWriteMethod() == null)
            continue;
          if (!notRequiredList.contains(propertyDescriptor.getName())) {
            if (metadata.getValueFromMethod(propertyDescriptor.getReadMethod().getName()) == null
                || metadata.getValueFromMethod(propertyDescriptor.getReadMethod().getName())
                    .toString().isEmpty()) {
              throw new Exception(new Throwable("Text object of Item (" + item.getId().toString()
                  + ") has invalid setting for element: " + propertyDescriptor.getName()));
            }
          }
        }
        break;
      case CONE_PERSON:
        // TODO: need to implement validation for this type.
        for (PropertyDescriptor propertyDescriptor : Introspector.getBeanInfo(ConePerson.class)
            .getPropertyDescriptors()) {
          if (propertyDescriptor.getWriteMethod() == null)
            continue;
          if (!notRequiredList.contains(propertyDescriptor.getName())) {
            if (metadata.getValueFromMethod(propertyDescriptor.getReadMethod().getName()) == null) {
              throw new Exception(new Throwable("Cone Person object of Item ("
                  + item.getId().toString() + ") has invalid setting for element: "
                  + propertyDescriptor.getName()));
            }
          }
        }
        break;
      case DATE:
        // TODO: need to implement validation for this type.
        for (PropertyDescriptor propertyDescriptor : Introspector.getBeanInfo(Date.class)
            .getPropertyDescriptors()) {
          if (propertyDescriptor.getWriteMethod() == null)
            continue;
          if (!notRequiredList.contains(propertyDescriptor.getName())) {
            if (metadata.getValueFromMethod(propertyDescriptor.getReadMethod().getName()) == null) {
              throw new Exception(new Throwable("Date object of Item (" + item.getId().toString()
                  + ") has invalid setting for element: " + propertyDescriptor.getName()));
            }
          }
        }
        break;
      case GEOLOCATION:
        // TODO: need to implement validation for this type.
        for (PropertyDescriptor propertyDescriptor : Introspector.getBeanInfo(Geolocation.class)
            .getPropertyDescriptors()) {
          if (propertyDescriptor.getWriteMethod() == null)
            continue;
          if (!notRequiredList.contains(propertyDescriptor.getName())) {
            if (metadata.getValueFromMethod(propertyDescriptor.getReadMethod().getName()) == null) {
              throw new Exception(new Throwable("Geolocation object of Item ("
                  + item.getId().toString() + ") has invalid setting for element: "
                  + propertyDescriptor.getName()));
            }
          }
        }
        break;
      case LICENSE:
        // TODO: need to implement validation for this type.
        for (PropertyDescriptor propertyDescriptor : Introspector.getBeanInfo(License.class)
            .getPropertyDescriptors()) {
          if (propertyDescriptor.getWriteMethod() == null)
            continue;
          if (!notRequiredList.contains(propertyDescriptor.getName())) {
            if (metadata.getValueFromMethod(propertyDescriptor.getReadMethod().getName()) == null) {
              throw new Exception(new Throwable("License object of Item ("
                  + item.getId().toString() + ") has invalid setting for element: "
                  + propertyDescriptor.getName()));
            }
          }
        }
        break;
      case LINK:
        // TODO: need to implement validation for this type.
        for (PropertyDescriptor propertyDescriptor : Introspector.getBeanInfo(Link.class)
            .getPropertyDescriptors()) {
          if (propertyDescriptor.getWriteMethod() == null)
            continue;
          if (!notRequiredList.contains(propertyDescriptor.getName())) {
            if (metadata.getValueFromMethod(propertyDescriptor.getReadMethod().getName()) == null) {
              throw new Exception(new Throwable("Link object of Item (" + item.getId().toString()
                  + ") has invalid setting for element element: " + propertyDescriptor.getName()));
            }
          }
        }
        break;
      case NUMBER:
        // TODO: need to implement validation for this type.
        for (PropertyDescriptor propertyDescriptor : Introspector.getBeanInfo(Number.class)
            .getPropertyDescriptors()) {
          if (propertyDescriptor.getWriteMethod() == null)
            continue;
          if (!notRequiredList.contains(propertyDescriptor.getName())) {
            if (metadata.getValueFromMethod(propertyDescriptor.getReadMethod().getName()) == null) {
              throw new Exception(new Throwable("Number object of Item (" + item.getId().toString()
                  + ") has invalid setting for element: " + propertyDescriptor.getName()));
            }
          }
        }
        break;
      case PUBLICATION:
        // TODO: need to implement validation for this type.
        for (PropertyDescriptor propertyDescriptor : Introspector.getBeanInfo(Publication.class)
            .getPropertyDescriptors()) {
          if (propertyDescriptor.getWriteMethod() == null)
            continue;
          if (!notRequiredList.contains(propertyDescriptor.getName())) {
            if (metadata.getValueFromMethod(propertyDescriptor.getReadMethod().getName()) == null) {
              throw new Exception(new Throwable("Publication object of Item ("
                  + item.getId().toString() + ") has invalid setting for element: "
                  + propertyDescriptor.getName()));
            }
          }
        }
        break;
      default:
        break;
    }
  }

  /**
   * @param metadata
   * @throws Exception
   */
  public static void validate(Collection<Metadata> metadata, Item item) throws Exception {
    for (Metadata md : metadata) {
      MetadataContentValidator.validate(md,
          MetadataTypesHelper.getTypesForNamespace(md.getTypeNamespace()), item);
    }
  }
}
