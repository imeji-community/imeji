package de.mpg.imeji.logic.ingest.parser;

import java.io.File;

import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

import de.mpg.imeji.logic.ingest.jaxb.JaxbGenericObject;
import de.mpg.imeji.logic.resource.vo.MetadataProfile;

/**
 * Parse the {@link MetadataProfile} xml
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class ProfileParser {
  /**
   * Parse a profile Xml
   * 
   * @param profileXml
   * @return
   * @throws SAXException
   * @throws JAXBException
   */
  public MetadataProfile parse(File profileXmlFile) throws JAXBException, SAXException {
    return new JaxbGenericObject<MetadataProfile>(MetadataProfile.class).unmarshal(profileXmlFile);
  }
}
