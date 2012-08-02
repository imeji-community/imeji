/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.imeji.logic.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import de.mpg.imeji.logic.ImejiBean2RDF;
import de.mpg.imeji.logic.ImejiJena;
import de.mpg.imeji.logic.ImejiRDF2Bean;
import de.mpg.imeji.logic.search.ImejiSPARQL;
import de.mpg.imeji.logic.search.Search;
import de.mpg.imeji.logic.search.SearchResult;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.Grant;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.logic.vo.Grant.GrantType;
import de.mpg.imeji.logic.vo.Properties.Status;
import de.mpg.j2j.helper.DateHelper;

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
    	writeCreateProperties(mdp, user);
        mdp.setStatus(Status.PENDING);
        if (mdp.getId() == null)
        {
	        URI uri = ObjectHelper.getURI(MetadataProfile.class, Integer.toString(getUniqueId()));
	        mdp.setId(uri);
        }
        imejiBean2RDF.create(imejiBean2RDF.toList(mdp), user);
        addCreatorGrant(mdp, user);
        return mdp.getId();
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
    	writeUpdateProperties(mdp, user);
        imejiBean2RDF.update(imejiBean2RDF.toList(mdp), user);
    }
    
    public void release(MetadataProfile mdp) throws Exception
    {
    	mdp.setStatus(Status.RELEASED);
    	mdp.setVersionDate(DateHelper.getCurrentDate());
    	update(mdp);
    }
    
    public void delete(MetadataProfile mdp, User user) throws Exception
    {
    	imejiBean2RDF = new ImejiBean2RDF(ImejiJena.profileModel);
    	imejiBean2RDF.delete(imejiBean2RDF.toList(mdp), user);
    	GrantController gc = new GrantController(user);
		gc.removeAllGrantsFor(user, mdp.getId());
    }
    
    public void withdraw(MetadataProfile mdp, User user) throws Exception
    {
    	mdp.setStatus(Status.WITHDRAWN);
    	mdp.setVersionDate(DateHelper.getCurrentDate());
    	update(mdp);
    }
    
    public int countAllProfiles()
    {
		return ImejiSPARQL.execCount("SELECT ?s count(DISTINCT ?s) WHERE { ?s a <http://imeji.org/terms/profile>}", ImejiJena.profileModel);
    }
    
    /**
     * @deprecated
     * @return
     */
    public List<MetadataProfile> retrieveAll()
    {
//    	rdf2Bean = new RDF2Bean(ImejiJena.profileModel);
//    	return (List<MetadataProfile>)rdf2Bean.load(MetadataProfile.class);
        return new ArrayList<MetadataProfile>();
    }
    
    /**
     * To be replaced, with a more generic method
     * 
     * @return
     * @deprecated
     */
    public  List<MetadataProfile> search()
    {
    	String q = "SELECT DISTINCT ?s WHERE {?s a <http://imeji.org/terms/mdprofile> . ?s <http://imeji.org/terms/properties> ?props . ?props <http://imeji.org/terms/status> ?status " +
    			".FILTER( ";
    	
    	q += "?status=<" + Status.RELEASED.getUri() + "> || (?status!=<" + Status.WITHDRAWN.getUri() + "> ";
    	
    	int i=0;
    	
    	if (user != null && user.getGrants().size() >0)
    	{
    		q += "&& (";
			for(Grant g : user.getGrants())
	    	{
	    		if (GrantType.SYSADMIN.equals(g.asGrantType()))
	    		{
	    			if (i > 0) q+= " || ";
	    			q += " true ";
	    			i++;
	    		}
	    		else if (GrantType.PROFILE_EDITOR.equals(g.asGrantType())|| GrantType.PROFILE_ADMIN.equals(g.asGrantType()))
	    		{
	    			if (i > 0) q+= " || ";
	    			q += " ?s=<" + g.getGrantFor() +"> ";
	    			i++;
	    		}
	    	}
			q += ")";
    	}
    	q += " ))}";
    	
    	//return ImejiSPARQL.execAndLoad(q, MetadataProfile.class);
    	 return new ArrayList<MetadataProfile>();
    }
    
    
    public MetadataProfile retrieve(String id) throws Exception
    {
    	imejiRDF2Bean = new ImejiRDF2Bean(ImejiJena.profileModel);
    	return retrieve(ObjectHelper.getURI(MetadataProfile.class, id));
    }
    
    public MetadataProfile retrieve(URI uri) throws Exception
    {
    	imejiRDF2Bean = new ImejiRDF2Bean(ImejiJena.profileModel);
    	MetadataProfile p =  ((MetadataProfile)imejiRDF2Bean.load(uri.toString(), user, new MetadataProfile()));
    	Collections.sort((List<Statement>) p.getStatements());
    	return p;
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
