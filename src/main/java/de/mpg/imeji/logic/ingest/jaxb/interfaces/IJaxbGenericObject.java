/**
 * 
 */
package de.mpg.imeji.logic.ingest.jaxb.interfaces;

import java.io.File;
import java.io.FileNotFoundException;

import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

/**
 * @author hnguyen
 * @param <T>
 */
public interface IJaxbGenericObject<T>
{
    /**
     * This method creates a xml filename from a type object through the given schema file.
     * 
     * @param xmlFile, the provided xml filename output to an xml file
     * @param t, the type object
     * @throws JAXBException
     * @throws SAXException
     * @throws FileNotFoundException
     */
    public void marshal(String xmlFilename, T t) throws JAXBException, SAXException, FileNotFoundException;

    /**
     * This method generates a type object from the xml filename.
     * 
     * @param xmlFile, the xml filename specified the content of the object type information
     * @return a type object
     * @throws JAXBException
     * @throws SAXException
     */
    public T unmarshal(String xmlFilename) throws JAXBException, SAXException;

    /**
     * This method creates a xml file from a type object through the given schema file.
     * 
     * @param xmlFile, the xml file to output to an xml file
     * @param t, the type object
     * @throws JAXBException
     * @throws SAXException
     * @throws FileNotFoundException
     */
    public void marshal(File xmlFile, T t) throws JAXBException, SAXException, FileNotFoundException;

    /**
     * This method generates a type object from the xml file.
     * 
     * @param xmlFile, the xml file specified the content of the object type information
     * @return a type object
     * @throws JAXBException
     * @throws SAXException
     */
    public T unmarshal(File xmlFile) throws JAXBException, SAXException;
}
