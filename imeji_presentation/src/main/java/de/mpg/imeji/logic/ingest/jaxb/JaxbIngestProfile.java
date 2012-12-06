/**
 * 
 */
package de.mpg.imeji.logic.ingest.jaxb;

import java.io.File;
import java.io.FileNotFoundException;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import de.mpg.imeji.logic.ingest.jaxb.interfaces.IJaxbIngestProfile;
import de.mpg.imeji.logic.ingest.jaxb.interfaces.IJaxbItem;
import de.mpg.imeji.logic.ingest.jaxb.interfaces.IJaxbItems;
import de.mpg.imeji.logic.ingest.jaxb.interfaces.IJaxbMetadataProfile;
import de.mpg.imeji.logic.ingest.jaxb.interfaces.IJaxbMetadataProfiles;
import de.mpg.imeji.logic.ingest.vo.IngestProfile;
import de.mpg.imeji.logic.ingest.vo.Items;
import de.mpg.imeji.logic.ingest.vo.MetadataProfiles;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.MetadataProfile;

/**
 * @author hnguyen
 */
public class JaxbIngestProfile extends JaxbUtil implements IJaxbItem, IJaxbItems, IJaxbMetadataProfile,
        IJaxbMetadataProfiles, IJaxbIngestProfile
{
    private Logger logger = Logger.getLogger(JaxbIngestProfile.class);

    /**
	 * 
	 */
    public JaxbIngestProfile()
    {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void marshalItem(String xmlFilename, Item item) throws JAXBException, SAXException, FileNotFoundException
    {
        String xsdFilename = IJaxbItem.xsdFilename;
        
        if(xsdFilename == null || xsdFilename.isEmpty()) {
			logger.error("\nItem Schema file not provided!");
			return;
		}
		
        if(xmlFilename == null || xmlFilename.isEmpty()) {
			logger.error("\nItem XML file not provided!");
			return;
		}
        super.marshal(xsdFilename, xmlFilename, item);
    }

    @Override
    public Item unmarshalItem(String xmlFilename) throws JAXBException, SAXException
    {
        String xsdFilename = IJaxbItem.xsdFilename;
        
        if(xsdFilename == null || xsdFilename.isEmpty()) {
			logger.error("\nItem Schema file not provided!");
			return null;
		}
		
        if(xmlFilename == null || xmlFilename.isEmpty()) {
			logger.error("\nItem XML file not provided!");
			return null;
		}

        return unmarshal(xsdFilename, xmlFilename, Item.class);
    }

	@Override
	public void marshalItem(File xmlFile, Item item) throws JAXBException, SAXException, FileNotFoundException
	{
		String xsdFilename = IJaxbItem.xsdFilename;
		
		if(xsdFilename == null || xsdFilename.isEmpty()) {
			logger.error("\nItem Schema file not provided!");
			return;
		}
		
        if(xmlFile == null) {
			logger.error("\nItem XML file not provided!");
			return;
		}
		
        super.marshal(xsdFilename, xmlFile, item);
	}

	@Override
	public Item unmarshalItem(File xmlFile) throws JAXBException, SAXException
	{
		String xsdFilename = IJaxbItem.xsdFilename;
		
		if(xsdFilename == null || xsdFilename.isEmpty()) {
			logger.error("\nItem Schema file not provided!");
			return null;
		}
		
        if(xmlFile == null) {
			logger.error("\nItem XML file not provided!");
			return null;
		}

        return unmarshal(xsdFilename, xmlFile, Item.class);
	}
    
    
    @Override
    public void marshalItems(String xmlFilename, Items items) throws JAXBException, SAXException, FileNotFoundException
    {
        String xsdFilename = IJaxbItems.xsdFilename;
        if(xsdFilename == null || xsdFilename.isEmpty()) {
			logger.error("\nItems Schema file not provided!");
			return;
		}
		
        if(xmlFilename == null || xmlFilename.isEmpty()) {
			logger.error("\nItems XML file not provided!");
			return;
		}
        super.marshal(xsdFilename, xmlFilename, items);
    }

    @Override
    public Items unmarshalItems(String xmlFilename) throws JAXBException, SAXException
    {
        String xsdFilename = IJaxbItems.xsdFilename;
        if(xsdFilename == null || xsdFilename.isEmpty()) {
			logger.error("\nItems Schema file not provided!");
			return null;
		}
		
        if(xmlFilename == null || xmlFilename.isEmpty()) {
			logger.error("\nItems XML file not provided!");
			return null;
		}
        return unmarshal(xsdFilename, xmlFilename, Items.class);
    }
    
	@Override
	public void marshalItems(File xmlFile, Items items) throws JAXBException, SAXException, FileNotFoundException {
		String xsdFilename = IJaxbItems.xsdFilename;
		if(xsdFilename == null || xsdFilename.isEmpty()) {
			logger.error("\nItems Schema file not provided!");
			return;
		}
		
        if(xmlFile == null) {
			logger.error("\nItems XML file not provided!");
			return;
		}
        super.marshal(xsdFilename, xmlFile, items);
	}

	@Override
	public Items unmarshalItems(File xmlFile) throws JAXBException, SAXException {
		String xsdFilename = IJaxbItems.xsdFilename;
		
		if(xsdFilename == null || xsdFilename.isEmpty()) {
			logger.error("\nItems Schema file not provided!");
			return null;
		}
		
		if (xmlFile == null) {
		    logger.error("\nItems XML file not provided!");
			return null;
		}
		
		return unmarshal(xsdFilename, xmlFile, Items.class);
	}

    @Override
    public void marshalMdProfile(String xmlFilename, MetadataProfile mdp) throws JAXBException, SAXException, FileNotFoundException
    {
        String xsdFilename = IJaxbMetadataProfile.xsdFilename;
        if(xsdFilename == null || xsdFilename.isEmpty()) {
			logger.error("\nMetadata profile Schema file not provided!");
			return;
		}
		
        if(xmlFilename == null || xmlFilename.isEmpty()) {
			logger.error("\nMetadata profile XML file not provided!");
			return;
		}
        super.marshal(xsdFilename, xmlFilename, mdp);
    }

    @Override
    public MetadataProfile unmarshalMdProfile(String xmlFilename) throws JAXBException, SAXException
    {
        String xsdFilename = IJaxbMetadataProfile.xsdFilename;
        if(xsdFilename == null || xsdFilename.isEmpty()) {
			logger.error("\nMetadata profile Schema file not provided!");
			return null;
		}
		
        if(xmlFilename == null || xmlFilename.isEmpty()) {
			logger.error("\nMetadata profile XML file not provided!");
			return null;
		}
        return super.unmarshal(xsdFilename, xmlFilename, MetadataProfile.class);
    }
    
	@Override
	public void marshalMdProfile(File xmlFile, MetadataProfile mdp) throws JAXBException, SAXException, FileNotFoundException {
		String xsdFilename = IJaxbMetadataProfile.xsdFilename;
		if(xsdFilename == null || xsdFilename.isEmpty()) {
			logger.error("\nMetadata profile Schema file not provided!");
			return;
		}
		
        if(xmlFile == null) {
			logger.error("\nMetadata profile XML file not provided!");
			return;
		}
		super.marshal(xsdFilename, xmlFile, mdp);
	}

	@Override
	public MetadataProfile unmarshalMdProfile(File xmlFile) throws JAXBException, SAXException {
		String xsdFilename = IJaxbMetadataProfile.xsdFilename;
		if(xsdFilename == null || xsdFilename.isEmpty()) {
			logger.error("\nMetadata profile Schema file not provided!");
			return null;
		}
		
        if(xmlFile == null) {
			logger.error("\nMetadata profile XML file not provided!");
			return null;
		}
        return super.unmarshal(xsdFilename, xmlFile, MetadataProfile.class);
	}

    @Override
    public void marshalMdProfiles(String xmlFilename, MetadataProfiles mdps) throws JAXBException, SAXException, FileNotFoundException
    {
        String xsdFilename = IJaxbMetadataProfiles.xsdFilename;
        if(xsdFilename == null || xsdFilename.isEmpty()) {
			logger.error("\nMetadata profiles Schema file not provided!");
			return;
		}
		
        if(xmlFilename == null || xmlFilename.isEmpty()) {
			logger.error("\nMetadata profiles XML file not provided!");
			return;
		}
        super.marshal(xsdFilename, xmlFilename, mdps);
    }

    @Override
    public MetadataProfiles unmarshalMdProfiles(String xmlFilename) throws JAXBException, SAXException
    {
        String xsdFilename = IJaxbMetadataProfiles.xsdFilename;
        if(xsdFilename == null || xsdFilename.isEmpty()) {
			logger.error("\nMetadata profiles Schema file not provided!");
			return null;
		}
		
        if(xmlFilename == null || xmlFilename.isEmpty()) {
			logger.error("\nMetadata profiles XML file not provided!");
			return null;
		}
        return super.unmarshal(xsdFilename, xmlFilename, MetadataProfiles.class);
    }
    
	@Override
	public void marshalMdProfiles(File xmlFile, MetadataProfiles mdps) throws JAXBException, SAXException, FileNotFoundException {
		String xsdFilename = IJaxbMetadataProfiles.xsdFilename;
		if(xsdFilename == null || xsdFilename.isEmpty()) {
			logger.error("\nMetadata profiles Schema file not provided!");
			return;
		}
		
        if(xmlFile == null) {
			logger.error("\nMetadata profiles XML file not provided!");
			return;
		}
        super.marshal(xsdFilename, xmlFile, mdps);
	}

	@Override
	public MetadataProfiles unmarshalMdProfiles(File xmlFile) throws JAXBException, SAXException {
		String xsdFilename = IJaxbMetadataProfiles.xsdFilename;
		if(xsdFilename == null || xsdFilename.isEmpty()) {
			logger.error("\nMetadata profiles Schema file not provided!");
			return null;
		}
		
        if(xmlFile == null) {
			logger.error("\nMetadata profiles XML file not provided!");
			return null;
		}
        return super.unmarshal(xsdFilename, xmlFile, MetadataProfiles.class);
	}

    @Override
    public void marshalIngestProfile(String xmlFilename, IngestProfile ingestProfile) throws JAXBException, SAXException, FileNotFoundException
    {
        String xsdFilename = IJaxbIngestProfile.xsdFilename;
        if(xsdFilename == null || xsdFilename.isEmpty()) {
			logger.error("\nIngest profile Schema file not provided!");
			return;
		}
		
        if(xmlFilename == null || xmlFilename.isEmpty()) {
			logger.error("\nIngest profile XML file not provided!");
			return;
		}
        super.marshal(xsdFilename, xmlFilename, ingestProfile);
    }

    @Override
    public IngestProfile unmarshalIngestProfile(String xmlFilename) throws JAXBException, SAXException
    {
        String xsdFilename = IJaxbIngestProfile.xsdFilename;
        if(xsdFilename == null || xsdFilename.isEmpty()) {
			logger.error("\nIngest profile Schema file not provided!");
			return null;
		}
		
        if(xmlFilename == null || xmlFilename.isEmpty()) {
			logger.error("\nIngest profile XML file not provided!");
			return null;
		}
        return super.unmarshal(xsdFilename, xmlFilename, IngestProfile.class);
    }

	@Override
	public void marshalIngestProfile(File xmlFile, IngestProfile ingestProfile) throws JAXBException, SAXException, FileNotFoundException {
		String xsdFilename = IJaxbIngestProfile.xsdFilename;
		if(xsdFilename == null || xsdFilename.isEmpty()) {
			logger.error("\nIngest profile Schema file not provided!");
			return;
		}
		
        if(xmlFile == null) {
			logger.error("\nIngest profile XML file not provided!");
			return;
		}
        super.marshal(xsdFilename, xmlFile, ingestProfile);		
	}

	@Override
	public IngestProfile unmarshalIngestProfile(File xmlFile) throws JAXBException, SAXException {
		String xsdFilename = IJaxbIngestProfile.xsdFilename;
		if(xsdFilename == null || xsdFilename.isEmpty()) {
			logger.error("\nIngest profile Schema file not provided!");
			return null;
		}
		
        if(xmlFile == null) {
			logger.error("\nIngest profile XML file not provided!");
			return null;
		}
        return super.unmarshal(xsdFilename, xmlFile, IngestProfile.class);
	}
}
