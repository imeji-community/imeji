/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.imeji.lang;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import thewebsemantic.LocalizedString;
import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.util.ProfileHelper;
import de.mpg.jena.vo.Image;
import de.mpg.jena.vo.MetadataProfile;
import de.mpg.jena.vo.Statement;

public class MetadataLabels implements Serializable
{
	private String lang = "en";

	private Map<URI, String> labels;
	private Map<URI, String> internationalizedLabels;

	public void init(List<Image> images) throws Exception
	{
		labels = new HashMap<URI, String>();

		Map<URI, MetadataProfile> profiles = ProfileHelper.loadProfiles(images);
		
		init1(new ArrayList<MetadataProfile>(profiles.values()));
	}
	
	public void init1(List<MetadataProfile> profiles) throws Exception
	{
		HashMap<URI, String> map = new HashMap<URI, String>();
		for (MetadataProfile p : profiles)
		{
			if (p != null)
			{
				init(p);
				map.putAll(internationalizedLabels);
			}
		}
		internationalizedLabels = new HashMap<URI, String>(map);
	}

	public void init(MetadataProfile profile) throws Exception
	{
		labels = new HashMap<URI, String>();
		internationalizedLabels = new HashMap<URI, String>();

		lang = ((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getLocale().getLanguage();

		if (profile != null)
		{
			for (Statement s : profile.getStatements())
			{
				boolean hasInternationalizedLabel = false;
				boolean hasLabel = false;

				String labelFallBack = null;

				for (LocalizedString ls : s.getLabels())
				{
					if (ls.getLang().equals("en"))
					{
						labels.put(s.getName(), ls.toString());
						hasLabel = true;
					}
					if (ls.getLang().equals(lang))
					{
						internationalizedLabels.put(s.getName(), ls.toString());
						hasInternationalizedLabel = true;
					}
					labelFallBack = ls.toString();
				}
				if (!hasLabel)
				{
					labels.put(s.getName(), labelFallBack);
				}
				if (!hasInternationalizedLabel) 
				{
					internationalizedLabels.put(s.getName(), labels.get(s.getName()));
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

	public Map<URI, String> getInternationalizedLabels() {
		return internationalizedLabels;
	}

	public void setInternationalizedLabels(Map<URI, String> internationalizedLabels) {
		this.internationalizedLabels = internationalizedLabels;
	}


}
