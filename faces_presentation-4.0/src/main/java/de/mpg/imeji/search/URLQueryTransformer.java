package de.mpg.imeji.search;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.controller.SearchCriterion.Filtertype;
import de.mpg.jena.controller.SearchCriterion.ImejiNamespaces;
import de.mpg.jena.controller.SearchCriterion.Operator;

public class URLQueryTransformer 
{
	public static List<SearchCriterion> transform2SCList(String query) throws Exception
	{
		boolean inverse = false;
        List<SearchCriterion> scList = new ArrayList<SearchCriterion>();
        if (query != null && !query.trim().equals(""))
        {
            StringReader reader = new StringReader(query);
            int bracketsOpened = 0;
            int bracketsClosed = 0;
            String substring = "";
            String lastOperator = "";
            List<SearchCriterion> currentSubList = new ArrayList<SearchCriterion>();
            int c = 0;
            while ((c = reader.read()) != -1)
            {
                substring += (char)c;
                if (c == '(')
                {
                    bracketsOpened++;
                    if (bracketsOpened - bracketsClosed == 1)
                    {
                        currentSubList = new ArrayList<SearchCriterion>();
                        substring = "";
                    }
                }
                else if (c == ')')
                {
                    bracketsClosed++;
                    if (bracketsOpened - bracketsClosed == 0)
                    {
                        SearchCriterion sc = new SearchCriterion();
                        sc.setChildren(currentSubList);
                    	scList.add(sc);
                        substring = "";
                    }
                }
                if (substring.equals("AND ") || substring.equals("OR ") || substring.equals("ANDNOT ") || substring.equals("ORNOT "))
                {
                    lastOperator = substring.trim();
                    substring = "";
                }
                else if (substring.trim().equals("NOT"))
                {
                    // lastOperator = substring.trim();
                    inverse = true;
                    substring = "";
                }
                else if (substring.matches("\\s*[^\\s]+=\".*\"\\s+"))
                {
                    String[] keyValue = substring.split("=");
                    String[] nsFilter = keyValue[0].split("\\.");
                    String value = keyValue[1].trim();
                    value = value.substring(1, value.length() - 1);
                    if (nsFilter[0].trim().equals("ANY_METADATA"))
                    {
                       currentSubList.add(new SearchCriterion(Operator.OR, ImejiNamespaces.IMAGE_FILENAME, value, Filtertype.REGEX));
                        currentSubList.add(new SearchCriterion(Operator.OR,  ImejiNamespaces.IMAGE_METADATA_TEXT, value, Filtertype.REGEX));
                       currentSubList.add(new SearchCriterion(Operator.OR,   ImejiNamespaces.IMAGE_METADATA_NUMBER, value, Filtertype.EQUALS_NUMBER));
                        currentSubList.add(new SearchCriterion(Operator.OR, ImejiNamespaces.IMAGE_METADATA_PERSON_FAMILY_NAME, value,  Filtertype.REGEX));
                        currentSubList.add(new SearchCriterion(Operator.OR,  ImejiNamespaces.IMAGE_METADATA_PERSON_GIVEN_NAME, value, Filtertype.REGEX));
                       currentSubList.add(new SearchCriterion(Operator.OR, ImejiNamespaces.IMAGE_METADATA_PERSON_ORGANIZATION_NAME, value,  Filtertype.REGEX));
                    }
                    else
                    {
                        ImejiNamespaces ns = ImejiNamespaces.valueOf(nsFilter[0].trim());
                        Filtertype filter = Filtertype.valueOf(nsFilter[1].trim());
                        Operator op = Operator.AND;
                        if (!lastOperator.equals(""))
                        {
                            op = Operator.valueOf(lastOperator.trim());
                        }
                        else if (inverse)
                        {
                        	op = Operator.ANDNOT;
                        }
                        // Operator op = Operator.valueOf(lastOperator.trim());
                        SearchCriterion sc = new SearchCriterion(op, ns, value, filter);
                        sc.setInverse(inverse);
                        currentSubList.add(sc);
                    }
                    substring = "";
                }
            }
            if (bracketsClosed == 0)
            {
            	 SearchCriterion sc = new SearchCriterion();
            	 sc.setChildren(currentSubList);
            	 scList.add(sc);
            }
            if (bracketsOpened != bracketsClosed)
            {
                throw new Exception("Invalid query!");
            }
        }
        return scList;
	}
	
	public static String transform2URLAdvanced(List<List<SearchCriterion>> scList)
	{
		String query ="";
		for (List<SearchCriterion> l : scList) 
		{
			query += transform2URL(l);
		}
		return query;
	}
	
	public static String transform2URL(List<SearchCriterion> scList)
	{
		String query ="";
		for (SearchCriterion sc : scList) 
		{
    		if (sc.getChildren().size() > 0) 
    		{
    			query += transform2URL(sc.getChildren());
			}
    		else
    		{
    			query += "" + sc.getOperator().name();
    			if (!"".equals(query))
        		{
        			
        		}
        		if (sc.isInverse())
        		{
        			query += " INVERSE ";
        		}
        		query += " ( " + sc.getNamespace().name() + "." + sc.getFilterType().name() +  "=\"" + sc.getValue() + "\" ) ";
    		}
		}
		return query;
	}
	
}
