package de.mpg.imeji.mdProfile;

import java.util.List;

import org.purl.escidoc.schemas.genericMetadata.metadata.x01.StatementType;

import de.mpg.imeji.vo.MdProfileVO;

public class MdProfileBean
{
    private MdProfileVO mdProfile = null;
    private List<StatementType> statements = null;

    public MdProfileBean()
    {
        mdProfile = new MdProfileVO();
    }

    public void init()
    {
        
    }
    
    public void addStatement()
    {
        
    }
    
    public void removeStatement()
    {
        
    }

    /**
     * @return the mdProfile
     */
    public MdProfileVO getMdProfile()
    {
        return mdProfile;
    }

    /**
     * @param mdProfile the mdProfile to set
     */
    public void setMdProfile(MdProfileVO mdProfile)
    {
        this.mdProfile = mdProfile;
    }

    /**
     * @return the metadataBeans
     */
    public List<StatementType> getStatements()
    {
        return statements;
    }

    /**
     * @param metadataBeans the metadataBeans to set
     */
    public void setStatements(List<StatementType> statements)
    {
        this.statements = statements;
    }
}
