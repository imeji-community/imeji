/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.search.query;

import de.mpg.imeji.logic.vo.Album;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Grant;
import de.mpg.imeji.logic.vo.Grant.GrantType;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Properties.Status;
import de.mpg.imeji.logic.vo.User;
import de.mpg.j2j.helper.J2JHelper;

/**
 * Simple security query add to any imeji sparql query, a security filter (according to user, searchtype, etc)
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class SimpleSecurityQuery
{
    /**
     * Static factory for the security query. A {@link String} is returned which sould be added to the complete sparql
     * query
     * 
     * @param user
     * @param pair
     * @param rdfType
     * @param includeWithdrawn
     * @return
     */
    public static String queryFactory(User user, String rdfType, Status status)
    {
        String statusFilter = getStatusAsFilter(status);
        if (Status.RELEASED.equals(status) || Status.WITHDRAWN.equals(status))
        {
            // If searching for released or withdrawn objects, no grant filter is needed (released and withdrawn objects
            // are all public)
            return statusFilter + " . ?s a <" + rdfType + "> ";
        }
        else if (user != null && user.getGrants().isEmpty() && Status.PENDING.equals(status))
        {
            // special case: a user without grants wants to see private objects: not possible
            return ".FILTER(false)";
        }
        //
        else if ((user == null || user.getGrants().isEmpty()))
        {
            // if user is null or has no rights, then can only see the released objects
            return " .FILTER(?status=<" + Status.RELEASED.getUri() + ">) . ?s a <" + rdfType + ">";
        }
        // else, check the grant and add the status filter...
        return getUserGrantsAsFilter(user, rdfType) + statusFilter + " . ?s a <" + rdfType + "> ";
    }

    /**
     * Return a SPARQL Filter with the allowed status of an object
     * 
     * @param includeWithdrawn
     * @return
     */
    private static String getStatusAsFilter(Status status)
    {
        if (status == null)
        {
            return " . FILTER (?status!=<" + Status.WITHDRAWN.getUri() + ">)";
        }
        else
        {
            return " . FILTER (?status=<" + status.getUri() + ">)";
        }
    }

    /**
     * Return a SPARQL Filter with all Grants of one {@link User}
     * 
     * @param user
     * @param rdfType
     * @return
     */
    public static String getUserGrantsAsFilter(User user, String rdfType)
    {
        String privileges = "";
        if (user != null && user.getGrants() != null && !user.getGrants().isEmpty())
        {
            int count = 0;
            for (Grant g : user.getGrants())
            {
                if (GrantType.CONTAINER_ADMIN.equals(g.asGrantType())
                        || GrantType.CONTAINER_EDITOR.equals(g.asGrantType())
                        || GrantType.VIEWER.equals(g.asGrantType())
                        || GrantType.IMAGE_EDITOR.equals(g.asGrantType())
                        || (J2JHelper.getResourceNamespace(new MetadataProfile()).equals(rdfType) && GrantType.PROFILE_ADMIN
                                .equals(g.asGrantType())))
                {
                    if (count > 0)
                    {
                        privileges += " || ";
                    }
                    privileges += getVariableName(rdfType) + "=<" + g.getGrantFor() + ">";
                    count++;
                }
                else if (GrantType.SYSADMIN.equals(g.asGrantType()))
                {
                    if (count > 0)
                        privileges += " || ";
                    privileges += "true";
                    count++;
                }
            }
        }
        if (!"".equals(privileges))
        {
            return " . FILTER(" + privileges + " || ?status=<http://imeji.org/terms/status#RELEASED>)";
        }
        return " . FILTER(?status=<http://imeji.org/terms/status#RELEASED>)";
    }

    /**
     * Return the variable name of for the object on with the security is checked
     * 
     * @param rdfType
     * @return
     */
    public static String getVariableName(String rdfType)
    {
        if (J2JHelper.getResourceNamespace(new CollectionImeji()).equals(rdfType)
                || J2JHelper.getResourceNamespace(new Album()).equals(rdfType)
                || J2JHelper.getResourceNamespace(new MetadataProfile()).equals(rdfType))
        {
            return "?s";
        }
        else
        {
            return "?c";
        }
    }
}
