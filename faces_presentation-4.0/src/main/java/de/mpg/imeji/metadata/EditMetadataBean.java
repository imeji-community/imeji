package de.mpg.imeji.metadata;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.richfaces.json.JSONCollection;
import org.richfaces.json.JSONException;

import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.util.ProfileHelper;
import de.mpg.imeji.util.SearchAndExportHelper;
import de.mpg.imeji.util.UrlHelper;
import de.mpg.jena.controller.ImageController;
import de.mpg.jena.util.ObjectHelper;
import de.mpg.jena.vo.ComplexType;
import de.mpg.jena.vo.Image;
import de.mpg.jena.vo.ImageMetadata;
import de.mpg.jena.vo.MetadataProfile;
import de.mpg.jena.vo.Statement;
import de.mpg.jena.vo.ComplexType.ComplexTypes;
import de.mpg.jena.vo.complextypes.ConePerson;
import de.mpg.jena.vo.complextypes.Date;
import de.mpg.jena.vo.complextypes.Publication;
import de.mpg.jena.vo.complextypes.Text;
import de.mpg.jena.vo.complextypes.util.ComplexTypeHelper;

public class EditMetadataBean
{
    private List<Image> images;
    private Image image;
    private Map<URI, MetadataProfile> profiles;
    private SessionBean sb;
    // private List<MdField> mdFields;
    private List<SelectItem> statementMenu;
    private List<SelectItem> exportFormatMenu;
    private MetadataProfile profile;
    private List<MetadataBean> metadata;
    private int mdPosition;
    private String prettyLink;
    private boolean overwrite = true;
    private static Logger logger = Logger.getLogger(EditMetadataBean.class);

    public EditMetadataBean()
    {
        this.sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        metadata = new ArrayList<MetadataBean>();
        statementMenu = new ArrayList<SelectItem>();
        exportFormatMenu = new ArrayList<SelectItem>();
        exportFormatMenu.add(new SelectItem("APA", "APA"));
        exportFormatMenu.add(new SelectItem("AJP", "AJP"));
        exportFormatMenu.add(new SelectItem("JUS", "JUS"));
    }

    public EditMetadataBean(List<Image> images)
    {
        this();
        this.prettyLink = "pretty:selected";
        this.images = images;
        this.image = null;
        profiles = ProfileHelper.loadProfiles(images);
        if (!profiles.isEmpty())
        {
            profile = profiles.values().iterator().next();
            for (Statement s : profile.getStatements())
            {
                statementMenu.add(new SelectItem(s.getName(), s.getName()));
            }
            addMetadata();
        }
    }

    public EditMetadataBean(Image image, String prettyLink)
    {
        this();
        this.prettyLink = prettyLink;
        this.image = image;
        profile = ProfileHelper.loadProfiles(image);
        for (Statement s : profile.getStatements())
            statementMenu.add(new SelectItem(s.getName(), s.getName()));
        if (image.getMetadata().size() != 0)
            addImageMetadataForEdit(image);
        else
            addMetadata();
    }

    public String expandAllMetadata()
    {
        metadata.clear();
        for (Statement s : profile.getStatements())
        {
            Map<String, String> mdAlreadyDisplayed = new HashMap<String, String>();
            if (image != null)
            {
                for (ImageMetadata im : image.getMetadata())
                {
                    if (im.getName() == s.getName())
                    {
                        MetadataBean mb = new MetadataBean(profile, s, im);
                        mb.setPrettyLink(prettyLink);
                        metadata.add(mb);
                        mdAlreadyDisplayed.put(s.getName(), s.getName());
                    }
                }
            }
            if (!mdAlreadyDisplayed.containsKey(s.getName()))
            {
                MetadataBean mb = new MetadataBean(profile, s);
                mb.setPrettyLink(prettyLink);
                metadata.add(mb);
            }
        }
        return prettyLink;
    }

    public String addImageMetadataForEdit(Image image)
    {
        for (Statement s : profile.getStatements())
        {
            for (ImageMetadata im : image.getMetadata())
            {
                if (im.getName() == s.getName())
                {
                    MetadataBean mb = new MetadataBean(profile, s, im);
                    mb.setPrettyLink(prettyLink);
                    metadata.add(mb);
                }
            }
        }
        return prettyLink;
    }

    public String save()
    {
        if (!edit())
        {
            BeanHelper.error("Error editing images");
            return prettyLink;
        }
        BeanHelper.info("Images edited");
        return prettyLink;
    }

    public boolean edit()
    {
        try
        {
            ImageController ic = new ImageController(sb.getUser());
            cleanMetadata();
            if (images != null && images.size() > 0 && "pretty:selected".equals(prettyLink))
            {
                if (metadata.size() > 0)
                {
                    for (Image im : images)
                    {
                        im = addNewImageMetadata(im, metadata, overwrite);
                    }
                    ic.update(images);
                }
                else
                {
                    this.addMetadata();
                    BeanHelper.error("No Metadata to edit");
                    return false;
                }
                this.images.clear();
            }
            else if (images == null && image != null)
            {
                if (metadata.size() > 0)
                {
                    image = updateImageMetadata(image, metadata);
                    ic.update(image);
                }
                else
                {
                    this.addMetadata();
                    BeanHelper.error("No Metadata to edit");
                    return false;
                }
            }
        }
        catch (Exception e)
        {
            logger.error("Error Edit: " + e);
            return false;
        }
        return true;
    }

    public void cleanMetadata()
    {
        for (int i = 0; i < metadata.size(); i++)
        {
            Object ct = metadata.get(i).getMetadata().getType();
            switch (metadata.get(i).getMetadata().getType().getEnumType())
            {
                case CONE_AUTHOR:
                    if ("".equals(((ConePerson)ct).getPerson().getFamilyName()))
                    {
                        BeanHelper.error(metadata.get(i).getMetadata().getName()
                                + " was not saved: it needs a Family Name");
                        metadata.remove(i);
                    }
                    break;
                case TEXT:
                    if ("".equals(((Text)ct).getText()))
                    {
                        metadata.remove(i);
                    }
                    break;
                case PUBLICATION:
                    if ("".equals(((Publication)ct).getUri().toString()))
                    {
                        metadata.remove(i);
                    }
                    else
                    {
                        ((Publication)ct).setCitation(SearchAndExportHelper.getCitation((Publication)ct));
                    }
                    break;
                case DATE:
                    if ("".equals(((Date)ct).getDate().toString()))
                    {
                        metadata.remove(i);
                    }
                    break;
                case URI:
                    URI uri = ((de.mpg.jena.vo.complextypes.URI)ct).getUri();
                    if ("".equals(uri))
                    {
                        metadata.remove(i);
                    }
                    else if (uri.getScheme() == null)
                    {
                        uri = URI.create("http://" + uri);
                        ((de.mpg.jena.vo.complextypes.URI)ct).setUri(uri);
                    }
                    else if (!UrlHelper.isValidURI(uri))
                    {
                        metadata.remove(i);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public Image updateImageMetadata(Image im, List<MetadataBean> mdbs) throws Exception
    {
        im.getMetadata().clear();
        for (MetadataBean mdb : metadata)
            im.getMetadata().add(mdb.getMetadata());
        return im;
    }

    public Image addNewImageMetadata(Image im, List<MetadataBean> mdbs, boolean overwrite) throws Exception
    {
        Map<String, List<ImageMetadata>> oldMdOfImageMappedByType = copy(im.getMetadata());
        List<ImageMetadata> mdFromMdbs = new ArrayList<ImageMetadata>();
        for (MetadataBean mdb : mdbs)
            mdFromMdbs.add(mdb.getMetadata());
        Map<String, List<ImageMetadata>> newMdOfImageMappedByType = copy(mdFromMdbs);
        Map<String, Boolean> statementsMultiplicity = new HashMap<String, Boolean>();
        for (Statement st : profile.getStatements())
        {
            if ("unbounded".equals(st.getMaxOccurs()))
                statementsMultiplicity.put(st.getName(), true);
            else
                statementsMultiplicity.put(st.getName(), false);
        }
        for (List<ImageMetadata> oldMds : oldMdOfImageMappedByType.values())
        {
            if (oldMds.size() > 0)
            {
                String mdType = oldMds.get(0).getName();
                boolean multiple = statementsMultiplicity.get(mdType);
                if (newMdOfImageMappedByType.containsKey(mdType))
                {
                    if (multiple)
                    {
                        newMdOfImageMappedByType.get(mdType).addAll(oldMds);
                    }
                    else
                    {
                        if (!overwrite)
                        {
                            newMdOfImageMappedByType.put(mdType, oldMds);
                        }
                    }
                }
                else
                {
                    newMdOfImageMappedByType.put(mdType, oldMds);
                }
            }
        }
        if (!newMdOfImageMappedByType.isEmpty())
        {
            im.getMetadata().clear();
        }
        for (List<ImageMetadata> mds : newMdOfImageMappedByType.values())
        {
            im.getMetadata().addAll(mds);
        }
        return im;
    }

    private Map<String, List<ImageMetadata>> copy(List<ImageMetadata> listToCopy)
    {
        Map<String, List<ImageMetadata>> copy = new HashMap<String, List<ImageMetadata>>();
        for (ImageMetadata md : listToCopy)
        {
            List<ImageMetadata> mdOfOneType = new ArrayList<ImageMetadata>();
            if (copy.containsKey(md.getName()))
                mdOfOneType = copy.get(md.getName());
            mdOfOneType.add(md);
            copy.put(md.getName(), mdOfOneType);
        }
        return copy;
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
        if (profile.getStatements() != null && profile.getStatements().size() > 0)
        {
            MetadataBean mb = new MetadataBean(profile, profile.getStatements().get(0));
            mb.setPrettyLink(prettyLink);
            if (metadata.size() == 0)
            {
                metadata.add(mb);
            }
            else
            {
                for (Statement st : profile.getStatements())
                {
                    if (st.getName().equals(metadata.get(getMdPosition()).getSelectedStatementName()))
                    {
                        mb = new MetadataBean(profile, st);
                    }
                }
                metadata.add(getMdPosition() + 1, mb);
            }
        }
        return prettyLink;
    }

    public String getPrettyLink()
    {
        return prettyLink;
    }

    public void setPrettyLink(String prettyLink)
    {
        this.prettyLink = prettyLink;
    }

    public String removeMetadata()
    {
        if (metadata.size() > 0)
        {
            metadata.remove(getMdPosition());
        }
        return prettyLink;
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
     * public List<SelectItem> getTypesMenu() { List<SelectItem> list = new ArrayList<SelectItem>(); for (MdField mdf :
     * mdFields) { list.add(new SelectItem(mdf.getLabel())); } return list; }
     */
    public int getNumberOfProfiles()
    {
        if (profiles == null && profile != null)
            return 1;
        return this.profiles.size();
    }

    /*
     * public List<MdField> getMdFields() { return mdFields; } public void setMdFields(List<MdField> mdFields) {
     * this.mdFields = mdFields; }
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

    public List<SelectItem> getExportFormatMenu()
    {
        return exportFormatMenu;
    }

    public void setExportFormatMenu(List<SelectItem> exportFormatMenu)
    {
        this.exportFormatMenu = exportFormatMenu;
    }

    public boolean isOverwrite()
    {
        return overwrite;
    }

    public void setOverwrite(boolean overwrite)
    {
        this.overwrite = overwrite;
    }

    public List<Image> getImages()
    {
        return images;
    }

    public void setImages(List<Image> images)
    {
        this.images = images;
    }
}
