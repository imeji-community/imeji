/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.servlet;

import java.io.IOException;

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

import org.apache.http.client.HttpResponseException;

import de.mpg.imeji.logic.export.ExportManager;
import de.mpg.imeji.logic.search.SearchResult;
import de.mpg.imeji.logic.search.vo.SearchQuery;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.search.URLQueryTransformer;
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
        String query = req.getParameter("q");
        User user = getSessionBean(req, resp).getUser();
        SearchQuery searchQuery = new SearchQuery();
        try
        {
            searchQuery = URLQueryTransformer.parseStringQuery(query);
            ExportManager exportManager = new ExportManager(resp.getOutputStream(), user, req.getParameterMap());
            resp.setHeader("Connection", "close");
            resp.setHeader("Content-Type", exportManager.getContentType());
            SearchResult result = exportManager.search(searchQuery);
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

    private SessionBean getSessionBean(HttpServletRequest req, HttpServletResponse resp)
    {
        //FacesContext fc = getFacesContext(req, resp);
        Object session = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("SessionBean");
        if (session == null)
        {
            try
            {
                SessionBean newSession = SessionBean.class.newInstance();
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("SessionBean", newSession);
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
