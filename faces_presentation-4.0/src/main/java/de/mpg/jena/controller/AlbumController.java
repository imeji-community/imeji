package de.mpg.jena.controller;

import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Model;

import thewebsemantic.Bean2RDF;
import thewebsemantic.RDF2Bean;
import thewebsemantic.Sparql;
import de.mpg.jena.ImejiJena;
import de.mpg.jena.security.Security;
import de.mpg.jena.sparql.ImejiSPARQL;
import de.mpg.jena.util.ObjectHelper;
import de.mpg.jena.vo.Album;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.Grant;
import de.mpg.jena.vo.User;
import de.mpg.jena.vo.Grant.GrantType;
import de.mpg.jena.vo.Properties.Status;


public class AlbumController extends ImejiController{


	public AlbumController(User user)
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
	public synchronized void create(Album ic) throws Exception
	{
		
		writeCreateProperties(ic.getProperties(), user);
	    ic.getProperties().setStatus(Status.PENDING); 
		ic.setId(new URI("http://imeji.mpdl.mpg.de/album/" + getUniqueId()));
		bean2RDF.saveDeep(ic);
		ic = rdf2Bean.load(Album.class, ic.getId());
		user = addCreatorGrant(ic, user);
		cleanGraph();
	}
	
	public User addCreatorGrant(Album alb, User user) throws Exception
	{
		GrantController gc = new GrantController(user);
		Grant grant = new Grant(GrantType.CONTAINER_ADMIN,alb.getId());
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
	public synchronized void update(Album ic)
	{
		writeUpdateProperties(ic.getProperties(), user);
//		Bean2RDF writer = new Bean2RDF(base);
//		writer.saveDeep(ic);
		cleanGraph();
	}
	
	/**
     * Updates a collection
     * -Logged in users:
     * --User is collection owner
     * --OR user is collection editor
     * @param ic
     * @param user
     */
    public Album retrieve(String id)
    {
        
        return rdf2Bean.load(Album.class, ObjectHelper.getURI(Album.class, id).toString());
    }
    

    public Album retrieve(URI selectedAlbumId)
    {
        return rdf2Bean.load(Album.class, selectedAlbumId);
        
    }
	
	public Collection<Album> retrieveAll()
	{
//		RDF2Bean reader = new RDF2Bean(base);
//		Security security = new Security();
//		if (security.isSysAdmin(user))
//			return reader.load(Album.class);
		return new ArrayList<Album>();
	}
	
	public void delete(Album album, User user) throws Exception{
		bean2RDF.delete(album);
	}
	
	
	public synchronized void release(Album album) throws Exception
    {
        //first check user credentials
        album.getProperties().setStatus(Status.RELEASED);
        update(album);
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
	public Collection<Album> search(List<SearchCriterion> scList, SortCriterion sortCri, int limit, int offset) throws Exception
	{
	    List<List<SearchCriterion>> list = new ArrayList<List<SearchCriterion>>();
	    if(scList!=null && scList.size()>0) list.add(scList);
	    String query = createQuery("SELECT", list, sortCri, "http://imeji.mpdl.mpg.de/album", limit, offset);
		//base.write(System.out);
	    Model base = null;
	    return ImejiSPARQL.execAndLoad(query,  Album.class);
	}
	
	public Collection<Album> searchAdvanced(List<List<SearchCriterion>> scList, SortCriterion sortCri, int limit, int offset) throws Exception
    {
        
        String query = createQuery("SELECT", scList, sortCri, "http://imeji.mpdl.mpg.de/album", limit, offset);
        //base.write(System.out);
        Model base = null;
        return ImejiSPARQL.execAndLoad(query,  Album.class);
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
