package de.mpg.escidoc.faces.mdProfile;

import java.util.ArrayList;
import java.util.List;

import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import javax.xml.validation.ValidatorHandler;

import de.escidoc.schemas.contentmodel.x01.ContentModelDocument;
import de.escidoc.schemas.contentmodel.x01.ContentModelDocument.ContentModel;
import de.mpg.escidoc.faces.metadata.Metadata;
import de.mpg.escidoc.faces.metadata.ScreenConfiguration;
import de.mpg.escidoc.faces.metadata.helper.ScreenManagerHelper;

public class MdProfileVO 
{
    private ContentModel contentModel = null;
    private List<Metadata> metadataList = null;
    private ScreenConfiguration screenConfiguration = null;
    private String name = null;
    
    public MdProfileVO()
    {
    	ContentModelDocument cmd = ContentModelDocument.Factory.newInstance();
	cmd.addNewContentModel();
	contentModel = cmd.getContentModel();
	metadataList = new ArrayList<Metadata>();
	screenConfiguration = new ScreenConfiguration();
    }
    
    public MdProfileVO(String name, List<Metadata> list)
    {
	this();
	this.metadataList = list;
	this.name = name;
	
	contentModel.addNewMdRecordDefinitions();
	contentModel.getMdRecordDefinitions().getMdRecordDefinitionArray(0).setName(this.name);

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

    
}
