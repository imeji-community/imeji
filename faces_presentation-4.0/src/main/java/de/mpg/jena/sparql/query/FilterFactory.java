package de.mpg.jena.sparql.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.controller.SearchCriterion.Filtertype;
import de.mpg.jena.controller.SearchCriterion.ImejiNamespaces;
import de.mpg.jena.controller.SearchCriterion.Operator;
import de.mpg.jena.util.DateFormatter;
import de.mpg.jena.vo.Grant;
import de.mpg.jena.vo.Grant.GrantType;
import de.mpg.jena.vo.User;

public class FilterFactory 
{
	public static String getFilter(List<SearchCriterion> scList, Map<String, QueryElement> els, User user, String specificFilter)
	{
		String sf = getSearchFilters(scList, els);
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
	
	public static String getSearchFilters(List<SearchCriterion> scList, Map<String, QueryElement> els)
	{
		String filter =" (";
		if (scList != null)
		{
			for(SearchCriterion sc : scList)
			{
				if (els.get(sc.getNamespace().getNs()).isOptional())
				{
					if (!sc.getChildren().isEmpty())
					{
						String sub = getSearchFilters(sc.getChildren(), els);
						if (!"( )".equals(sub.trim())) filter += sub;
					}
					String newFilter = "";
					if (sc.getNamespace() != null)
					{
						 newFilter= getSimpleFilter(sc, els.get(sc.getNamespace().getNs()).getName());
					}
					if (!"(".equals(filter.trim()) && !"".equals(newFilter))
					{
						filter += getOperatorString(sc) + newFilter;
					}
					else filter += newFilter;
				}
			}
		}
		filter += " ) ";
		return filter;
	}
	
	public static String getAdvancedFilter(List<SearchCriterion> scList, Map<String, SubQuery> subQueries, Map<String, QueryElement> els)
	{
		String f = "";
		
		List<SearchCriterion> firstParents = new ArrayList<SearchCriterion>();
		
		for (SearchCriterion sc: scList)
		{
			SearchCriterion lpsc = findLastParent(sc);
			if (lpsc != null) firstParents.add(lpsc);
		}
		
		for (SearchCriterion sc: firstParents)
		{
			if (sc.getNamespace() != null && sc.getNamespace().equals(ImejiNamespaces.ID_URI))
			{
				if (!"".equals(f)) f+= getOperatorString(sc);
				f += "?s" + "=<" + sc.getValue() + ">";
			}
			else
			{
				if(sc.getNamespace() != null && !"".equals(subQueries.get(sc.getNamespace().getNs() + sc.getFilterType() + sc.getValue()).print()))
				{
					if (!"".equals(f)) f+= getOperatorString(sc);
					f += "?s" + "=?" + subQueries.get(sc.getNamespace().getNs() + sc.getFilterType() + sc.getValue()).getName();
				}
				else if (!sc.getChildren().isEmpty())
				{
					if (!"".equals(f)) f+= getOperatorString(sc);
					f += "("  + getAdvancedFilter(sc.getChildren(), subQueries, els) + ")";
				}
			}
		}
		
		return f;
	}
	
	private static SearchCriterion findLastParent(SearchCriterion sc)
	{
		if (sc.getParent() != null) findLastParent(sc.getParent());
		return sc;
	}
	
	
	public static String generateUserFilters(User user, Map<String, QueryElement> els)
	{
		String f = "";
		
		if(els.get("http://imeji.mpdl.mpg.de/visibility") != null)
		{
			f +=  "?" + els.get("http://imeji.mpdl.mpg.de/visibility").getName() + "=";
			if (els.get("http://imeji.mpdl.mpg.de/visibility").getValue() == null) f+= "<http://imeji.mpdl.mpg.de/status/PUBLIC>";
			else  f+= "<http://imeji.mpdl.mpg.de/visibility/"+ els.get("http://imeji.mpdl.mpg.de/visibility").getValue() + ">";
		}
		if(els.get("http://imeji.mpdl.mpg.de/status") != null)
		{
			if (!"".equals(f)) f += " || ";
			f +=  "?" + els.get("http://imeji.mpdl.mpg.de/status").getName() + "=";
			if (els.get("http://imeji.mpdl.mpg.de/status").getValue() == null) f+= "<http://imeji.mpdl.mpg.de/status/RELEASED>";
			else  f+= "<http://imeji.mpdl.mpg.de/status/"+ els.get("http://imeji.mpdl.mpg.de/status").getValue() + ">";
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
					if (els.get("http://imeji.mpdl.mpg.de/collection") != null) f += "?" + els.get("http://imeji.mpdl.mpg.de/collection").getName() + "=<" + g.getGrantFor() + ">";
					if (els.get("http://imeji.mpdl.mpg.de/album") != null) f += "?" + els.get("http://imeji.mpdl.mpg.de/album").getName() + "=<" + g.getGrantFor() + ">";
				}
				else if(GrantType.SYSADMIN.equals(g.getGrantType()))
				{
					if (!"".equals(f)) f += " || ";
					f += " true";
				}
			}
		}
		f = " .FILTER(" + f + ")";
		return f;
	}
	
	private static String getOperatorString(SearchCriterion sc)
	{
		if (sc.getOperator().equals(Operator.AND) || sc.getOperator().equals(Operator.NOTAND))
		{
			return " && ";
		}
        else if (sc.getOperator().equals(Operator.OR) || sc.getOperator().equals(Operator.NOTOR))
        {
        	return " || ";
        }
		return " && ";
	}
	
	
	public static String getSimpleFilter(SearchCriterion sc, String variable)
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
                filter +=  variable + "=<" + sc.getValue() + ">";
            }
            else if (sc.getFilterType().equals(Filtertype.REGEX))
            {
                filter += "regex(" + variable + ", '"  + sc.getValue() + "', 'i')";
            }
            else if (sc.getFilterType().equals(Filtertype.EQUALS))
            {
                filter += variable + "='" + sc.getValue()+ "'";
            }
            else if (sc.getFilterType().equals(Filtertype.NOT))
            {
                filter +=  variable + "!='" + sc.getValue() + "'";
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
            	filter += variable + "='" + DateFormatter.format(sc.getValue()) + "'^^<http://www.w3.org/2001/XMLSchema#dateTime>";
            }
            else if (sc.getFilterType().equals(Filtertype.GREATER_DATE))
            {
				filter += variable + ">='" + DateFormatter.format(sc.getValue()) + "'^^<http://www.w3.org/2001/XMLSchema#dateTime>";
            }
            else if (sc.getFilterType().equals(Filtertype.LESSER_DATE))
            {
				filter += variable + "<='" + DateFormatter.format(sc.getValue()) + "'^^<http://www.w3.org/2001/XMLSchema#dateTime>";
            }
        }
		return filter;
	}
}
