package de.mpg.jena.controller;

import java.net.URI;
import java.util.Collection;
import java.util.List;

import thewebsemantic.Bean2RDF;
import thewebsemantic.RDF2Bean;
import thewebsemantic.Sparql;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.User;


public class CollectionController extends ImejiController{

	private User user;
	
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
	public void create(CollectionImeji ic) throws Exception
	{
		
		writeCreateProperties(ic.getProperties(), user);
		ic.setId(new URI("http://imeji.mpdl.mpg.de/collection/" + getUniqueId()));
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
	public void update(CollectionImeji ic)
	{
		writeUpdateProperties(ic.getProperties(), user);
		base.begin();
		Bean2RDF writer = new Bean2RDF(base);
		writer.saveDeep(ic);
		base.commit();
	}
	
	public Collection<CollectionImeji> retrieveAll()
	{
		RDF2Bean reader = new RDF2Bean(base);
		return reader.load(CollectionImeji.class);
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
	public Collection<CollectionImeji> search(User user, List<SearchCriterion> scList)
	{
		String query = createQuery(scList, "http://imeji.mpdl.mpg.de/collection");
		return Sparql.exec(base, CollectionImeji.class, query);
	}
	
}
