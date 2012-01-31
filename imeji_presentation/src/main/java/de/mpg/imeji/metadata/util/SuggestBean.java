/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.imeji.metadata.util;

import java.io.Serializable;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.richfaces.json.JSONCollection;

import thewebsemantic.LocalizedString;
import de.mpg.jena.vo.MetadataProfile;
import de.mpg.jena.vo.Statement;

public class SuggestBean implements Serializable
{
	private Map<URI, Suggest> suggests = null;

	public void init(MetadataProfile profile)
	{
		suggests = new HashMap<URI,Suggest>();

		for (Statement s : profile.getStatements())
		{
			suggests.put(s.getName(), new Suggest(s));
		}
	}

	public Map<URI, Suggest> getSuggests() {
		return suggests;
	}

	public void setSuggests(Map<URI, Suggest> suggests) {
		this.suggests = suggests;
	}


	public class Suggest
	{
		private Statement statement = null;

		public Suggest(Statement s) 
		{
			statement = s;
		}

		public List<SelectItem> getRestrictedValues()
		{
			if (statement.getLiteralConstraints() != null && statement.getLiteralConstraints().size() > 0)
			{	            
				List<SelectItem> list = new ArrayList<SelectItem>();
				list.add(new SelectItem(null, "-"));
				for (LocalizedString str : statement.getLiteralConstraints())
				{
					list.add(new SelectItem(str.toString(), str.toString()));
				}
				return list;
			}
			return null;
		}

		public List<Object> autoComplete(Object suggest)
		{
			if (statement.getVocabulary() != null)
			{
				if (suggest.toString().isEmpty())
				{
					suggest = "a";
				}
				else if (!suggest.toString().isEmpty())
				{
					try
					{
						HttpClient client = new HttpClient();
						GetMethod getMethod = new GetMethod(statement.getVocabulary().toString() + URLEncoder.encode(suggest.toString(), "UTF-8"));
						client.executeMethod(getMethod);
						String responseString = getMethod.getResponseBodyAsString().trim();
						JSONCollection jsc = new JSONCollection(formatResultString(responseString));
						return Arrays.asList(jsc.toArray());
					}
					catch (Exception e)
					{
						throw new RuntimeException(e);
					}
				}
			}
			return null;
		}
		
		// FOR DEMO PURPOSE
		private String formatResultString(String s)
		{
			if (s.contains("\"Placemark\": "))
			{
				// is google Geo coding api v2
				return s.split("\"Placemark\": ")[1];
			}
			return s;
		}

		public boolean getHasRestrictedValues()
		{
			if (statement != null) 
			{
				if (statement.getLiteralConstraints() != null && statement.getLiteralConstraints().size() > 0)
				{
					return true;
				}
			}
			return false;
		}

		public boolean getDoAutoComplete()
		{
			if (statement != null) 
			{
				if (statement.getLiteralConstraints() != null && statement.getLiteralConstraints().size() > 0)
				{
					return false;
				}
				if (statement.getVocabulary() != null)
				{
					return true;
				}
			}
			return false;
		}
	}
}
