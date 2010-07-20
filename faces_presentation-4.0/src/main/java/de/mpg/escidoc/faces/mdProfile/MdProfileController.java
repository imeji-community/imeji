package de.mpg.escidoc.faces.mdProfile;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.axis.encoding.Base64;
import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.XmlException;

import de.escidoc.core.x01.structuralRelations.ContentModelDocument;
import de.escidoc.schemas.commontypes.x04.LinkForCreate;
import de.escidoc.schemas.contentmodel.x01.*;
import de.escidoc.schemas.contentstreams.x07.ContentStreamDocument;
import de.escidoc.schemas.item.x09.ItemDocument;
import de.mpg.escidoc.faces.metadata.schema.SimpleSchema;
import de.mpg.escidoc.faces.util.UserHelper;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.referenceobjects.ContextRO;
import de.mpg.escidoc.services.common.referenceobjects.ItemRO;
import de.mpg.escidoc.services.common.valueobjects.FileVO;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.common.valueobjects.MetadataSetVO;
import de.mpg.escidoc.services.common.valueobjects.FileVO.Storage;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;

public class MdProfileController
{
    private XmlTransforming xmlTransforming = null;
    
    public MdProfileController()
    {
	try
	{
	    InitialContext context = new InitialContext();
	    xmlTransforming  = (XmlTransforming) context.lookup(XmlTransforming.SERVICE_NAME);
	} 
	catch (NamingException e)
	{
	    throw new RuntimeException("MdProfileController: Error initialization: " + e);
	}
    }

    
    public MdProfileVO create(MdProfileVO profile, String userHandle)
    {
	try
	{
	    //Create Schema
	    SimpleSchema simpleSchema = new SimpleSchema(profile.getName(), null, profile.getMetadataList());
	    
	    // Create Item with Schema as component
	    ItemDocument itemDocument = ItemDocument.Factory.newInstance();
	    itemDocument.addNewItem();
	    itemDocument.getItem().addNewProperties();
	    itemDocument.getItem().getProperties().addNewContentModel();
	    itemDocument.getItem().getProperties().getContentModel().setObjid("escidoc:13234");
	    itemDocument.getItem().getProperties().addNewContext();
	    itemDocument.getItem().getProperties().getContext().setObjid("escidoc:13234");
	    itemDocument.getItem().addNewComponents();
	    itemDocument.getItem().getComponents().addNewComponent();
	    itemDocument.getItem().getComponents().getComponentArray(0).addNewContent();
	    itemDocument.getItem().getComponents().getComponentArray(0).addNewProperties();
	    itemDocument.getItem().getComponents().getComponentArray(0).getProperties().setContentCategory("text/xml");
	    itemDocument.getItem().getComponents().getComponentArray(0).getProperties().setMimeType("text/xml");
	    itemDocument.getItem().getComponents().getComponentArray(0).getProperties().setFileName(profile.getName() + ".xsd");
	    itemDocument.getItem().getComponents().getComponentArray(0).getContent().setStringValue(Base64.encode(simpleSchema.getXsd().getBytes()));

	    //ServiceLocator.getItemHandler(userHandle).create(itemDocument.xmlText());
	    
	    //Create content-model with reference to previous item component
	    de.escidoc.schemas.contentmodel.x01.ContentModelDocument cmd = de.escidoc.schemas.contentmodel.x01.ContentModelDocument.Factory.newInstance();
	    cmd.addNewContentModel();
	    cmd.getContentModel().addNewProperties();
	    cmd.getContentModel().getProperties().setName(profile.getName());
	    cmd.getContentModel().getProperties().setDescription("Content model for " + profile.getName() + " profile");
	    cmd.getContentModel().addNewContentStreams();
	    cmd.getContentModel().getContentStreams().addNewContentStream();
	    cmd.getContentModel().getContentStreams().getContentStreamArray(0).addNewStorage();
	    cmd.getContentModel().getContentStreams().getContentStreamArray(0).setStorage(XmlAnySimpleType.Factory.newValue("internal-managed"));
	    cmd.getContentModel().getContentStreams().getContentStreamArray(0).setHref("url of the item");
	    
	    //ServiceLocator.getContentModelHandler(userHandle).create(cmd.xmlText());
	    
	    System.out.println("SCHEMA GENERATED:");
	    System.out.println(simpleSchema.getXsd());
	    System.out.println("END SCHEMA");
	
	    System.out.println("Item GENERATED:");
	    System.out.println(itemDocument.xmlText());
	    System.out.println("END item");
	    
	    System.out.println("content model GENERATED:");
	    System.out.println(cmd.xmlText());
	    System.out.println("END content model");
	
	    
	    
	    return profile;
	} 
	catch (Exception e)
	{
	    throw new RuntimeException("Error creating Md Profile", e);
	}
    }
    
}
