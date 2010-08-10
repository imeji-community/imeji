package de.mpg.escidoc.faces.mdProfile;

import java.util.ArrayList;
import java.util.List;

import org.dublincore.xml.dcDsp.x2008.x01.x14.DescriptionSetTemplateDocument;

import de.escidoc.schemas.contentmodel.x01.ContentModelDocument;
import de.escidoc.schemas.contentmodel.x01.ContentModelDocument.ContentModel;
import de.mpg.escidoc.faces.metadata.Metadata;
import de.mpg.escidoc.faces.metadata.ScreenConfiguration;

public class MdProfileVO 
{
    private String id = null;;
    private List<Metadata> metadataList = null;
    private String name = null;
    
    public MdProfileVO()
    {
	metadataList = new ArrayList<Metadata>();
    }
    
    public MdProfileVO(String name, List<Metadata> list)
    {
	this();
	this.metadataList = list;
	this.name = name;
    }

    public void init()
    {
	
    }
    
    /**
     * @return the metadataList
     */
    public List<Metadata> getMetadataList()
    {
        return metadataList;
    }

    /**
     * @param metadataList the metadataList to set
     */
    public void setMetadataList(List<Metadata> metadataList)
    {
        this.metadataList = metadataList;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }

    public void setId(String id) 
    {
	this.id = id;
    }
    
   public String getId() 
   {
   	return id;
   }

    
}
