package de.mpg.imeji.image;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.xml.rpc.ServiceException;

import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyPublishedException;
import de.escidoc.core.common.exceptions.application.violated.LockingException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.metadata.EditMetadataBean;
import de.mpg.imeji.upload.deposit.DepositController;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.util.LoginHelper;
import de.mpg.imeji.util.UrlHelper;
import de.mpg.imeji.vo.util.ImejiFactory;
import de.mpg.jena.controller.CollectionController;
import de.mpg.jena.controller.ImageController;
import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.controller.SearchCriterion.Filtertype;
import de.mpg.jena.controller.SearchCriterion.ImejiNamespaces;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.Image;
import de.mpg.jena.vo.Properties.Status;

public class SelectedBean extends ImagesBean
{
    private int totalNumberOfRecords;
    private SessionBean sb;
    public String getEscidocUserHandle() throws Exception {
        String userName = PropertyReader.getProperty("imeji.escidoc.user");
        String password = PropertyReader.getProperty("imeji.escidoc.password");
        escidocUserHandle = LoginHelper.login(userName, password);
		return escidocUserHandle;
	}

	public void setEscidocUserHandle(String escidocUserHandle) {
		this.escidocUserHandle = escidocUserHandle;
	}

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
            if (UrlHelper.getParameterBoolean("reset"))
            {
                editMetadataBean = new EditMetadataBean((List<Image>)images);
            }
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
        sb.getSelected().clear();
        return "pretty:images";
    }
        
    public String deleteAll() throws Exception{
    	ImageController imageController = new ImageController(sb.getUser());
    	for(int i= 0;  i<sb.getSelectedSize(); i++){
    		Image img = imageController.retrieve(sb.getSelected().get(i));
        	CollectionController collectionController = new CollectionController(sb.getUser());
        	CollectionImeji coll = collectionController.retrieve(img.getCollection());
        	if(coll.getProperties().getStatus() != Status.RELEASED){
        		DepositController.deleteImejiItem(img, getEscidocUserHandle(), sb.getUser());
        		sb.getSelected().remove(img.getId());
        	}
    	}
    	if(sb.getSelected().size()==0){
    		BeanHelper.info(sb.getMessage("success_delete"));
    		return "pretty:images";
    	}
    	else{    
            BeanHelper.info(sb.getMessage("released_item_delete_error"));
    		return "pretty:";
    	}
    }
        
    public void logInEscidoc() throws Exception{
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
}
