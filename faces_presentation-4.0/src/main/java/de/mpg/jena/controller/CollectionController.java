package de.mpg.jena.controller;

import java.net.URI;
import java.util.Collection;
import java.util.List;

import com.hp.hpl.jena.tdb.TDB;

import thewebsemantic.Bean2RDF;
import thewebsemantic.RDF2Bean;
import thewebsemantic.Sparql;
import de.mpg.jena.util.ObjectHelper;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.User;
import de.mpg.jena.vo.Properties.Status;


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
	    ic.getProperties().setStatus(Status.PENDING); 
		ic.setId(ObjectHelper.getURI(CollectionImeji.class, Integer.toString(getUniqueId())));
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
	
	public CollectionImeji retrieve(String id)
	{
	    RDF2Bean reader = new RDF2Bean(base);
        return (CollectionImeji)reader.load(ObjectHelper.getURI(CollectionImeji.class, id).toString());
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
	public Collection<CollectionImeji> search(User user, List<SearchCriterion> scList, SortCriterion sortCri, int limit, int offset)
	{
		String query = createQuery(scList, sortCri, "http://imeji.mpdl.mpg.de/collection", limit, offset);
		Collection<CollectionImeji> res = Sparql.exec(getModel(), CollectionImeji.class, query);
		closeModel();
		return res;
	}
	
}
