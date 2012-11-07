package de.mpg.imeji.logic.ingest.parser;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;

import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

import de.mpg.imeji.logic.ingest.jaxb.JaxbIngestProfile;
import de.mpg.imeji.logic.ingest.jaxb.interfaces.IJaxbMetadataProfile;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Statement;
public class ProfileParser
{
	
    /**
     * Parse a profile Xml
     * @param profileXml
     * @return
     */
    public MetadataProfile parse(String profileXml)
    {
        MetadataProfile mdp = new MetadataProfile();
        
        // TODO
        
        mdp.setTitle("");
        mdp.setDescription("");
        Collection<Statement> statements = new LinkedList<Statement>();
        
        
//        mdp.setStatements(statements);
//        mdp.setCreated(created);
//        mdp.setCreatedBy(createdBy);
//        mdp.setDescription(description);
//        mdp.setDiscardComment(discardComment);
//        mdp.setId(id);
//        mdp.setModified(modified);
        
//        private URI id;
////      @j2jResource("http://imeji.org/terms/properties")
////      private Properties properties = new Properties();
//      @j2jLiteral("http://purl.org/dc/elements/1.1/title")
//      private String title;
//      @j2jLiteral("http://purl.org/dc/elements/1.1/description")
//      private String description;
//      @j2jList("http://imeji.org/terms/statement")
        return mdp;
    }
    
    /**
     * Parse a profile Xml with a predefined Xsd schema file
     * @param profileXml
     * @param profileXsd
     * @return
     */
    public MetadataProfile parse(String profileXml, String profileXSD)
    {
    	return parse(new File(profileXml), new File(profileXSD));
    }
    
    /**
     * Parse a profile Xml with a predefined Xsd schema file
     * @param profileXml
     * @param profileXsd
     * @return
     */
    public MetadataProfile parse(File profileXml, File profileXSD)
    {
        MetadataProfile mdp = new MetadataProfile();
        
        
        mdp.setTitle("");
        mdp.setDescription("");
        Collection<Statement> statements = new LinkedList<Statement>();
        mdp.setStatements(statements);
        
        System.out.println("hello world");
//        private URI id;
////      @j2jResource("http://imeji.org/terms/properties")
////      private Properties properties = new Properties();
//      @j2jLiteral("http://purl.org/dc/elements/1.1/title")
//      private String title;
//      @j2jLiteral("http://purl.org/dc/elements/1.1/description")
//      private String description;
//      @j2jList("http://imeji.org/terms/statement")
        return mdp;
    }    
    
    public MetadataProfile getMetadataProfile( String xmlFile ) throws JAXBException, SAXException
	{
    	return new JaxbIngestProfile().unmarshalMdProfile(xmlFile);
	}
}
