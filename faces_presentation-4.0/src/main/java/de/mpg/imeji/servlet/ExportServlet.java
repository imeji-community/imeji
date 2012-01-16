package de.mpg.imeji.servlet;

import java.io.IOException;
import java.util.List;

import javax.faces.FactoryFinder;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.search.URLQueryTransformer;
import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.export.ExportManager;
import de.mpg.jena.search.SearchResult;
import de.mpg.jena.vo.User;

public class ExportServlet extends HttpServlet
{
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{		
		String query = req.getParameter("q");

		User user = getSessionBean(req, resp).getUser();
		
		List<SearchCriterion> scList;
		
		try 
		{
			scList = URLQueryTransformer.transform2SCList(query);
		} 
		catch (Exception e) 
		{
			throw new RuntimeException(e);
		}

		ExportManager exportManager = new ExportManager(resp.getOutputStream(), user, req.getParameterMap());
		
		resp.setHeader("Connection", "close");
		resp.setHeader("Content-Type", exportManager.getContentType());
		
		SearchResult result = exportManager.search(scList);
		exportManager.export(result);
		
	}

	private SessionBean getSessionBean(HttpServletRequest req, HttpServletResponse resp)
	{
		FacesContext fc =  getFacesContext(req, resp);

		Object session = fc.getCurrentInstance()
		.getExternalContext()
		.getSessionMap()
		.get("SessionBean");
		
		if (session == null)
		{
			try
			{
				SessionBean newSession = SessionBean.class.newInstance();
				FacesContext
				.getCurrentInstance()
				.getExternalContext()
				.getSessionMap()
				.put("SessionBean", newSession);
				return newSession;
			}
			catch (Exception e)
			{
				throw new RuntimeException("Error creating Session", e);
			}
		}

		return (SessionBean) session;
	}

	/**
	 * Get Faces Context from Filter
	 * @param request
	 * @param response
	 * @return
	 */
	private FacesContext getFacesContext(ServletRequest request, ServletResponse response) 
	{
		// Try to get it first
		FacesContext facesContext = FacesContext.getCurrentInstance();
		//		if (facesContext != null) return facesContext;

		FacesContextFactory contextFactory = (FacesContextFactory)FactoryFinder.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
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
		protected static void setFacesContextAsCurrentInstance(FacesContext facesContext) {
			FacesContext.setCurrentInstance(facesContext);
		}
	}
}
