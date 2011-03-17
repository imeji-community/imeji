package de.mpg.jena.sparql.query;

import java.util.Map;

import de.mpg.jena.controller.SortCriterion;
import de.mpg.jena.controller.SearchCriterion.ImejiNamespaces;
import de.mpg.jena.controller.SortCriterion.SortOrder;

public class SortQueryFactory 
{
	public static String create(SortCriterion sortCriterion,  Map<String, QueryElement> els)
	{
		String q = "";
        if (sortCriterion != null)
        {
            ImejiNamespaces ns = sortCriterion.getSortingCriterion();
            QueryElement sortEl = els.get(ns.getNs());
            if (sortEl != null)
            {
	            if (sortCriterion.getSortOrder().equals(SortOrder.DESCENDING))
	                q = " ORDER BY DESC(?" + sortEl.getName() + ") ";
	            else
	            {
	                q = " ORDER BY ?" + sortEl.getName() + " ";
	            }
            }
        }
		return q;
	}
	
}
