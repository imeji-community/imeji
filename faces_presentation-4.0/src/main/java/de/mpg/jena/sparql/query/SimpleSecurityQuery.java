package de.mpg.jena.sparql.query;

import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.controller.SearchCriterion.ImejiNamespaces;
import de.mpg.jena.controller.SearchCriterion.Operator;
import de.mpg.jena.vo.Grant.GrantType;
import de.mpg.jena.vo.Properties.Status;
import de.mpg.jena.vo.Grant;
import de.mpg.jena.vo.User;

public class SimpleSecurityQuery 
{
	public static String getQuery(User user, SearchCriterion sc)
	{
		String f = "?status!=<http://imeji.mpdl.mpg.de/status/WITHDRAWN> && (";
		String op = " ";
		
		
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
		
		if (ImejiNamespaces.IMAGE_COLLECTION.equals(sc.getNamespace()))
		{
			imageCollection = sc.getValue();
		}
		
		boolean myImages = ImejiNamespaces.MY_IMAGES.equals(sc.getNamespace());
		
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
						uf += "?coll=<" + g.getGrantFor() + ">";
						//if (els.get("http://imeji.mpdl.mpg.de/album") != null) uf += "?" + els.get("http://imeji.mpdl.mpg.de/album").getName() + "=<" + g.getGrantFor() + ">";
						
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
						uf += "?coll=<" +imageCollection + ">";
					}
				}
			}
		}

		if(imageCollection != null && !hasGrantForCollection)
		{
			uf += "?coll=<" +imageCollection + "> && ?status=<http://imeji.mpdl.mpg.de/status/RELEASED>";
		}
		else if (user != null && user.getGrants() != null && user.getGrants().isEmpty() && myImages) 
		{
			f = " false ";
		}
		
		uf+= ")";
		
//		if ("http://imeji.mpdl.mpg.de/image".equals(type) && !isCollection && user != null)
//		{
//			uf+= ")";
//		}
		
		if (!"".equals(uf)) 
		{
			f = " .FILTER(" + f + op + "( ?status=<http://imeji.mpdl.mpg.de/status/RELEASED> || " + uf + "))";
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
