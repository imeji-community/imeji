package de.mpg.jena.controller;

import java.net.URI;
import java.util.List;

import thewebsemantic.Bean2RDF;
import thewebsemantic.LocalizedString;
import thewebsemantic.RDF2Bean;

import com.hp.hpl.jena.rdf.model.Resource;

import de.mpg.jena.util.ObjectHelper;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.MetadataProfile;
import de.mpg.jena.vo.Person;
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
        return (MetadataProfile)ObjectHelper.castAllHashSetToList(mdp);
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
    
    public List<MetadataProfile> retrieveAll()
    {
        RDF2Bean reader = new RDF2Bean(base);
        return (List<MetadataProfile>)reader.load(MetadataProfile.class);
    }
    
    public MetadataProfile retrieve(String id) throws Exception
    {
        return this.retrieve(ObjectHelper.getURI(MetadataProfile.class, id));
    }
    
    public MetadataProfile retrieve(URI uri) throws Exception
    {
        RDF2Bean reader = new RDF2Bean(base);
        return ((MetadataProfile)ObjectHelper.castAllHashSetToList(reader.load(uri.toString())));
    }
    
    public static void main(String[] arg) throws Exception
    {
        
        MetadataProfile mdp = new MetadataProfile();
        CollectionImeji coll = new CollectionImeji();
        coll.getMetadata().setTitle("title");
        coll.getMetadata().getPersons().add(new Person());
        mdp.setTitle("Test MDProfile");
        Statement st = new Statement();
        st.setType(new URI("http://testtype"));
        st.getLabels().add(new LocalizedString("tesr", "en"));
        
        
        mdp.getStatements().add(st);
        
        ProfileController pc = new ProfileController(null);
        CollectionController cc = new CollectionController(null);
        mdp = pc.create(mdp);
        coll.setProfile(mdp);
        coll = cc.create(coll);
        coll.getMetadata().setDescription("update");
        cc.update(coll);
        coll = cc.retrieve(coll.getId());
        mdp.setTitle("new title");
        
        pc.update(mdp);
        
        mdp = pc.retrieve(mdp.getId());
        
        base.write(System.out, "RDF/XML");
        
        
    }
}
