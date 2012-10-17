/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.jena.vo;

import java.io.Serializable;
import java.net.URI;
import java.util.Date;

import thewebsemantic.Embedded;
import thewebsemantic.Namespace;
import thewebsemantic.RdfType;

@Namespace("http://imeji.mpdl.mpg.de/")
@RdfType("properties")
@Embedded
public class Properties implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7606191181572686126L;

	private URI createdBy;
	
	private URI modifiedBy;
	
	private Date creationDate;

	private Date lastModificationDate;
	
	private Date versionDate;
	
	private Status status = Status.PENDING;
	
	private int version = 0;
	
	private String discardComment;
	
	
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
	
	public Date getVersionDate() {
		return versionDate;
	}


	public void setVersionDate(Date versionDate) {
		this.versionDate = versionDate;
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
	
	public String getDiscardComment() {
		return discardComment;
	}
	
	public void setDiscardComment(String discardComment) {
		this.discardComment = discardComment;
	}

}
