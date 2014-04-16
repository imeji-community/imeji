/*
 *
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License"). You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE
 * or http://www.escidoc.de/license.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at license/ESCIDOC.LICENSE.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */
/*
 * Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
 * für wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 */
package de.mpg.imeji.presentation.auth;

import java.io.IOException;

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

import de.mpg.imeji.logic.auth.authentication.HttpAuthentication;
import de.mpg.imeji.presentation.history.HistorySession;
import de.mpg.imeji.presentation.session.SessionBean;

/**
 * {@link Filter} for imeji authentification
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class AuthenticationFilter implements Filter
{
    private FilterConfig filterConfig = null;
    private ServletContext servletContext;

    /*
     * (non-Javadoc)
     * @see javax.servlet.Filter#destroy()
     */
    @Override
    public void destroy()
    {
        setFilterConfig(null);
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse,
     * javax.servlet.FilterChain)
     */
    @Override
    public void doFilter(ServletRequest serv, ServletResponse resp, FilterChain chain) throws IOException,
            ServletException
    {
        HttpServletRequest request = (HttpServletRequest)serv;
        SessionBean session = getSession(request, resp);
        if (session.getUser() == null)
        {
            servletContext = request.getSession().getServletContext();
            HttpAuthentication httpAuthentification = new HttpAuthentication(request);
            session.setUser(httpAuthentification.doLogin());
        }
        chain.doFilter(serv, resp);
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    @Override
    public void init(FilterConfig arg0) throws ServletException
    {
        this.setFilterConfig(arg0);
    }

    public FilterConfig getFilterConfig()
    {
        return filterConfig;
    }

    public void setFilterConfig(FilterConfig filterConfig)
    {
        this.filterConfig = filterConfig;
    }

    /**
     * Get the {@link HistorySession} from the {@link FacesContext}
     * 
     * @param request
     * @param resp
     * @return
     */
    private SessionBean getSession(ServletRequest request, ServletResponse resp)
    {
        return (SessionBean)getBean(SessionBean.class, request, resp);
    }

    /**
     * @param c
     * @param request
     * @param resp
     * @return
     */
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

    public abstract static class InnerFacesContext extends FacesContext
    {
        protected static void setFacesContextAsCurrentInstance(FacesContext facesContext)
        {
            FacesContext.setCurrentInstance(facesContext);
        }
    }
}
