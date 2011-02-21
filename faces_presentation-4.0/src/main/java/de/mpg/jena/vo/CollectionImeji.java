package de.mpg.jena.vo;

import java.io.Serializable;
import java.net.URI;

import thewebsemantic.Namespace;
import thewebsemantic.RdfType;

@Namespace("http://imeji.mpdl.mpg.de/")
@RdfType("collection")
public class CollectionImeji extends Container implements Serializable{

    
    private URI profile = null;

	public URI getProfile() {
		return profile;
	}

	public void setProfile(URI profile) {
		this.profile = profile;
	}
    
    

    
   
}
