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
import de.mpg.jena.controller.ImageController;
import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.export.ExportManager;
import de.mpg.jena.search.SearchResult;
import de.mpg.jena.util.ObjectHelper;
import de.mpg.jena.vo.Album;
import de.mpg.jena.vo.CollectionImeji;
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
		String collectionId = req.getParameter("col");
		String albumId = req.getParameter("alb");
		String format =  req.getParameter("format");
		int maximumNumberOfRecords = 20;
		
		if (req.getParameter("n") != null)
		{
			maximumNumberOfRecords = Integer.parseInt(req.getParameter("n"));
		}

		User user = getSessionBean(req, resp).getUser();

		ImageController imageController = new ImageController(user);

		List<SearchCriterion> scList;
		try 
		{
			scList = URLQueryTransformer.transform2SCList(query);
		} 
		catch (Exception e) 
		{
			throw new RuntimeException(e);
		}

		SearchResult result;
		
		if (collectionId != null)
		{
			result = imageController.searchImagesInContainer(ObjectHelper.getURI(CollectionImeji.class, collectionId), scList, null, maximumNumberOfRecords, 0);
		}
		else if (albumId != null)
		{
			result = imageController.searchImagesInContainer(ObjectHelper.getURI(Album.class, albumId), scList, null, maximumNumberOfRecords, 0);
		}
		else
		{
			result = imageController.searchImages(scList, null);
		}

		if (result.getNumberOfRecords() > 0 && result.getNumberOfRecords() > maximumNumberOfRecords)
		{
			result.setResults(result.getResults().subList(0, maximumNumberOfRecords));
		}
		
		ExportManager exportManager = new ExportManager(resp.getOutputStream());
		exportManager.export(result, format);
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
		UIViewRoot view = facesContext.getApplication().getViewHandler().createView(facesContext, "faces");
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
