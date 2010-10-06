package de.mpg.imeji.image;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.faces.event.ValueChangeEvent;
import com.ocpsoft.pretty.PrettyContext;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;
import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.metadata.EditMetadataBean;
import de.mpg.imeji.upload.deposit.DepositController;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.util.LoginHelper;
import de.mpg.imeji.util.UrlHelper;
import de.mpg.imeji.vo.util.ImejiFactory;
import de.mpg.jena.controller.ImageController;
import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.controller.SearchCriterion.Filtertype;
import de.mpg.jena.controller.SearchCriterion.ImejiNamespaces;
import de.mpg.jena.vo.Image;
import de.mpg.jena.vo.Properties.Status;

public class SelectedBean extends ImagesBean
{
    private int totalNumberOfRecords;
    private SessionBean sb;
    private Collection<Image> images;
    private EditMetadataBean editMetadataBean;
    private String mdEdited;
    private URI currentCollection;
    private String escidocUserHandle;

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
        if (uris.size() != 0)
        {
            try
            {
                totalNumberOfRecords = controller.search(uris, null, -1, offset).size();
                images = controller.search(uris, null, limit, offset);
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
        if (UrlHelper.getParameterBoolean("reset") || editMetadataBean.getImages().size() == 0)
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

    public String clearAll()
    {
        String prettyLink = PrettyContext.getCurrentInstance().getCurrentMapping().getId();
        sb.getSelected().clear();
        if (prettyLink.equalsIgnoreCase("selected"))
            return "pretty:images";
        else
            return "pretty:";
    }



    public String deleteAll() throws Exception
    {
        List<URI> selectedList = new ArrayList<URI>();
        for (URI uri : sb.getSelected())
        {
            selectedList.add(uri);
        }
        for (URI uri : selectedList)
        {
            ImageController imageController = new ImageController(sb.getUser());
            Image img = imageController.retrieve(uri);
            if (img.getProperties().getStatus() != Status.RELEASED)
            {
                imageController.delete(img, sb.getUser());
                sb.getSelected().remove(uri);
            }
        }
        if (sb.getSelected().size() == 0)
        {
            BeanHelper.info(sb.getMessage("success_delete"));
            return "pretty:images";
        }
        else
        {
            BeanHelper.info(sb.getMessage("released_item_delete_error"));
            return "pretty:";
        }
    }

    public void logInEscidoc() throws Exception
    {
        String userName = PropertyReader.getProperty("imeji.escidoc.user");
        String password = PropertyReader.getProperty("imeji.escidoc.password");
        escidocUserHandle = LoginHelper.login(userName, password);
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

    public void setCurrentCollection(URI currentCollection)
    {
        this.currentCollection = currentCollection;
    }

    public URI getCurrentCollection()
    {
        return currentCollection;
    }

    public SessionBean getSb()
    {
        return sb;
    }

    public void setSb(SessionBean sb)
    {
        this.sb = sb;
    }




    public void setEscidocUserHandle(String escidocUserHandle)
    {
        this.escidocUserHandle = escidocUserHandle;
    }
}
