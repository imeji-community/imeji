/**
 * 
 */
package de.mpg.imeji.logic.ingest.jaxb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import de.mpg.imeji.logic.ingest.jaxb.interfaces.IJaxbItem;

/**
 * @author http://www.torsten-horn.de/techdocs/java-xml-jaxb.htm modified by hnguyen
 */
public class JaxbUtil
{
    public static <T> T unmarshal(String xsdFile, String xmlFile, Class<T> clss) throws JAXBException, SAXException
    {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = (xsdFile == null || xsdFile.trim().length() == 0) ? null : schemaFactory
                .newSchema(getFileURLInClasspath(xsdFile));
        JAXBContext jaxbContext = JAXBContext.newInstance(clss.getPackage().getName());
        return unmarshal(jaxbContext, schema, xmlFile, clss);
    }

    public static <T> T unmarshal(JAXBContext jaxbContext, Schema schema, String xmlFile, Class<T> clss)
            throws JAXBException
    {
        Unmarshaller unmarshaller = (Unmarshaller)jaxbContext.createUnmarshaller();
        ((javax.xml.bind.Unmarshaller)unmarshaller).setSchema(schema);
        return clss.cast(((javax.xml.bind.Unmarshaller)unmarshaller).unmarshal(new File(xmlFile)));
    }

    public static void marshal(String xsdFile, String xmlFile, Object jaxbElement) throws JAXBException, SAXException
    {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = (xsdFile == null || xsdFile.trim().length() == 0) ? null : schemaFactory
                .newSchema(getFileURLInClasspath(xsdFile));
        JAXBContext jaxbContext = JAXBContext.newInstance(jaxbElement.getClass().getPackage().getName());
        marshal(jaxbContext, schema, xmlFile, jaxbElement);
    }

    public static void marshal(JAXBContext jaxbContext, Schema schema, String xmlFile, Object jaxbElement)
            throws JAXBException
    {
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setSchema(schema);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        try
        {
            marshaller.marshal(jaxbElement, new FileOutputStream(new File(xmlFile)));
        }
        catch (FileNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void toString(Object obj) throws JAXBException
    {
        JAXBContext ctx = JAXBContext.newInstance(obj.getClass());
        Marshaller marshaller = ctx.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(obj, System.out);
    }

    public static URL getFileURLInClasspath(String path)
    {
        return JaxbUtil.class.getClassLoader().getResource(path);
    }
}