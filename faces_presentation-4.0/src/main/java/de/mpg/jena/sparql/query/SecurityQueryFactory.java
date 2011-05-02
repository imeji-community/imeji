package de.mpg.jena.sparql.query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.controller.SearchCriterion.Filtertype;
import de.mpg.jena.controller.SearchCriterion.ImejiNamespaces;
import de.mpg.jena.controller.SearchCriterion.Operator;
import de.mpg.jena.vo.Grant;
import de.mpg.jena.vo.Grant.GrantType;
import de.mpg.jena.vo.User;

/**
 * Create the security part (user roles, objects status/visibility) of the sparql query.
 * @author saquet
 *
 */
public class SecurityQueryFactory 
{
	private Map<String,QueryElement> els = new HashMap<String, QueryElement>();
	private User user = null;
	private String type = "http://imeji.mpdl.mpg.de/image";
	private SearchCriterion status = new SearchCriterion(Operator.OR, ImejiNamespaces.PROPERTIES_STATUS, "http://imeji.mpdl.mpg.de/status/RELEASED" , Filtertype.URI);
	private SearchCriterion imageCollection = null;
	private boolean myImages = false;
	private boolean isCollection = false;
	
	public SecurityQueryFactory(Map<String,QueryElement> els, String type, User user, boolean isCollection) 
	{
		this.els = els;
		this.user = user;
		this.type = type;
		this.isCollection = isCollection;
	}
	
	public String getVariablesAsSparql()
	{
		String v = " . ?s <http://imeji.mpdl.mpg.de/properties> ?props . ?props <http://imeji.mpdl.mpg.de/status> ?status";
		if ("http://imeji.mpdl.mpg.de/image".equals(type))
		{
			 //v += " . ?s <http://imeji.mpdl.mpg.de/visibility> ?visibility";
			 v += " . ?s <http://imeji.mpdl.mpg.de/collection> ?coll";
		}
		if ("http://imeji.mpdl.mpg.de/collection".equals(type) || "http://imeji.mpdl.mpg.de/album".equals(type))
		{
			 //v += " . ?s <http://imeji.mpdl.mpg.de/visibility> ?visibility";
			 v += " . ?s <http://imeji.mpdl.mpg.de/container/metadata> ?contMd";
		}
		return v;
	}
	
	/**
	 * If the query contains search criterion related to security, they are integrated to the security filter and removed for the search filters
	 * @param scList
	 * @return
	 */
	public List<SearchCriterion> setSecuritySearchCriterion(List<SearchCriterion> scList)
	{
		for (int i=0; i < scList.size(); i++)
		{
			if (ImejiNamespaces.PROPERTIES_STATUS.equals(scList.get(i).getNamespace()))
			{
				status = scList.get(i);
				scList.remove(i);
				i--;
			}
			else if (ImejiNamespaces.MY_IMAGES.equals(scList.get(i).getNamespace()))
			{
				myImages = true;
				scList.remove(i);
				i--;
			}
			else if (ImejiNamespaces.IMAGE_COLLECTION.equals(scList.get(i).getNamespace()))
			{
				imageCollection = scList.get(i);
				scList.remove(i);
				i--;
			}
		}
		return scList;
	}
	
	public String getSecurityFilter()
	{
		String f = "";
		String op = " ";

		if ("http://imeji.mpdl.mpg.de/image".equals(type) && !isCollection)
		{
			f+= "?status!=<http://imeji.mpdl.mpg.de/status/WITHDRAWN> && (";
		}

		if ((!myImages && imageCollection == null) || (Operator.AND.equals(status.getOperator())))
		{
			f+="?status=<" + status.getValue() + ">";
			
			if (Operator.AND.equals(status.getOperator()) || imageCollection != null)
			{
				op = " && ";
			}
			else
			{
				op = " || ";
			}
		}
		String uf ="";
		boolean hasGrantForCollection = false;
		if (user != null && user.getGrants() != null && !user.getGrants().isEmpty())
		{
			for (Grant g : user.getGrants())
			{
				if (imageCollection == null || imageCollection.getValue().equals(g.getGrantFor().toString()) || GrantType.SYSADMIN.equals(g.getGrantType()))
				{
					if (	GrantType.CONTAINER_ADMIN.equals(g.getGrantType())
						|| 	GrantType.CONTAINER_EDITOR.equals(g.getGrantType())
						||	GrantType.PRIVILEGED_VIEWER.equals(g.getGrantType()))
					{
						if (!"".equals(uf))
						{
							uf += " || ";
						}
						if (els.get("http://imeji.mpdl.mpg.de/collection") != null) uf += "?" + els.get("http://imeji.mpdl.mpg.de/collection").getName() + "=<" + g.getGrantFor() + ">";
						if (els.get("http://imeji.mpdl.mpg.de/album") != null) uf += "?" + els.get("http://imeji.mpdl.mpg.de/album").getName() + "=<" + g.getGrantFor() + ">";
						
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
						uf += "?coll=<" +imageCollection.getValue() + ">";
					}
				}
			}
		}
		if(imageCollection != null && !hasGrantForCollection)
		{
			uf += "?coll=<" +imageCollection.getValue() + "> && ?status=<http://imeji.mpdl.mpg.de/status/RELEASED>";
		}
		else if (user != null && user.getGrants() != null && user.getGrants().isEmpty() && myImages) f = " false ";
		
		if ("http://imeji.mpdl.mpg.de/image".equals(type) && !isCollection)
		{
			uf+= ")";
		}
		
		if (!"".equals(uf)) f = " .FILTER(" + f + op + "(" + uf + "))";
		else if (!"".equals(f)) f = " .FILTER(" + f + ")";
		
		return f;
	}
}
