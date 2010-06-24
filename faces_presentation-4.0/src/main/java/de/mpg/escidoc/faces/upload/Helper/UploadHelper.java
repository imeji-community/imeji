package de.mpg.escidoc.faces.upload.Helper;

import java.io.File;

public class UploadHelper 
{
	public UploadHelper() 
	{
		// TODO Auto-generated constructor stub
	}
	
	public void createThumbnail(File file)
	{
		file = ImageHelper.resizeImage(file, 10, 20);
	}
	
	public void createWebResolution(File file)
	{
		file = ImageHelper.resizeImage(file, 25, 50);
	}
	
	public void uploadImageViaStagingArea(File file)
	{
		
	}
}
