package de.mpg.escidoc.faces.metadata.wrapper;

import java.util.ArrayList;
import java.util.List;

import org.purl.escidoc.schemas.genericMetadata.metadata.x01.StatementType;

import de.mpg.escidoc.faces.metadata.Metadata;

/**
 * Wrapped Metadata.
 * Use for tree representation.
 * @author saquet
 *
 */
public class MetadataWrapped extends Metadata
{
    
    /**
     * Constructor for MetadataWrapped.
     * @param parent Description or StatementType.
     */
    public MetadataWrapped(Object parent)
    {
        super(parent);
    }
        
    public MetadataWrapped(Metadata md)
    {
        super(md);
    }
    
    /**
     * Get the list of child of the node
     */
    public List<MetadataWrapped> getChild()
    {
       List<MetadataWrapped> listChild =  new ArrayList<MetadataWrapped>();
     
       for (int i = 0; i <  super.getNode().getStatementArray().length; i++)
       {
            listChild.add(new MetadataWrapped(super.getNode().getStatementArray(i)));
       }
       
       return listChild;
    }


    /**
     * Tests to see if the parent node has at least one child.
     */
    public boolean hasChild()
    {
        StatementType[] md = super.getNode().getStatementArray();
        
        if (md.length > 0)
        {
            return true;
        }
        
        return false;
    }
}
