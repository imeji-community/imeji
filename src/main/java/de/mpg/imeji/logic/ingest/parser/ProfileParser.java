package de.mpg.imeji.logic.ingest.parser;

import java.io.File;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

import de.mpg.imeji.logic.ingest.jaxb.JaxbGenericObject;
import de.mpg.imeji.logic.ingest.vo.MetadataProfiles;
import de.mpg.imeji.logic.vo.MetadataProfile;

public class ProfileParser
{
    /**
     * Parse a profile Xml
     * 
     * @param profileXml
     * @return
     * @throws SAXException
     * @throws JAXBException
     */
    public MetadataProfile parse(File profileXmlFile) throws JAXBException, SAXException
    {
        return new JaxbGenericObject<MetadataProfile>(MetadataProfile.class).unmarshal(profileXmlFile);
    }

    /**
     * Parse a list of item
     * 
     * @param itemListXml
     * @return
     * @throws SAXException
     * @throws JAXBException
     */
    public List<MetadataProfile> parseList(File profileListXmlFile) throws JAXBException, SAXException
    {
        return new JaxbGenericObject<MetadataProfiles>(MetadataProfiles.class).unmarshal(profileListXmlFile).getMetadataProfile();
    }
}
