package de.mpg.imeji.facet;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.metamodel.PluralAttribute.CollectionType;

import thewebsemantic.LocalizedString;
import de.mpg.imeji.beans.Navigation;
import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.image.ImageBean;
import de.mpg.imeji.image.ImagesBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.util.ProfileHelper;
import de.mpg.jena.controller.CollectionController;
import de.mpg.jena.controller.ImageController;
import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.controller.SearchCriterion.Filtertype;
import de.mpg.jena.controller.SearchCriterion.ImejiNamespaces;
import de.mpg.jena.controller.SearchCriterion.Operator;
import de.mpg.jena.util.ComplexTypeHelper;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.Image;
import de.mpg.jena.vo.MetadataProfile;
import de.mpg.jena.vo.Statement;
import de.mpg.jena.vo.ComplexType.ComplexTypes;

public class FacetsBean
{
    private List<FacetGroupBean> groups = new ArrayList<FacetGroupBean>();
    private Navigation nav = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
    private SessionBean sb;

    public FacetsBean(List<Image> images)
    {
        this.sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        Map<URI, MetadataProfile> profiles = ProfileHelper.loadProfiles(images);
        CollectionController cc = new CollectionController(sb.getUser());
        try
        {
            for (MetadataProfile mdp : profiles.values())
            {
                SearchCriterion sc = new SearchCriterion(Operator.AND, ImejiNamespaces.COLLECTION_PROFILE, mdp.getId().toString(), Filtertype.URI);
                List<SearchCriterion> scList = new ArrayList<SearchCriterion>();
                scList.add(sc);
                Collection<CollectionImeji> coll = cc.search(scList, null, 1, 0);
                groups.add(new FacetGroupBean(generateFacets(mdp, coll.iterator().next()), mdp.getTitle()));
            }
                
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public List<FacetBean> generateFacets(MetadataProfile profile, CollectionImeji coll) throws Exception
    {
        List<FacetBean> facetbeans = new ArrayList<FacetBean>();
        for (Statement st : profile.getStatements())
        {
            if (st.getLiteralConstraints().size() > 0)
            {
                for (LocalizedString ls : st.getLiteralConstraints())
                  facetbeans.add(generateFacet(profile.getId(), st, true, ls.toString(), coll));
            }
            else
            {
                facetbeans.add(generateFacet(profile.getId(), st, true, null, coll));
                facetbeans.add(generateFacet(profile.getId(), st, false, null, coll));
            }
        }
        return facetbeans;
    }

    public FacetBean generateFacet(URI id, Statement st, boolean hasValue, String value, CollectionImeji coll)
            throws Exception
    {
        URI uri = generateUri(id, st, hasValue, value);
        String label = st.getName();
        if (st.getLabels().size() > 0)
            label = st.getLabels().toArray()[0].toString();
        if (value != null)
            label = value;
        if (!hasValue)
            label = "No " + label;
        return new FacetBean(uri, label, getCount(id, st, hasValue, value, coll));
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
        return URI.create(nav.getImagesUrl() + "?q=" + URLEncoder.encode(generateQuery(id, st, hasValue, value), "UTF-8"));
    }

    public String generateQuery(URI id, Statement st, boolean hasValue, String value)
            throws UnsupportedEncodingException
    {
        if (value == null)
            value = "";
        
        
        ComplexTypes ct = ComplexTypeHelper.getComplexTypesEnum(st.getType());  
        ImejiNamespaces ns = null;

        
        String index = id.getPath().replaceAll("/", ".").substring(1) + "." + st.getName();
        
        String query = "";
        if(!hasValue)
        {
            query += "INVERSE ";
        }
        query += "( " + ImejiNamespaces.IMAGE_METADATA_NAME.name() + "." + Filtertype.EQUALS.name() + "=\"" + st.getName() + "\" ) ";
        return  query;
        //return "?q=" + URLEncoder.encode(index + "='" + value + "'", "UTF-8");
    }

    public int getCount(URI id, Statement st, boolean hasValue, String value, CollectionImeji coll) throws Exception
    {
        String query = generateQuery(id, st, hasValue, value);
        List<List<SearchCriterion>> scList = ImagesBean.transformQuery(query);
        ImageController ic = new ImageController(sb.getUser());
        
        return ic.searchAdvancedInContainer(coll.getId(), scList, null, -1, 0).size();
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
