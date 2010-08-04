package de.mpg.escidoc.faces.upload;

import de.escidoc.www.services.om.ItemHandler;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.framework.ServiceLocator;

public class UploadThread extends Thread
{
	private Upload upload = null;
	private String userHandle = null;
	
	public UploadThread(Upload upload, String userHandle) 
	{
		this.upload = upload;
		this.userHandle = userHandle;
	}

	/**
	 * {@link Override}
	 */
	public void run() 
	{
		// here should be implemented the upload
		for (int i = 0; i < upload.getItems().size(); i++) 
		{
			try 
			{
				String itemXml = ServiceLocator.getItemHandler(userHandle).create(upload.getItems().get(0).getItemDocument().xmlText());
				itemXml = ServiceLocator.getItemHandler(userHandle).submit(itemXml, "TaskParam");
				itemXml = ServiceLocator.getItemHandler(userHandle).assignObjectPid(itemXml, "");
				itemXml = ServiceLocator.getItemHandler(userHandle).assignVersionPid(itemXml, "");
				itemXml = ServiceLocator.getItemHandler(userHandle).release(itemXml, "");
			} 
			catch (Exception e) 
			{
				throw new RuntimeException(e);
			}
		}
		
		this.terminate();
	}

	/**
	 * {@link Override}
	 */
	public void terminate() 
	{
		
	}
	
}
