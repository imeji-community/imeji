/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.lang;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.ProfileHelper;
import de.mpg.j2j.misc.LocalizedString;

public class MetadataLabels
{
    private String lang = "en";
    private Map<URI, String> labels;
    private Map<URI, String> internationalizedLabels;

    public void init(List<Item> items) throws Exception
    {
        labels = new HashMap<URI, String>();
        Map<URI, MetadataProfile> profiles = ProfileHelper.loadProfiles(items);
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
                        labels.put(s.getId(), ls.getValue());
                        hasLabel = true;
                    }
                    if (ls.getLang().equals(lang))
                    {
                        internationalizedLabels.put(s.getId(), ls.getValue());
                        hasInternationalizedLabel = true;
                    }
                    labelFallBack = ls.getValue();
                }
                if (!hasLabel)
                {
                    labels.put(s.getId(), labelFallBack);
                }
                if (!hasInternationalizedLabel)
                {
                    internationalizedLabels.put(s.getId(), labels.get(s.getId()));
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

    public Map<URI, String> getInternationalizedLabels()
    {
        return internationalizedLabels;
    }

    public void setInternationalizedLabels(Map<URI, String> internationalizedLabels)
    {
        this.internationalizedLabels = internationalizedLabels;
    }
}
