package de.mpg.escidoc.faces.util;

import de.escidoc.schemas.contentmodel.x01.ContentModelDocument;

public class ContentModelHelper 
{
	/**
	 * Generate the XML of a content-model, with required properties by eSciDoc
	 * @param name
	 * @param description
	 * @return
	 */
	public static String generate(String name, String description)
	{
		ContentModelDocument cm = ContentModelDocument.Factory.newInstance();
    	
		cm.getContentModel().getProperties().setDescription(description);
    	cm.getContentModel().getProperties().setName(name);
    			
    	return cm.getContentModel().xmlText();
	}
}
