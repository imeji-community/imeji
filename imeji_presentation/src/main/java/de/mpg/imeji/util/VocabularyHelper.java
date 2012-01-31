/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.imeji.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.faces.model.SelectItem;

public class VocabularyHelper
{
	private List<SelectItem> vocabularies;
	private Properties properties;

	public VocabularyHelper() 
	{
		try 
		{
			loadProperties();
			initVocabularies();
		} 
		catch (IOException e) 
		{
			throw new RuntimeException(e);
		}

	}

	public void initVocabularies()
	{
		vocabularies = new ArrayList<SelectItem>();
		vocabularies.add(new SelectItem("", "--"));
		for (Object o : properties.keySet())
		{
			vocabularies.add(new SelectItem(properties.getProperty(o.toString()), o.toString()));
		}
	}

	public void loadProperties() throws IOException
	{
		InputStream instream = null;
		try 
		{
			instream= PropertyReader.getInputStream("vocabulary.properties");
			properties = new Properties();
			properties.load(instream);

		} 
		catch (Exception e) 
		{
			throw new RuntimeException(e);
		}
		finally
		{
			instream.close();
		}
	}

	public String getVocabularyName(URI uri)
	{
		if (uri == null)
		{
			return null;
		}
		else
		{
			for (SelectItem voc : vocabularies)
			{
				if (voc.getValue().toString().equals(uri.toString()))
				{
					return voc.getLabel();
				}
			}
		}
		return "unknown";
	}

	public List<SelectItem> getVocabularies()
	{
		return vocabularies;
	}
}
