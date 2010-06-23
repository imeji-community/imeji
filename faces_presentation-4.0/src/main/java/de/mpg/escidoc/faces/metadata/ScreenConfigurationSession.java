package de.mpg.escidoc.faces.metadata;

import org.purl.escidoc.schemas.genericMetadata.records.x01.MdRecordsDocument;

import de.mpg.escidoc.services.common.util.ResourceUtil;

public class ScreenConfigurationSession
{
    /**
     * The path of the screen configuration file.
     */
    //private static final String SCREEN_CONFIG_FILE = "C:\\Project\\Faces_Project\\workingDirs\\faces\\faces\\faces_ear\\src\\main\\resources\\screenConfig.xml";
    private static final String SCREEN_CONFIG_FILE = "screenConfig.xml";
        
    /**
     * The screen configuration file.
     */
    private MdRecordsDocument mdRecordsDocument = null;
    
    
    public ScreenConfigurationSession()
    {
    	readConfig();
    }

    public MdRecordsDocument getMdRecordsDocument()
    {
        return mdRecordsDocument;
    }

    public void setMdRecordsDocument(MdRecordsDocument mdRecordsDocument)
    {
        this.mdRecordsDocument = mdRecordsDocument;
    }
    
    /**
     * Read the configuration from the screenConfig.xml
     */
    public void readConfig()
    {
    	try
        {
            mdRecordsDocument = MdRecordsDocument.Factory.parse(ResourceUtil.getResourceAsFile(SCREEN_CONFIG_FILE));
        }
        catch (Exception e)
        {
           throw new RuntimeException(e);
        }
    }
}
