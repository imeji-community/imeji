package de.mpg.escidoc.faces.beans;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import de.mpg.escidoc.faces.util.BeanHelper;

public class CleanerBean
{
    public CleanerBean()
    {
        // TODO Auto-generated constructor stub
    }
    
    public String getCleanedSessionBean()
    {
        FacesContext fc =  FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
        SessionBean sessionBean = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
        // Reinitialize the form when user not anymore in it
        if (!"/faces/jsf/AddAlbum.xhtml".equals(request.getRequestURI()) 
                && !"/faces/jsf/EditAlbum.xhtml".equals(request.getRequestURI())
                && request.getParameter("album") == null)
        {
           //TODO remove this.
        }
        return "";
    }
}
