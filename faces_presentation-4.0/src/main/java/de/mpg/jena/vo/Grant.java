package de.mpg.jena.vo;

import java.net.URI;

import thewebsemantic.Embedded;
import thewebsemantic.RdfType;

@Embedded
@RdfType("grant")
public class Grant {

	enum GrantType {
		CONTAINER_ADMIN, CONTAINER_EDITOR, IMAGE_UPLOADER, IMAGE_EDITOR, PRIVILEGED_VIEWER
	}
	
	private GrantType grantType;
	
	private URI grantFor;
	
	
	
	public void setGrantType(GrantType grantType) {
		this.grantType = grantType;
	}
	public GrantType getGrantType() {
		return grantType;
	}
	public void setGrantFor(URI grantFor) {
		this.grantFor = grantFor;
	}
	public URI getGrantFor() {
		return grantFor;
	}
	
	
}
