package de.mpg.imeji.logic.ingest.validator;

import java.io.File;
import java.util.List;

import de.escidoc.core.client.exceptions.application.invalid.InvalidItemStatusException;
import de.mpg.imeji.logic.ingest.factory.ItemSchemaFactory;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.MetadataProfile;

public class ItemContentValidator
{
	
	/**
     * Validate the provided item
     * @param item
     */
    public void validate(Item item) throws Exception
    {    	
    	
        if(item == null)
        	throw new Exception(new Throwable("item is null"));
        
        if(item.getCollection().toString().isEmpty())
        	throw new Exception(new Throwable("collection uri not provided"));
        
         if(item.getCreated().toString().isEmpty())
        	 throw new Exception(new Throwable("created day not provided"));
         
         if(item.getCreatedBy().toString().isEmpty())
        	 throw new Exception(new Throwable("creator not provided"));
         
         if(item.getEscidocId().isEmpty())
        	 throw new Exception(new Throwable("escidocid day not provided"));
    }
	
	
    /**
     * Valid the xml against the profile
     * @param itemListXml
     * @param mdp
     */
    public void validate(List<Item> items) throws Exception
    {
    	for (Item item : items) {
			this.validate(item);
		}
    }
}
