/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.history;

import java.io.IOException;
import java.util.Map;

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

import com.ocpsoft.pretty.PrettyContext;

import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.session.SessionBean;

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
        try
        {
            // Limit the case to filter: dispachertype only forward, and only HTTP GET method
            if (DispatcherType.FORWARD.compareTo(serv.getDispatcherType()) == 0)
            {
                HttpServletRequest request = (HttpServletRequest)serv;
                servletContext = request.getSession().getServletContext();
                if ("GET".equals(request.getMethod()))
                {
                    dofilterImpl(request, resp);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            chain.doFilter(serv, resp);
        }
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
        Navigation nav = getNavigation(request, resp);
        SessionBean session = getSessionBean(request, resp);
        if (session != null)
        {
            String label = PageURIHelper
                    .getPageLabel(PrettyContext.getCurrentInstance(request).getRequestURL().toURL());
            String h = request.getParameter("h");
            String uri = nav.getApplicationUri() + PrettyContext.getCurrentInstance(request).getRequestURL().toURL();
            Map<String, String[]> params = PrettyContext.getCurrentInstance(request).getRequestQueryString()
                    .getParameterMap();
            Page p = new Page(uri, label, params, session.getUser());
            if (request.getParameter("h") == null)
            {
                hs.addPage(p);
            }
            else
            {
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
     * Return the {@link SessionBean}
     * 
     * @param req
     * @return
     */
    private SessionBean getSessionBean(HttpServletRequest req, ServletResponse resp)
    {
        return (SessionBean)getBean(SessionBean.class, req, resp);
    }

    /**
     * Get the {@link HistorySession} from the {@link FacesContext}
     * 
     * @param request
     * @param resp
     * @return
     */
    private HistorySession getHistorySession(HttpServletRequest req, ServletResponse resp)
    {
        return (HistorySession)getBean(HistorySession.class, req, resp);
    }

    /**
     * return the current {@link Navigation}
     * 
     * @param request
     * @param resp
     * @return
     */
    private Navigation getNavigation(HttpServletRequest req, ServletResponse resp)
    {
        return (Navigation)getBean(Navigation.class, req, resp);
    }

    private Object getBean(Class<?> c, ServletRequest request, ServletResponse resp)
    {
        String name = c.getSimpleName();
        FacesContext fc = getFacesContext(request, resp);
        Object result = fc.getExternalContext().getSessionMap().get(name);
        if (result == null)
        {
            try
            {
                Object b = c.newInstance();
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(name, b);
                return b;
            }
            catch (Exception e)
            {
                throw new RuntimeException("Error creating History Session", e);
            }
        }
        else
        {
            return result;
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
