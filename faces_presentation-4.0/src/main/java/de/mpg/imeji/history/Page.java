package de.mpg.imeji.history;

import java.net.URI;

public class Page 
{
	private String name;
	private URI uri;
	private String fileName;
	
	
	public enum ImejiPages
	{
		IMAGES("/Images.xhtml");
		
		private String fileName="";
		
		private ImejiPages(String fileName) 
		{
			this.fileName = fileName;
		}
	}
	
	public Page(String fileName, URI uri)
	{
		this.uri = uri;
		this.fileName = fileName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileName() {
		return fileName;
	}
	
}
