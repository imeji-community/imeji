package de.mpg.imeji.rest.to;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@XmlRootElement
@XmlType (propOrder = {	
		"id",
		"createdBy",
		"modifiedBy",
		"createdDate",
		"modifiedDate",
		"versionDate",
		"status",
		"visibility",
		"version",
		"discardComment",
		"collectionId",
		"filename",
		"mimetype",
		"checksumMd5",
		"webResolutionUrlUrl",
		"thumbnailUrl",
		"fileUrl",
		"metadata"
		})
@JsonInclude(Include.NON_NULL)
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

	public List<MetadataSetTO> filterMetadataByTypeURI(URI type) {
		List<MetadataSetTO> filtered = new ArrayList<MetadataSetTO>();
		for ( MetadataSetTO md: this.metadata )
			if (md.getTypeUri().equals(type))
				filtered.add(md);
		return filtered;
	}

	public MetadataSetTO findMetadata(URI statement, URI type) {
		for (MetadataSetTO md: this.metadata)
			if (md.getTypeUri().equals(type) && md.getStatementUri().equals(statement))
				return md;
		return null;
	}

	public void setMetadata(List<MetadataSetTO> metadata) {
		this.metadata = metadata;
	}


}
