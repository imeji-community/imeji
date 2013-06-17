/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.servlet;

import java.io.IOException;
import java.util.Date;

import javax.faces.FactoryFinder;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.http.client.HttpResponseException;

import de.mpg.imeji.logic.export.ExportManager;
import de.mpg.imeji.logic.search.SearchResult;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.session.SessionBean;

public class ExportServlet extends HttpServlet
{
    /**
     * 
     */
    private static final long serialVersionUID = -777947169051357999L;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {
        // String query = req.getParameter("q");
        SessionBean session = getSessionBean(req, resp);
        User user = session.getUser();
        String instanceName = session.getInstanceName();
        // SearchQuery searchQuery = new SearchQuery();
        try
        {
            // searchQuery = URLQueryTransformer.parseStringQuery(query);
            ExportManager exportManager = new ExportManager(resp.getOutputStream(), user, req.getParameterMap());
            String exportName = instanceName + "_";
            exportName += new Date().toString().replace(" ", "_").replace(":", "-");
            if (exportManager.getContentType().equalsIgnoreCase("application/xml"))
            {
                exportName += ".xml";
            }
            if (exportManager.getContentType().equalsIgnoreCase("application/zip"))
            {
                exportName += ".zip";
            }
            resp.setHeader("Connection", "close");
            resp.setHeader("Content-Type", exportManager.getContentType());
            resp.setHeader("Content-disposition", "filename=" + exportName);
            SearchResult result = exportManager.search();
            exportManager.export(result);
        }
        catch (HttpResponseException he)
        {
            resp.sendError(he.getStatusCode(), he.getMessage());
        }
        catch (IOException e)
        {
            resp.sendError(500, e.getMessage());
        }
    }

    /**
     * Get the {@link SessionBean} from the {@link HttpSession}
     * 
     * @param req
     * @param resp
     * @return
     */
    private SessionBean getSessionBean(HttpServletRequest req, HttpServletResponse resp)
    {
        FacesContext fc = getFacesContext(req, resp);
        Object session = fc.getExternalContext().getSessionMap().get("SessionBean");
        if (session == null)
        {
            try
            {
                SessionBean newSession = SessionBean.class.newInstance();
                fc.getExternalContext().getSessionMap().put("SessionBean", newSession);
                return newSession;
            }
            catch (Exception e)
            {
                throw new RuntimeException("Error creating Session", e);
            }
        }
        return (SessionBean)session;
    }

    /**
     * Get Faces Context from Filter
     * 
     * @param request
     * @param response
     * @return
     */
    private FacesContext getFacesContext(ServletRequest request, ServletResponse response)
    {
        // Try to get it first
        FacesContext facesContext = FacesContext.getCurrentInstance();
        // if (facesContext != null) return facesContext;
        FacesContextFactory contextFactory = (FacesContextFactory)FactoryFinder
                .getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
        LifecycleFactory lifecycleFactory = (LifecycleFactory)FactoryFinder.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
        Lifecycle lifecycle = lifecycleFactory.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);
        facesContext = contextFactory.getFacesContext(getServletContext(), request, response, lifecycle);
        // Set using our inner class
        InnerFacesContext.setFacesContextAsCurrentInstance(facesContext);
        // set a new viewRoot, otherwise context.getViewRoot returns null
        UIViewRoot view = facesContext.getApplication().getViewHandler().createView(facesContext, "imeji");
        facesContext.setViewRoot(view);
        return facesContext;
    }

    public abstract static class InnerFacesContext extends FacesContext
    {
        protected static void setFacesContextAsCurrentInstance(FacesContext facesContext)
        {
            FacesContext.setCurrentInstance(facesContext);
        }
    }
}
