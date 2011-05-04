package de.mpg.jena.controller;

import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import thewebsemantic.NotFoundException;
import thewebsemantic.RDF2Bean;
import de.mpg.jena.ImejiBean2RDF;
import de.mpg.jena.ImejiJena;
import de.mpg.jena.ImejiRDF2Bean;
import de.mpg.jena.concurrency.locks.Lock;
import de.mpg.jena.concurrency.locks.Locks;
import de.mpg.jena.security.Security;
import de.mpg.jena.sparql.ImejiSPARQL;
import de.mpg.jena.sparql.QuerySPARQL;
import de.mpg.jena.sparql.query.QuerySPARQLImpl;
import de.mpg.jena.util.ObjectHelper;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.Grant;
import de.mpg.jena.vo.Grant.GrantType;
import de.mpg.jena.vo.Image;
import de.mpg.jena.vo.Properties.Status;
import de.mpg.jena.vo.User;


public class CollectionController extends ImejiController
{
	private static ImejiRDF2Bean imejiRDF2Bean = null;
	private static ImejiBean2RDF imejiBean2RDF = null;
	private static Logger logger = Logger.getLogger(CollectionController.class);
	
	public CollectionController(User user)
	{
		super(user);
		imejiBean2RDF = new ImejiBean2RDF(ImejiJena.collectionModel);
        imejiRDF2Bean = new ImejiRDF2Bean(ImejiJena.collectionModel);
	}
	
	/**
	 * Creates a new collection. 
	 * - Add a unique id
	 * - Write user properties
	 * @param ic
	 * @param user
	 */
	public URI create(CollectionImeji ic, URI profile) throws Exception
	{			
		writeCreateProperties(ic.getProperties(), user);
	    ic.getProperties().setStatus(Status.PENDING); 
		ic.setId(ObjectHelper.getURI(CollectionImeji.class, Integer.toString(getUniqueId())));
		ic.setProfile(profile);
		imejiBean2RDF = new ImejiBean2RDF(ImejiJena.collectionModel);
		imejiBean2RDF.create(ic, user);
		user = addCreatorGrant(ic, user);
		cleanGraph(ImejiJena.collectionModel);
		return ic.getId();
	}
	
	private User addCreatorGrant(CollectionImeji c, User user) throws Exception
	{
		GrantController gc = new GrantController(user);
		Grant grant = new Grant(GrantType.CONTAINER_ADMIN, c.getId());
		gc.addGrant(user, grant);
		UserController uc = new UserController(user);
		return uc.retrieve(user.getEmail());
	}

    /**
	 * Updates a collection
	 * @param ic
	 * @param user
	 */
	public void update(CollectionImeji ic) throws Exception
	{
		writeUpdateProperties(ic.getProperties(), user);
		imejiBean2RDF = new ImejiBean2RDF(ImejiJena.collectionModel);
		imejiBean2RDF.saveDeep(ic, user);
		cleanGraph(ImejiJena.collectionModel);
	}
	
	public void release(CollectionImeji ic) throws Exception
    {
		if (hasNoImagesLocked(ic.getImages())) 
		{	
			ic.getProperties().setStatus(Status.RELEASED);
			ic.getProperties().setVersionDate(new Date());
			
			ImageController imageController = new ImageController(user);
			
			for(URI uri: ic.getImages())
		    {
		    	try 
		    	{
		    		imageController.release(imageController.retrieve(uri));
				} 
		    	catch (NotFoundException e) 
				{
					logger.error("Release image error: " + uri + " could not be found");
				}
		    }
		  
	        update(ic);
	        
	        ProfileController pc = new ProfileController(user);
		    pc.retrieve(ic.getProfile());
		    pc.release(pc.retrieve(ic.getProfile()));
		}
		else
		{
			throw new RuntimeException("Collection has at least one image locked by an other user.");
		}
    }
	
	public void delete(CollectionImeji collection, User user) throws Exception
	{	
		if (hasNoImagesLocked(collection.getImages())) 
		{
			ImageController imageController = new ImageController(user);
			
			for(URI uri : collection.getImages())
			{
				try 
				{
					imageController.delete(imageController.retrieve(uri), user);
				} 
				catch (NotFoundException e) 
				{
					logger.error("Delete image error: " + uri + " could not be found");
				}
			}
			
			ProfileController pc = new ProfileController(user);
			
			try 
			{
				pc.delete(pc.retrieve(collection.getProfile()), user);
			} 
			catch (Exception e) 
			{
				logger.warn("Profile " +  collection.getProfile() + " could not be deleted!", e);
			}
			
			imejiBean2RDF = new ImejiBean2RDF(ImejiJena.collectionModel);
			imejiBean2RDF.delete(collection, user);
			cleanGraph(ImejiJena.collectionModel);
		}
		else
		{
			throw new RuntimeException("Collection has at least one image locked by an other user.");
		}
	}
	
	public void withdraw(CollectionImeji ic) throws Exception
    {
		if (hasNoImagesLocked(ic.getImages())) 
		{
			ic.getProperties().setStatus(Status.WITHDRAWN);
			ic.getProperties().setVersionDate(new Date());
			
			ImageController imageController = new ImageController(user);
			
		    for(URI uri: ic.getImages())
		    {
		    	try 
		    	{
		    		imageController.withdraw(imageController.retrieve(uri));
				} 
		    	catch (NotFoundException e) 
		    	{
		    		logger.error("Withdraw image error: " + uri + " could not be found");
				}
		    }
	        update(ic);
		}
		else
		{
			throw new RuntimeException("Collection has at least one image locked by an other user.");
		}
    }
	
	public boolean hasNoImagesLocked(Collection<URI> collection)
	{
		for (URI u : collection)
		{
			if (Locks.isLocked(u.toString(), user.getEmail()))
			{
				return false;
			}
		}
		return true;
	}
	
	public CollectionImeji retrieve(String id)
	{
		imejiRDF2Bean = new ImejiRDF2Bean(ImejiJena.collectionModel);
		return (CollectionImeji)imejiRDF2Bean.load(ObjectHelper.getURI(CollectionImeji.class, id).toString(), user);
	}
	
	public CollectionImeji retrieve(URI uri)
    {
		imejiRDF2Bean = new ImejiRDF2Bean(ImejiJena.collectionModel);
		return (CollectionImeji)imejiRDF2Bean.load(uri.toString(), user);
    }
	
	public int countAllCollections()
	{
		return ImejiSPARQL.execCount("SELECT ?s count(DISTINCT ?s) WHERE { ?s a <http://imeji.mpdl.mpg.de/collection>}");
	}
	
	public int getCollectionSize(String uri)
	{
		String query = "SELECT ?s count(DISTINCT ?s) WHERE { ?s a <http://imeji.mpdl.mpg.de/image> .<" + uri + "> <http://imeji.mpdl.mpg.de/images> ?s }";
		return ImejiSPARQL.execCount(query);
	}
	
	/**
	 * 
	 * @deprecated
	 * @return
	 */
	public Collection<CollectionImeji> retrieveAll()
	{
		Security security = new Security();
		rdf2Bean = new RDF2Bean(ImejiJena.collectionModel);
		if (security.isSysAdmin(user)) 
		{
			return rdf2Bean.load(CollectionImeji.class);
		}
			
		return new ArrayList<CollectionImeji>();
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
		QuerySPARQL querySPARQL = new QuerySPARQLImpl();
	    String query = querySPARQL.createQuery(scList, sortCri,	"http://imeji.mpdl.mpg.de/collection", "", "", limit, offset, user, false);
	    Collection<CollectionImeji> res = ImejiSPARQL.execAndLoad(query, CollectionImeji.class);
	    
		return res;
	}
	
	public int getNumberOfResults(List<SearchCriterion> scList) throws Exception
    {
        QuerySPARQL querySPARQL = new QuerySPARQLImpl();
        String query = querySPARQL.createCountQuery(scList, null, "http://imeji.mpdl.mpg.de/collection", "", "", -1, 0, user, false);
    	return ImejiSPARQL.execCount(query);
    }

	
//	public Collection<CollectionImeji> searchAdvanced(List<List<SearchCriterion>> scList, SortCriterion sortCri, int limit, int offset) throws Exception
//    {
//       
//        String query = createQuery("SELECT", scList, sortCri, "http://imeji.mpdl.mpg.de/collection", limit, offset);
//        Collection<CollectionImeji> res = ImejiSPARQL.execAndLoad(ImejiJena.collectionModel,query, CollectionImeji.class);
//        return res;
//    }

	
	
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
        
        Security security = new Security();
         
        if(user==null)
	    {
	       filter += "?status = <http://imeji.mpdl.mpg.de/status/RELEASED>";
	    }
        else if (security.isSysAdmin(user))
        {
        	filter += "?status = <http://imeji.mpdl.mpg.de/status/RELEASED> || ?status = <http://imeji.mpdl.mpg.de/status/PENDING>";
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
	        			break;
	        		default: 
	        			filter += " || ?s=<" + grant.getGrantFor().toString() + ">";
	        			break;
	             }
	         }   
         }
        filter += ")";
        return filter;
    }
	
}
