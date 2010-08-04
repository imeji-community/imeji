package de.mpg.escidoc.faces.upload;

import java.util.ArrayList;
import java.util.List;

/**
 * Thread started on initialization of Faces
 * @author saquet
 *
 */
public class UploadManager extends Thread
{
	private List<UploadThread> uploadThreads = null;
	private boolean signal = true;
	private String userHandler = null;
	
	public UploadManager() 
	{
		uploadThreads = new ArrayList<UploadThread>();
	}
	
	/**
	 * Add a new Upload to the manager.
	 * <br> At the end, the report of the upload will be written 
	 * <br> Upload will be removed when finished.
	 * @param upload
	 */
	public void addNewUpload(Upload upload, String userHandle)
	{

		uploadThreads.add(new UploadThread(upload, userHandler));

	}
	
	/**
	 * {@link Override}
	 */
	public void run()
	{
		while (signal) 
		{
			for (UploadThread uploadThread : uploadThreads) 
			{
				if (!uploadThread.isAlive()) 
				{
					uploadThread.run();
				}
			}
		}
	}
	
	/**
	 * {@link Override}
	 */
	public void terminate()
	{
		signal = false;
	}

}
