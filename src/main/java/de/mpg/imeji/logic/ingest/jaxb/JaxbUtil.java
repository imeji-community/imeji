/**
 * 
 */
package de.mpg.imeji.logic.ingest.jaxb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

/**
 * @author http://www.torsten-horn.de/techdocs/java-xml-jaxb.htm modified by hnguyen
 */
public class JaxbUtil {
  public static <T> T unmarshal(String xsdFilename, String xmlFilename, Class<T> clss)
      throws JAXBException, SAXException {
    SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.XMLNS_ATTRIBUTE_NS_URI);
    Schema schema = (xsdFilename == null || xsdFilename.trim().length() == 0) ? null
        : schemaFactory.newSchema(getFileURLInClasspath(xsdFilename));
    JAXBContext jaxbContext = JAXBContext.newInstance(clss.getPackage().getName());
    return unmarshal(jaxbContext, schema, xmlFilename, clss);
  }

  public static <T> T unmarshal(JAXBContext jaxbContext, Schema schema, String xmlFilename,
      Class<T> clss) throws JAXBException {
    Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
    unmarshaller.setSchema(schema);
    return clss.cast(unmarshaller.unmarshal(new File(xmlFilename)));
  }

  public static <T> T unmarshal(String xsdFilename, File xmlFile, Class<T> clss)
      throws JAXBException, SAXException {
    SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.XMLNS_ATTRIBUTE_NS_URI);
    Schema schema = (xsdFilename == null || xsdFilename.trim().length() == 0) ? null
        : schemaFactory.newSchema(getFileURLInClasspath(xsdFilename));
    JAXBContext jaxbContext = JAXBContext.newInstance(clss.getPackage().getName());
    return unmarshal(jaxbContext, schema, xmlFile, clss);
  }

  public static <T> T unmarshal(JAXBContext jaxbContext, Schema schema, File xmlFile, Class<T> clss)
      throws JAXBException {
    Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
    unmarshaller.setSchema(schema);
    return clss.cast(unmarshaller.unmarshal(xmlFile));
  }

  public static void marshal(String xsdFilename, String xmlFilename, Object jaxbElement)
      throws JAXBException, SAXException, FileNotFoundException {
    SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.XMLNS_ATTRIBUTE_NS_URI);
    Schema schema = (xsdFilename == null || xsdFilename.trim().length() == 0) ? null
        : schemaFactory.newSchema(getFileURLInClasspath(xsdFilename));
    JAXBContext jaxbContext =
        JAXBContext.newInstance(jaxbElement.getClass().getPackage().getName());
    marshal(jaxbContext, schema, xmlFilename, jaxbElement);
  }

  public static void marshal(JAXBContext jaxbContext, Schema schema, String xmlFilename,
      Object jaxbElement) throws JAXBException, FileNotFoundException {
    Marshaller marshaller = jaxbContext.createMarshaller();
    marshaller.setSchema(schema);
    marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    marshaller.marshal(jaxbElement, new FileOutputStream(new File(xmlFilename)));
  }

  public static void marshal(String xsdFilename, File xmlFile, Object jaxbElement)
      throws JAXBException, SAXException, FileNotFoundException {
    SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.XMLNS_ATTRIBUTE_NS_URI);
    Schema schema = (xsdFilename == null || xsdFilename.trim().length() == 0) ? null
        : schemaFactory.newSchema(getFileURLInClasspath(xsdFilename));
    JAXBContext jaxbContext =
        JAXBContext.newInstance(jaxbElement.getClass().getPackage().getName());
    marshal(jaxbContext, schema, xmlFile, jaxbElement);
  }

  public static void marshal(JAXBContext jaxbContext, Schema schema, File xmlFile,
      Object jaxbElement) throws JAXBException, FileNotFoundException {
    Marshaller marshaller = jaxbContext.createMarshaller();
    marshaller.setSchema(schema);
    marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    marshaller.marshal(jaxbElement, new FileOutputStream(xmlFile));
  }

  public static void toString(Object obj) throws JAXBException {
    JAXBContext ctx = JAXBContext.newInstance(obj.getClass());
    Marshaller marshaller = ctx.createMarshaller();
    marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    marshaller.marshal(obj, System.out);
  }

  public static void writeToOutputStream(Object obj, OutputStream os) throws JAXBException {
    JAXBContext ctx = JAXBContext.newInstance(obj.getClass());
    Marshaller marshaller = ctx.createMarshaller();
    marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    marshaller.marshal(obj, os);
  }

  public static URL getFileURLInClasspath(String path) {
    return JaxbUtil.class.getClassLoader().getResource(path);
  }
}
