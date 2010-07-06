package de.mpg.escidoc.faces.album.beans;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import de.mpg.escidoc.faces.beans.Navigation;
import de.mpg.escidoc.faces.beans.SessionBean;
import de.mpg.escidoc.faces.util.BeanHelper;
import de.mpg.escidoc.services.common.util.ResourceUtil;
import de.mpg.escidoc.services.framework.PropertyReader;

public class ExportBean
{
    private String FACES_COLLECTION_KEY = "escidoc.faces.collection.file.path";
    private String facesCollectionPath = null;
    
    private AlbumSessionOld albumSession = null;
    private Navigation navigation = null;
    private SessionBean sessionBean = null;
    
    public ExportBean()
    {
        albumSession = (AlbumSessionOld)BeanHelper.getSessionBean(AlbumSessionOld.class);
        navigation = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
        sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        
    	try
        {
            facesCollectionPath = PropertyReader.getProperty(FACES_COLLECTION_KEY);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public void export()
    {
        
    }
    
    public String getCheckExportFormat() throws IOException
    {
    	if (!albumSession.getExportManager().getParameters().getOrignal()
    			&& !albumSession.getExportManager().getParameters().getThumbnails()
    			&& !albumSession.getExportManager().getParameters().getWeb()) 
    	{
			sessionBean.setMessage("Please select at least one Picture resolution!");
    		FacesContext
					.getCurrentInstance()
						.getExternalContext()
							.redirect(navigation.getExportUrl() 
										+ "/"
										+ albumSession.getCurrent().getVersion().getObjectId());
		}
    	
    	return "";
    }
    
    public String exportWholeCollection() throws IOException
    {
        FacesContext ctx = FacesContext.getCurrentInstance();
        SessionBean sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        
        if (sessionBean.isAgreement() && sessionBean.getAllowed())
        {
            File file = ResourceUtil.getResourceAsFile(facesCollectionPath);
            String contentType = "application/zip";
            
            // Initialize response
            HttpServletResponse response = (HttpServletResponse)ctx.getExternalContext().getResponse();
            response.setContentType(contentType);
            response.setHeader("Content-Disposition", "attachment;filename=\"" + file.getName() + "\"");
            
            OutputStream out = response.getOutputStream();
            InputStream in = new FileInputStream(file);
            
            byte[] bytes = new byte[1024];
            int bytesRead;
            
            while ((bytesRead = in.read(bytes)) != -1) 
            {
                out.write(bytes, 0, bytesRead);
            }
            
            in.close();
            out.flush();
            out.close();
            
            sessionBean.setAgreement(false);
        }
        
        if (!sessionBean.isAgreement())
        {
            Navigation navigation = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
            sessionBean.setMessage(sessionBean.getMessage("message_export_agreement"));
            ctx.getExternalContext().redirect(navigation.getApplicationUrl() + "confirmation/download");
        }
        
        return "";
    }
}
