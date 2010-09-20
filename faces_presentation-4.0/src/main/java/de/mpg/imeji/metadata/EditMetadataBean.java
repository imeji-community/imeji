package de.mpg.imeji.metadata;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.model.SelectItem;

import thewebsemantic.LocalizedString;

import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.mdProfile.MdProfileBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.util.ProfileHelper;
import de.mpg.imeji.vo.util.ImejiFactory;
import de.mpg.jena.controller.CollectionController;
import de.mpg.jena.controller.ImageController;
import de.mpg.jena.util.ComplexTypeHelper;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.ComplexType;
import de.mpg.jena.vo.Image;
import de.mpg.jena.vo.ImageMetadata;
import de.mpg.jena.vo.MetadataProfile;
import de.mpg.jena.vo.Statement;

public class EditMetadataBean
{
    private List<Image> images;

    private Map<URI, MetadataProfile> profiles;
    private SessionBean sb;
    //private List<MdField> mdFields;
    
    private List<SelectItem> statementMenu;
    private MetadataProfile profile;
    private List<MetadataBean> metadata;
    private int mdPosition;
    

    public EditMetadataBean(List<Image> images)
    {
        this.sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        this.images = images;
        profiles = ProfileHelper.loadProfiles(images);
        //mdFields = ProfileHelper.getFields(profiles);
        metadata = new ArrayList<MetadataBean>();

        profile = profiles.values().iterator().next();
        statementMenu = new ArrayList<SelectItem>();
        for (Statement s : profile.getStatements())
        {
            statementMenu.add(new SelectItem(s.getName(), s.getName()));
        }
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
        Map<String, List<ImageMetadata>> mdMap = new HashMap<String, List<ImageMetadata>>();
        List<ImageMetadata> newMetadata = new ArrayList<ImageMetadata>();
       
        for(ImageMetadata imd : im.getMetadata())
        {
           if(mdMap.containsKey(imd.getName()))
           {
               List<ImageMetadata> imList = mdMap.get(imd.getName());
               imList.add(imd);
           }
           else
           {
               List<ImageMetadata> imList = new ArrayList<ImageMetadata>();
               imList.add(imd);
               mdMap.put(imd.getName(), imList);
           }
        }
       
        im.getMetadata().clear();
        
        // add new mdvalues (overwrite old)
        for(MetadataBean mdb : metadata)
        {
            if(mdMap.containsKey(mdb.getMetadata().getName()))
            {
                List<ImageMetadata> imList = mdMap.get(mdb.getMetadata().getName());
                imList.clear();
            }
            im.getMetadata().add(mdb.getMetadata());
           
        }
        
        for(List<ImageMetadata> imList : mdMap.values())
        {
            im.getMetadata().addAll(imList);
        }
        
        
        
        return im; 
    }
/*
    public Image setMetadataValue(Image im, MdField f) throws Exception
    {
        if (!hasMetadata(im, f.getParent().getName()))
            im.getMetadata().add(
                    new ImageMetadata(f.getParent().getName(), ComplexTypeHelper.setComplexTypeValue(f.getParent()
                            .getType(), f.getName(), f.getValue())));
        else
        {
            for (ImageMetadata md : im.getMetadata())
            {
                if (md.getName().equals(f.getParent().getName()))
                    md.setType(ComplexTypeHelper
                            .setComplexTypeValue(f.getParent().getType(), f.getName(), f.getValue()));
            }
        }
        return im;
    }
*/
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
        MetadataBean mb = new MetadataBean(profile, profile.getStatements().get(0));
        
        if(metadata.size()==0)
        {
            metadata.add(mb); 
        }
        else
        {
            metadata.add(getMdPosition() + 1, mb);
        }
        return "pretty:selected";
    }

    public String removeMetadata()
    {
        if (metadata.size() > 0)
        {
            metadata.remove(getMdPosition());
        }
        return "pretty:";
    }

    public List<MetadataBean> getMetadata()
    {
        return metadata;
    }

    public void setMetadata(List<MetadataBean> metadata)
    {
        this.metadata = metadata;
    }

    /*
    public List<SelectItem> getTypesMenu()
    {
        List<SelectItem> list = new ArrayList<SelectItem>();
        for (MdField mdf : mdFields)
        {
            list.add(new SelectItem(mdf.getLabel()));
        }
        return list;
    }
*/
    public int getNumberOfProfiles()
    {
        return this.profiles.size();
    }
/*
    public List<MdField> getMdFields()
    {
        return mdFields;
    }

    public void setMdFields(List<MdField> mdFields)
    {
        this.mdFields = mdFields;
    }
*/
    public void setStatementMenu(List<SelectItem> statementMenu)
    {
        this.statementMenu = statementMenu;
    }

    public List<SelectItem> getStatementMenu()
    {
        return statementMenu;
    }

    public void setMdPosition(int mdPosition)
    {
        this.mdPosition = mdPosition;
    }

    public int getMdPosition()
    {
        return mdPosition;
    }
    
}
