/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.jena.vo;

import java.io.Serializable;
import java.net.URI;

import thewebsemantic.Namespace;
import thewebsemantic.RdfProperty;
import thewebsemantic.RdfType;

@Namespace("http://imeji.mpdl.mpg.de/")
@RdfType("collection")
public class CollectionImeji extends Container implements Serializable{

    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1621325170719016484L;
	private URI profile = null;
    private MetadataSet metadataSet = new MetadataSet();

    @RdfProperty("http://imeji.mpdl.mpg.de/mdprofile")
	public URI getProfile() {
		return profile;
	}

	public void setProfile(URI profile) {
		this.profile = profile;
	}
    
    public MetadataSet getMetadataSet() 
    {
		return metadataSet;
	}

	public void setMetadataSet(MetadataSet metadataSet) 
	{
		this.metadataSet = metadataSet;
	}

    
   
}
