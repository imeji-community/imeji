package de.mpg.imeji.metadata;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.metadata.MetadataBean.MdField;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.vo.util.ImejiFactory;
import de.mpg.jena.controller.CollectionController;
import de.mpg.jena.controller.ImageController;
import de.mpg.jena.util.ComplexTypeHelper;
import de.mpg.jena.util.ObjectHelper;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.ComplexType;
import de.mpg.jena.vo.Image;
import de.mpg.jena.vo.ImageMetadata;
import de.mpg.jena.vo.MetadataProfile;
import de.mpg.jena.vo.Statement;

public class EditMetadataBean
{
    private List<Image> images;
    private List<MetadataBean> metadata;
    private Map<URI, MetadataProfile> profiles;
    private SessionBean sb;
    private List<MdField> mdFields;

    public EditMetadataBean(List<Image> images)
    {
        this.sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        this.images = images;
        profiles = loadProfiles(images);
        mdFields = getFields(profiles);
        metadata = new ArrayList<MetadataBean>();
        addMetadata();
    }

    public boolean edit()
    {
        try
        {
            for (Image im : images)
            {
                im = setImageMetadata(im, metadata);
            }
            ImageController ic = new ImageController(sb.getUser());
            ic.update(images);         
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }

    public Image setImageMetadata(Image im, List<MetadataBean> mdbs) throws Exception
    {
        for (MetadataBean mdb : mdbs)
        {
            im = setMetadataValue(im, mdb.getField());
        }
        return im;
    }

    public Image setMetadataValue(Image im, MdField f) throws Exception
    {
        if (!hasMetadata(im, f.getName()))
            im.getMetadata().add(
                    new ImageMetadata(f.getParent().getName(), ComplexTypeHelper.setComplexTypeValue(f.getParent()
                            .getType(), f.getName(), f.getValue())));
        else
        {
            for (ImageMetadata md : im.getMetadata())
            {
                if (md.getName().equals(f.getName()))
                    md.setType(ComplexTypeHelper
                            .setComplexTypeValue(f.getParent().getType(), f.getName(), f.getValue()));
            }
        }
        return im;
    }

    public boolean hasMetadata(Image im, String name)
    {
        for (ImageMetadata md : im.getMetadata())
        {
            if (name.equals(md.getName()))
                return true;
        }
        return false;
    }

    public String addMetadata()
    {
        if (getComplextTypes(profiles).size() > 0)
        {
            MetadataBean mdb = new MetadataBean(new ImageMetadata(getComplextTypes(profiles).get(0).getLabel(),
                    getComplextTypes(profiles).get(0)));
            mdb.setMdFields(this.mdFields);
            metadata.add(mdb);
        }
        return "pretty:";
    }

    public String removeMetadata()
    {
        if (metadata.size() > 0)
        {
            metadata.remove(metadata.size() - 1);
        }
        return "pretty:";
    }

    /**
     * Load profiles defined in a list of images
     */
    public Map<URI, MetadataProfile> loadProfiles(List<Image> imgs)
    {
        CollectionController c = new CollectionController(sb.getUser());
        Map<URI, MetadataProfile> pMap = new HashMap<URI, MetadataProfile>();
        for (Image im : imgs)
        {
            CollectionImeji coll = c.retrieve(im.getCollection());
            if (pMap.get(coll.getProfile().getId()) == null)
            {
                pMap.put(coll.getProfile().getId(), coll.getProfile());
            }
        }
        return pMap;
    }

    public List<ComplexType> getComplextTypes(Map<URI, MetadataProfile> pMap)
    {
        List<ComplexType> cts = new ArrayList<ComplexType>();
        for (MetadataProfile mdp : pMap.values())
        {
            for (Statement s : mdp.getStatements())
            {
                cts.add(ImejiFactory.newComplexType(s.getType()));
            }
        }
        return cts;
    }

    public List<MdField> getFields(Map<URI, MetadataProfile> pMap)
    {
        List<MdField> mdfs = new ArrayList<MdField>();
        for (ComplexType ct : getComplextTypes(pMap))
        {
            MetadataBean mb = new MetadataBean(new ImageMetadata(ct.getLabel(), ct));
            for (MdField mdf : mb.getMdFields())
            {
                mdfs.add(mdf);
            }
        }
        return mdfs;
    }

    public List<MetadataBean> getMetadata()
    {
        return metadata;
    }

    public void setMetadata(List<MetadataBean> metadata)
    {
        this.metadata = metadata;
    }

    public List<SelectItem> getTypesMenu()
    {
        List<SelectItem> list = new ArrayList<SelectItem>();
        for (MdField mdf : mdFields)
        {
            list.add(new SelectItem(mdf.getLabel()));
        }
        return list;
    }

    public int getNumberOfProfiles()
    {
        return this.profiles.size();
    }

    public List<MdField> getMdFields()
    {
        return mdFields;
    }

    public void setMdFields(List<MdField> mdFields)
    {
        this.mdFields = mdFields;
    }
}
