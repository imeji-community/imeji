package de.mpg.escidoc.faces.metadata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.xmlbeans.XmlAnySimpleType;
import org.purl.escidoc.schemas.genericMetadata.metadata.x01.StatementType;
import org.purl.escidoc.schemas.genericMetadata.metadata.x01.DescriptionDocument.Description;
import org.purl.escidoc.schemas.genericMetadata.records.x01.MdRecordDocument.MdRecord;
import org.purl.escidoc.schemas.genericMetadata.records.x01.MdRecordsDocument.MdRecords;

import de.mpg.escidoc.faces.metadata.helper.ScreenManagerHelper;


public class ScreenConfiguration
{    
    /**
     * The id of the screen (for example view, search...).
     */
    private String screenId = null;
    
    /**
     * The resource class of the screen (for example face-item, medium, diamonds...).
     */
    private String resourceClass = null;
        
    /**
     * The helper.
     */
    private ScreenManagerHelper helper = null;
    
    /**
     *  The Map of the metadata with values
     */
    private Map<String, Metadata> mdMap = null;
    
    /**
     *  The List of the metadata with values.
     */
    private List<Metadata> mdList = null;
    
    /**
     * The node (StatementType) parent 
     */
    private StatementType parent = null;
    
    
    /**
     * Default constructor.
     * The Screen manager is only initialized out of screenConfig.xml (i.e no values)
     */
    public ScreenConfiguration()
    {
        // Initialize helper for mdr.
        helper = new ScreenManagerHelper(); 
        
        // Set default resource class
        this.resourceClass = helper.getScreenConfig().getMdRecordArray(0)
                                    .getDescriptionArray(0).getResourceClass();
        
        // Set default screen id.
        this.screenId = helper.getScreenConfig().getMdRecordArray(0)
                                    .getDescriptionArray(0).getScreenId();
        
        // Initialize the first statement of the screen manager
        initializeParent(null, helper.getDescriptionScreen());
    }
    
    /**
     * Default Constructor for one view
     * The Screen manager is only initialized out of screenConfig.xml (i.e no values)
     * @param screenId - the id of the screen (view, search, edit...)
     */
    public ScreenConfiguration(String screenId)
    {
        // Initialize helper for mdr.
        helper = new ScreenManagerHelper(screenId);   
        
        // Set default resource class
        this.resourceClass = helper.getScreenConfig().getMdRecordArray(0)
                                    .getDescriptionArray(0).getResourceClass();
        
        // Set default screen id.
        this.screenId = screenId;
        
        // Initialize the first statement of the screen manager
        initializeParent(null, helper.getDescriptionScreen());
    }
    
    
    /**
     * Constructor initialized with values out of a metadata record.
     * View and resource are initialized by default (first ones in the screenConfig.xml)
     * @param mdr - the metadata record.
     */
    public ScreenConfiguration(MdRecord mdr)
    {
        // Initialize helper for mdr.
        helper = new ScreenManagerHelper(); 
        
        // Set default resource class
        resourceClass = helper.getScreenConfig().getMdRecordArray(0)
                                    .getDescriptionArray(0).getResourceClass();
        
        // Set default screen id.
        screenId = helper.getScreenConfig().getMdRecordArray(0)
                                    .getDescriptionArray(0).getScreenId();
        
        // Initialize the first statement of the screen manager
        initializeParent(mdr, helper.getDescriptionScreen());
    }
    
    /**
     * Constructor for a specific screen and a specific resource.
     * Values are initialized by the values of the metadata record.
     * @param screenId - the id of the screen (view, search, edit...).
     * @param resourceClass - the class of the resource (face-item, medium, diamonds...).
     * @param mdr - the metadata record.
     */
    public ScreenConfiguration(MdRecord mdr, String screenId, String resourceClass)
    {
        // Initialize helper for md-record, resource class and screen id.
        helper = new ScreenManagerHelper(mdr.getName().getStringValue(), resourceClass, screenId);
        
        // Set screen id
        this.screenId = screenId;
        
        // Set resource class
        this.resourceClass = resourceClass;
        
        // Initialize the first statement of the screen manager
        initializeParent(mdr, helper.getDescriptionScreen());
    }    
    
    /**
     * Create a statement out of description defined in the screenConfig.xml.
     * The statement is initialized with the values of the MdRecords.
     * @param mdr
     * @param description 
     */
    private void initializeParent(MdRecord mdr, Description d)
    {
        mdList = new ArrayList<Metadata>();
        mdMap = new HashMap<String, Metadata>();
       
        // Create the Node
        parent = StatementType.Factory.newInstance();
        
        // Set up parent statement.
        parent.setId(resourceClass);
        
        //TODO : Check if there is a label for the Resource class
        parent.setLabel(XmlAnySimpleType.Factory.newValue(resourceClass));
        
        // Set the statement array
        parent.setStatementArray(d.getStatementArray());
                
        // Set the Gui component of the parent node: Should have no gui component by default
        parent.setGuiComponent(XmlAnySimpleType.Factory.newValue("none"));
        
        // Set the Ids of all the node
        Metadata mdInit = new Metadata(parent);
        mdInit.setGroup(parent.getId());
        mdInit.setIndex("escidoc." + parent.getId());
        mdMap.put(mdInit.getName(), mdInit);
        parent.setStatementArray(setUniqueIds(parent).getStatementArray());
        
        // Set the values of all the node (if the md-record is not null)
        if (mdr != null)
        {
            //Define a local Description for the mdRecord 
            Description valuesDescription = null;
            
            // Get values out of the mdRecords according to the resource class
            for (int i = 0; i < mdr.sizeOfDescriptionArray(); i++)
            {
                if (resourceClass.equals(mdr.getDescriptionArray(i).getResourceClass()))
                {
                    valuesDescription = mdr.getDescriptionArray(i);
                }
            }
            
            // Set the values of the statement
            for (int i = 0; i < parent.sizeOfStatementArray(); i++)
            {
                if (parent.getStatementArray(i).sizeOfStatementArray() == 0)
                {
                    // If no child set the value
                    String[] index = mdMap.get(parent.getStatementArray(i).getId()).getIndex().split("\\.");
                    
                    if (index.length > 2)
                    {
                        System.arraycopy(index, 2, index, 0, index.length - 2); 
                        parent
                            .getStatementArray(i)
                                .setValueArray(
                                        extractValueArray(valuesDescription.getStatementArray(), index));
                    }
                   
                   if ( parent.getStatementArray(i).sizeOfValueArray() > 0 )
                   {
                       mdMap
                           .get(parent.getStatementArray(i).getId() )
                               .setSimpleValue( parent.getStatementArray(i).getValueArray(0) );
                   }
                }
                else if (parent.getStatementArray(i).sizeOfStatementArray() > 0)
                {
                    parent
                    .getStatementArray(i)
                        .setStatementArray(
                                populateListofStatement(
                                        parent.getStatementArray(i).getStatementArray()
                                        , valuesDescription.getStatementArray()));
                }
            }
        }
    }
    
    
    /**
     * Set values of StatementType through its whole xml tree.
     * @param parent
     * @param mdr
     * @return
     */
    private StatementType[] populateListofStatement(StatementType[] parent, StatementType[] mdr)
    {
        for (int i = 0; i < parent.length; i++)
        {
            if (parent[i].sizeOfStatementArray() > 0)
            {
                parent[i]
                       .setStatementArray(
                               populateListofStatement(parent[i].getStatementArray(),mdr));               
            }
            else
            {
                String[] index = mdMap.get(parent[i].getId()).getIndex().split("\\.");
                
                if (index.length > 2)
                {
                    System.arraycopy(index, 2, index, 0, index.length - 2); 
                }
                
                parent[i].setValueArray(extractValueArray(mdr, index));
                
                if (parent[i].sizeOfValueArray() > 0 )
                {
                    mdMap
                        .get(parent[i].getId())
                            .setSimpleValue(parent[i].getValueArray(0));
                }
            }
        }
        
        return parent;
    }
    
    /**
     * Extract the value for an index (which is created according to md-record tree) its value out of the md-record.
     * @param mdr
     * @param index
     * @return
     */
    private String[] extractValueArray(StatementType[] mdr, String[] index)
    {   
        StatementType st = null;
        String[] value = null;;
        
        for (int i = 0; i < mdr.length; i++)
        {
            if (index[0].equals(mdr[i].getId()))
            {
                st = mdr[i];
            }
        }
        
        if (st != null)
        {
            if (st.sizeOfStatementArray() > 0)
            {
                System.arraycopy(index, 1, index, 0, index.length - 1);
                value = extractValueArray(st.getStatementArray(), index);
            }
            else 
            {
                value = st.getValueArray();
            }
        }
        
        return value;
     }
    
    /**
     * Set unique IDs to a statement tree so that the hierarchy of the metadata is also defined in IDs.  
     * @param parent
     * @return
     */
    public StatementType setUniqueIds(StatementType parent)
    {      
         
        for (int i = 0; i < parent.sizeOfStatementArray(); i++)
        {            
            String index = null;
            String group = null;
            Metadata mdParent = mdMap.get(parent.getId());
            
            // Set the index and the group according to screen configuration
            if ("group".equals(parent.getStatementArray(i).getGuiComponent().getStringValue()))
            {
                group = mdParent.getGroup() + "." +  parent.getStatementArray(i).getId();
                // A group doesn't take in account the it's own id for the index
                index = mdParent.getIndex();
            }
            else 
            {
                
                group = mdParent.getGroup();
                
                if (!"group".equals(mdParent.getGuiComponent()))
                {
                    group += "."  + parent.getStatementArray(i).getId();
                }
                
                index = mdParent.getIndex() + "." + parent.getStatementArray(i).getId();
            }
            
            // Set the id of the element
            parent
                .getStatementArray(i)
                    .setId
                        (parent.getId() + "." + parent.getStatementArray(i).getId());
            
            // Create Metadata out of the StatementType
            Metadata md = new Metadata(parent.getStatementArray(i));
            md.setIndex(index);
            md.setGroup(group);
            
            //Initialize lists
            mdMap.put(md.getName(), md);
            mdList.add(md);
            helper.getMdMap().put(md.getIndex(), md);
            helper.getMdList().add(md);
            helper.getIndexList().add(md.getIndex());
            
            // If child, call method again for the child
            if (parent.getStatementArray(i).sizeOfStatementArray() > 0)
            {
                parent
                    .setStatementArray
                        (i
                        , setUniqueIds(parent.getStatementArray(i)));
            }
        }
        
        return parent;
    }
    
    /**
     * Set the value of a statementType with the correct StatementType from a StatementType[].
     * Checking performed with ID: Should be done (when FW ready) with namespace.
     * @param parent
     * @param mdr
     * @return
     */
    private StatementType checkIdAndSetValue(StatementType parent, StatementType[] mdr)
    {
        String id1 = parent.getId();
        
        for (int i = 0; i < mdr.length; i++)
        {
            String id2 = mdr[i].getId();
            
            if (id1.substring(id1.length() - id2.length(), id1.length()).equals(id2))
            {
                parent.setValueArray(mdr[i].getValueArray());
            }
        }
        
        return parent;
    }
   
       
    /**
     * TODO
     * Validate a mdRecords according to the screen configuration. 
     * @param mdRecords
     * @return
     */
    public boolean validate(MdRecords mdRecords)
    {
        return true;
    }

    public String getScreenId()
    {
        return screenId;
    }

    public void setScreenId(String screenId)
    {
        this.screenId = screenId;
    }

    public String getResourceClass()
    {
        return resourceClass;
    }

    public void setResourceClass(String resourceClass)
    {
        this.resourceClass = resourceClass;
    }

    /**
     * Get The metadata Map with md-record values
     * @return 
     */
//    public Map<String, Metadata> getMdMap()
//    {
//        return mdMap;
//    }
//    
//    /**
//     * Set The metadata Map with md-record values
//     * @param mdMap
//     */
//    public void setMdMap(Map<String, Metadata> mdMap)
//    {
//        this.mdMap = mdMap;
//    }
//
//    public List<Metadata> getMdList()
//    {
//        return mdList;
//    }
//
//    public void setMdList(List<Metadata> mdList)
//    {
//        this.mdList = mdList;
//   }

    public StatementType getParent()
    {
        return parent;
    }

    public void setParent(StatementType parent)
    {
        this.parent = parent;
    }

    public ScreenManagerHelper getHelper()
    {
        return helper;
    }

    public void setHelper(ScreenManagerHelper helper)
    {
        this.helper = helper;
    }

    public Map<String, Metadata> getMdMap()
    {
        return mdMap;
    }

    public void setMdMap(Map<String, Metadata> mdMap)
    {
        this.mdMap = mdMap;
    }

    public List<Metadata> getMdList()
    {
        return mdList;
    }

    public void setMdList(List<Metadata> mdList)
    {
        this.mdList = mdList;
    }
}
