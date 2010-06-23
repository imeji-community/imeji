package de.mpg.escidoc.faces.metadata.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.myfaces.trinidad.model.ChildPropertyTreeModel;
import org.purl.escidoc.schemas.genericMetadata.metadata.x01.DescriptionDocument.Description;
import org.purl.escidoc.schemas.genericMetadata.records.x01.MdRecordsDocument;
import org.purl.escidoc.schemas.genericMetadata.records.x01.MdRecordsDocument.MdRecords;

import de.mpg.escidoc.faces.metadata.Metadata;
import de.mpg.escidoc.faces.metadata.ScreenConfigurationSession;
import de.mpg.escidoc.faces.util.BeanHelper;

public class ScreenManagerHelper
{
    /**
     * The screen configuration file.
     */
    //private static final String SCREEN_CONFIG_FILE = "C:\\Project\\Faces_Project\\workingDirs\\faces\\faces\\faces_ear\\src\\main\\resources\\screenConfig.xml";
    private static final String SCREEN_CONFIG_FILE = "screenConfig.xml";
    
    /**
     * The screen configuration of the whole md-records.
     */
    private MdRecords screenConfig = null;
    
    /**
     * The list of the metadata used by the application.
     */
    private List<Metadata> mdList = null;
    
    /**
     * The Map of the metadata used by the application.
     */
    private Map<String, Metadata> mdMap = null;
    
    /**
     * Define the description currently used by the application.
     */
    private Description descriptionScreen = null;
        
    /**
     * The tree of metadata.
     */
    private ChildPropertyTreeModel mdTree = null; 
    
    private List<String> indexList = null;
    
    private ScreenConfigurationSession session = null;
    
    /**
     * Constructor for the default md-record, default resource-class and default screen-id.
     */
    public ScreenManagerHelper()
    {
        try
        {            
            session = (ScreenConfigurationSession)BeanHelper.getSessionBean(ScreenConfigurationSession.class);
            
            MdRecordsDocument document = session.getMdRecordsDocument();
            screenConfig = document.getMdRecords();
            
            // Read the default screen configuration for the default MdRecord
            descriptionScreen = screenConfig.getMdRecordArray(0).getDescriptionArray(0);
            
            // Initialize the list of metadata
            initMdList();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Constructor for one screen id 8for example search, view, edit...).
     * @param screenId
     */
    public ScreenManagerHelper(String screenId)
    {
        try
        { 
            session = (ScreenConfigurationSession)BeanHelper.getSessionBean(ScreenConfigurationSession.class);
            
            MdRecordsDocument document = session.getMdRecordsDocument();
            
            screenConfig = document.getMdRecords();
            
            // Select the correct description.
            for (int i = 0; i < screenConfig.getMdRecordArray(0).sizeOfDescriptionArray(); i++)
            {
                if (screenId.equals(screenConfig.getMdRecordArray(0).getDescriptionArray(i).getScreenId()))
                {
                    descriptionScreen = screenConfig.getMdRecordArray(0).getDescriptionArray(i);
                }
            }
            
            // Initialize the list of metedata.
            initMdList();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Constructor for one md-record and one resource-class and one screen-id.
     * @param resourceClass
     */
    public ScreenManagerHelper(String mdRecordName, String resourceClass, String screenID)
    {
        try
        {            
            session = (ScreenConfigurationSession)BeanHelper.getSessionBean(ScreenConfigurationSession.class);
            
            MdRecordsDocument document = session.getMdRecordsDocument();
            screenConfig = document.getMdRecords();
            
            for (int i = 0; i < screenConfig.getMdRecordArray().length; i++)
            {
                for (int j = 0; j < screenConfig.getMdRecordArray(i).getDescriptionArray().length; j++)
                {
                    if (screenID.equals(screenConfig.getMdRecordArray(i).getDescriptionArray(j).getScreenId())
                            && resourceClass.equals(screenConfig.getMdRecordArray(i).getDescriptionArray(j).getResourceClass()))
                    {
                        descriptionScreen =  screenConfig.getMdRecordArray(i).getDescriptionArray(j);
                    }
                }
            }
            
            initMdList();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Initialize the metadata List and the metadata map.
     */
    private void initMdList()
    {
        mdList = new ArrayList<Metadata>();
        mdMap = new HashMap<String, Metadata>();
        indexList = new ArrayList<String>();
    }

    /**
     * Get the configuration of the complete screen configuration of a md-records.
     * @return MdRecords
     */
    public MdRecords getScreenConfig()
    {
        return screenConfig;
    }

    /**
     * Set the complete screen configuration of a md-records-
     * @param screenConfig Mdrecords
     */
    public void setScreenConfig(MdRecords screenConfig)
    {
        this.screenConfig = screenConfig;
    }

    public List<Metadata> getMdList()
    {
        return mdList;
    }

    public void setMdList(List<Metadata> mdList)
    {
        this.mdList = mdList;
    }

    public Map<String, Metadata> getMdMap()
    {
        return mdMap;
    }

    public void setMdMap(Map<String, Metadata> mdMap)
    {
        this.mdMap = mdMap;
    }

    public Description getDescriptionScreen()
    {
        return descriptionScreen;
    }

    public void setDescriptionScreen(Description descriptionScreen)
    {
        this.descriptionScreen = descriptionScreen;
    }

    public ChildPropertyTreeModel getMdTree()
    {
        return mdTree;
    }
    
    public void setMdTree(ChildPropertyTreeModel mdTree)
    {
        this.mdTree = mdTree;
    }

    public List<String> getIndexList()
    {
        return indexList;
    }

    public void setIndexList(List<String> indexList)
    {
        this.indexList = indexList;
    }
}
