package de.mpg.imeji.upload;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.collection.CollectionSessionBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.util.LoginHelper;
import de.mpg.imeji.util.UrlHelper;
import de.mpg.jena.controller.CollectionController;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.User;

public class UploadBean
{
    private CollectionImeji collection;
    private SessionBean sessionBean;
    private CollectionSessionBean collectionSession;
    private CollectionController collectionController;
    private String id;
    private String escidocContext = "escidoc:108013";
    private String escidocUserHandle;
    private User user;

    public UploadBean()
    {
        HttpServletRequest req = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
        sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        collectionSession = (CollectionSessionBean)BeanHelper.getSessionBean(CollectionSessionBean.class);
        collectionController = new CollectionController(sessionBean.getUser());
    }

    public void init() throws Exception
    {
        if (UrlHelper.getParameterBoolean("init"))
        {
            logInEscidoc();
            loadCollection();
        }
    }

    public void loadCollection()
    {
        if (id != null)
        {
            try
            {
                collection = collectionController.retrieve(id);
            }
            catch (Exception e)
            {
                BeanHelper.error("Collection " + id + " not found.");
            }
        }
        else
        {
            BeanHelper.error("No Collection information found. Please check your URL.");
        }
    }

    public void logInEscidoc() throws Exception
    {
        String userName = PropertyReader.getProperty("imeji.escidoc.user");
        String password = PropertyReader.getProperty("imeji.escidoc.password");
        escidocUserHandle = LoginHelper.login(userName, password);
    }

    public CollectionImeji getCollection()
    {
        return collection;
    }

    public void setCollection(CollectionImeji collection)
    {
        this.collection = collection;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getEscidocContext()
    {
        return escidocContext;
    }

    public void setEscidocContext(String escidocContext)
    {
        this.escidocContext = escidocContext;
    }

    public String getEscidocUserHandle()
    {
        return escidocUserHandle;
    }

    public void setEscidocUserHandle(String escidocUserHandle)
    {
        this.escidocUserHandle = escidocUserHandle;
    }

    public User getUser()
    {
        return sessionBean.getUser();
    }

    public void setUser(User user)
    {
        this.user = user;
    }
}
