package de.mpg.jena.controller;

import java.net.URI;
import java.util.Date;
import java.util.List;

import thewebsemantic.RDF2Bean;

import de.mpg.jena.ImejiBean2RDF;
import de.mpg.jena.ImejiJena;
import de.mpg.jena.ImejiRDF2Bean;
import de.mpg.jena.sparql.ImejiSPARQL;
import de.mpg.jena.util.ObjectHelper;
import de.mpg.jena.vo.Grant;
import de.mpg.jena.vo.Grant.GrantType;
import de.mpg.jena.vo.MetadataProfile;
import de.mpg.jena.vo.Properties.Status;
import de.mpg.jena.vo.User;

public class ProfileController extends ImejiController
{
	private static ImejiRDF2Bean imejiRDF2Bean = new ImejiRDF2Bean(ImejiJena.profileModel);
	private static ImejiBean2RDF imejiBean2RDF = new ImejiBean2RDF(ImejiJena.profileModel);
	
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
    public URI create(MetadataProfile mdp) throws Exception
    {
     	imejiBean2RDF = new ImejiBean2RDF(ImejiJena.profileModel);
    	writeCreateProperties(mdp.getProperties(), user);
        mdp.getProperties().setStatus(Status.PENDING);
        URI uri = ObjectHelper.getURI(MetadataProfile.class, Integer.toString(getUniqueId()));
        mdp.setId(uri);
        imejiBean2RDF.create(mdp, user);
        addCreatorGrant(mdp, user);
        return uri;
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
     * @throws Exception 
     */
    public void update(MetadataProfile mdp) throws Exception
    {
    	imejiBean2RDF = new ImejiBean2RDF(ImejiJena.profileModel);
    	writeUpdateProperties(mdp.getProperties(), user);
        imejiBean2RDF.saveDeep(mdp, user);
    }
    
    public void release(MetadataProfile mdp) throws Exception
    {
    	mdp.getProperties().setStatus(Status.RELEASED);
    	mdp.getProperties().setVersionDate(new Date());
    	update(mdp);
    }
    
    public void delete(MetadataProfile mdp, User user) throws Exception
    {
    	imejiBean2RDF = new ImejiBean2RDF(ImejiJena.profileModel);
    	imejiBean2RDF.delete(mdp, user);
    }
    
    public int countAllProfiles()
    {
		return ImejiSPARQL.execCount("SELECT ?s count(DISTINCT ?s) WHERE { ?s a <http://imeji.mpdl.mpg.de/profile>}");
    }
    
    /**
     * @deprecated
     * @return
     */
    public List<MetadataProfile> retrieveAll()
    {
    	rdf2Bean = new RDF2Bean(ImejiJena.profileModel);
    	return (List<MetadataProfile>)rdf2Bean.load(MetadataProfile.class);
    }
    
    public  List<MetadataProfile> search()
    {
    	String q = "SELECT DISTINCT ?s WHERE {?s a <http://imeji.mpdl.mpg.de/mdprofile> . ?s <http://imeji.mpdl.mpg.de/properties> ?props . ?props <http://imeji.mpdl.mpg.de/status> ?status " +
    			".FILTER( ";
    	
    	q += "?status=<http://imeji.mpdl.mpg.de/status/RELEASED> ";
    	
    	for(Grant g : user.getGrants())
    	{
    		if (GrantType.PROFILE_EDITOR.equals(g.getGrantType())|| GrantType.PROFILE_ADMIN.equals(g.getGrantType()))
    		{
    			q += " || ?s=<" + g.getGrantFor() +"> ";
    		}
    	}
    	
    	q += " )}";
    	
    	return ImejiSPARQL.execAndLoad(q, MetadataProfile.class);
    }
    
    public MetadataProfile retrieve(String id) throws Exception
    {
    	imejiRDF2Bean = new ImejiRDF2Bean(ImejiJena.profileModel);
    	return this.retrieve(ObjectHelper.getURI(MetadataProfile.class, id));
    }
    
    public MetadataProfile retrieve(URI uri) throws Exception
    {
    	imejiRDF2Bean = new ImejiRDF2Bean(ImejiJena.profileModel);
    	return ((MetadataProfile)ObjectHelper.castAllHashSetToList(imejiRDF2Bean.load(uri.toString(), user)));
    }
    
    public static void main(String[] arg) throws Exception
    {
        
//        MetadataProfile mdp = new MetadataProfile();
//        CollectionImeji coll = new CollectionImeji();
//        coll.getMetadata().setTitle("title");
//        coll.getMetadata().getPersons().add(new Person());
//        mdp.setTitle("Test MDProfile");
//        Statement st = new Statement();
//        st.setType(new URI("http://testtype"));
//        st.getLabels().add(new LocalizedString("tesr", "en"));
//        
//        
//        mdp.getStatements().add(st);
//        
//        ProfileController pc = new ProfileController(null);
//        CollectionController cc = new CollectionController(null);
//        mdp = pc.create(mdp);
//        coll.setProfile(mdp);
//        coll = cc.create(coll);
//        coll.getMetadata().setDescription("update");
//        cc.update(coll);
//        coll = cc.retrieve(coll.getId());
//        mdp.setTitle("new title");
//        
//        pc.update(mdp);
//        
//        mdp = pc.retrieve(mdp.getId());
//        
//        base.write(System.out, "RDF/XML");
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
