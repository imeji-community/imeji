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
	public List<List<SearchCriterion>> transform(String query) throws Exception
	{
		boolean inverse = false;
        List<List<SearchCriterion>> scList = new ArrayList<List<SearchCriterion>>();
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
                        scList.add(currentSubList);
                        substring = "";
                    }
                }
                if (substring.equals("AND ") || substring.equals("OR "))
                {
                    lastOperator = substring.trim();
                    substring = "";
                }
                else if (substring.trim().equals("INVERSE"))
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
                        currentSubList.add(new SearchCriterion(Operator.OR, ImejiNamespaces.IMAGE_FILENAME, value,
                                Filtertype.REGEX));
                        currentSubList.add(new SearchCriterion(Operator.OR,
                                ImejiNamespaces.IMAGE_METADATA_COMPLEXTYPE_TEXT, value, Filtertype.REGEX));
                        currentSubList.add(new SearchCriterion(Operator.OR,
                                ImejiNamespaces.IMAGE_METADATA_COMPLEXTYPE_NUMBER, value, Filtertype.EQUALS_NUMBER));
                        currentSubList
                                .add(new SearchCriterion(Operator.OR,
                                        ImejiNamespaces.IMAGE_METADATA_COMPLEXTYPE_PERSON_FAMILY_NAME, value,
                                        Filtertype.REGEX));
                        currentSubList.add(new SearchCriterion(Operator.OR,
                                ImejiNamespaces.IMAGE_METADATA_COMPLEXTYPE_PERSON_GIVEN_NAME, value, Filtertype.REGEX));
                        currentSubList.add(new SearchCriterion(Operator.OR,
                                ImejiNamespaces.IMAGE_METADATA_COMPLEXTYPE_PERSON_ORGANIZATION_NAME, value,
                                Filtertype.REGEX));
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
                scList.add(currentSubList);
            }
            if (bracketsOpened != bracketsClosed)
            {
                throw new Exception("Invalid query!");
            }
        }
        return scList;
	}
	
//	 public static List<SearchCriterion> transformQueryOld(String query) throws Exception
//	    {
//	        boolean inverse = false;
//	        List<SearchCriterion> scList = new ArrayList<SearchCriterion>();
//	        if (query != null && !query.trim().equals(""))
//	        {
//	            StringReader reader = new StringReader(query);
//	            int bracketsOpened = 0;
//	            int bracketsClosed = 0;
//	            String substring = "";
//	            String lastOperator = null;
//	            // List<SearchCriterion> currentSubList = new ArrayList<SearchCriterion>();
//	            SearchCriterion currentSC = null;
//	            SearchCriterion currentParentSC = null;
//	            int c = 0;
//	            while ((c = reader.read()) != -1)
//	            {
//	                substring += (char)c;
//	                if (c == '(')
//	                {
//	                    bracketsOpened++;
//	                    if (bracketsOpened - bracketsClosed > 1)
//	                    {
//	                        currentSC = new SearchCriterion();
//	                        if (lastOperator != null)
//	                        {
//	                            currentSC.setOperator(Operator.valueOf(lastOperator.trim()));
//	                        }
//	                        else
//	                        {
//	                            currentSC.setOperator(null);
//	                        }
//	                        lastOperator = null;
//	                        currentParentSC.getChildren().add(currentSC);
//	                        currentSC.setParent(currentParentSC);
//	                    }
//	                    currentParentSC = currentSC;
//	                    currentSC = null;
//	                    substring = "";
//	                }
//	                else if (c == ')')
//	                {
//	                    bracketsClosed++;
//	                    currentSC = currentParentSC;
//	                    currentParentSC = currentSC.getParent();
//	                    substring = "";
//	                }
//	                if (substring.equals(" AND ") || substring.equals(" OR "))
//	                {
//	                    lastOperator = substring.trim();
//	                    substring = "";
//	                }
//	                else if (substring.equals(" MINUS "))
//	                {
//	                    lastOperator = substring.trim();
//	                    inverse = true;
//	                    substring = "";
//	                }
//	                else if (substring.matches("\\s*[^\\s]+=\".*\""))
//	                {
//	                    String[] keyValue = substring.split("=");
//	                    String[] nsFilter = keyValue[0].split("\\.");
//	                    String value = keyValue[1].trim();
//	                    value = value.substring(1, value.length() - 1);
//	                    /*
//	                     * if(nsFilter[0].trim().equals("ANY_METADATA")) { currentSubList.add(new
//	                     * SearchCriterion(Operator.OR, ImejiNamespaces.IMAGE_FILENAME, value , Filtertype.REGEX));
//	                     * currentSubList.add(new SearchCriterion(Operator.OR,
//	                     * ImejiNamespaces.IMAGE_METADATA_COMPLEXTYPE_TEXT, value , Filtertype.REGEX));
//	                     * currentSubList.add(new SearchCriterion(Operator.OR,
//	                     * ImejiNamespaces.IMAGE_METADATA_COMPLEXTYPE_NUMBER, value, Filtertype.REGEX));
//	                     * currentSubList.add(new SearchCriterion(Operator.OR,
//	                     * ImejiNamespaces.IMAGE_METADATA_COMPLEXTYPE_PERSON_FAMILY_NAME, value, Filtertype.REGEX));
//	                     * currentSubList.add(new SearchCriterion(Operator.OR,
//	                     * ImejiNamespaces.IMAGE_METADATA_COMPLEXTYPE_PERSON_GIVEN_NAME, value, Filtertype.REGEX));
//	                     * currentSubList.add(new SearchCriterion(Operator.OR,
//	                     * ImejiNamespaces.IMAGE_METADATA_COMPLEXTYPE_PERSON_ORGANIZATION_NAME, value, Filtertype.REGEX)); }
//	                     * else {
//	                     */
//	                    ImejiNamespaces ns = ImejiNamespaces.valueOf(nsFilter[0].trim());
//	                    Filtertype filter = Filtertype.valueOf(nsFilter[1].trim());
//	                    Operator op = Operator.AND;
//	                    if (lastOperator != null)
//	                    {
//	                        op = Operator.valueOf(lastOperator.trim());
//	                    }
//	                    else
//	                    {
//	                        op = null;
//	                    }
//	                    lastOperator = null;
//	                    currentSC = new SearchCriterion(op, ns, value, filter);
//	                    if (currentParentSC != null)
//	                    {
//	                        currentParentSC.getChildren().add(currentSC);
//	                        currentSC.setParent(currentParentSC);
//	                    }
//	                    else
//	                    {
//	                        scList.add(currentSC);
//	                    }
//	                    // }
//	                    substring = "";
//	                }
//	            }
//	        }
//	        return scList;
//	    }
	
	
}
