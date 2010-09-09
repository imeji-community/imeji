package de.mpg.imeji.facet;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import thewebsemantic.LocalizedString;
import de.mpg.imeji.beans.Navigation;
import de.mpg.imeji.image.ImageBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.util.ProfileHelper;
import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.vo.Image;
import de.mpg.jena.vo.MetadataProfile;
import de.mpg.jena.vo.Statement;

public class FacetsBean
{
    private List<FacetGroupBean> groups = new ArrayList<FacetGroupBean>();
    private Navigation nav = (Navigation)BeanHelper.getApplicationBean(Navigation.class);

    public FacetsBean(List<Image> images)
    {
        Map<URI, MetadataProfile> profiles = ProfileHelper.loadProfiles(images);
        try
        {
            for (MetadataProfile mdp : profiles.values())
                groups.add(new FacetGroupBean(generateFacets(mdp), mdp.getTitle()));
        }
        catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException(e);
        }
    }

    public List<FacetBean> generateFacets(MetadataProfile profile) throws UnsupportedEncodingException
    {
        Map<String, FacetBean> map = new HashMap<String, FacetBean>();
        for (Statement st : profile.getStatements())
        {
            if (st.getLiteralConstraints().size() > 0)
            {
                for (LocalizedString ls : st.getLiteralConstraints())
                    map.put(ls.toString(), generateFacet(profile.getId(), st, true, ls.toString()));
            }
            else
            {
                map.put(st.getName(), generateFacet(profile.getId(), st, true, null));
                map.put("no -" + st.getName(), generateFacet(profile.getId(), st, false, null));
            }
        }
        return new ArrayList<FacetBean>(map.values());
    }

    public FacetBean generateFacet(URI id, Statement st, boolean hasValue, String value)
            throws UnsupportedEncodingException
    {
        URI uri = generateUri(id, st, hasValue, value);
        String label = st.getName();
        if (st.getLabels().size() > 0)
            label = st.getLabels().toArray()[0].toString();
        if (value != null)
            label = value;
        if (!hasValue)
            label = "No " + label;
        return new FacetBean(uri, label, getCount(id, st, hasValue, value));
    }

    public List<FacetBean> clearDuplicate(List<FacetBean> list)
    {
        Map<String, FacetBean> fMap = new HashMap<String, FacetBean>();
        for (FacetBean f : list)
            fMap.put(f.getLabel(), f);
        return new ArrayList<FacetBean>(fMap.values());
    }

    public URI generateUri(URI id, Statement st, boolean hasValue, String value) throws UnsupportedEncodingException
    {
        return URI.create(nav.getImagesUrl() + generateQuery(id, st, hasValue, value));
    }

    public String generateQuery(URI id, Statement st, boolean hasValue, String value)
            throws UnsupportedEncodingException
    {
        if (value == null)
            value = "";
        String index = id.getPath().replaceAll("/", ".").substring(1) + "." + st.getName();
        return "?q=" + URLEncoder.encode(index + "='" + value + "'", "UTF-8");
    }

    public int getCount(URI id, Statement st, boolean hasValue, String value) throws UnsupportedEncodingException
    {
        String query = generateQuery(id, st, hasValue, value);
        return 0;
    }

    public List<FacetGroupBean> getGroups()
    {
        return groups;
    }

    public void setGroups(List<FacetGroupBean> groups)
    {
        this.groups = groups;
    }
}
