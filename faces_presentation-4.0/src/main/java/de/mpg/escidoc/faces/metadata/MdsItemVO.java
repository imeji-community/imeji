package de.mpg.escidoc.faces.metadata;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.myfaces.trinidad.model.ChildPropertyTreeModel;
import org.apache.myfaces.trinidad.model.TreeModel;
import org.apache.xmlbeans.XmlException;
import org.purl.escidoc.schemas.genericMetadata.records.x01.MdRecordsDocument;
import org.purl.escidoc.schemas.genericMetadata.records.x01.MdRecordDocument.MdRecord;
import org.purl.escidoc.schemas.genericMetadata.records.x01.MdRecordsDocument.MdRecords;

import de.mpg.escidoc.faces.metadata.helper.MdRecordsHelper;
import de.mpg.escidoc.faces.metadata.wrapper.MetadataWrapped;

public class MdsItemVO
{
    /**
     * The screen Manager.
     */
    private ScreenConfiguration screen = null;
    
    /**
     * The generic md records after XSLT.
     */
    private MdRecords genericMdRecords = null;
    
    /**
     * The tree of Metadata of the md-records
     */
    private TreeModel tree = null;
    
    /**
     * The first md-record of the md-records.
     */
    private MdRecord firstMdRecord = null;
    
    /**
     * The list of the metadata of the first md-record.
     */
    private List<Metadata> metadata = null;
    
    /**
     * A map of the values of the first md-record.
     * The key are the name of the medatata.
     */
    private Map<String, String> value = null;
    
    /**
     * A map of the labels of the first md-record.
     * The key are the name of the medatata.
     */
    private Map<String, String> label = null;
    
    /**
     * A list of all description (i.e md-record type) element of the first md-record.
     * Each description element is described a a tree.
     */
    private List<ChildPropertyTreeModel> descriptionTreeList = null; 
    
    protected MdsItemVO()
    {
        metadata = new ArrayList<Metadata>();
        value = new HashMap<String, String>();
        label = new HashMap<String, String>();
    }
   
   
    /**
     * Initialize the md-records of the item from the xml containing md-records.
     * @param mdrXml
     */
    public MdsItemVO(String mdrXml)
    {
        this();
    	// First initialization
        descriptionTreeList = new ArrayList<ChildPropertyTreeModel>();
        MdRecordsHelper mdrh = new MdRecordsHelper();
        String genericMdRecordsXml = null;    
            
        // XSLT to transform a specific md-records into a generic md-records
        try
        {
            genericMdRecordsXml = mdrh.transformToGenericMdRecord(mdrXml);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error transforming md-records", e);
        }
        
        // Parse the generic md-records
        try
        {
            MdRecordsDocument mdrDoc = MdRecordsDocument.Factory.parse(genericMdRecordsXml);
            genericMdRecords = mdrDoc.getMdRecords();
        }
        catch (XmlException e)
        {
            throw new RuntimeException("Error parsing generic md-records", e);
        }
        
        // Read the first md-record
        firstMdRecord = genericMdRecords.getMdRecordArray(0);
        System.out.println(firstMdRecord.xmlText());
        
        // Initialize the screen manager
        screen = new ScreenConfiguration(firstMdRecord);
        
        // Initialize a MetadataWrapped to prepare tree initialization 
        
        for (int i = 0; i < screen.getParent().sizeOfStatementArray(); i++)
        {
            MetadataWrapped mdw = new MetadataWrapped(screen.getParent().getStatementArray(i));
            
            mdw = prepareFormular(mdw);
            // Initialize the tree
            ChildPropertyTreeModel m = new ChildPropertyTreeModel(mdw, "child");
            
            // Add this tree to the list of tree.
            descriptionTreeList.add(m);
        }
        
    }
    
    /**
     * Set all the id of the MetadataWrapped with the correct indexes.
     * @param mdw
     * @return
     */
    private MetadataWrapped prepareFormular(MetadataWrapped mdw)
    {
        Metadata metadata = screen.getMdMap().get( mdw.getName() );
        
        if (metadata != null)
        {
              mdw.getNode().setId(metadata.getIndex());
              mdw.setIndex(metadata.getIndex());
              mdw.setGroup("title");
        }
        
        for (int i = 0; i < mdw.getChild().size(); i++)
        {
            Metadata md = screen.getMdMap().get( mdw.getChild().get(i).getName() );
            mdw.getChild().get(i).getNode().setId(md.getIndex());
            
            value.put(md.getIndex(), md.getSimpleValue());
            label.put(md.getIndex(), md.getLabel());
            
            if (mdw.getChild().get(i).hasChild())
            {
                mdw.getChild().set(i, prepareFormular(mdw.getChild().get(i)));
            }
        }
        
        return mdw;
    }
    

    /**
     * The list of the metadata.
     * For example: Emotion, Age, etc.
     * @return
     */
    public List<Metadata> getMetadataList()
    {
        return metadata;
    }

    /**
     * The list of the metadata.
     * For example: Emotion, Age, etc.
     * @param metadata
     */
    public void setMetadataList(List<Metadata> metadata)
    {
        this.metadata = metadata;
    }
    
    /**
     * Get the value of the metadata with its name.
     * Works if the metadata is in the first md-record.
     * @param MdName.
     * @return value as String.
     */
    public String getValue(String MdName)
    {
        for (int i = 0; i < firstMdRecord.sizeOfDescriptionArray(); i++)
        {
            for (int j = 0; j < firstMdRecord.getDescriptionArray(i).sizeOfStatementArray(); j++)
            {
                if ( firstMdRecord.getDescriptionArray(i).getStatementArray(j).getId().equals(MdName))
                {
                    return  firstMdRecord.getDescriptionArray(i).getStatementArray(j).getValueArray(0);
                }
            }
        }
                
        return null;
    }
    
    /**
     * Get the value of the metadata according to it's position in the list.
     * @param mdPosition.
     * @return value as String.
     */
    public String getValue(int mdPosition)
    {
        return null;
    }

    public Map<String, String> getValue()
    {
        return value;
    }

    public void setValue(Map<String, String> value)
    {
        this.value = value;
    }

    public Map<String, String> getLabel()
    {
        return label;
    }

    public void setLabel(Map<String, String> label)
    {
        this.label = label;
    }

    public TreeModel getTree()
    {
        return tree;
    }


    public void setTree(TreeModel tree)
    {
        this.tree = tree;
    }


    public MdRecords getGenericMdRecords()
    {
        return genericMdRecords;
    }


    public void setGenericMdRecords(MdRecords genericMdRecords)
    {
        this.genericMdRecords = genericMdRecords;
    }


    public MdRecord getFirstMdRecord()
    {
        return firstMdRecord;
    }


    public void setFirstMdRecord(MdRecord firstMdRecord)
    {
        this.firstMdRecord = firstMdRecord;
    }


    public List<ChildPropertyTreeModel> getDescriptionTreeList()
    {
        return descriptionTreeList;
    }


    public void setDescriptionTreeList(List<ChildPropertyTreeModel> descriptionTreeList)
    {
        this.descriptionTreeList = descriptionTreeList;
    }


    public ScreenConfiguration getScreen()
    {
        return screen;
    }


    public void setScreen(ScreenConfiguration screen)
    {
        this.screen = screen;
    }



    
}
