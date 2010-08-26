package de.mpg.jena.controller;

import java.net.URI;

import thewebsemantic.Bean2RDF;
import thewebsemantic.RDF2Bean;

import com.hp.hpl.jena.rdf.model.Resource;

import de.mpg.jena.vo.MetadataProfile;
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
        mdp.setId(new URI("http://imeji.mpdl.mpg.de/mdProfile/" + getUniqueId()));
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
        writer.saveDeep(mdp);
        base.commit();
    }
}
