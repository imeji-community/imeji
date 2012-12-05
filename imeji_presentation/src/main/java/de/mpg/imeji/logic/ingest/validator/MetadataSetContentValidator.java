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
import de.mpg.imeji.logic.vo.MetadataSet;

public class MetadataSetContentValidator
{
	
	/**
     * Validate the provided item
     * @param item
     */
    public void validate(MetadataSet metadataSet) throws Exception, IntrospectionException
    {    	
    	    	
    	if(metadataSet == null)
        	throw new Exception(new Throwable("metadataSet is null"));
    	
		for(PropertyDescriptor propertyDescriptor : Introspector.getBeanInfo(Item.class).getPropertyDescriptors()){

			
			if(propertyDescriptor.getWriteMethod() == null) continue;
			
			if(propertyDescriptor.getReadMethod().getReturnType() == String.class || propertyDescriptor.getReadMethod().getReturnType() == URI.class) {
				if(metadataSet.getValueFromMethod(propertyDescriptor.getReadMethod().getName()).toString().isEmpty()) {
					throw new Exception(new Throwable("metadataSet object has invalid setting for attribute: " + propertyDescriptor.getName()));
				}
			}		
		}
    }
	
	
    /**
     * Valid the xml against the profile
     * @param itemListXml
     * @param mdp
     */
    public void validate(List<MetadataSet> metadataSets) throws Exception
    {
    	for (MetadataSet metadataSet : metadataSets) {
			this.validate(metadataSet);
		}
    }
}
