/**
 * 
 */
package de.mpg.imeji.logic.ingest.jaxb.interfaces;

import java.io.File;
import java.io.FileNotFoundException;

import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

import de.mpg.imeji.logic.ingest.util.ImejiSchemaFilename;
import de.mpg.imeji.logic.ingest.vo.IngestProfile;

/**
 * @author hnguyen
 */
public interface IJaxbIngestProfile
{
    public String xsdFilename = ImejiSchemaFilename.IMEJI_INGEST_PROFILE_XSDFILE;

    /**
     * This method exports the meta data profiles and the items to the xml file through the given schema file.
     * 
     * @param xmlFile, the xml file to output
     * @throws JAXBException
     * @throws SAXException
     * @throws FileNotFoundException
     */
    public void marshalIngestProfile(String xmlFilename, IngestProfile ingestProfile) throws JAXBException,
            SAXException, FileNotFoundException;

    /**
     * This method generates the ingest profile from the xml file.
     * 
     * @param xmlFile, the xml file specified a list of ingest profile
     * @return the MetadataProfile object
     * @throws JAXBException
     * @throws SAXException
     */
    public IngestProfile unmarshalIngestProfile(String xmlFilename) throws JAXBException, SAXException;

    /**
     * This method exports the meta data profiles and the items to the xml file through the given schema file.
     * 
     * @param xmlFile, the xml file to output
     * @throws JAXBException
     * @throws SAXException
     * @throws FileNotFoundException
     */
    public void marshalIngestProfile(File xmlFile, IngestProfile ingestProfile) throws JAXBException, SAXException,
            FileNotFoundException;

    /**
     * This method generates the ingest profile from the xml file.
     * 
     * @param xmlFile, the xml file specified a list of ingest profile
     * @return the MetadataProfile object
     * @throws JAXBException
     * @throws SAXException
     */
    public IngestProfile unmarshalIngestProfile(File xmlFile) throws JAXBException, SAXException;
}
