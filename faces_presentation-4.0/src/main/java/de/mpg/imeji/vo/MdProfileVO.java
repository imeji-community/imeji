package de.mpg.imeji.vo;

import java.util.ArrayList;
import java.util.List;

import org.purl.escidoc.schemas.genericMetadata.metadata.x01.StatementType;


public class MdProfileVO
{
    private List<StatementVO> statements = null;
    private String name = null;
    private String description;

    public MdProfileVO()
    {
        statements = new ArrayList<StatementVO>();
    }

    public MdProfileVO(String name, List<StatementVO> list)
    {
        this();
        this.statements = list;
        this.name = name;
    }

    public void init()
    {
    }

    /**
     * @return the metadataList
     */
    public List<StatementVO> getStatements()
    {
        return statements;
    }

    /**
     * @param metadataList the metadataList to set
     */
    public void setStatements(List<StatementVO> statements)
    {
        this.statements = statements;
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
