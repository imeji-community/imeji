/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.security;

import javax.wsdl.OperationType;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.security.Operations.OperationsType;
import de.mpg.imeji.logic.security.operations.OperationsContainer;
import de.mpg.imeji.logic.security.operations.OperationsImage;
import de.mpg.imeji.logic.security.operations.OperationsProfile;
import de.mpg.imeji.logic.vo.Container;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.User;

/**
 * imeji security: Check authorization for operations (defined by {@link OperationType}) on an imeji {@link Object} for
 * one {@link User}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class Security
{
    private static Logger logger = Logger.getLogger(Security.class);

    /**
     * Check authorization for operations (defined by {@link OperationType}) on an imeji {@link Object} for one
     * {@link User}<br/>
     * - {@link User} can be null (e.g. nobody is logged in)<br/>
     * - Return <code>true</code> if the operation is allowed
     * 
     * @param op
     * @param user
     * @param object
     * @return
     */
    public boolean check(OperationsType op, User user, Object object)
    {
        try
        {
            if (isSysAdmin(user))
                return true;
            if (object == null)
                return false;
            // TODO no rules for users defined so far
            if (object instanceof User)
                return true;
            Operations operation = instantiateOperation(object);
            switch (op)
            {
                case CREATE:
                    return operation.create(user, object);
                case READ:
                    return operation.read(user, object);
                case UPDATE:
                    return operation.update(user, object);
                case DELETE:
                    return operation.delete(user, object);
            }
        }
        catch (Exception e)
        {
            logger.error("Error in security", e);
        }
        return false;
    }

    /**
     * True is the user has system administrator role
     * 
     * @param user
     * @return
     */
    public boolean isSysAdmin(User user)
    {
        Authorization auth = new Authorization();
        if (user != null)
            return auth.isSysAdmin(user);
        return false;
    }

    /**
     * True if the {@link User} has privileged viewer role for this {@link Container}
     * 
     * @param user
     * @param uriContainer
     * @return
     */
    public boolean isPrivilegedViewer(User user, Item item)
    {
        OperationsImage op = new OperationsImage();
        return op.readRestricted(user, item);
    }

    /**
     * Create {@link Operations} according to the {@link Object} type (iten, Container, MetadataProfile)
     * 
     * @param object
     * @return
     */
    private Operations instantiateOperation(Object object)
    {
        if (object instanceof Item)
        {
            return new OperationsImage();
        }
        else if (object instanceof Container)
        {
            return new OperationsContainer();
        }
        else if (object instanceof MetadataProfile)
        {
            return new OperationsProfile();
        }
        return null;
    }
}
