/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.search.query;

import de.mpg.imeji.logic.search.vo.SearchIndexes;
import de.mpg.imeji.logic.search.vo.SearchCriterion;
import de.mpg.imeji.logic.search.vo.SearchCriterion.Operator;
import de.mpg.imeji.logic.vo.Grant;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.logic.vo.Grant.GrantType;

public class SimpleSecurityQuery
{
    public static String getQuery(User user, SearchCriterion sc, String type, boolean includeWithdrawn)
    {
        String f = "?status!=<http://imeji.org/terms/status/WITHDRAWN> && (";
        if (includeWithdrawn)
        {
            f = "(";
        }
        String op = " ";
        if (user == null || user.getGrants().isEmpty())
        {
            if (includeWithdrawn)
                return " .FILTER(?status!=<http://imeji.org/terms/status/PENDING>)";
            return " .FILTER(?status=<http://imeji.org/terms/status/RELEASED>)";
        }
        if (sc != null && SearchIndexes.PROPERTIES_STATUS.equals(sc.getNamespace()))
        {
            f = "?status=<" + sc.getValue() + ">";
            if (Operator.AND.equals(sc.getOperator()))
            {
                op = " && (";
            }
            else
            {
                op = " || (";
            }
        }
        String uf = "";
        String imageCollection = null;
        if (sc != null && SearchIndexes.IMAGE_COLLECTION.equals(sc.getNamespace()))
        {
            imageCollection = sc.getValue();
        }
        boolean myImages = sc != null && SearchIndexes.MY_IMAGES.equals(sc.getNamespace());
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
                        if ("http://imeji.org/terms/collection".equals(type)
                                || "http://imeji.org/terms/album".equals(type))
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
            uf += "?c=<" + imageCollection + "> && ?status=<http://imeji.org/terms/status/RELEASED>";
        }
        else if (user != null && user.getGrants() != null && user.getGrants().isEmpty() && myImages)
        {
            f = " false ";
        }
        uf += ")";
        if (!"".equals(uf.trim()))
        {
            f = " .FILTER(" + f + op + "(";
            if (sc == null || (sc != null && !SearchIndexes.MY_IMAGES.equals(sc.getNamespace())))
            {
                f += "?status=<http://imeji.org/terms/status/RELEASED> || ";
            }
            f += uf + "))";
        }
        else if (!"".equals(f.trim()))
        {
            f = " .FILTER(" + f + ")";
        }
        else if ("".equals(f))
        {
            f = " .FILTER(?status=<http://imeji.org/terms/status/RELEASED>)";
        }
        return f;
    }
}
