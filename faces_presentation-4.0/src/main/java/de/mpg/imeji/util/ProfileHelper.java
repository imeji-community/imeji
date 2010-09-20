package de.mpg.imeji.util;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import thewebsemantic.LocalizedString;

import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.metadata.MetadataBean;
import de.mpg.imeji.metadata.MetadataBean.MdField;
import de.mpg.imeji.vo.util.ImejiFactory;
import de.mpg.jena.controller.CollectionController;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.ComplexType;
import de.mpg.jena.vo.Image;
import de.mpg.jena.vo.ImageMetadata;
import de.mpg.jena.vo.MetadataProfile;
import de.mpg.jena.vo.Statement;

public class ProfileHelper
{
    public static Map<URI, MetadataProfile> loadProfiles(List<Image> imgs)
    {
        SessionBean sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        CollectionController c = new CollectionController(sb.getUser());
        Map<URI, MetadataProfile> pMap = new HashMap<URI, MetadataProfile>();
        for (Image im : imgs)
        {
            CollectionImeji coll = c.retrieve(im.getCollection());
            if (pMap.get(coll.getProfile().getId()) == null)
                pMap.put(coll.getProfile().getId(), coll.getProfile());
        }
        return pMap;
    }

    /*
    public static List<MdField> getStatements(Map<URI, MetadataProfile> pMap)
    {
        List<MdField> mdfs = new ArrayList<MdField>();
        for (ComplexType ct : getComplextTypes(pMap))
        {
            MetadataBean mb = new MetadataBean(new ImageMetadata(ct.getLabel(), ct));
            for (MdField mdf : mb.getMdFields())
                mdfs.add(mdf);
        }
        return mdfs;
    }
*/
    public static List<ComplexType> getComplextTypes(Map<URI, MetadataProfile> pMap)
    {
        List<ComplexType> cts = new ArrayList<ComplexType>();
        for (MetadataProfile mdp : pMap.values())
        {
            for (Statement s : mdp.getStatements())
            {
                ComplexType ct = ImejiFactory.newComplexType(s.getType());
                if (s.getLabels().size() > 0)
                    ct.setLabel(s.getLabels().toArray()[0].toString());
                cts.add(ct);
            }
        }
        return cts;
    }
    /*

    public static List<MdField> getComplexTypes(MetadataProfile mdp)
    {
        List<MdField> mdfs = new ArrayList<MdField>();
        for (Statement s : mdp.getStatements())
        {
            ComplexType ct = ImejiFactory.newComplexType(s.getType());
            if (s.getLabels().size() > 0)
                ct.setLabel(s.getLabels().toArray()[0].toString());
            MetadataBean mb = new MetadataBean(new ImageMetadata(ct.getLabel(), ct));
            for (LocalizedString str : s.getLiteralConstraints())
                mb.getField().getLiteralOptions().add(str.toString());
            for (MdField mdf : mb.getMdFields())
                mdfs.add(mdf);
        }
        return mdfs;
    }
*/
//    public static List<MdField> getFields(Map<URI, MetadataProfile> pMap)
//    {
//        List<MdField> mdfs = new ArrayList<MdField>();
//        for (MetadataProfile mdp : pMap.values())
//        {
//           mdfs.addAll(getComplexTypes(mdp));
//        }
//
//        return mdfs;
//    }
}
