package de.mpg.imeji.rest.defaultTO;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;

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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DefaultItemTO extends DefaultPropertiesTO implements Serializable{
	
	private static final long serialVersionUID = -1870847854605861134L;
	
	private String visibility;
	
	private String collectionId;
	
    private String filename;
    
    private String mimetype;
    
    private String checksumMd5;

    private URI webResolutionUrlUrl;
    
    private URI thumbnailUrl;
    
    private URI fileUrl;
	
	private String fetchUrl;
	
	private Map<String, JsonNode> metadata = new HashMap<String, JsonNode>();

	public String getCollectionId() {
		return collectionId;
	}

	public void setCollectionId(String collectionId) {
		this.collectionId = collectionId;
	}

	public String getFetchUrl() {
		return fetchUrl;
	}

	public void setFetchUrl(String fetchUrl) {
		this.fetchUrl = fetchUrl;
	}	

	public Map<String, JsonNode> getMetadata() {
		return metadata;
	}

	public void setMetadata(Map<String, JsonNode> metadata) {
		this.metadata = metadata;
	}

	public String getVisibility() {
		return visibility;
	}

	public void setVisibility(String visibility) {
		this.visibility = visibility;
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

	
	



	
	
	

	
	
	
	

}
