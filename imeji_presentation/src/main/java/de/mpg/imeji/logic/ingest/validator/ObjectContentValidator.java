package de.mpg.imeji.logic.ingest.validator;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.net.URI;
import java.util.List;

import de.escidoc.core.client.exceptions.application.invalid.InvalidItemStatusException;
import de.mpg.imeji.logic.ingest.factory.ItemSchemaFactory;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.MetadataProfile;

public class ObjectContentValidator
{
	
	/**
     * Validate the provided item
	 * @param <T>
     * @param item
     */
    public <T> void validate(Class<T> clazz) throws Exception, IntrospectionException
    {    	
    	    	
    	if(clazz == null)
        	throw new Exception(new Throwable("object is null"));
    	
		for(PropertyDescriptor propertyDescriptor : Introspector.getBeanInfo(clazz.getClass()).getPropertyDescriptors()){

			
			if(propertyDescriptor.getWriteMethod() == null) continue;
			
			if(propertyDescriptor.getReadMethod().getReturnType() == String.class || propertyDescriptor.getReadMethod().getReturnType() == URI.class) {
//				if(clazz.getValueFromMethod(propertyDescriptor.getReadMethod().getName()).toString().isEmpty()) {
//					throw new Exception(new Throwable("item object has invalid setting for: " + propertyDescriptor.getName()));
//				}
			}		
		}
    }
	
	
    /**
     * Valid the xml against the profile
     * @param itemListXml
     * @param mdp
     */
    public void validate(List<Item> items) throws Exception
    {
    	for (Item item : items) {
//			this.validate(item);
		}
    }
}
