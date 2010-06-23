package de.mpg.escidoc.faces.metadata.wrapper;

import java.util.ArrayList;
import java.util.List;

import org.purl.escidoc.schemas.genericMetadata.metadata.x01.StatementType;

/**
 * Extends functionalities for StatementType.
 * @author saquet
 *
 */
public class StatementWrapped
{
    StatementType statement = null;
    String index = null;
    String id = null;
    
    public StatementWrapped(StatementType statement)
    {
        this.statement = statement;
        id = statement.getId();
    }
    
    public List<StatementWrapped> getChildList()
    {
        List<StatementWrapped> list = new ArrayList<StatementWrapped>();
        
        int size = statement.sizeOfStatementArray();
        
        for (int i = 0; i < statement.sizeOfStatementArray(); i++)
        {
            list.add(new StatementWrapped(statement.getStatementArray(i)));
        }
        
        return list;
    }
    
    public boolean hasChild()
    {
        if (statement.sizeOfStatementArray() > 0 )
        {
            return true;            
        }
        
        return false;
    }

    public String getIndex()
    {
        return index;
    }

    public void setIndex(String index)
    {
        this.index = index;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public StatementType getStatement()
    {
        return statement;
    }

    public void setStatement(StatementType statement)
    {
        this.statement = statement;
    }
}
