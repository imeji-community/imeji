package de.mpg.escidoc.faces.metastore2.vo;

import java.util.Date;

import thewebsemantic.Embedded;
import thewebsemantic.Namespace;
import thewebsemantic.RdfType;
import thewebsemantic.RdfProperty;



@Namespace("http://imeji.mpdl.mpg.de/")
@RdfType("properties")
@Embedded
public class Properties {
	
	private User createdBy;
	
	private User modifiedBy;
	
	private Date creationDate;

	private Date lastModificationDate;
	
	private Status status;
	
	private int version = 0;
	
	
	
	@Namespace("http://imeji.mpdl.mpg.de/")
	@RdfType("status")
	public enum Status
	{
		PENDING, RELEASED, WITHDRAWN;
	}

	
	public void setLastModificationDate(Date lastModificationDate) {
		this.lastModificationDate = lastModificationDate;
	}

	public Date getLastModificationDate() {
		return lastModificationDate;
	}
	
	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setModifiedBy(User modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public User getModifiedBy() {
		return modifiedBy;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Status getStatus() {
		return status;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public int getVersion() {
		return version;
	}

}
