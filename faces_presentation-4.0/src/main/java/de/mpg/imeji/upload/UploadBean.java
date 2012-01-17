/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.imeji.upload;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.opensaml.ws.transport.http.HttpServletRequestAdapter;

import de.escidoc.core.resources.om.item.Item;
import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.collection.ViewCollectionBean;
import de.mpg.imeji.escidoc.EscidocHelper;
import de.mpg.imeji.upload.deposit.DepositController;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.util.LoginHelper;
import de.mpg.imeji.util.PropertyReader;
import de.mpg.imeji.util.UrlHelper;
import de.mpg.jena.controller.UserController;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.User;

public class UploadBean
{
	private CollectionImeji collection;
	private SessionBean sessionBean;
	private String id;
	private String escidocContext;
	private String escidocUserHandle;
	private User user;
	private String title;
	private String format;
	private String mimetype;
	private String description;

	private String totalNum ;
	private int sNum;
	private int fNum;
	private List<String> sFiles;
	private List<String> fFiles;

	public UploadBean()
	{
		sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);

		try 
		{
			escidocContext = PropertyReader.getProperty("escidoc.imeji.context.id");
			logInEscidoc();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}    

	public void status()
	{
		if(UrlHelper.getParameterBoolean("init"))
		{
			loadCollection();
			totalNum = "";
			sNum = 0;
			fNum = 0;
			sFiles = new ArrayList<String>();
			fFiles= new ArrayList<String>();

		}
		else if (UrlHelper.getParameterBoolean("start"))
		{
			try 
			{
				upload();
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
		else if(UrlHelper.getParameterBoolean("done"))
		{
			try 
			{
				totalNum = UrlHelper.getParameterValue("totalNum");
				report();
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
	}


	public void upload() throws IOException, FileUploadException
	{
		HttpServletRequest req = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
		boolean isMultipart = ServletFileUpload.isMultipartContent(req);
		if(isMultipart)
		{  
			ServletFileUpload upload = new ServletFileUpload();
			// Parse the request
			FileItemIterator iter = upload.getItemIterator(req);
			while (iter.hasNext()) 
			{
				FileItemStream item = iter.next();
				String name = item.getFieldName();
				InputStream stream = item.openStream();
				if (!item.isFormField()) 
				{
					title =item.getName();
					StringTokenizer st = new StringTokenizer(title, ".");
					while (st.hasMoreTokens())
					{
						format = st.nextToken();
					}
					mimetype = "image/" + format;

					// TODO remove static image description
					description = "";
					try
					{
						UserController uc = new UserController(null);
						User user = uc.retrieve(getUser().getEmail());
						try
						{
							DepositController controller = new DepositController();
							Item escidocItem = controller.createEscidocItem(stream, title, mimetype, format);
							controller.createImejiImage(collection, user, escidocItem.getOriginObjid(), title
									, URI.create(EscidocHelper.getOriginalResolution(escidocItem))
									, URI.create(EscidocHelper.getThumbnailUrl(escidocItem))
									, URI.create(EscidocHelper.getWebResolutionUrl(escidocItem)));
							sNum += 1;
							sFiles.add(title);
						} 
						catch (Exception e)
						{
							fNum += 1;
							fFiles.add(title);
							throw new RuntimeException(e);
						}
					}
					catch (Exception e)
					{
						throw new RuntimeException(e);
					}
				} 
			}
		}
	}

	public String report() throws Exception{
		setTotalNum(totalNum);
		setsNum(sNum);
		setsFiles(sFiles);
		setfNum(fNum);
		setfFiles(fFiles);
		return "";
	}

	public String getTotalNum() {
		System.err.println("totalNum = " +totalNum);
		return totalNum;
	}

	public void setTotalNum(String totalNum) {
		this.totalNum = totalNum;
	}

	public int getsNum() {
		return sNum;
	}

	public void setsNum(int sNum) {
		this.sNum = sNum;
	}

	public int getfNum() {
		return fNum;
	}

	public void setfNum(int fNum) {
		this.fNum = fNum;
	}

	public List<String> getsFiles() {
		return sFiles;
	}

	public void setsFiles(List<String> sFiles) {
		this.sFiles = sFiles;
	}

	public List<String> getfFiles() {
		return fFiles;
	}

	public void setfFiles(List<String> fFiles) {
		this.fFiles = fFiles;
	}

	public void loadCollection()
	{
		if (id != null)
		{
			((ViewCollectionBean)BeanHelper.getSessionBean(ViewCollectionBean.class)).setId(id);
			((ViewCollectionBean)BeanHelper.getSessionBean(ViewCollectionBean.class)).init();
			//collection = ObjectLoader.loadCollection(ObjectHelper.getURI(CollectionImeji.class,id), sessionBean.getUser());
			collection = ((ViewCollectionBean)BeanHelper.getSessionBean(ViewCollectionBean.class)).getCollection();
		}
		else
		{
			BeanHelper.error(sessionBean.getLabel("error") + "No ID in URL");
		}
	}

	public void logInEscidoc() throws Exception
	{
		String userName = PropertyReader.getProperty("imeji.escidoc.user");
		String password = PropertyReader.getProperty("imeji.escidoc.password");
		escidocUserHandle = LoginHelper.login(userName, password);
	}

	public CollectionImeji getCollection()
	{
		return collection;
	}

	public void setCollection(CollectionImeji collection)
	{
		this.collection = collection;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getEscidocContext()
	{
		return escidocContext;
	}

	public void setEscidocContext(String escidocContext)
	{
		this.escidocContext = escidocContext;
	}

	public String getEscidocUserHandle()
	{
		return escidocUserHandle;
	}

	public void setEscidocUserHandle(String escidocUserHandle)
	{
		this.escidocUserHandle = escidocUserHandle;
	}

	public User getUser()
	{
		return sessionBean.getUser();
	}

	public void setUser(User user)
	{
		this.user = user;
	}
}
