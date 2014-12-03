package de.mpg.imeji.rest.api;

import java.util.List;

import javax.ws.rs.NotSupportedException;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.auth.exception.NotAllowedError;
import de.mpg.imeji.logic.vo.User;
import de.mpg.j2j.exceptions.NotFoundException;

/**
 * The generic Interface defining all methods (i.e verbs) available for the
 * imeji objects (Item, Collection, etc.). <br/>
 * <br/>
 * If a method doesn't make sense for one object type, then should return a
 * {@link NotSupportedException} <br/>
 * <br/>
 * Authorization is always done via the {@link User} which is passed in every
 * method
 * 
 * @author saquet
 *
 * @param <V>
 */
public interface API<V> {
	static Logger logger = Logger.getLogger(API.class);

	/**
	 * Create an Object in imeji
	 * 
	 * @param o
	 * @param u
	 * @return
	 */
	public V create(V o, User u) throws NotFoundException, NotAllowedError,
			NotSupportedException, Exception;

	/**
	 * Read an object from imneji with its id
	 * 
	 * @param id
	 * @param u
	 * @return
	 * @throws NotFoundException
	 * @throws NotAllowedError
	 * @throws Exception
	 */
	public V read(String id, User u) throws NotFoundException, NotAllowedError,
			NotSupportedException, Exception;

	/**
	 * Update an object in imeji
	 * 
	 * @param o
	 * @param u
	 * @return
	 */
	public V update(V o, User u) throws NotFoundException, NotAllowedError,
			NotSupportedException, Exception;

	/**
	 * Delete an object in imeji
	 * 
	 * @param o
	 * @param u
	 * @return
	 */
	public boolean delete(String i, User u) throws NotFoundException,
			NotAllowedError, NotSupportedException, Exception;

	/**
	 * Release an object
	 * 
	 * @param o
	 * @param u
	 */
	public void release(V o, User u) throws NotFoundException, NotAllowedError,
			NotSupportedException, Exception;

	/**
	 * Withdraw an object
	 * 
	 * @param o
	 * @param u
	 */
	public void withdraw(V o, User u) throws NotFoundException,
			NotAllowedError, NotSupportedException, Exception;

	/**
	 * Share an object defined by id to a user defined by userid. The roles to
	 * added are passed into a List of String
	 * 
	 * @param id
	 * @param userId
	 * @param roles
	 * @param u
	 */
	public void share(String id, String userId, List<String> roles, User u)
			throws NotFoundException, NotAllowedError, NotSupportedException,
			Exception;

	/**
	 * Unshare an object defined by id from a user defined by userid. The roles
	 * to be removed are passed into a List of String
	 * 
	 * @param id
	 * @param userId
	 * @param roles
	 * @param u
	 */
	public void unshare(String id, String userId, List<String> roles, User u)
			throws NotFoundException, NotAllowedError, NotSupportedException,
			Exception;

	/**
	 * Search for an object according to a query
	 * 
	 * @param q
	 * @param u
	 * @return
	 */
	public List<String> search(String q, User u) throws NotSupportedException,
			Exception;

}
