package de.mpg.imeji.logic.ingest.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

import de.mpg.imeji.logic.ingest.jaxb.JaxbIngestProfile;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.MetadataProfile;
public class ProfileParser
{
	
    /**
     * Parse a profile Xml
     * @param profileXml
     * @return
     */
    public MetadataProfile parse(File profileXmlFile)
    {
        MetadataProfile mdp = null;
		
		try {
			mdp = new JaxbIngestProfile().unmarshalMdProfile(profileXmlFile);
		} catch (JAXBException e) {			
			e.printStackTrace();
		} catch (SAXException e) {		
			e.printStackTrace();
		}
		return mdp;
    }
    
    /**
     * Parse a list of item
     * 
     * @param itemListXml
     * @return
     */
    public List<MetadataProfile> parseList(File profileListXmlFile)
    {
        List<MetadataProfile> profileList = new ArrayList<MetadataProfile>();
        
		try {
			profileList = new JaxbIngestProfile().unmarshalMdProfiles(profileListXmlFile).getMetadataProfile();
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		
        return profileList;
    }
    
}
