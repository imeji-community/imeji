package de.mpg.imeji.logic.ingest.parser;

import java.io.File;

import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

import de.mpg.imeji.logic.ingest.jaxb.JaxbIngestProfile;
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
        MetadataProfile mdp = new MetadataProfile();
		
		try {
			mdp = new JaxbIngestProfile().unmarshalMdProfile(profileXmlFile);
		} catch (JAXBException e) {			
			e.printStackTrace();
		} catch (SAXException e) {		
			e.printStackTrace();
		}
		return mdp;
    }   
}
