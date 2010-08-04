package de.mpg.escidoc.faces.deposit;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingContentException;
import de.escidoc.core.common.exceptions.application.missing.MissingElementValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingMdRecordException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ContentModelNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.FileNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ReferencedResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.RelationPredicateNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyAttributeViolationException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyElementViolationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.schemas.item.x09.ItemDocument;
import de.escidoc.schemas.result.x01.ResultDocument;
import de.mpg.escidoc.faces.item.ImejiItemVO;
import de.mpg.escidoc.faces.metadata.MdsImejiItemVO;
import de.mpg.escidoc.services.framework.ServiceLocator;

public class DepositController {

	
	public static ImejiItemVO createImejiItem(BufferedImage bufferedImage, String title, String description, String mimetype, String format, String userHandle)
	{

	
		ImejiItemVO imejiItem = new ImejiItemVO(title, description);
		
		try 
		{
			imejiItem.attachFile(bufferedImage, title, mimetype, format, userHandle);
		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return imejiItem;
	}
	
	public static String depositImejiItem (ImejiItemVO item, String userHandle) throws Exception
	{
		String itemXml = ServiceLocator.getItemHandler(userHandle).create(item.getItemDocument().xmlText());
		item.setItem(ItemDocument.Factory.parse(itemXml));
		
		String taskParam = "<param last-modification-date=\"" 
			+ item.getItemDocument().getItem().getLastModificationDate() 
			+ "\"><comment>Submit for upload.</comment></param>";
		
		String lastModificationDate = ServiceLocator.getItemHandler(userHandle).submit(item.getItemDocument().getItem().getObjid(), taskParam);
		
		ResultDocument rdoc = ResultDocument.Factory.parse(lastModificationDate);
		
		taskParam = "<param last-modification-date=\"" 
			+ rdoc.getResult().getLastModificationDate() 
			+ "\"><url>"+ ServiceLocator.getFrameworkUrl() 
			+ "ir/item/" + item.getItemDocument().getItem().getObjid() 
			+ "</url></param>";
		
		lastModificationDate = ServiceLocator.getItemHandler(userHandle).assignObjectPid(item.getItemDocument().getItem().getObjid(), taskParam);
		rdoc = ResultDocument.Factory.parse(lastModificationDate);
		
		taskParam = "<param last-modification-date=\"" 
			+ rdoc.getResult().getLastModificationDate() 
			+ "\"><url>"+ ServiceLocator.getFrameworkUrl() 
			+ "ir/item/" + item.getItemDocument().getItem().getObjid() 
			+ ":1</url></param>";
		
		lastModificationDate = ServiceLocator.getItemHandler(userHandle).assignVersionPid(item.getItemDocument().getItem().getObjid(), taskParam);
		rdoc = ResultDocument.Factory.parse(lastModificationDate);
		
		taskParam = "<param last-modification-date=\"" 
			+ rdoc.getResult().getLastModificationDate() 
			+ "\"><comment>Release for upload.</comment></param>";
		
		lastModificationDate = ServiceLocator.getItemHandler(userHandle).release(item.getItemDocument().getItem().getObjid(), taskParam);
		
		return itemXml;
	}
}
