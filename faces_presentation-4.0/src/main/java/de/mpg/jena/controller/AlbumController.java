package de.mpg.jena.controller;

import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import de.mpg.jena.ImejiBean2RDF;
import de.mpg.jena.ImejiJena;
import de.mpg.jena.ImejiRDF2Bean;
import de.mpg.jena.sparql.ImejiSPARQL;
import de.mpg.jena.sparql.QuerySPARQL;
import de.mpg.jena.sparql.query.QuerySPARQLImpl;
import de.mpg.jena.util.ObjectHelper;
import de.mpg.jena.vo.Album;
import de.mpg.jena.vo.Grant;
import de.mpg.jena.vo.Grant.GrantType;
import de.mpg.jena.vo.Properties.Status;
import de.mpg.jena.vo.User;


public class AlbumController extends ImejiController
{
	private static ImejiRDF2Bean imejiRDF2Bean = null;
	private static ImejiBean2RDF imejiBean2RDF = null;
	
	public AlbumController(User user)
	{
		super(user);
		imejiBean2RDF = new ImejiBean2RDF(ImejiJena.albumModel);
		imejiRDF2Bean = new ImejiRDF2Bean(ImejiJena.albumModel);
	}
	
	/**
	 * Creates a new collection. 
	 * - Add a unique id
	 * - Write user properties
	 * @param ic
	 * @param user
	 */
	public void create(Album ic) throws Exception
	{
		writeCreateProperties(ic.getProperties(), user);
	    ic.getProperties().setStatus(Status.PENDING); 
		ic.setId(new URI("http://imeji.mpdl.mpg.de/album/" + getUniqueId()));
		imejiBean2RDF = new ImejiBean2RDF(ImejiJena.albumModel);
		imejiBean2RDF.create(ic, user);
		imejiRDF2Bean = new ImejiRDF2Bean(ImejiJena.albumModel);
		ic = imejiRDF2Bean.load(Album.class, ic.getId().toString());
		user = addCreatorGrant(ic, user);
		cleanGraph(ImejiJena.albumModel);
	}
	
	public User addCreatorGrant(Album alb, User user) throws Exception
	{
	 	GrantController gc = new GrantController(user);
		Grant grant = new Grant(GrantType.CONTAINER_ADMIN, alb.getId());
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
	public void update(Album ic) throws Exception
	{
		imejiBean2RDF = new ImejiBean2RDF(ImejiJena.albumModel);
		writeUpdateProperties(ic.getProperties(), user);
		imejiBean2RDF.saveDeep(ic, user);
		cleanGraph(ImejiJena.albumModel);
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
    	imejiRDF2Bean = new ImejiRDF2Bean(ImejiJena.albumModel);
    	return imejiRDF2Bean.load(Album.class, ObjectHelper.getURI(Album.class, id).toString());
    }

    public Album retrieve(URI selectedAlbumId)
    {
    	imejiRDF2Bean = new ImejiRDF2Bean(ImejiJena.albumModel);
        return (Album) imejiRDF2Bean.load(selectedAlbumId.toString(), user);
    }
	
	public Collection<Album> retrieveAll()
	{
//		RDF2Bean reader = new RDF2Bean(base);
//		Security security = new Security();
//		if (security.isSysAdmin(user))
//			return reader.load(Album.class);
		return new ArrayList<Album>();
	}
	
	public void delete(Album album, User user) throws Exception
	{
		imejiBean2RDF = new ImejiBean2RDF(ImejiJena.albumModel);
		imejiBean2RDF.delete(album, user);
		cleanGraph(ImejiJena.albumModel);
	}
	
	
	public synchronized void release(Album album) throws Exception
    {
        //first check user credentials
        album.getProperties().setStatus(Status.RELEASED);
        album.getProperties().setVersionDate(new Date());
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
	    QuerySPARQL querySPARQL = new QuerySPARQLImpl();
	    String query = querySPARQL.createQuery(scList, sortCri,	"http://imeji.mpdl.mpg.de/album", "", "", limit, offset, user, false);
	    return ImejiSPARQL.execAndLoad(query, Album.class);
	}
	
	public int getNumberOfResults(List<SearchCriterion> scList) throws Exception
    {
        QuerySPARQL querySPARQL = new QuerySPARQLImpl();
        String query = querySPARQL.createCountQuery(scList, null, "http://imeji.mpdl.mpg.de/albumn", "", "", -1, 0, user, false);
    	return ImejiSPARQL.execCount(query);
    }
	
//	public Collection<Album> searchAdvanced(List<List<SearchCriterion>> scList, SortCriterion sortCri, int limit, int offset) throws Exception
//    {
//        String query = createQuery("SELECT", scList, sortCri, "http://imeji.mpdl.mpg.de/album", limit, offset);
//        //base.write(System.out);
//        Model base = null;
//        return ImejiSPARQL.execAndLoad(query,  Album.class);
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
