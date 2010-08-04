package de.mpg.escidoc.faces.init;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import de.escidoc.core.common.exceptions.application.notfound.ContentModelNotFoundException;
import de.mpg.escidoc.faces.util.LoginHelper;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * Initialize application on server start
 * @author saquet
 *
 */
public class InitializerServlet extends HttpServlet
{
	private String adminHandle = null;
    @Override
    public void init() throws ServletException
    {
        super.init();
        adminHandle = LoginHelper.loginSystemAdmin();
        initContentModels();
    }
    
    /**
     * Check that content models are created.
     * <br> If not, create them.
     */
    public void initContentModels()
    {
    	try 
    	{
        	//initContentModel(PropertyReader.getProperty("escidoc.faces.container.content-model.id"), "Faces Album Content Model");
        	//initContentModel(PropertyReader.getProperty("escidoc.faces.collection.content-model.id"), "Faces Collection Content Model");
		} 
    	catch (Exception e) 
    	{
			throw new RuntimeException("Error initializing content-models: " + e);
		}
    }
    
    public void initContentModel(String pid, String name) throws Exception
    {
    	try 
    	{
    		ServiceLocator.getContentModelHandler(adminHandle).retrieve(pid);
		} 
    	catch (ContentModelNotFoundException e) 
    	{
    		String param = "<escidocContentModel:content-model xmlns:escidocContentModel=\"http://www.escidoc.de/schemas/contentmodel/0.1\" xmlns:prop=\"http://escidoc.de/core/01/properties/\">"
    			+ "<escidocContentModel:properties>"
    			+ "<prop:name>" + name+ "</prop:name>"
    			+ "<prop:description>" + name + "</prop:description>" 
    			+ "<prop:pid>" + pid + "</prop:pid>" 
    			+ "</escidocContentModel:properties>"
    		    + "</escidocContentModel:content-model>";
    		// TODO wait for filter for content model.
    		//String response = ServiceLocator.getContentModelHandler(adminHandle).create(param);
    		//System.out.println("response");
		}
    }
    
    /* (non-Javadoc)
     * @see javax.servlet.GenericServlet#destroy()
     */
    public void destroy()
    {
        super.destroy();
    }
    
}
