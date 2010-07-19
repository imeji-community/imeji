package de.mpg.escidoc.faces.mdProfile;

import de.mpg.escidoc.faces.metadata.schema.SimpleSchema;

public class MdProfileController
{
    public MdProfileController()
    {
	// TODO Auto-generated constructor stub
    }

    
    public MdProfileVO create(MdProfileVO profile, String userHandle)
    {
	SimpleSchema schema = new SimpleSchema(profile.getName(), null, profile.getMetadataList());
	
	System.out.println("SCHEMA GENERATED:");
	System.out.println(schema.getXsd());
	System.out.println("END SCHEMA");
	
	return profile;
    }
}
