package de.mpg.imeji.rest.to;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class ItemTO extends PropertiesTO implements Serializable{

	private static final long serialVersionUID = 8408059450327059926L;

	private String visibility;
    
	private String collectionId;
	
    private String filename;
    
    private String mimetype;
    
    private String checksumMd5;

    private URI webResolutionUrlUrl;
    
    private URI thumbnailUrl;
    
    private URI fileUrl;
    
    private List<MetadataSetTO> metadata = new ArrayList<MetadataSetTO>();
    


	public String getVisibility() {
		return visibility;
	}


	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}


	public String getCollectionId() {
		return collectionId;
	}


	public void setCollectionId(String collectionId) {
		this.collectionId = collectionId;
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

	

	public String getChecksumMd5() {
		return checksumMd5;
	}


	public void setChecksumMd5(String checksumMd5) {
		this.checksumMd5 = checksumMd5;
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


	public List<MetadataSetTO> getMetadata() {
		return metadata;
	}


	public void setMetadata(List<MetadataSetTO> metadata) {
		this.metadata = metadata;
	}





    
    
    

}
