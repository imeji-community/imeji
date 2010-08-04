package de.mpg.escidoc.faces.deposit;

import java.awt.image.BufferedImage;
import de.escidoc.schemas.item.x09.ItemDocument;
import de.escidoc.schemas.result.x01.ResultDocument;
import de.mpg.escidoc.faces.item.ImejiItemVO;

import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * 
 * @author yu
 *
 */

public class DepositController {

	
	public static ImejiItemVO createImejiItem(BufferedImage bufferedImage, String title, String description, String mimetype, String format, String userHandle, String collection, String context){
		ImejiItemVO imejiItem = new ImejiItemVO(title, description,collection,context);
		try {
			imejiItem.attachFile(bufferedImage, title, mimetype, format, userHandle);
		} catch (Exception e) {
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
