package de.mpg.imeji.vo;

import java.util.ArrayList;
import java.util.List;

import org.purl.escidoc.schemas.genericMetadata.metadata.x01.StatementType;


public class MdProfileVO
{
    private List<StatementType> metadataList = null;
    private String name = null;
    private String description;

    public MdProfileVO()
    {
        metadataList = new ArrayList<StatementType>();
    }

    public MdProfileVO(String name, List<StatementType> list)
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
    public List<StatementType> getMetadataList()
    {
        return metadataList;
    }

    /**
     * @param metadataList the metadataList to set
     */
    public void setMetadataList(List<StatementType> metadataList)
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

    /**
     * @return the description
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description)
    {
        this.description = description;
    }


}
