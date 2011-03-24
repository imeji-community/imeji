package de.mpg.imeji.search;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.controller.SearchCriterion.Filtertype;
import de.mpg.jena.controller.SearchCriterion.ImejiNamespaces;
import de.mpg.jena.controller.SearchCriterion.Operator;

public class URLQueryTransformer 
{
	/**
	 * @deprecated
	 * @param query
	 * @return
	 * @throws Exception
	 */
	public static List<SearchCriterion> transform2SCList2(String query) throws Exception
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
                    	currentSubList.add(new SearchCriterion(Operator.OR, ImejiNamespaces.IMAGE_METADATA_DATE, value, Filtertype.EQUALS_DATE));
                        currentSubList.add(new SearchCriterion(Operator.OR, ImejiNamespaces.IMAGE_METADATA_TEXT, value, Filtertype.REGEX));
                        currentSubList.add(new SearchCriterion(Operator.OR, ImejiNamespaces.IMAGE_METADATA_NUMBER, value, Filtertype.EQUALS_NUMBER));
                        currentSubList.add(new SearchCriterion(Operator.OR, ImejiNamespaces.IMAGE_METADATA_PERSON_FAMILY_NAME, value,  Filtertype.REGEX));
                        currentSubList.add(new SearchCriterion(Operator.OR, ImejiNamespaces.IMAGE_METADATA_PERSON_GIVEN_NAME, value, Filtertype.REGEX));
                        currentSubList.add(new SearchCriterion(Operator.OR, ImejiNamespaces.IMAGE_METADATA_PERSON_ORGANIZATION_NAME, value,  Filtertype.REGEX));
                       // currentSubList.add(new SearchCriterion(Operator.OR, ImejiNamespaces.IMAGE_METADATA_SEARCH, value,  Filtertype.REGEX));
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
                        	op = Operator.NOTAND;
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
	
	public static List<SearchCriterion> transform2SCList(String query) throws Exception
	{
		List<SearchCriterion> scList = parseQuery(query, Operator.AND);
        return scList;
	}
	
	private static List<SearchCriterion> parseQuery(String query, Operator op) throws IOException
	{
		List<SearchCriterion> scList = new ArrayList<SearchCriterion>();
		
		String subQuery ="";
		String scString ="";
		
		int bracketsOpened = 0;
        int bracketsClosed = 0;
        
		StringReader reader = new StringReader(query);
		int c = 0;
        while ((c = reader.read()) != -1)
        {
        	if (bracketsOpened - bracketsClosed != 0) subQuery+= (char)c;
        	else scString += (char)c;
        	
            if (c == '(')
            {
                bracketsOpened++;
            }
            if (c == ')')
            {
                bracketsClosed++;
                scString = "";
            }
            if (scString.trim().equals("AND") || scString.trim().equals("OR") || scString.trim().equals("NOTAND") || scString.trim().equals("NOTOR"))
            {
                op = Operator.valueOf(scString.trim());
                scString = "";
            }
            if (bracketsOpened - bracketsClosed == 0)
            {
            	
            	List<SearchCriterion> children = parseQuery(subQuery, op);
            	if (children.size() > 0)
            	{
            		scList.add(new SearchCriterion(op, children));
            		subQuery ="";
            	}
            }
            if (scString.matches("\\s*[^\\s]+=\".*\"\\s+"))
        	{
            	String[] keyValue = scString.split("=");
                String[] nsFilter = keyValue[0].split("\\.");
                String value = keyValue[1].trim();
                value = value.substring(1, value.length() - 1);
                
                if (nsFilter[0].trim().equals("ANY_METADATA"))
                {
                	scList.add(new SearchCriterion(Operator.OR, ImejiNamespaces.IMAGE_FILENAME, value, Filtertype.REGEX));
//               	 	scList.add(new SearchCriterion(Operator.OR,  ImejiNamespaces.IMAGE_METADATA_TEXT, value, Filtertype.REGEX));
//               	 	scList.add(new SearchCriterion(Operator.OR,   ImejiNamespaces.IMAGE_METADATA_NUMBER, value, Filtertype.EQUALS_NUMBER));
//                    scList.add(new SearchCriterion(Operator.OR, ImejiNamespaces.IMAGE_METADATA_PERSON_FAMILY_NAME, value,  Filtertype.REGEX));
//                    scList.add(new SearchCriterion(Operator.OR,  ImejiNamespaces.IMAGE_METADATA_PERSON_GIVEN_NAME, value, Filtertype.REGEX));
//                    scList.add(new SearchCriterion(Operator.OR, ImejiNamespaces.IMAGE_METADATA_PERSON_ORGANIZATION_NAME, value,  Filtertype.REGEX));
                    scList.add(new SearchCriterion(Operator.OR, ImejiNamespaces.IMAGE_METADATA_SEARCH, value,  Filtertype.REGEX));
                }
                else
                {
                    ImejiNamespaces ns = ImejiNamespaces.valueOf(nsFilter[0].trim());
                    Filtertype filter = Filtertype.valueOf(nsFilter[1].trim());
                    SearchCriterion sc = new SearchCriterion(op, ns, value, filter);
                    scList.add(sc);
                }
                scString ="";
        	}
        }
		
		return scList;
	}
		
	/**
	 * Use transform2URL
	 * @param scList
	 * @return
	 */
	@Deprecated
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
			if (!"".equals(query) 
					|| Operator.NOTAND.equals(sc.getOperator())
					|| Operator.NOTOR.equals(sc.getOperator()))
    		{
				query += " " + sc.getOperator().name() + " ";
    		}
			if (sc.getChildren().size() > 0) 
    		{
				query += " ( ";
    			query +=  transform2URL(sc.getChildren());
    			query += " ) ";
			}
    		else
    		{
    			String value = "";
    			if (sc.getValue() != null) value = sc.getValue();
    			if (sc.getNamespace() != null)
    			{
    				query += " " +sc.getNamespace().name() + "." + sc.getFilterType().name() +  "=\"" + value + "\" ";
    			}
    		}
		}
		return query;
	}
	
}
