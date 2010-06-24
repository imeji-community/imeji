package de.mpg.escidoc.faces.upload;

public class UploadThread extends Thread
{
	private Upload upload = null;
	
	public UploadThread(Upload upload) 
	{
		this.upload = upload;
	}

	/**
	 * {@link Override}
	 */
	public void run() 
	{
		// here should be implemented the upload
		this.terminate();
	}

	/**
	 * {@link Override}
	 */
	public void terminate() 
	{
		
	}
	
}
