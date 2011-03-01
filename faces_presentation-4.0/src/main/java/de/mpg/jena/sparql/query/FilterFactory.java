package de.mpg.jena.sparql.query;

import java.util.List;
import java.util.Map;

import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.controller.SearchCriterion.Filtertype;
import de.mpg.jena.controller.SearchCriterion.ImejiNamespaces;
import de.mpg.jena.controller.SearchCriterion.Operator;
import de.mpg.jena.vo.Grant;
import de.mpg.jena.vo.Grant.GrantType;
import de.mpg.jena.vo.User;

public class FilterFactory 
{
	public static String getFilter(List<SearchCriterion> scList, Map<String, QueryElement> els, User user, String specificFilter)
	{
		String sf = generateSearchFilters(scList, els);
		String uf = generateUserFilters(user, els);
		
		String filter = " .FILTER( " + uf + " ) ";
		if (!"( )".equals(sf.trim()))
		{
			filter += "  .FILTER( " + sf + " )";
		}
		
		if (!"".equals(specificFilter.trim()))
		{
			filter += " .FILTER( " + specificFilter + " )";
		}
		
		return filter;
	}
	
	private static String generateSearchFilters(List<SearchCriterion> scList, Map<String, QueryElement> els)
	{
		String filter =" (";
		if (scList != null)
		{
			for(SearchCriterion sc : scList)
			{
				if (!sc.getChildren().isEmpty())
				{
					String sub = generateSearchFilters(sc.getChildren(), els);
					if (!"( )".equals(sub.trim())) filter += sub;
				}
				String newFilter = "";
				if (sc.getNamespace() != null)
				{
					 newFilter= getFilterString(sc, els.get(sc.getNamespace().getNs()).getName());
				}
				if (!"(".equals(filter.trim()) && !"".equals(newFilter))
				{
					filter += getOperatorString(sc) + newFilter;
				}
				else if (sc.getOperator().equals(Operator.NOT))
				{
					//filter += getOperatorString(sc) + "{ " + newFilter + " }";
				}
				else filter += newFilter;
			}
		}
		filter += " ) ";
		return filter;
	}
	
	private static String generateUserFilters(User user, Map<String, QueryElement> els)
	{
		String f = "";
		
		if(els.get("http://imeji.mpdl.mpg.de/visibility") != null)
		{
			f +=  "?" + els.get("http://imeji.mpdl.mpg.de/visibility").getName() + "=<http://imeji.mpdl.mpg.de/image/visibility/PUBLIC>";
		}
		if(els.get("http://imeji.mpdl.mpg.de/status") != null)
		{
			if (!"".equals(f)) f += " || ";
			f +=  "?" + els.get("http://imeji.mpdl.mpg.de/status").getName() + "=<http://imeji.mpdl.mpg.de/status/RELEASED>";
		}
		
		if (user != null && user.getGrants() != null && !user.getGrants().isEmpty())
		{
			for (Grant g : user.getGrants())
			{
				if (	GrantType.CONTAINER_ADMIN.equals(g.getGrantType())
					|| 	GrantType.CONTAINER_EDITOR.equals(g.getGrantType())
					||	GrantType.PRIVILEGED_VIEWER.equals(g.getGrantType()))
				{
					if (!"".equals(f)) f += " || ";
					f += "?" + els.get("http://imeji.mpdl.mpg.de/collection").getName() + "=<" + g.getGrantFor() + ">";
				}
				else if(GrantType.SYSADMIN.equals(g.getGrantType()))
				{
					if (!"".equals(f)) f += " || ";
					f += " true";
				}
			}
		}
		
		return f;
	}
	
	private static String getOperatorString(SearchCriterion sc)
	{
		if (sc.getOperator().equals(SearchCriterion.Operator.AND))
		{
			return " && ";
		}
        else if (sc.getOperator().equals(SearchCriterion.Operator.OR))
        {
        	return " || ";
        }
        else if (sc.getOperator().equals(SearchCriterion.Operator.NOT))
        {
        	return " NOT EXISTS ";
        }
		return " && ";
	}
	
	
	private static String getFilterString(SearchCriterion sc, String variable)
	{
		String filter ="";
		variable = "?" + variable;
		
		if (sc.getValue() != null)
        {
			if (sc.getFilterType().equals(Filtertype.URI) && !sc.getNamespace().equals(ImejiNamespaces.ID_URI))
            {
                filter += "str(" + variable + ")='" + sc.getValue() + "'";
            }
            else if (sc.getFilterType().equals(Filtertype.URI) && sc.getNamespace().equals(ImejiNamespaces.ID_URI))
            {
                filter += "?s" + "=<" + sc.getValue() + ">";
            }
            else if (sc.getFilterType().equals(Filtertype.REGEX))
            {
                filter += "regex(" + variable + ", '"  + sc.getValue() + "', 'i')";
            }
            else if (sc.getFilterType().equals(Filtertype.EQUALS))
            {
                filter += variable + "='" + sc.getValue()+ "'";
            }
            else if (sc.getFilterType().equals(Filtertype.BOUND))
            {
                filter += "bound(" + variable + ")=" + sc.getValue() + "";
            }
            else if (sc.getFilterType().equals(Filtertype.EQUALS_NUMBER))
            {
            	try{ Double d = Double.valueOf(sc.getValue());
            	filter += variable + "='" + d + "'^^<http://www.w3.org/2001/XMLSchema#double>";}
            	catch (Exception e) {/* Not a double*/}
            }
            else if (sc.getFilterType().equals(Filtertype.GREATER_NUMBER))
            {
            	try{ Double d = Double.valueOf(sc.getValue());
            	filter += variable + ">='" + d + "'^^<http://www.w3.org/2001/XMLSchema#double>";}
            	catch (Exception e) {/* Not a double*/}
            }
            else if (sc.getFilterType().equals(Filtertype.LESSER_NUMBER))
            {
            	try{ Double d = Double.valueOf(sc.getValue());
            	filter += variable + "<='" + d + "'^^<http://www.w3.org/2001/XMLSchema#double>";}
            	catch (Exception e) {/* Not a double*/}
            }
            else if (sc.getFilterType().equals(Filtertype.EQUALS_DATE))
            {
                filter += variable + "='" + sc.getValue() + "'^^<http://www.w3.org/2001/XMLSchema#dateTime>";
            }
            else if (sc.getFilterType().equals(Filtertype.GREATER_DATE))
            {
                filter += variable + ">='" + sc.getValue() + "'^^<http://www.w3.org/2001/XMLSchema#dateTime>";
            }
            else if (sc.getFilterType().equals(Filtertype.LESSER_DATE))
            {
                filter += variable + "<='" + sc.getValue() + "'^^<http://www.w3.org/2001/XMLSchema#dateTime>";
            }
        }
		return filter;
	}
}
