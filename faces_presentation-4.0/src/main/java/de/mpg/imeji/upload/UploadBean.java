package de.mpg.imeji.upload;

import java.io.IOException;


import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import javax.faces.context.FacesContext;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.xalan.xsltc.compiler.sym;

import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.imeji.beans.Navigation;
import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.collection.CollectionSessionBean;
import de.mpg.imeji.escidoc.ItemVO;

import de.mpg.imeji.upload.deposit.DepositController;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.util.LoginHelper;
import de.mpg.imeji.util.UrlHelper;

import de.mpg.jena.controller.CollectionController;
import de.mpg.jena.controller.UserController;

import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.User;

public class UploadBean
{
    private CollectionImeji collection;
    private SessionBean sessionBean;
    private CollectionSessionBean collectionSession;
    private CollectionController collectionController;
    private String id;
    private String escidocContext = "escidoc:108013";
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
    
    public UploadBean(){
    	sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        collectionSession = (CollectionSessionBean)BeanHelper.getSessionBean(CollectionSessionBean.class);
        collectionController = new CollectionController(sessionBean.getUser());
        try {
			logInEscidoc();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }    
          
    public void status(){
    	if(UrlHelper.getParameterBoolean("init")){
			loadCollection();
    	    totalNum = "";
    	    sNum = 0;
    	    fNum = 0;
    	    sFiles = new ArrayList<String>();
    	    fFiles= new ArrayList<String>();
    		
    	}else if (UrlHelper.getParameterBoolean("start")){
    		try {
				upload();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}else if(UrlHelper.getParameterBoolean("done")){
    		try {
    			totalNum = UrlHelper.getParameterValue("totalNum");
				report();
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
	}
    
	public void upload() throws IOException{
    	HttpServletRequest req = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
    	title = req.getParameter("name");
        ServletInputStream inputStream = req.getInputStream();
        StringTokenizer st = new StringTokenizer(title, ".");
        while (st.hasMoreTokens())
            format = st.nextToken();
        mimetype = "image/" + format;
     
        // TODO remove static image description
        description = "";
        try{
            UserController uc = new UserController(null);
            User user = uc.retrieve(getUser().getEmail());
            try{
                ItemVO item = DepositController.createImejiItem(inputStream, title, description, mimetype, format, escidocUserHandle, collection.getId().toString(), escidocContext);
               DepositController.depositImejiItem(item, escidocUserHandle, collection, user, title);
               sNum += 1;
               sFiles.add(title);
            } catch (Exception e){
            	fNum += 1;
            	fFiles.add(title);
                throw new RuntimeException(e);
            }
        }catch (Exception e){
            throw new RuntimeException(e);
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

	public void loadCollection(){
        if (id != null){
            try{
                collection = collectionController.retrieve(id);
            }catch (Exception e){
                BeanHelper.error("Collection " + id + " not found.");
            }
        }else{
            BeanHelper.error("No Collection information found. Please check your URL.");
        }
    }

    public void logInEscidoc() throws Exception{
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
