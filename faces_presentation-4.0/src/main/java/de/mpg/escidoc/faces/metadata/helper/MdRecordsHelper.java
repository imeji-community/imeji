package de.mpg.escidoc.faces.metadata.helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import de.mpg.escidoc.faces.metadata.Metadata;
import de.mpg.escidoc.services.common.util.ResourceUtil;

public class MdRecordsHelper
{
    /**
     * The transformation to get a generic md-record from a classic (non generic) md-record
     */
    private static final String DEFAULT_XSLT = "WEB-INF/classes/xslt/transform2GenericMdRecord.xsl";
    //private static final String DEFAULT_XSLT = "src/main/resources/xslt/transform2GenericMdRecord.xsl";
    
    /**
     * Transform the md-record xml into a generic format.
     * Valid the md-record according to template.
     * @param mds : the xml of the md-record.
     * @return the xml of the generic md-record.
     * @throws TransformerException
     * @throws IOException
     */
    public static String transformToGenericMdRecord(String mds) throws TransformerException, IOException
    {
        String genericMdRecord = "";
        InputStream is = new ByteArrayInputStream(mds.getBytes());
        OutputStream os = new ByteArrayOutputStream();
        
        Source xmlSource = new StreamSource(is);
        Source xsltSource = new StreamSource( ResourceUtil.getResourceAsStream(DEFAULT_XSLT));
        Result result = new StreamResult(os);
        
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer(xsltSource);
     
        transformer.transform(xmlSource, result);   
        genericMdRecord = os.toString();
        
        return genericMdRecord;
    }
    
    public static String TransformToDefaultXMLSchema(List<Metadata> metadataList)
    {
	String xml = "";
	
	for (Metadata m : metadataList)
	{
	    xml += "<";
	    if (m.getNamespace() != null)
	    {
		xml+= m.getNamespace() + ":";
	    }
	    xml += m.getName() + ">";
	    if (m.getSimpleValue() != null)
	    {
		xml += m.getSimpleValue();
	    }
	    xml += "</";
	    if (m.getNamespace() != null)
	    {
		xml+= m.getNamespace() + ":";
	    }
	    xml += m.getName() + ">";
	}
	
	return xml;
    }
}
