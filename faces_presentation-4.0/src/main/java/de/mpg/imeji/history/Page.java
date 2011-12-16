package de.mpg.imeji.history;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.filter.Filter;
import de.mpg.imeji.util.BeanHelper;

public class Page implements Serializable
{
	public enum ImejiPages
	{
		IMAGES("Images.xhtml", "history_images"), COLLECTION_IMAGES("CollectionBrowse.xhtml", "history_images_collection"), SEARCH("AdvancedSearch.xhtml", "history_advanced_search"),
		HOME("Welcome.xhtml", "history_home"), IMAGE("Image.xhtml", "history_image"), COLLECTIONS("Collections.xhtml", "history_collections"), ALBUMS("Albums.xhtml", "history_albums"),
		COLLECTION_HOME("CollectionEntryPage.xhtml", "Collection"), SEARCH_RESULTS_IMAGES("Images.xhtml", "Search results"), EDIT("Edit.xhtml", "Edit images"),
		COLLECTION_IMAGE("CollectionImage.xhtml","history_image"),ALBUM_IMAGES("AlbumBrowse.xhtml", "history_images_album"), ALBUM_HOME("AlbumEntryPage.xhtml", "history_album"),
		ALBUM_IMAGE("AlbumImage.xhtml","history_image"), HELP("Help.xhtml", "help"), COLLECTION_INFO("CollectionView.xhtml","history_collection_info"), UPLOAD("Upload.xhtml","history_upload");

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
	private List<Filter> filters = new ArrayList<Filter>();
	private String query = "";
	private String id = null;

	public Page(ImejiPages type, URI uri)
	{
		this.uri = uri;
		this.type = type;
		//		SessionBean session = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
		//		this.name = session.getLabel(type.getLabel());
		this.name = type.getLabel();
	}

	public boolean equals(Page page)
	{
		if (isNull() && page.isNull()) return true;
		else if (isNull() || page == null || page.isNull()) return false;
		else return (type.equals(page.getType()) && uri.equals(page.getUri()));
	}

	public boolean isNull()
	{
		return (type == null && uri == null);
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

	public String getName() 
	{
		return name;
	}

	public String getInternationalizedName()
	{
		try 
		{
			String s = ((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getLabel(name);
			if (id != null) s += " id " + id;
			return s;
		} 
		catch (Exception e) 
		{
			return name;
		}
	}

	public void setName(String name) {
		System.out.println("SETNAME" + name);
		this.name = name;
	}

	public List<Filter> getFilters() {
		return filters;
	}

	public void setFilters(List<Filter> filters) {
		this.filters = filters;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}	



}
