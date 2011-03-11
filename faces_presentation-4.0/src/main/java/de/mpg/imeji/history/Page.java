package de.mpg.imeji.history;

import java.net.URI;

public class Page 
{
	public enum ImejiPages
	{
		IMAGES("Images.xhtml", "Images"), COLLECTION_IMAGES("CollectionBrowse.xhtml", "Images of Collection"), SEARCH("SearchPictures.xhtml", "Advanced Search"),
		HOME("Welcome.xhtml", "Home"), IMAGE("Image.xhtml", "Image"), COLLECTIONS("Collections.xhtml", "Collections"), ALBUMS("Albums.xhtml", "Albums"),
		COLLECTION_HOME("CollectionEntryPage.xhtml", "Collection"), SEARCH_RESULTS_IMAGES("Images.xhtml", "Search results"), EDIT("Edit.xhtml", "Edit images");
		
		private String fileName="";
		private String label;
		
		private ImejiPages(String fileName, String label) 
		{
			this.fileName = fileName;
			this.label = label;
		}
		
		public String getLabel() {
			return label;
		}
		
		public String getFileName() {
			return fileName;
		}
	}
	
	private ImejiPages type;
	private URI uri;
	private String name;
	
	public Page(ImejiPages type, URI uri)
	{
		this.uri = uri;
		this.type = type;
		this.name = type.getLabel();
	}

	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

	public ImejiPages getType() {
		return type;
	}

	public void setType(ImejiPages type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
