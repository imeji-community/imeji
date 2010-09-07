package de.mpg.imeji.image;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import de.mpg.imeji.beans.BasePaginatorListSessionBean;
import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.metadata.EditMetadataBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.util.UrlHelper;
import de.mpg.imeji.vo.util.ImejiFactory;
import de.mpg.jena.controller.CollectionController;
import de.mpg.jena.controller.ImageController;
import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.controller.SearchCriterion.Filtertype;
import de.mpg.jena.controller.SearchCriterion.ImejiNamespaces;
import de.mpg.jena.util.ObjectHelper;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.ComplexType;
import de.mpg.jena.vo.Image;
import de.mpg.jena.vo.ImageMetadata;
import de.mpg.jena.vo.MetadataProfile;
import de.mpg.jena.vo.Statement;
import de.mpg.jena.vo.ComplexType.ComplexTypes;

public class SelectedBean extends ImagesBean
{
    private int totalNumberOfRecords;
    private SessionBean sb;
    private Collection<Image> images;
    private EditMetadataBean editMetadataBean;
    private String mdEdited;

    public SelectedBean()
    {
        super();
        this.sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
    }

    @Override
    public String getNavigationString()
    {
        return "pretty:selected";
    }

    @Override
    public int getTotalNumberOfRecords()
    {
        return totalNumberOfRecords;
    }

    @Override
    public List<ImageBean> retrieveList(int offset, int limit)
    {
        ImageController controller = new ImageController(sb.getUser());
        images = new ArrayList<Image>();
        List<SearchCriterion> uris = new ArrayList<SearchCriterion>();
        for (URI uri : sb.getSelected())
        {
            uris.add(new SearchCriterion(SearchCriterion.Operator.OR, ImejiNamespaces.ID_URI, uri.toString(),
                    Filtertype.URI));
        }
        try
        {
            totalNumberOfRecords = controller.search(uris, null, -1, offset).size();
            images = controller.search(uris, null, limit, offset);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        if (UrlHelper.getParameterBoolean("reset"))
        {
            editMetadataBean = new EditMetadataBean((List<Image>)images);
        }
        return ImejiFactory.imageListToBeanList(images);
    }

    public String save()
    {
        if (!editMetadataBean.edit())
        {
            BeanHelper.error("Error editing images");
        }
        BeanHelper.info("Images edited");
        return getNavigationString();
    }

    public EditMetadataBean getEditMetadataBean()
    {
        return editMetadataBean;
    }

    public void setEditMetadataBean(EditMetadataBean editMetadataBean)
    {
        this.editMetadataBean = editMetadataBean;
    }

    public void mdEditedListener(ValueChangeEvent event)
    {
        if (event != null && event.getNewValue() != event.getOldValue())
        {
            this.mdEdited = event.getNewValue().toString();
        }
    }

    public String getMdEdited()
    {
        return mdEdited;
    }

    public void setMdEdited(String mdEdited)
    {
        this.mdEdited = mdEdited;
    }
}
