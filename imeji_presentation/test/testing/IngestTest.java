package testing;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.ingest.jaxb.JaxbIngestProfile;
import de.mpg.imeji.logic.ingest.mapper.ItemMapper;
import de.mpg.imeji.logic.ingest.mapper.ProfileMapper;
import de.mpg.imeji.logic.ingest.parser.ItemParser;
import de.mpg.imeji.logic.ingest.parser.ProfileParser;
import de.mpg.imeji.logic.ingest.validator.ItemValidator;
import de.mpg.imeji.logic.ingest.vo.Items;
import de.mpg.imeji.logic.ingest.vo.MetadataProfiles;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.MetadataProfile;

public class IngestTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

//	@Test
	public void test() {
		String xmlFilename = "C:\\Users\\hnguyen\\Development\\GitHub\\imeji\\imeji_presentation\\test\\testResources\\items.xml";
		
		try {

			JaxbIngestProfile jmp = new JaxbIngestProfile();
			
			Items items = jmp.unmarshalItems(xmlFilename);
			
			ItemMapper im = new ItemMapper(items.getItem());
			
			items.setItem(im.getMappedItems());
			
			JaxbIngestProfile.toString(items);

			
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("JAXBException");
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("SAXException");
		}
	}
	
	@Test
	public void itemParsingTest() {
		String xmlFilename = "C:\\Users\\hnguyen\\Development\\GitHub\\imeji\\imeji_presentation\\test\\testResources\\items_lite.xml";
		
		ItemParser ip = new ItemParser();
        List<Item> itemList = ip.parseItemList(new File(xmlFilename));
        
        ItemMapper im = new ItemMapper(itemList);
        List<Item> mappedItemList = im.getMappedItems();
        
        Items items = new Items();
        items.setItem(mappedItemList);
        try {
			JaxbIngestProfile.toString(items);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
////        FileNameMapper fm = new FileNameMapper(itemList);
////        itemList = fm.getMappedList
//        ItemValidator iv = new ItemValidator();
//        iv.valid(itemListXmlFile, profile);
//        ItemController ic = new ItemController(user);
//        ic.update(itemList);
	}
	
//	@Test
	public void profileParsingTest() {
		String xmlFilename = "C:\\Users\\hnguyen\\Development\\GitHub\\imeji\\imeji_presentation\\test\\testResources\\mdp_lite.xml";
		
		ProfileParser pp = new ProfileParser();
        MetadataProfile profile = pp.parse(new File(xmlFilename));
        
        try {
			JaxbIngestProfile.toString(profile);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

//	@Test
	public void profilesParsingTest() {
		String xmlFilename = "C:\\Users\\hnguyen\\Development\\GitHub\\imeji\\imeji_presentation\\test\\testResources\\mdps_lite.xml";
		
		ProfileParser pp = new ProfileParser();
        List<MetadataProfile> profileList = pp.parseList(new File(xmlFilename));
        
        ProfileMapper pm = new ProfileMapper(profileList);
        List<MetadataProfile> mappedItemList = pm.getMappedProfiles();
        
        MetadataProfiles mdps = new MetadataProfiles();
        mdps.setMetadataProfile(mappedItemList);
        try {
			JaxbIngestProfile.toString(mdps);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
