package de.mpg.imeji.logic.ingest.factory;

import java.io.FileNotFoundException;

import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

import de.mpg.imeji.logic.ingest.jaxb.JaxbIngestProfile;
import de.mpg.imeji.logic.vo.MetadataProfile;

public class ItemSchemaFactory
{
    public Object create(MetadataProfile mdp)
    {
        // Here is the schema created according to the profile
        // It must return the schema object instead of a simple Object
    	//TODO
		
    	String xmlOutputFilename = "mdp-output.xml";
    	
		try {			
			new JaxbIngestProfile().marshalMdProfile(xmlOutputFilename, mdp);
			
		} catch (JAXBException e) {			
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
        return null;
    }
}
