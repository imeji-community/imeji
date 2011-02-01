package de.mpg.jena.vo;

import java.io.Serializable;
import java.net.URI;

import thewebsemantic.Embedded;
import thewebsemantic.Namespace;
import thewebsemantic.RdfType;

@Namespace("http://imeji.mpdl.mpg.de/")
@RdfType("grant")
@Embedded
public class Grant implements Serializable {

    @Namespace("http://imeji.mpdl.mpg.de/")
    @RdfType("grantType")
	public enum GrantType {
        SYSADMIN, CONTAINER_ADMIN, CONTAINER_EDITOR, IMAGE_UPLOADER
        , IMAGE_EDITOR, PRIVILEGED_VIEWER, PROFILE_ADMIN, PROFILE_EDITOR;
	}
	
	private GrantType grantType;
	
	private URI grantFor;
	
	public Grant()
	{
	    
	}
	
	public Grant(GrantType gt, URI gf)
	{
	    this.grantType = gt;
	    this.grantFor = gf;
	}
	
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
