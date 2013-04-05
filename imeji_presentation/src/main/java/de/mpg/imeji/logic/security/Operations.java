/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.security;

import de.mpg.imeji.logic.vo.User;

/**
 * CRUD operations allowed in Imeji. Implemented accorded to: http://colab.mpdl.mpg.de/mediawiki/Imeji_User_Management
 * 
 * @author saquet
 */
public interface Operations
{
    /**
     * CRUD operations.
     */
    public enum OperationsType
    {
        CREATE, READ, UPDATE, DELETE;
    }

    /**
     * True if the {@link User} can CREATE the {@link Object}
     * 
     * @param user
     * @param object
     * @return
     */
    public boolean create(User user, Object object);

    /**
     * True if the {@link User} can READ the {@link Object}
     * 
     * @param user
     * @param object
     * @return
     */
    public boolean read(User user, Object object);

    /**
     * True if the {@link User} can UPDATE the {@link Object}
     * 
     * @param user
     * @param object
     * @return
     */
    public boolean update(User user, Object object);

    /**
     * True if the {@link User} can DELETE the {@link Object}
     * 
     * @param user
     * @param object
     * @return
     */
    public boolean delete(User user, Object object);
}
