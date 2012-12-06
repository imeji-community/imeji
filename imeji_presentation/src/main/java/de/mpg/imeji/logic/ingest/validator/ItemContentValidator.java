package de.mpg.imeji.logic.ingest.validator;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.MetadataSet;

/**
 * 
 * @author hnguyen
 *
 */
public class ItemContentValidator
{
	private static final ArrayList<String> notRequiredList = new ArrayList<String>(
			Arrays.asList("discardComment", "versionDate")
			);
	
	/**
	 * 
	 * @param item
	 * @throws Exception
	 * @throws IntrospectionException
	 */
    @SuppressWarnings("unchecked")
	public static void validate(Item item) throws Exception, IntrospectionException
    {    	
    	    	
    	if(item == null)
        	throw new Exception(new Throwable("Item is null"));
    	
		for(PropertyDescriptor propertyDescriptor : Introspector.getBeanInfo(Item.class).getPropertyDescriptors()){

			
			if(propertyDescriptor.getWriteMethod() == null) continue;
			
			if(!notRequiredList.contains(propertyDescriptor.getName())) {
				if(item.getValueFromMethod(propertyDescriptor.getReadMethod().getName()) == null) {
					throw new Exception(new Throwable("Item object ("+item.getId().toString()+")has invalid setting for element: " + propertyDescriptor.getName()));					
				} else {
					if(propertyDescriptor.getPropertyType() == List.class) {
						MetadataSetContentValidator.validate((List<MetadataSet>)item.getValueFromMethod(propertyDescriptor.getReadMethod().getName()),item);
					}
				}
			}
			
		}
    }
	
	/**
	 * 
	 * @param items
	 * @throws Exception
	 */
    public static void validate(List<Item> items) throws Exception
    {
    	for (Item item : items) {
    		ItemContentValidator.validate(item);
		}
    }
}
