package de.mpg.jena.controller;

import java.net.URI;
import java.util.List;

import thewebsemantic.LocalizedString;
import thewebsemantic.RDF2Bean;

import com.hp.hpl.jena.rdf.model.Resource;

import de.mpg.jena.util.ObjectHelper;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.Grant;
import de.mpg.jena.vo.Grant.GrantType;
import de.mpg.jena.vo.MetadataProfile;
import de.mpg.jena.vo.Person;
import de.mpg.jena.vo.Properties.Status;
import de.mpg.jena.vo.Statement;
import de.mpg.jena.vo.User;

public class ProfileController extends ImejiController
{
    
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
        bean2RDF.saveDeep(mdp);
        MetadataProfile res = rdf2Bean.load(MetadataProfile.class, mdp.getId());
        user = addCreatorGrant(res, user);
        base.commit();
        return (MetadataProfile)ObjectHelper.castAllHashSetToList(mdp);
    }
    
	private User addCreatorGrant(MetadataProfile p, User user) throws Exception
	{
		GrantController gc = new GrantController(user);
		Grant grant = new Grant(GrantType.PROFILE_ADMIN, p.getId());
		gc.addGrant(user, grant);
		UserController uc = new UserController(user);
		return uc.retrieve(user.getEmail());
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
        Resource r = bean2RDF.saveDeep(mdp);
        base.commit();
    }
    
    public void delete(MetadataProfile mdp, User user){
    	bean2RDF.delete(mdp);
    }
    
    public List<MetadataProfile> retrieveAll()
    {
        return (List<MetadataProfile>)rdf2Bean.load(MetadataProfile.class);
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
    
    @Override
    protected String getSpecificFilter() throws Exception
    {
       return "";
    }

    @Override
    protected String getSpecificQuery() throws Exception
    {
        return "";
    }
}
