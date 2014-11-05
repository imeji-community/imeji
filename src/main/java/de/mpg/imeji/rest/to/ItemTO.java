package de.mpg.imeji.rest.to;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;



public class ItemTO extends PropertiesTO implements Serializable{

	private static final long serialVersionUID = -6786476307736639709L;
	
	private String visibility = Visibility.PRIVATE.name();
    
	private String collection;
	
    private String filename;
    
    private String mimetype;

    private URI webResolutionUrlUrl;
    
    private URI thumbnailUrl;
    
    private URI fileUrl;
    
    private List<MetadataTO> metadata = new ArrayList<MetadataTO>();
    
    
    public enum Visibility
    {
        PUBLIC, PRIVATE;
    }


	public String getVisibility() {
		return visibility;
	}


	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}


	public String getCollection() {
		return collection;
	}


	public void setCollection(String collection) {
		this.collection = collection;
	}


	public String getFilename() {
		return filename;
	}


	public void setFilename(String filename) {
		this.filename = filename;
	}


	public String getMimetype() {
		return mimetype;
	}


	public void setMimetype(String mimetype) {
		this.mimetype = mimetype;
	}


	public URI getWebResolutionUrlUrl() {
		return webResolutionUrlUrl;
	}


	public void setWebResolutionUrlUrl(URI webResolutionUrlUrl) {
		this.webResolutionUrlUrl = webResolutionUrlUrl;
	}


	public URI getThumbnailUrl() {
		return thumbnailUrl;
	}


	public void setThumbnailUrl(URI thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}


	public URI getFileUrl() {
		return fileUrl;
	}


	public void setFileUrl(URI fileUrl) {
		this.fileUrl = fileUrl;
	}


	public List<MetadataTO> getMetadata() {
		return metadata;
	}


	public void setMetadata(List<MetadataTO> metadata) {
		this.metadata = metadata;
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}
    
    
    
    

}
