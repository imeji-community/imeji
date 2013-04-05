/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.history;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.FactoryFinder;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * {@link Filter} for the imeji history
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class HistoryFilter implements Filter
{
    private FilterConfig filterConfig = null;
    private ServletContext servletContext;

    @Override
    public void destroy()
    {
        setFilterConfig(null);
    }

    @Override
    public void doFilter(ServletRequest serv, ServletResponse resp, FilterChain chain) throws IOException,
            ServletException
    {
        // Limit the case to filter: dispachertype only forward, and only HTTP GET method
        if (DispatcherType.FORWARD.compareTo(serv.getDispatcherType()) == 0)
        {
            HttpServletRequest request = (HttpServletRequest)serv;
            if ("GET".equals(request.getMethod()))
            {
                servletContext = request.getSession().getServletContext();
                dofilterImpl(request, resp);
            }
        }
        chain.doFilter(serv, resp);
    }

    /**
     * Implement the History filter
     * 
     * @param request
     * @param resp
     */
    private void dofilterImpl(HttpServletRequest request, ServletResponse resp)
    {
        HistorySession hs = getHistorySession(request, resp);
        String q = request.getParameter("q");
        String h = request.getParameter("h");
        String f = request.getParameter("f");
        String id = request.getParameter("id");
        // Parameter used by pretty query to pass parameter defined in pretty-config in the url pattern
        String[] ids = request.getParameterValues("com.ocpsoft.vP_0");
        if (id != null)
        {
            // The id has been defined as a url parameter
            List<String> l = new ArrayList<String>();
            l.add(id);
            ids = l.toArray(new String[1]);
        }
        // If f exists, then it is a filter, not added to history
        if (f == null && request.getPathInfo() != null)
        {
            if (h == null)
            {
                // if h not defined, then it is a new page
                hs.add(request.getPathInfo().replaceAll("/", ""), q, ids);
            }
            else if (!"".equals(h))
            {
                // If h defined, then it is an history link
                hs.remove(Integer.parseInt(h));
            }
        }
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException
    {
        this.setFilterConfig(arg0);
    }

    /**
     * Get the {@link HistorySession} from the {@link FacesContext}
     * 
     * @param request
     * @param resp
     * @return
     */
    private HistorySession getHistorySession(ServletRequest request, ServletResponse resp)
    {
        String name = HistorySession.class.getSimpleName();
        FacesContext fc = getFacesContext(request, resp);
        Object result = fc.getExternalContext().getSessionMap().get(name);
        if (result == null)
        {
            try
            {
                HistorySession newBean = HistorySession.class.newInstance();
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(name, newBean);
                return newBean;
            }
            catch (Exception e)
            {
                throw new RuntimeException("Error creating History Session", e);
            }
        }
        else
        {
            return (HistorySession)result;
        }
    }

    /**
     * Get {@link FacesContext} from Filter
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
        facesContext = contextFactory.getFacesContext(servletContext, request, response, lifecycle);
        // Set using our inner class
        InnerFacesContext.setFacesContextAsCurrentInstance(facesContext);
        // set a new viewRoot, otherwise context.getViewRoot returns null
        UIViewRoot view = facesContext.getApplication().getViewHandler().createView(facesContext, "imeji");
        facesContext.setViewRoot(view);
        return facesContext;
    }

    public FilterConfig getFilterConfig()
    {
        return filterConfig;
    }

    public void setFilterConfig(FilterConfig filterConfig)
    {
        this.filterConfig = filterConfig;
    }

    public abstract static class InnerFacesContext extends FacesContext
    {
        protected static void setFacesContextAsCurrentInstance(FacesContext facesContext)
        {
            FacesContext.setCurrentInstance(facesContext);
        }
    }
}
