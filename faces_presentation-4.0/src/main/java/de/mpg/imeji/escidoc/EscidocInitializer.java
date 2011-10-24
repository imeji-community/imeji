package de.mpg.imeji.escidoc;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.xml.rpc.ServiceException;

import org.apache.xmlbeans.XmlException;

import de.escidoc.core.x01.structuralRelations.ContentModelDocument;
import de.escidoc.schemas.contentmodel.x01.ContentModelDocument.ContentModel;
import de.escidoc.www.services.cmm.ContentModelHandler;
import de.mpg.escidoc.services.framework.ServiceLocator;
import de.mpg.imeji.util.LoginHelper;
import de.mpg.imeji.util.PropertyReader;

public class EscidocInitializer 
{
	private String userHandle = null;
	
	public void run()
	{
		login();
		if (!hasContentModel())
		{
			createContentModel(parseContentModel().xmlText());
		}
	}
	
	private void login()
	{
		userHandle = LoginHelper.loginSystemAdmin();
	}
	
	private boolean hasContentModel()
	{
		try 
		{
			ContentModelHandler cmHandler = ServiceLocator.getContentModelHandler(userHandle);
			cmHandler.retrieve(PropertyReader.getProperty("escidoc.faces.content-model.id"));
		} 
		catch (Exception e) 
		{
			return false;
		}
		return true;
	}
	
	private void createContentModel(String cmXml)
	{
		try 
		{
			ContentModelHandler cmHandler = ServiceLocator.getContentModelHandler(userHandle);
			cmHandler.create(cmXml);
		} 
		catch (Exception e) 
		{
			throw new RuntimeException(e);
		}
	}
	
	private ContentModelDocument parseContentModel()
	{
		ContentModelDocument cmDoc;
		try 
		{
			cmDoc = ContentModelDocument.Factory.parse(new File("resources/escidocObjects/content-model.xml"));
		} 
		catch (Exception e) 
		{
			throw new RuntimeException(e);
		}
		return cmDoc;
	}
}
