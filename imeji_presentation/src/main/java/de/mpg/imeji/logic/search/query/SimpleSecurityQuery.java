/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.search.query;

import de.mpg.imeji.logic.search.vo.SearchIndex;
import de.mpg.imeji.logic.search.vo.SearchPair;
import de.mpg.imeji.logic.vo.Grant;
import de.mpg.imeji.logic.vo.Grant.GrantType;
import de.mpg.imeji.logic.vo.Properties.Status;
import de.mpg.imeji.logic.vo.User;

public class SimpleSecurityQuery
{
    public static String getQuery(User user, SearchPair pair, String rdfType, boolean includeWithdrawn)
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
        if (pair != null && SearchIndex.names.PROPERTIES_STATUS.name().equals(pair.getIndex().getName()))
        {
            f = "?status=<" + pair.getValue() + ">";
            op = " && (";
        }
        String uf = "";
        String imageCollection = null;
        if (pair != null && SearchIndex.names.IMAGE_COLLECTION.equals(pair.getIndex().getName()))
        {
            imageCollection = pair.getValue();
        }
        boolean myImages = (pair != null && SearchIndex.names.MY_IMAGES.equals(pair.getIndex().getName()));
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
                            || GrantType.PRIVILEGED_VIEWER.equals(g.asGrantType())
                            || GrantType.IMAGE_EDITOR.equals(g.asGrantType()))
                    {
                        if (!"".equals(uf))
                        {
                            uf += " || ";
                        }
                        if ("http://imeji.org/terms/collection".equals(rdfType)
                                || "http://imeji.org/terms/album".equals(rdfType))
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
                        uf += "?c=<" + imageCollection + ">";
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
            if (pair == null || (pair != null && !SearchIndex.names.MY_IMAGES.equals(pair.getIndex().getName())))
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
