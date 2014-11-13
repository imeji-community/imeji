package de.mpg.imeji.rest.to;

import java.io.Serializable;
import java.net.URI;
import java.util.Calendar;

import org.glassfish.grizzly.http.server.util.SimpleDateFormats;

import de.mpg.j2j.annotations.j2jLiteral;
import de.mpg.j2j.annotations.j2jResource;

public class PropertiesTO implements Serializable{


	private static final long serialVersionUID = 1508003946046656414L;

	private String id;

    private UserTOBasic createdBy;

    private UserTOBasic modifiedBy;

    private String createdDate;

    private String modifiedDate;

    private String versionDate;

    private String status;

    private int version = 0;

    private String discardComment;
    
   
    public PropertiesTO()
    {
    	
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public UserTOBasic getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(UserTOBasic createdBy) {
		this.createdBy = createdBy;
	}

	public UserTOBasic getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(UserTOBasic modifiedBy) {
		this.modifiedBy = modifiedBy;
	}



	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}



	public String getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(String modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getVersionDate() {
		return versionDate;
	}

	public void setVersionDate(String versionDate) {
		this.versionDate = versionDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getDiscardComment() {
		return discardComment;
	}

	public void setDiscardComment(String discardComment) {
		this.discardComment = discardComment;
	}

    
    
    
	
}
