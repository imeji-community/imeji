/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.jena.search.query;

import de.mpg.jena.controller.SearchCriterion.ImejiNamespaces;
import de.mpg.jena.controller.SortCriterion;
import de.mpg.jena.controller.SortCriterion.SortOrder;

public class SortQueryFactory 
{	
	public static String create(SortCriterion sortCriterion)
	{
		String q = "";
        if (sortCriterion != null)
        {
	        if (sortCriterion.getSortOrder().equals(SortOrder.DESCENDING))
	        {
	        	q = " ORDER BY DESC(" + lowerCasePrefix(sortCriterion, "sort0") + ") ";
	        }
	        else
	        {
	            q = " ORDER BY " + lowerCasePrefix(sortCriterion, "sort0") + " ";
	        }
        }
		return q;
	}
	
	private static String lowerCasePrefix(SortCriterion sortCriterion, String name)
	{
		if (ImejiNamespaces.CONTAINER_METADATA_TITLE.equals(sortCriterion.getSortingCriterion()))
		{
			return "fn:lower-case(?" + name + ")";
		}
		else
		{
			return "?" + name;
		}
	}
	
}
