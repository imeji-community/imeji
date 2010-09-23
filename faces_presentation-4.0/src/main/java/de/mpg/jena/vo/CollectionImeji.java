package de.mpg.jena.vo;

import java.io.Serializable;

import thewebsemantic.Namespace;
import thewebsemantic.RdfType;

@Namespace("http://imeji.mpdl.mpg.de/")
@RdfType("collection")
public class CollectionImeji extends Container implements Serializable{

    private MetadataProfile profile = new MetadataProfile();
    
    public MetadataProfile getProfile()
    {
        return profile;
    }

    public void setProfile(MetadataProfile profile)
    {
        this.profile = profile;
    }
    
   
}
