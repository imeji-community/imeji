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
import de.mpg.imeji.logic.ingest.jaxb.interfaces.IJaxbMetadataProfile;
import de.mpg.imeji.logic.ingest.jaxb.interfaces.IJaxbMetadataProfiles;
import de.mpg.imeji.logic.ingest.util.ImejiNamespacePrefixMapper;
import de.mpg.imeji.logic.ingest.vo.IngestProfile;
import de.mpg.imeji.logic.ingest.vo.MetadataProfiles;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.MetadataProfile;

/**
 * @author hnguyen
 *
 */
public class JaxbIngestProfile extends JaxbUtil implements IJaxbItem, IJaxbMetadataProfile, IJaxbMetadataProfiles, IJaxbIngestProfile {

	private Logger logger = Logger.getLogger(JaxbIngestProfile.class);
	protected String xsd_mdp_file = ImejiNamespacePrefixMapper.IMEJI_INGEST_PROFILE_XSDFILE;
	
	
	/**
	 * 
	 */
	public JaxbIngestProfile() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void marshalItem(String xmlFile, Item item) throws JAXBException, SAXException {
		
		String xsdFile = IJaxbItem.xsdFile;
		
		if( xmlFile.isEmpty() || xsdFile.isEmpty() )
		{
			logger.error("\nSchema file or Xml file not provided!");						
		}

		super.marshal(xsdFile, xmlFile, item);
	}	

	@Override
	public Item unmarshalItem(String xmlFile) throws JAXBException, SAXException {		
		String xsdFile = IJaxbItem.xsdFile;
		if( xmlFile.isEmpty() || xsdFile.isEmpty() )
		{
			logger.error("\nSchema file or Xml file not provided!");			
			return null;
		}		

		return unmarshal( xsdFile , xmlFile, Item.class );	      
	}

	@Override
	public void marshalMdProfile(String xmlFile, MetadataProfile mdp) throws JAXBException, SAXException {		
		String xsdFile = IJaxbMetadataProfile.xsdFile;		
		if( xmlFile.isEmpty() || xsdFile.isEmpty() )
		{
			logger.error("\nSchema file or Xml file not provided!");						
		}

		super.marshal( xsdFile, xmlFile, mdp );
	}
			

	@Override
	public MetadataProfile unmarshalMdProfile(String xmlFile) throws JAXBException, SAXException {
		String xsdFile = IJaxbMetadataProfile.xsdFile;
		if( xmlFile.isEmpty() || xsdFile.isEmpty() )
		{
			logger.error("\nSchema file or Xml file not provided!");			
			return null;
		}		

		return super.unmarshal( xsdFile , xmlFile, MetadataProfile.class );	      
	}

	@Override
	public void marshalMdProfiles(String xmlFile, MetadataProfiles mdp)
			throws JAXBException, SAXException {
		String xsdFile = IJaxbMetadataProfiles.xsdFile;
		
		if( xmlFile.isEmpty() || xsdFile.isEmpty() )
		{
			logger.error("\nSchema file or Xml file not provided!");						
		}

		super.marshal( xsdFile, xmlFile, mdp );
		
	}

	@Override
	public MetadataProfiles unmarshalMdProfiles(String xmlFile) throws JAXBException, SAXException {
		String xsdFile = IJaxbMetadataProfiles.xsdFile;
		
		if( xmlFile.isEmpty() || xsdFile.isEmpty() )
		{
			logger.error("\nSchema file or Xml file not provided!");			
			return null;
		}		

		return super.unmarshal( xsdFile , xmlFile, MetadataProfiles.class );
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
