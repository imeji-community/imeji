/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.history;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
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
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotAllowedException;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.ocpsoft.pretty.PrettyContext;
import com.ocpsoft.pretty.PrettyFilter;
import com.ocpsoft.pretty.faces.application.PrettyNavigationHandler;
import com.ocpsoft.pretty.faces.config.PrettyConfig;
import com.ocpsoft.pretty.faces.config.PrettyConfigParser;
import com.ocpsoft.pretty.faces.config.PrettyConfigurator;
import com.ocpsoft.pretty.faces.config.mapping.UrlMapping;
import com.ocpsoft.pretty.faces.url.URL;

import de.mpg.imeji.exceptions.AuthenticationError;
import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.exceptions.NotAllowedError;
import de.mpg.imeji.exceptions.NotFoundException;
import de.mpg.imeji.logic.controller.SpaceController;
import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.session.SessionBean;

/**
 * {@link Filter} for the imeji history
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class HistoryFilter implements Filter {
	private FilterConfig filterConfig = null;
	private ServletContext servletContext;

	@Override
	public void destroy() {
		setFilterConfig(null);
	}

	@Override
	public void doFilter(ServletRequest serv, ServletResponse resp,
			FilterChain chain) throws IOException, ServletException {
		try {
			
			
			// Limit the case to filter: dispachertype only forward, and only
			// HTTP GET method
			if (DispatcherType.FORWARD.compareTo(serv.getDispatcherType()) == 0) {
				HttpServletRequest request = (HttpServletRequest) serv;
				servletContext = request.getSession().getServletContext();
				if ("GET".equals(request.getMethod())) {
					dofilterImpl(request, resp);
				}
			}
			
		} catch (Exception e) {
			System.out.println("In History filter Exception");
			if (e instanceof NotFoundException || e instanceof NotFoundException
					|| e instanceof NullPointerException) 
			{
				if ("SPACE_NOT_FOUND".equals(e.getMessage())) {
					
					((HttpServletResponse) resp).sendRedirect( ((Navigation)getNavigation((HttpServletRequest)serv, resp)).getApplicationUrl() );
				}
				else
				{
					((HttpServletResponse) resp).sendError(
							Status.NOT_FOUND.getStatusCode(), "RESOURCE_NOT_FOUND");
				}
				
			} else if (e instanceof AuthenticationError) {
				redirectToLoginPage(serv, resp);
			} else if (e instanceof NotAllowedError
					|| e instanceof NotAllowedException) {
				((HttpServletResponse) resp).sendError(
						Status.FORBIDDEN.getStatusCode(), "FORBIDDEN");
			} else if (e instanceof BadRequestException) {
				((HttpServletResponse) resp).sendError(
						Status.BAD_REQUEST.getStatusCode(), "BAD_REQUEST");
			} else {

				((HttpServletResponse) resp).sendError(
						Status.INTERNAL_SERVER_ERROR.getStatusCode(),
						"INTERNAL_SERVER_ERROR");
			}

		} finally {
			chain.doFilter(serv, resp);
		}
	}
	
	/**
	 * Redicrect the request to the login page
	 * 
	 * @param serv
	 * @param resp
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	private void redirectToLoginPage(ServletRequest serv, ServletResponse resp)
			throws UnsupportedEncodingException, IOException {
		HttpServletRequest request = (HttpServletRequest) serv;
		Navigation nav = getNavigation(request, resp);
		String url = nav.getApplicationUri()
				+ PrettyContext.getCurrentInstance(request).getRequestURL()
						.toURL();
		Map<String, String[]> params = PrettyContext
				.getCurrentInstance(request).getRequestQueryString()
				.getParameterMap();
		((HttpServletResponse) resp).sendRedirect(serv.getServletContext()
				.getContextPath()
				+ "/login?redirect="
				+ URLEncoder.encode(
						url + HistoryUtil.paramsMapToString(params), "UTF-8"));

	}

	/**
	 * Implement the History filter
	 * 
	 * @param request
	 * @param resp
	 * @throws Exception
	 */
	private void dofilterImpl(HttpServletRequest request, ServletResponse resp)
			throws Exception {
		HistorySession hs = getHistorySession(request, resp);
		Navigation nav = getNavigation(request, resp);
		SessionBean session = getSessionBean(request, resp);
		if (session != null) {
			checkSpaceMatching(request, session, hs); 
			//System.out.println("In dofilterImpl= "+session.getSpaceId());
			String h = request.getParameter("h");
			String url = nav.getApplicationUri()
					+ PrettyContext.getCurrentInstance(request).getRequestURL()
							.toURL();
			Map<String, String[]> params = PrettyContext
					.getCurrentInstance(request).getRequestQueryString()
					.getParameterMap();
			HistoryPage p = new HistoryPage(url, params, session.getUser());
			if (request.getParameter("h") == null) {
				hs.addPage(p);
			} else {
				hs.remove(Integer.parseInt(h));
			}
		}
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		this.setFilterConfig(arg0);
	}

	/**
	 * Return the {@link SessionBean}
	 * 
	 * @param req
	 * @return
	 */
	private SessionBean getSessionBean(HttpServletRequest req,
			ServletResponse resp) {
		return (SessionBean) getBean(SessionBean.class, req, resp);
		
	}

	/**
	 * Get the {@link HistorySession} from the {@link FacesContext}
	 * 
	 * @param request
	 * @param resp
	 * @return
	 */
	private HistorySession getHistorySession(HttpServletRequest req,
			ServletResponse resp) {
		return (HistorySession) getBean(HistorySession.class, req, resp);
	}

	/**
	 * return the current {@link Navigation}
	 * 
	 * @param request
	 * @param resp
	 * @return
	 */
	private Navigation getNavigation(HttpServletRequest req,
			ServletResponse resp) {
		return (Navigation) getBean(Navigation.class, req, resp);
	}

	private Object getBean(Class<?> c, ServletRequest request,
			ServletResponse resp) {
		String name = c.getSimpleName();
		FacesContext fc = getFacesContext(request, resp);
		Object result = fc.getExternalContext().getSessionMap().get(name);
		if (result == null) {
			try {
				Object b = c.newInstance();
				FacesContext.getCurrentInstance().getExternalContext()
						.getSessionMap().put(name, b);
				return b;
			} catch (Exception e) {
				throw new RuntimeException("Error creating History Session", e);
			}
		} else {
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
	private FacesContext getFacesContext(ServletRequest request,
			ServletResponse response) {
		// Try to get it first
		FacesContext facesContext = FacesContext.getCurrentInstance();
		// if (facesContext != null) return facesContext;
		FacesContextFactory contextFactory = (FacesContextFactory) FactoryFinder
				.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
		LifecycleFactory lifecycleFactory = (LifecycleFactory) FactoryFinder
				.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
		Lifecycle lifecycle = lifecycleFactory
				.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);
		facesContext = contextFactory.getFacesContext(servletContext, request,
				response, lifecycle);
		// Set using our inner class
		InnerFacesContext.setFacesContextAsCurrentInstance(facesContext);
		// set a new viewRoot, otherwise context.getViewRoot returns null
		UIViewRoot view = facesContext.getApplication().getViewHandler()
				.createView(facesContext, "imeji");
		facesContext.setViewRoot(view);
	
		return facesContext;
	}

	public FilterConfig getFilterConfig() {
		return filterConfig;
	}

	public void setFilterConfig(FilterConfig filterConfig) {
		this.filterConfig = filterConfig;
	}

	public abstract static class InnerFacesContext extends FacesContext {
		protected static void setFacesContextAsCurrentInstance(
				FacesContext facesContext) {
			FacesContext.setCurrentInstance(facesContext);
		}
	}
	
	private void checkSpaceMatching (HttpServletRequest request, SessionBean session, HistorySession hs) throws NotFoundException, ImejiException   {
		//TODO CHANGE ME
		String spaceHome = "space_home";
		
		String matchingUrl= PrettyContext.getCurrentInstance(request).getRequestURL().toURL();

		PrettyConfig pc = PrettyContext.getCurrentInstance(FacesContext.getCurrentInstance()).getConfig();
		
		if (pc.isURLMapped(new URL(matchingUrl))) {
			//System.out.println("URL IS MAPPED "+matchingUrl);
			UrlMapping myMap = 
				pc.getMappingForUrl(PrettyContext.getCurrentInstance(request).getRequestURL());
				//System.out.println("URL IS MAPPED with pattern "+myMap.getPattern()+" and id "+myMap.getId());

			if (myMap.getId().startsWith("space_")){
				String mySpaceId = PrettyContext.getCurrentInstance(request).getRequestURL().toURL();
				//System.out.println("PreCalculated mySpaceId= "+mySpaceId+" 2");
				//System.out.println(StringUtils.substringAfter(matchingUrl, "/space/"));
				mySpaceId = spaceHome.equals(myMap.getId())?StringUtils.substringAfter(matchingUrl, "/space/"): 
							StringUtils.substringBefore(StringUtils.substringAfter(matchingUrl, "/space/"), "/");
				//System.out.println("Calculated mySpaceId= "+mySpaceId+ " sessopmSüace= "+session.getSpaceId());
				if (!mySpaceId.equals(session.getSpaceId())) {
					hs.getPages().clear();
					SpaceController sc = new SpaceController();
						if (!sc.isSpaceByLabel(mySpaceId) ) {
							session.setSpaceId("");
							throw new NotFoundException("SPACE_NOT_FOUND");
						}
				}
			}
			else
			{
				if (!("".equals(session.getSpaceId()))) {
					//Clean old history pages when switching to a new space
					hs.getPages().clear();
					session.setSpaceId("");
				}
			}
		}
	}
}
