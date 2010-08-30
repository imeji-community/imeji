package de.mpg.jena.controller;

import java.net.URI;

import javax.servlet.jsp.jstl.fmt.LocalizationContext;

import thewebsemantic.Bean2RDF;
import thewebsemantic.LocalizedString;
import thewebsemantic.RDF2Bean;
import thewebsemantic.custom_datatypes.XmlLiteral;

import com.hp.hpl.jena.datatypes.xsd.impl.XMLLiteralType;
import com.hp.hpl.jena.rdf.model.Resource;

import de.mpg.jena.util.ObjectHelper;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.MetadataProfile;
import de.mpg.jena.vo.Statement;
import de.mpg.jena.vo.User;
import de.mpg.jena.vo.Properties.Status;

public class ProfileController extends ImejiController
{
    private User user;
    
    public ProfileController(User user)
    {
        super(user);
    }
    
    /**
     * Creates a new collection. 
     * - Add a unique id
     * - Write user properties
     * @param ic
     * @param user
     */
    public MetadataProfile create(MetadataProfile mdp) throws Exception
    {
        
        writeCreateProperties(mdp.getProperties(), user);
        mdp.getProperties().setStatus(Status.PENDING); 
        mdp.setId(ObjectHelper.getURI(MetadataProfile.class, Integer.toString(getUniqueId())));
        base.begin();
        Bean2RDF writer = new Bean2RDF(base);
        Resource r = writer.saveDeep(mdp);
        RDF2Bean reader = new RDF2Bean(base);
        mdp = (MetadataProfile)reader.load(r.getURI());
        base.commit();
        return mdp;
    }
    
    /**
     * Updates a collection
     * -Logged in users:
     * --User is collection owner
     * --OR user is collection editor
     * @param ic
     * @param user
     */
    public void update(MetadataProfile mdp)
    {
        writeUpdateProperties(mdp.getProperties(), user);
        base.begin();
        Bean2RDF writer = new Bean2RDF(base);
        Resource r = writer.saveDeep(mdp);
        base.commit();
    }
    
    public MetadataProfile retrieve(String id)
    {
        RDF2Bean reader = new RDF2Bean(base);
        return (MetadataProfile)reader.load(ObjectHelper.getURI(MetadataProfile.class, id).toString());
    }
    
    public static void main(String[] arg) throws Exception
    {
        
        MetadataProfile mdp = new MetadataProfile();
        mdp.setTitle("Test MDProfile");
        Statement st = new Statement();
        st.setType(new URI("http://testtype"));
        st.getLabels().add(new LocalizedString("tesr", "en"));
        
        
        mdp.getStatements().add(st);
        
        ProfileController pc = new ProfileController(null);
        mdp = pc.create(mdp);
        
        mdp.setTitle("new title");
        
        pc.update(mdp);
        
        base.write(System.out, "RDF/XML");
        
        
    }
}
