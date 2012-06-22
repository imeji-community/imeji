/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.imeji.logic.security;

import de.mpg.imeji.logic.vo.User;

/**
 * CRUD operations allowed in Imeji.
 * 
 * Implemented accorded to: 
 * http://colab.mpdl.mpg.de/mediawiki/Imeji_User_Management
 * 
 * @author saquet
 *
 */
public interface Operations 
{
	/**
 	*	CRUD operations.
 	*/
	public enum OperationsType
	{
		CREATE, READ, UPDATE, DELETE;
	}
	
	public boolean create(User user, Object object);
	
	public boolean read(User user, Object object);
	
	public boolean update(User user, Object object);

	public boolean delete(User user, Object object);
	
}
