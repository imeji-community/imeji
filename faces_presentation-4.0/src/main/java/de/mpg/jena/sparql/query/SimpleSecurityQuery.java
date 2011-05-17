package de.mpg.jena.sparql.query;

import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.controller.SearchCriterion.ImejiNamespaces;
import de.mpg.jena.controller.SearchCriterion.Operator;
import de.mpg.jena.vo.Grant;
import de.mpg.jena.vo.Grant.GrantType;
import de.mpg.jena.vo.User;

public class SimpleSecurityQuery 
{
	public static String getQuery(User user, SearchCriterion sc, String type, boolean includeWithdrawn)
	{
		String f = "?status!=<http://imeji.mpdl.mpg.de/status/WITHDRAWN> && (";
		
		if(includeWithdrawn)
		{
			f= "(";	
		}
		
		String op = " ";

		if (user == null)
		{
			return  " .FILTER(?status=<http://imeji.mpdl.mpg.de/status/RELEASED>)";
		}
		
		if (sc != null && ImejiNamespaces.PROPERTIES_STATUS.equals(sc.getNamespace()))
		{
			f = "?status=<" +  sc.getValue() + ">";

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
		
		if (sc != null && ImejiNamespaces.IMAGE_COLLECTION.equals(sc.getNamespace()))
		{
			imageCollection = sc.getValue();
		}
		
		boolean myImages = sc!= null && ImejiNamespaces.MY_IMAGES.equals(sc.getNamespace());
		
		boolean hasGrantForCollection = false;
		
		if (user != null && user.getGrants() != null && !user.getGrants().isEmpty())
		{
			for (Grant g : user.getGrants())
			{
				if (imageCollection == null || imageCollection.equals(g.getGrantFor().toString()) || GrantType.SYSADMIN.equals(g.getGrantType()))
				{
					if (	GrantType.CONTAINER_ADMIN.equals(g.getGrantType())
						|| 	GrantType.CONTAINER_EDITOR.equals(g.getGrantType())
						||	GrantType.PRIVILEGED_VIEWER.equals(g.getGrantType())
						|| 	GrantType.IMAGE_EDITOR.equals(g.getGrantType()))
					{
						if (!"".equals(uf))
						{
							uf += " || ";
						}
						
						if ("http://imeji.mpdl.mpg.de/collection".equals(type) || "http://imeji.mpdl.mpg.de/album".equals(type))
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
					else if(GrantType.SYSADMIN.equals(g.getGrantType()) && imageCollection == null )
					{
						if (!"".equals(uf)) uf += " || ";
						uf += " true";
						hasGrantForCollection = true;
					}
					else if (imageCollection != null )
					{
						uf += "?c=<" +imageCollection + ">";
					}
				}
			}
		}

		if(imageCollection != null && !hasGrantForCollection)
		{
			uf += "?c=<" +imageCollection + "> && ?status=<http://imeji.mpdl.mpg.de/status/RELEASED>";
		}
		else if (user != null && user.getGrants() != null && user.getGrants().isEmpty() && myImages) 
		{
			f = " false ";
		}
		
		uf+= ")";
		
		if (!"".equals(uf)) 
		{
			f = " .FILTER(" + f + op + "(";
			if (sc == null || (sc != null  && !ImejiNamespaces.MY_IMAGES.equals(sc.getNamespace())))
			{
				f += "?status=<http://imeji.mpdl.mpg.de/status/RELEASED> || ";
			}
			f += uf + "))";
		}
		else if (!"".equals(f)) 
		{
			f = " .FILTER(" + f + ")";
		}
		else if ("".equals(f))
		{
			f = " .FILTER(?status=<http://imeji.mpdl.mpg.de/status/RELEASED>)";
		}

		return f;
	}
}
