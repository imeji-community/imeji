/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.search.query;

import de.mpg.imeji.logic.search.vo.SearchIndex;
import de.mpg.imeji.logic.search.vo.SearchPair;
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
    public static String queryFactory(User user, SearchPair pair, String rdfType, boolean includeWithdrawn)
    {
        String f = "?status!=<" + Status.WITHDRAWN.getUri() + "> && (";
        if (includeWithdrawn)
        {
            f = "(";
        }
        String op = " ";
        if (user == null || user.getGrants().isEmpty())
        {
            if (includeWithdrawn)
            {
                return " .FILTER(?status!=<" + Status.PENDING.getUri() + ">)";
            }
            return " .FILTER(?status=<" + Status.RELEASED.getUri() + ">)";
        }
        if (pair != null && SearchIndex.names.status.name().equals(pair.getIndex().getName()))
        {
            f = "?status=<" + pair.getValue() + ">";
            op = " && (";
        }
        String uf = "";
        String imageCollection = null;
        if (pair != null && SearchIndex.names.col.name().equals(pair.getIndex().getName()))
        {
            imageCollection = pair.getValue();
        }
        boolean myImages = (pair != null && SearchIndex.names.user.name().equals(pair.getIndex().getName()));
        boolean hasGrantForCollection = false;
        if (user != null && user.getGrants() != null && !user.getGrants().isEmpty())
        {
            for (Grant g : user.getGrants())
            {
                if (imageCollection == null || imageCollection.equals(g.getGrantFor().toString())
                        || GrantType.SYSADMIN.equals(g.asGrantType()))
                {
                    if (GrantType.CONTAINER_ADMIN.equals(g.asGrantType())
                            || GrantType.CONTAINER_EDITOR.equals(g.asGrantType())
                            || GrantType.VIEWER.equals(g.asGrantType())
                            || GrantType.PRIVILEGED_VIEWER.equals(g.asGrantType())
                            || GrantType.IMAGE_EDITOR.equals(g.asGrantType())
                            || (J2JHelper.getResourceNamespace(new MetadataProfile()).equals(rdfType) && GrantType.PROFILE_ADMIN
                                    .equals(g.asGrantType())))
                    {
                        if (!"".equals(uf))
                        {
                            uf += " || ";
                        }
                        if (J2JHelper.getResourceNamespace(new CollectionImeji()).equals(rdfType)
                                || J2JHelper.getResourceNamespace(new Album()).equals(rdfType)
                                || J2JHelper.getResourceNamespace(new MetadataProfile()).equals(rdfType))
                        {
                            uf += "?s";
                        }
                        else
                        {
                            uf += "?c";
                        }
                        uf += "=<" + g.getGrantFor() + ">";
                        hasGrantForCollection = true;
                    }
                    else if (GrantType.SYSADMIN.equals(g.asGrantType()) && imageCollection == null)
                    {
                        if (!"".equals(uf))
                            uf += " || ";
                        uf += " true";
                        hasGrantForCollection = true;
                    }
                    else if (imageCollection != null)
                    {
                        uf = "?c=<" + imageCollection + ">";
                    }
                }
            }
        }
        if (imageCollection != null && !hasGrantForCollection)
        {
            uf += "?c=<" + imageCollection + "> && ?status=<" + Status.RELEASED.getUri() + ">";
        }
        else if (user != null && user.getGrants() != null && user.getGrants().isEmpty() && myImages)
        {
            f = " false ";
        }
        uf += ")";
        if (!"".equals(uf.trim()))
        {
            f = " .FILTER(" + f + op + "(";
            if (pair == null || (pair != null && !SearchIndex.names.user.toString().equals(pair.getIndex().getName())))
            {
                f += "?status=<" + Status.RELEASED.getUri() + "> || ";
            }
            f += uf + "))";
        }
        else if (!"".equals(f.trim()))
        {
            f = " .FILTER(" + f + ")";
        }
        else if ("".equals(f))
        {
            f = " .FILTER(?status=" + Status.RELEASED.getUri() + ">)";
        }
        return f;
    }
}
