/**
 * 
 */
package de.mpg.imeji.logic.ingest.jaxb;

import java.io.File;
import java.io.FileNotFoundException;

import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

import de.mpg.imeji.logic.ingest.jaxb.interfaces.IJaxbGenericObject;
import de.mpg.imeji.logic.ingest.util.ImejiSchemaFilename;

/**
 * @author hnguyen
 * @param <T>
 *
 */
public class JaxbGenericObject<T> extends JaxbUtil implements IJaxbGenericObject<T> {

	protected String xsdFilename = ImejiSchemaFilename.IMEJI_INGEST_PROFILE_XSDFILE;

	protected Class<T> type;	
	
    public JaxbGenericObject(Class<T> type) {
         this.type = type;
    }
    
    public JaxbGenericObject(Class<T> type, String xsdFilename) {
        this.type = type;
        this.xsdFilename = xsdFilename;
   }

    public Class<T> getClassType() {
        return this.type;
    }
	
	public String getXsdFilename() {
		return this.xsdFilename;
	}

	@Override
	public void marshal(String xmlFilename, T t) throws JAXBException,
			SAXException, FileNotFoundException {
		super.marshal(this.getXsdFilename(), xmlFilename, t);
	}

	@Override
	public T unmarshal(String xmlFilename) throws JAXBException, SAXException {		
		return super.unmarshal(this.getXsdFilename(), xmlFilename, this.getClassType() );
	}

	@Override
	public void marshal(File xmlFile, T t) throws JAXBException, SAXException,
			FileNotFoundException {
		super.marshal(this.getXsdFilename(), xmlFile, t);
	}

	@Override
	public T unmarshal(File xmlFile) throws JAXBException, SAXException {
		return unmarshal(this.getXsdFilename(), xmlFile, this.getClassType());
	}

}
