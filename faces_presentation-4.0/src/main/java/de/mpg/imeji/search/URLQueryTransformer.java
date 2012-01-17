/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.imeji.search;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.controller.SearchCriterion.Filtertype;
import de.mpg.jena.controller.SearchCriterion.ImejiNamespaces;
import de.mpg.jena.controller.SearchCriterion.Operator;
import de.mpg.jena.util.ObjectHelper;

public class URLQueryTransformer 
{

	public static List<SearchCriterion> transform2SCList(String query) throws IOException
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

	public static boolean isSimpleSearch(List<SearchCriterion> scList)
	{
		for (SearchCriterion sc : scList) 
		{			
			if (sc.getChildren().size()> 0)
			{
				return isSimpleSearch(sc.getChildren());
			}
			if(ImejiNamespaces.IMAGE_METADATA_SEARCH.equals(sc.getNamespace()))
			{
				return true;
			}
		}
		return false;
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

	public static String transform2SimpleQuery(List<SearchCriterion> scList)
	{
		String query ="";

		String metadataNamespace = null;
		String metadataValue = null;
		String filename = null;
		Filtertype filter = Filtertype.EQUALS;

		for (SearchCriterion sc : scList) 
		{
			if (!"".equals(query))
			{
				query += " " + sc.getOperator().name() + " ";
			}

			if(isPersonCriterion(sc) != null)
			{
				query += isPersonCriterion(sc); 
			}
			else if (sc.getChildren().size() > 0) 
			{
				String subquery = transform2SimpleQuery(sc.getChildren());
				if (subquery.contains("OR") || subquery.contains("AND"))
				{
					query += " ( ";
				}
				query +=  subquery;
				if (subquery.contains("OR") || subquery.contains("AND"))
				{
					query += " ) ";
				}
			}
			else
			{
				String value = "";
				if (sc.getValue() != null) value = sc.getValue();
				if (sc.getNamespace() != null)
				{
					if (sc.getNamespace().name().contains(ImejiNamespaces.IMAGE_METADATA.name()))
					{
						if (sc.getNamespace().equals(ImejiNamespaces.IMAGE_METADATA_NAMESPACE))
						{
							metadataNamespace = value;
						}
						else
						{
							metadataValue = value;
							filter = sc.getFilterType();
						}
					}
					else if(sc.getNamespace().equals(ImejiNamespaces.IMAGE_FILENAME))
					{
						filename = value;
					}
					else
					{
						query += " " + getNamespaceAsLabel(sc.getNamespace().getNs()) + getFilterAsLabel(sc.getFilterType()) + getIdAsLabel(value) + "  ";
					}

					if(metadataNamespace != null && metadataValue != null)
					{
						query += " " + getNamespaceAsLabel(metadataNamespace) + getFilterAsLabel(filter) + metadataValue;
					}

					if (filename != null && metadataValue != null)
					{
						query += value;
					}
				}
			}
		}

		return query;
	}

	/**
	 * Return a non null value if the metadata is a Person type
	 * @param sc
	 * @return
	 */
	public static String isPersonCriterion(SearchCriterion sc)
	{
		if (sc.getChildren() != null && sc.getChildren().size() > 0)
		{
			return isPersonCriterion(sc.getChildren().get(0));
		}
		else if (sc.getNamespace() != null )
		{
			if (ImejiNamespaces.IMAGE_METADATA_PERSON.equals(sc.getNamespace().getParent()))
			{
				return getNamespaceAsLabel(sc.getNamespace().getParent().getNs())  + " = "+  sc.getValue();
			}
		}
		return null;
	}

	public static String getNamespaceAsLabel(String namespace)
	{
		String s[] =  namespace.split("/");
		if (s.length > 0)
		{
			return namespace.split("/")[s.length -1];
		}
		return namespace;
	}

	public static String getIdAsLabel(String uri)
	{
		if(URI.create(uri).isAbsolute())
		{
			String id = ObjectHelper.getId(URI.create(uri));

			if (id != null)
			{
				return "id " + id;
			}
		}
		return uri;
	}

	public static String getFilterAsLabel(Filtertype filter)
	{
		switch (filter) 
		{
		case GREATER_DATE:
			return " >= ";
		case GREATER_NUMBER:
			return " >= ";
		case LESSER_DATE:
			return " <= ";
		case LESSER_NUMBER:
			return " <= ";
		default:
			return " = ";
		}
	}

}
