package de.mpg.imeji.escidoc;

import java.io.File;

import javax.naming.InitialContext;

import org.apache.log4j.Logger;

import de.escidoc.schemas.contentmodel.x01.ContentModelDocument;
import de.escidoc.schemas.context.x07.ContextDocument;
import de.escidoc.www.services.cmm.ContentModelHandler;
import de.escidoc.www.services.om.ContextHandler;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.valueobjects.TaskParamVO;
import de.mpg.escidoc.services.framework.ServiceLocator;
import de.mpg.imeji.util.LoginHelper;
import de.mpg.imeji.util.PropertyReader;

public class EscidocInitializer 
{
	private String userHandle = null;
	private static Logger logger = Logger.getLogger(EscidocInitializer.class);
	
	public void run()
	{
		login();
		if (!hasContentModel())
		{
			createContentModel(parseContentModel().xmlText());
		}
		
		if (!hasContext())
		{
			createContext(parseContext().xmlText());
		}
		
		if (!hasImejiUser()) 
		{
			logger.warn("NO IMEJI USER: please create one or use admin user instead, or create one");
		}
	}
	
	private void login()
	{
		userHandle = LoginHelper.loginSystemAdmin();
		System.out.println(userHandle);
	}
	
	private boolean hasContentModel()
	{
		try 
		{
			ContentModelHandler cmHandler = ServiceLocator.getContentModelHandler(userHandle);
			String cm = cmHandler.retrieve(PropertyReader.getProperty("escidoc.faces.content-model.id"));
			logger.info("CONTENT MODEL FOUND: " + cm);
		} 
		catch (Exception e) 
		{
			logger.warn("Error reading content model (might not exist)", e);
			return false;
		}
		return true;
	}
	
	private void createContentModel(String cmXml)
	{
		try 
		{
			ContentModelHandler cmHandler = ServiceLocator.getContentModelHandler(userHandle);
			ContentModelDocument doc = ContentModelDocument.Factory.parse(cmHandler.create(cmXml));
			logger.info("Content model created: " + doc.getContentModel().getObjid());
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
			cmDoc = ContentModelDocument.Factory.parse(new File("../server/default/conf/content-model.xml"));
		} 
		catch (Exception e) 
		{
			throw new RuntimeException(e);
		}
		return cmDoc;
	}
	
	private boolean hasContext()
	{
		try 
		{
			ContextHandler handler = ServiceLocator.getContextHandler(userHandle);
			String ctx = handler.retrieve(PropertyReader.getProperty("escidoc.faces.context.id"));
			logger.info("CONTEXT FOUND: " + ctx);
		} 
		catch (Exception e) 
		{
			logger.warn("Error reading context (might not exist)", e);
			return false;
		}
		return true;
	}
	
	private void createContext(String ctxXml)
	{
		try 
		{
			ContextHandler handler = ServiceLocator.getContextHandler(userHandle);
			ContextDocument doc = ContextDocument.Factory.parse(handler.create(ctxXml));
			TaskParamVO taskParam = new TaskParamVO(doc.getContext().getLastModificationDate().getTime());
			InitialContext context = new InitialContext();
			XmlTransforming xmlTr = (XmlTransforming)context.lookup(XmlTransforming.SERVICE_NAME);
			handler.open(doc.getContext().getObjid(), xmlTr.transformToTaskParam(taskParam));
			logger.info("Context created and opened: " + doc.getContext().getObjid()) ;
		} 
		catch (Exception e) 
		{
			throw new RuntimeException(e);
		}
	}
	
	private ContextDocument parseContext()
	{
		ContextDocument doc;
		try 
		{
			doc = ContextDocument.Factory.parse(new File("../server/default/conf/context.xml"));
		} 
		catch (Exception e) 
		{
			throw new RuntimeException(e);
		}
		return doc;
	}
	
	private boolean hasImejiUser()
	{
		try 
		{
			String handle = LoginHelper.login(PropertyReader.getProperty("imeji.escidoc.user"), PropertyReader.getProperty("imeji.escidoc.password"));
			if (handle == null) throw new RuntimeException("Login with imeji user return null");
			else logger.info("IMEJI USER FOUND");
		} 
		catch (Exception e) 
		{
			logger.warn("Error logging with imeji user (might not exist)", e);
			return false;
		}
		return true;
	}
}
