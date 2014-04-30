package de.mpg.imeji.logic.ingest.validator;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.imeji.logic.vo.MetadataSet;

/**
 * @author hnguyen
 */
public class MetadataSetContentValidator
{
    private static final ArrayList<String> notRequiredList = new ArrayList<String>();

    /**
     * @param metadataSet
     * @throws Exception
     * @throws IntrospectionException
     */
    @SuppressWarnings("unchecked")
    public static void validate(MetadataSet metadataSet, Item item) throws Exception, IntrospectionException
    {
        if (metadataSet == null)
            throw new Exception(new Throwable("MetadataSet is null"));
        for (PropertyDescriptor propertyDescriptor : Introspector.getBeanInfo(MetadataSet.class)
                .getPropertyDescriptors())
        {
            if (propertyDescriptor.getWriteMethod() == null)
                continue;
            if (!notRequiredList.contains(propertyDescriptor.getName()))
            {
                if (metadataSet.getValueFromMethod(propertyDescriptor.getReadMethod().getName()) == null)
                {
                    throw new Exception(new Throwable("MetadataSet object of Item (" + item.getId().toString()
                            + ") has invalid setting for element: " + propertyDescriptor.getName()));
                }
                else
                {
                    if (propertyDescriptor.getPropertyType() == Collection.class)
                    {
                        MetadataContentValidator.validate((Collection<Metadata>)metadataSet
                                .getValueFromMethod(propertyDescriptor.getReadMethod().getName()), item);
                    }
                }
            }
        }
    }

    /**
     * @param metadataSets
     * @throws Exception
     */
    public static void validate(List<MetadataSet> metadataSets, Item item) throws Exception
    {
        for (MetadataSet metadataSet : metadataSets)
        {
            MetadataSetContentValidator.validate(metadataSet, item);
        }
    }
}
