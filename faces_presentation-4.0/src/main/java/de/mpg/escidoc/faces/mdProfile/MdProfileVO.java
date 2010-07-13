package de.mpg.escidoc.faces.mdProfile;

import java.util.List;

import de.escidoc.schemas.contentmodel.x01.ContentModelDocument;
import de.escidoc.schemas.contentmodel.x01.ContentModelDocument.ContentModel;

public class MdProfileVO 
{
    private ContentModel contentModel = null;
    private List<String> metadata = null;
    
    public MdProfileVO()
    {
	ContentModelDocument cmd = ContentModelDocument.Factory.newInstance();
    }

}
