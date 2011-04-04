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
		boolean bound = false;
		
		int bracketsOpened = 0;
        int bracketsClosed = 0;
        
        if (query == null) query = "";
        
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
            if (scString.trim().equals("AND") || scString.trim().equals("OR") || scString.trim().equals("NOTAND") 
            		|| scString.trim().equals("NOTOR"))
            {
                op = Operator.valueOf(scString.trim());
                scString = "";
            }
            if (scString.trim().equals("BOUND"))
            {
                bound = true;
                scString = "";
            }
            if (bracketsOpened - bracketsClosed == 0)
            {
            	
            	List<SearchCriterion> children = parseQuery(subQuery, op);
            	if (children.size() > 0)
            	{
            		SearchCriterion sc = new SearchCriterion(op, children);
            		sc.setBound(bound);
            		scList.add(sc);
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
                    sc.setBound(bound);
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
				if (sc.isBound()) query += " BOUND";
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
