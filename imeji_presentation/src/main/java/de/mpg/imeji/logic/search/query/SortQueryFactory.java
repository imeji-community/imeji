/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.imeji.logic.search.query;

import de.mpg.imeji.logic.search.vo.SearchIndexes;
import de.mpg.imeji.logic.search.vo.SortCriterion;
import de.mpg.imeji.logic.search.vo.SortCriterion.SortOrder;

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
		if (SearchIndexes.CONTAINER_METADATA_TITLE.equals(sortCriterion.getSortingCriterion()))
		{
			return "fn:lower-case(?" + name + ")";
		}
		else
		{
			return "?" + name;
		}
	}
	
}
