/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.imeji.search;

import java.util.List;

import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.controller.SearchCriterion.ImejiNamespaces;

public class SearchFormularHelper 
{
	
	public static String getCollectionId(List<SearchCriterion> scList)
	{
		for (SearchCriterion sc : scList)
		{
			if (ImejiNamespaces.IMAGE_COLLECTION.equals(sc.getNamespace()))
			{
				return sc.getValue();
			}
		}
		return null;
	}
}
