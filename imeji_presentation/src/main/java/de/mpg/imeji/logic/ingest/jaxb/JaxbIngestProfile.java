/**
 * 
 */
package de.mpg.imeji.logic.ingest.jaxb;

import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import de.mpg.imeji.logic.ingest.jaxb.interfaces.IJaxbIngestProfile;
import de.mpg.imeji.logic.ingest.jaxb.interfaces.IJaxbItem;
import de.mpg.imeji.logic.ingest.jaxb.interfaces.IJaxbItems;
import de.mpg.imeji.logic.ingest.jaxb.interfaces.IJaxbMetadataProfile;
import de.mpg.imeji.logic.ingest.jaxb.interfaces.IJaxbMetadataProfiles;
import de.mpg.imeji.logic.ingest.util.ImejiNamespacePrefixMapper;
import de.mpg.imeji.logic.ingest.vo.IngestProfile;
import de.mpg.imeji.logic.ingest.vo.Items;
import de.mpg.imeji.logic.ingest.vo.MetadataProfiles;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.MetadataProfile;

/**
 * @author hnguyen
 *
 */
public class JaxbIngestProfile extends JaxbUtil implements IJaxbItem, IJaxbItems, IJaxbMetadataProfile, IJaxbMetadataProfiles, IJaxbIngestProfile {

	private Logger logger = Logger.getLogger(JaxbIngestProfile.class);
	
	
	/**
	 * 
	 */
	public JaxbIngestProfile() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void marshalItem(String xmlFile, Item item) throws JAXBException, SAXException {
		
		String xsdFile = IJaxbItem.xsdFilename;
		
		if( xmlFile.isEmpty() || xsdFile.isEmpty() )
		{
			logger.error("\nSchema file or Xml file not provided!");						
		}

		super.marshal(xsdFile, xmlFile, item);
	}	

	@Override
	public Item unmarshalItem(String xmlFile) throws JAXBException, SAXException {		
		String xsdFile = IJaxbItem.xsdFilename;
		if( xmlFile.isEmpty() || xsdFile.isEmpty() )
		{
			logger.error("\nSchema file or Xml file not provided!");			
			return null;
		}		

		return unmarshal( xsdFile , xmlFile, Item.class );
	}
	
	@Override
	public void marshalItems(String xmlFilename, Items items)
			throws JAXBException, SAXException {
		String xsdFilename = IJaxbItems.xsdFilename;
		
		if( xmlFilename.isEmpty() || xsdFilename.isEmpty() )
		{
			logger.error("\nSchema file or Xml file not provided!");						
		}

		super.marshal(xsdFilename, xmlFilename, items);
		
	}

	@Override
	public Items unmarshalItems(String xmlFilename) throws JAXBException,
			SAXException {
		String xsdFilename = IJaxbItems.xsdFilename;
		if( xmlFilename.isEmpty() || xsdFilename.isEmpty() )
		{
			logger.error("\nSchema file or Xml file not provided!");			
			return null;
		}		

		return unmarshal( xsdFilename , xmlFilename, Items.class );
	}

	@Override
	public void marshalMdProfile(String xmlFilename, MetadataProfile mdp) throws JAXBException, SAXException {		
		String xsdFilename = IJaxbMetadataProfile.xsdFilename;		
		if( xmlFilename.isEmpty() || xsdFilename.isEmpty() )
		{
			logger.error("\nSchema file or Xml file not provided!");						
		}

		super.marshal( xsdFilename, xmlFilename, mdp );
	}
			

	@Override
	public MetadataProfile unmarshalMdProfile(String xmlFilename) throws JAXBException, SAXException {
		String xsdFilename = IJaxbMetadataProfile.xsdFilename;
		if( xmlFilename.isEmpty() || xsdFilename.isEmpty() )
		{
			logger.error("\nSchema file or Xml file not provided!");			
			return null;
		}		

		return super.unmarshal( xsdFilename , xmlFilename, MetadataProfile.class );	      
	}

	@Override
	public void marshalMdProfiles(String xmlFilename, MetadataProfiles mdps)
			throws JAXBException, SAXException {
		String xsdFilename = IJaxbMetadataProfiles.xsdFilename;
		
		if( xmlFilename.isEmpty() || xsdFilename.isEmpty() )
		{
			logger.error("\nSchema file or Xml file not provided!");						
		}

		super.marshal( xsdFilename, xmlFilename, mdps );
		
	}

	@Override
	public MetadataProfiles unmarshalMdProfiles(String xmlFilename) throws JAXBException, SAXException {
		String xsdFilename = IJaxbMetadataProfiles.xsdFilename;
		
		if( xmlFilename.isEmpty() || xsdFilename.isEmpty() )
		{
			logger.error("\nSchema file or Xml file not provided!");			
			return null;
		}		

		return super.unmarshal( xsdFilename , xmlFilename, MetadataProfiles.class );
	}
	
	@Override
	public void marshalIngestProfile(String xmlFile, IngestProfile ingestProfile)
			throws JAXBException, SAXException {
		String xsdFile = ImejiNamespacePrefixMapper.IMEJI_INGEST_PROFILE_XSDFILE;
		
		if( xmlFile.isEmpty() || xsdFile.isEmpty() )
		{
			logger.error("\nSchema file or Xml file not provided!");						
		}

		super.marshal( xsdFile, xmlFile, ingestProfile );
		
	}
	
	@Override	
	public IngestProfile unmarshalIngestProfile(String xmlFile) throws JAXBException, SAXException {
		String xsdFile = ImejiNamespacePrefixMapper.IMEJI_INGEST_PROFILE_XSDFILE;
		
		if( xmlFile.isEmpty() || xsdFile.isEmpty() )
		{
			logger.error("\nSchema file or Xml file not provided!");			
			return null;
		}		

		return super.unmarshal( xsdFile , xmlFile, IngestProfile.class );
	}
}
