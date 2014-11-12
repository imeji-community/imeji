package de.mpg.imeji.rest.to;

import java.io.Serializable;
import java.net.URI;
import java.util.Calendar;

import org.glassfish.grizzly.http.server.util.SimpleDateFormats;

import de.mpg.j2j.annotations.j2jLiteral;
import de.mpg.j2j.annotations.j2jResource;

public class PropertiesTO implements Serializable{

	private static final long serialVersionUID = 679290120403475256L;

	private String id;

    private UserTOBasic createdBy;

    private UserTOBasic modifiedBy;

    private SimpleDateFormats created;

    private SimpleDateFormats modified;

    private SimpleDateFormats versionDate;

    private String status = Status.PENDING.getText();

    private int version = 0;

    private String discardComment;
    
    public enum Status
    {
        PENDING("PENDING"), RELEASED("RELEASED"), WITHDRAWN("WITHDRAWN");
        private String text;

        private Status(String text)
        {
            this.text = text;
        }

        public String getText()
        {
            return text;
        }
    }
    
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

	public SimpleDateFormats getCreated() {
		return created;
	}

	public void setCreated(SimpleDateFormats created) {
		this.created = created;
	}

	public SimpleDateFormats getModified() {
		return modified;
	}

	public void setModified(SimpleDateFormats modified) {
		this.modified = modified;
	}

	public SimpleDateFormats getVersionDate() {
		return versionDate;
	}

	public void setVersionDate(SimpleDateFormats versionDate) {
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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
    
    
    
    
	
}
