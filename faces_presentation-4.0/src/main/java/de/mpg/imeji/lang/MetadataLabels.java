package de.mpg.imeji.lang;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import thewebsemantic.LocalizedString;
import de.mpg.imeji.util.ProfileHelper;
import de.mpg.jena.controller.ProfileController;
import de.mpg.jena.vo.Image;
import de.mpg.jena.vo.MetadataProfile;
import de.mpg.jena.vo.Statement;

public class MetadataLabels
{
	private String lang = "eng";
	
	private Map<URI, String> labels = new HashMap<URI, String>();
	
	public MetadataLabels() {
		// TODO Auto-generated constructor stub
	}
	
	public void init(List<Image> images) throws Exception
	{
		labels = new HashMap<URI, String>();
		
		Map<URI, MetadataProfile> profiles = ProfileHelper.loadProfiles(images);
		
		for (MetadataProfile p : profiles.values())
		{
			if (p != null)
			{
				for (Statement s : p.getStatements())
				{
					for (LocalizedString ls : s.getLabels())
					{
						if (ls.getLang().equals(lang)) labels.put(s.getName(), ls.toString());
					}
				}
			}
		}
	}
	
	public void init(MetadataProfile profile) throws Exception
	{
		labels = new HashMap<URI, String>();
		
		if (profile != null)
		{
			for (Statement s : profile.getStatements())
			{
				for (LocalizedString ls : s.getLabels())
				{
					if (ls.getLang().equals(lang)) labels.put(s.getName(), ls.toString());
				}
			}
		}
	}

	public String getLang() 
	{
		return lang;
	}

	public void setLang(String lang) 
	{
		this.lang = lang;
	}

	public Map<URI, String> getLabels() 
	{
		return labels;
	}

	public void setLabels(Map<URI, String> labels) 
	{
		this.labels = labels;
	}
	
	
}
