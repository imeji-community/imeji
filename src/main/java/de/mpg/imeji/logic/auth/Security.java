///**
// * License: src/main/resources/license/escidoc.license
// */
//package de.mpg.imeji.logic.auth;
//
//import javax.wsdl.OperationType;
//
//import org.apache.log4j.Logger;
//
//import de.mpg.imeji.logic.auth.exception.NotAllowedError;
//import de.mpg.imeji.logic.security.Operations.OperationsType;
//import de.mpg.imeji.logic.vo.Container;
//import de.mpg.imeji.logic.vo.Item;
//import de.mpg.imeji.logic.vo.MetadataProfile;
//import de.mpg.imeji.logic.vo.User;
//import de.mpg.imeji.presentation.beans.PropertyBean;
//
///**
// * imeji security: Check authorization for operations (defined by {@link OperationType}) on an imeji {@link Object} for
// * one {@link User}
// * 
// * @author saquet (initial creation)
// * @author $Author$ (last modification)
// * @version $Revision$ $LastChangedDate$
// */
//public class Security
//{
//    private static Logger logger = Logger.getLogger(Security.class);
//
//    /**
//     * Check {@link Authorization} for Java {@link Object} (by looking for their uris)
//     * 
//     * @param op
//     * @param user
//     * @param object
//     * @return
//     */
//    public boolean check(OperationsType op, User user, Object object)
//    {
//        try
//        {
//            if (isAdmin(user))
//                return true;
//            if (object == null)
//                return false;
//            // TODO no rules for users defined so far
//            if (object instanceof User)
//                return true;
//            Authorization auth = new Authorization();
//            switch (op)
//            {
//                case CREATE:
//                    return auth.create(user, getRelevantURIForSecurity(object));
//                case READ:
//                    return auth.read(user, getRelevantURIForSecurity(object));
//                case UPDATE:
//                    return auth.update(user, getRelevantURIForSecurity(object));
//                case DELETE:
//                    return auth.delete(user, getRelevantURIForSecurity(object));
//            }
//        }
//        catch (Exception e)
//        {
//            logger.error("Error in security", e);
//        }
//        return false;
//    }
//
//    /**
//     * True is the user has system administrator role
//     * 
//     * @param user
//     * @return
//     * @throws NotAllowedError
//     */
//    public boolean isAdmin(User user)
//    {
//        Authorization auth = new Authorization();
//        if (user != null)
//            return auth.administrate(user, PropertyBean.baseURI());
//        return false;
//    }
//
//    private String getRelevantURIForSecurity(Object obj)
//    {
//        if (obj instanceof Item)
//            return ((Item)obj).getCollection().toString();
//        else if (obj instanceof Container)
//            return ((Container)obj).getId().toString();
//        else if (obj instanceof MetadataProfile)
//            return ((MetadataProfile)obj).getId().toString();
//        else if (obj instanceof User)
//            return ((User)obj).getId().toString();
//        return null;
//    }
//}
