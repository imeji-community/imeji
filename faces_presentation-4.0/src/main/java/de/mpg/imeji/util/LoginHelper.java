package de.mpg.imeji.util;

import java.util.StringTokenizer;

import javax.servlet.http.HttpServletResponse;
import javax.xml.rpc.ServiceException;

import org.apache.axis.encoding.Base64;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.cookie.CookieSpec;
import org.apache.commons.httpclient.methods.PostMethod;

import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;
import de.mpg.imeji.beans.SessionBean;

public class LoginHelper 
{
	private static SessionBean sessionBean = null;
	
	/**
	 * Get handle of System administrator of eSciDoc instance.
	 * @return
	 */
	public static String loginSystemAdmin()
	{
		String handle = null;
		try 
		{
			handle = login(PropertyReader.getProperty("framework.admin.username"),
	                PropertyReader.getProperty("framework.admin.password"));
		} 
		catch (Exception e) 
		{
			sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
			BeanHelper.info(sessionBean.getLabel("error") + ", wrong administrator user. Check config file or FW: " + e);
		}
		return handle;
	}
	
	public static String login(String userName, String password) throws Exception
	{
		String frameworkUrl = ServiceLocator.getFrameworkUrl();
        StringTokenizer tokens = new StringTokenizer( frameworkUrl, "//" );
                
        tokens.nextToken();
        StringTokenizer hostPort = new StringTokenizer(tokens.nextToken(), ":");
        
        String host = hostPort.nextToken();
        int port = Integer.parseInt( hostPort.nextToken() );
        
        HttpClient client = new HttpClient();
        client.getHostConfiguration().setHost( host, port, "http");
        client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
        
        PostMethod login = new PostMethod( frameworkUrl + "/aa/j_spring_security_check");
        login.addParameter("j_username", userName);
        login.addParameter("j_password", password);

        client.executeMethod(login);
        //System.out.println("Login form post: " + login.getStatusLine().toString());
                
        login.releaseConnection();
        CookieSpec cookiespec = CookiePolicy.getDefaultSpec();
        Cookie[] logoncookies = cookiespec.match(
                host, port, "/", false, 
                client.getState().getCookies());
        
        Cookie sessionCookie = logoncookies[0];
        
        PostMethod postMethod = new PostMethod("/aa/login");
        postMethod.addParameter("target", frameworkUrl);
        client.getState().addCookie(sessionCookie);
        client.executeMethod(postMethod);
        //System.out.println("Login second post: " + postMethod.getStatusLine().toString());
      
        if (HttpServletResponse.SC_SEE_OTHER != postMethod.getStatusCode())
        {
            throw new HttpException("Wrong status code: " + login.getStatusCode());
        }
        
        String userHandle = null;
        Header headers[] = postMethod.getResponseHeaders();
        for (int i = 0; i < headers.length; ++i)
        {
            if ("Location".equals(headers[i].getName()))
            {
                String location = headers[i].getValue();
                int index = location.indexOf('=');
                userHandle = new String(Base64.decode(location.substring(index + 1, location.length())));
                //System.out.println("location: "+location);
                //System.out.println("handle: "+userHandle);
            }
        }
        
        if (userHandle == null)
        {
            throw new ServiceException("User not logged in.");
        }
        return userHandle;
	}
}
