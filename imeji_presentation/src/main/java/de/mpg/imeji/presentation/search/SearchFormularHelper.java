/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.imeji.presentation.search;

import java.util.List;

import de.mpg.imeji.logic.search.vo.SearchIndexes;
import de.mpg.imeji.logic.search.vo.SearchCriterion;

public class SearchFormularHelper 
{
	
	public static String getCollectionId(List<SearchCriterion> scList)
	{
		for (SearchCriterion sc : scList)
		{
			if (SearchIndexes.IMAGE_COLLECTION.equals(sc.getNamespace()))
			{
				return sc.getValue();
			}
		}
		return null;
	}
}
