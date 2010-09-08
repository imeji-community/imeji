package de.mpg.jena.controller;

import java.net.URI;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.List;

import com.hp.hpl.jena.tdb.TDB;

import thewebsemantic.Bean2RDF;
import thewebsemantic.RDF2Bean;
import thewebsemantic.Sparql;
import de.mpg.jena.controller.exceptions.AuthenticationException;
import de.mpg.jena.controller.exceptions.AuthorizationException;
import de.mpg.jena.util.ObjectHelper;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.Grant;
import de.mpg.jena.vo.Person;
import de.mpg.jena.vo.User;
import de.mpg.jena.vo.Grant.GrantType;
import de.mpg.jena.vo.Properties.Status;


public class CollectionController extends ImejiController{
	
	public CollectionController(User user)
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
	public CollectionImeji create(CollectionImeji ic) throws Exception
	{
	    //first write properties
	    writeCreateProperties(ic.getProperties(), user);
	    
	    //then check user credentials
	    checkUserCredentials(ic);

	    ic.getProperties().setStatus(Status.PENDING); 
		ic.setId(ObjectHelper.getURI(CollectionImeji.class, Integer.toString(getUniqueId())));
		base.begin();
		//Workarround: activate lazylist
		ic.getImages().size();
		for(Person p : ic.getMetadata().getPersons())
        {
           p.getOrganizations().size();
        }
		bean2RDF.saveDeep(ic);
		CollectionImeji res = rdf2Bean.load(CollectionImeji.class, ic.getId());
		base.commit();
		return ic;
	}
	
	
    /**
	 * Updates a collection
	 * -Logged in users:
	 * --User is collection owner
	 * --OR user is collection editor
	 * @param ic
	 * @param user
	 */
	public void update(CollectionImeji ic) throws Exception
	{
	    //first check user credentials
	    checkUserCredentials(ic);
		writeUpdateProperties(ic.getProperties(), user);
		base.begin();
		//Workarround: activate lazylist
        ic.getImages().size();
        for(Person p : ic.getMetadata().getPersons())
        {
           p.getOrganizations().size();
        }
		bean2RDF.saveDeep(ic);
		ic = rdf2Bean.load(CollectionImeji.class, ic.getId());
		System.out.println(ic.getImages().size());
		base.commit();
	}
	
	public void release(CollectionImeji ic) throws Exception
    {
        //first check user credentials
	    ic.getProperties().setStatus(Status.RELEASED);
        update(ic);
    }
	
	private void checkUserCredentials(CollectionImeji c) throws Exception
    {
        if(user==null)
        {
            throw new AuthenticationException("User is null!");
        }
        else if (!user.getEmail().equals(c.getProperties().getCreatedBy().getEmail()))
        {
            for (Grant g : user.getGrants())
            {
                if(g.getGrantFor().equals(c.getId()) && (g.getGrantType().equals(GrantType.CONTAINER_ADMIN) || g.getGrantType().equals(GrantType.CONTAINER_EDITOR)))
                {
                    return;
                }
            }
            throw new AuthorizationException("User not authorized to create/update Collection " + c.getId());
        }
            
        
    }

	public CollectionImeji retrieve(String id)
	{
        return (CollectionImeji)rdf2Bean.load(ObjectHelper.getURI(CollectionImeji.class, id).toString());
	}
	
	public CollectionImeji retrieve(URI uri)
    {
        return (CollectionImeji)rdf2Bean.load(CollectionImeji.class, uri);
    }
	
	
	
	/**
	 * Search for collections
	 * - Logged-out user:
	 * --Collection must be released
	 * 
	 * -Logged-in users
	 * --Collection is released
	 * --OR Collection is pending AND user is owner
	 * --OR Collection is withdrawn AND user is owner
	 * --OR Collection is pending AND user has grant "Container Editor" for it.
	 * @param user
	 * @param scList
	 * @return
	 */
	public Collection<CollectionImeji> search(List<SearchCriterion> scList, SortCriterion sortCri, int limit, int offset) throws Exception
	{
		String query = createQuery(scList, sortCri, "http://imeji.mpdl.mpg.de/collection", limit, offset);
		Collection<CollectionImeji> res = Sparql.exec(getModel(), CollectionImeji.class, query);
		return res;
	}

  @Override
    protected String getSpecificQuery() throws Exception
    {
      return " . ?s <http://imeji.mpdl.mpg.de/properties> ?props . ?props <http://imeji.mpdl.mpg.de/createdBy> ?createdBy . ?props <http://imeji.mpdl.mpg.de/status> ?status";
    }
	
    @Override
    protected String getSpecificFilter() throws Exception
    {
        //Add filters for user management
        String filter ="(";
      
          
         
         if(user==null)
         {
             
             filter += "?status = <http://imeji.mpdl.mpg.de/status/RELEASED>";
         }
         else
         {
            
             String userUri = "http://xmlns.com/foaf/0.1/Person/" + URLEncoder.encode(user.getEmail(), "UTF-8");
             filter += "?status = <http://imeji.mpdl.mpg.de/status/RELEASED> || ?createdBy=<" +  userUri + ">";
             
            
                 for(Grant grant : user.getGrants())
                 {
                     switch(grant.getGrantType())
                     {
                         case CONTAINER_ADMIN : //Add specifics here
                             
                         default : filter += " || ?s=<" + grant.getGrantFor().toString() + ">";
                     }
                     
                 }
             }
         
     
          filter += ")";
         return filter;
    }

  
	
}
