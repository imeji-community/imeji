package de.mpg.jena.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import thewebsemantic.Bean2RDF;
import thewebsemantic.RDF2Bean;
import thewebsemantic.Sparql;
import de.mpg.jena.vo.Album;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.User;
import de.mpg.jena.vo.Properties.Status;


public class AlbumController extends ImejiController{

	private User user;
	
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
	public void create(Album ic) throws Exception
	{
		
		writeCreateProperties(ic.getProperties(), user);
	    ic.getProperties().setStatus(Status.PENDING); 
		ic.setId(new URI("http://imeji.mpdl.mpg.de/album/" + getUniqueId()));
		base.begin();
		Bean2RDF writer = new Bean2RDF(base);
		writer.saveDeep(ic);
		base.commit();
	}
	
	/**
	 * Updates a collection
	 * -Logged in users:
	 * --User is collection owner
	 * --OR user is collection editor
	 * @param ic
	 * @param user
	 */
	public void update(Album ic)
	{
		writeUpdateProperties(ic.getProperties(), user);
		base.begin();
		Bean2RDF writer = new Bean2RDF(base);
		writer.saveDeep(ic);
		base.commit();
	}
	
	public Collection<Album> retrieveAll()
	{
		RDF2Bean reader = new RDF2Bean(base);
		return reader.load(Album.class);
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
	    String query = createQuery(list, sortCri, "http://imeji.mpdl.mpg.de/album", limit, offset);
		//base.write(System.out);
		return Sparql.exec(base, Album.class, query);
	}
	
	public Collection<Album> searchAdvanced(List<List<SearchCriterion>> scList, SortCriterion sortCri, int limit, int offset) throws Exception
    {
        
        String query = createQuery(scList, sortCri, "http://imeji.mpdl.mpg.de/album", limit, offset);
        //base.write(System.out);
        return Sparql.exec(base, Album.class, query);
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
