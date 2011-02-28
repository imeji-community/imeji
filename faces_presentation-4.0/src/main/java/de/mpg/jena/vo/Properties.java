package de.mpg.jena.vo;

import java.io.Serializable;
import java.net.URI;
import java.util.Date;

import thewebsemantic.Embedded;
import thewebsemantic.Namespace;
import thewebsemantic.RdfType;
import thewebsemantic.RdfProperty;



@Namespace("http://imeji.mpdl.mpg.de/")
@RdfType("properties")
@Embedded
public class Properties implements Serializable {
	
	private URI createdBy;
	
	private URI modifiedBy;
	
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
	
	public Properties() {
		// TODO Auto-generated constructor stub
	}

	
	public void setLastModificationDate(Date lastModificationDate) {
		this.lastModificationDate = lastModificationDate;
	}

	public Date getLastModificationDate() {
		return lastModificationDate;
	}
	
	public void setCreatedBy(URI createdBy) {
		this.createdBy = createdBy;
	}

	public URI getCreatedBy() {
		return createdBy;
	}

	public void setModifiedBy(URI modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public URI getModifiedBy() {
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
