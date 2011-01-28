package de.mpg.jena.controller;

import java.net.URI;
import java.security.MessageDigest;
import java.util.Collection;

import de.mpg.jena.vo.User;

public class UserController extends ImejiController
{
	
	public UserController(User user)
	{
		super(user);
	}
	
	public void create(User user)
	{
		base.begin();
		bean2RDF.saveDeep(user);
		base.commit();
	}
	
	public User retrieve(String email)
	{
		return rdf2Bean.load(User.class, email);
	}
	
	public Collection<User> retrieveAll()
	{
		return rdf2Bean.load(User.class);
	}
	
	
	public User retrieve(URI id)
	{
		String email = id.getPath().split("/Person/")[1];
		if(email != null)
		{
			return retrieve(email);
		}
		return null;
	}
	
	public void update(User user)
	{
		base.begin();
		bean2RDF.saveDeep(user);
		cleanGraph();
		base.commit();
	}
	
	public static String convertToMD5(String pass) throws Exception
	{
		
		MessageDigest dig = MessageDigest.getInstance("MD5");
		dig.update(pass.getBytes("UTF-8"));
		byte messageDigest[] = dig.digest();     
		StringBuffer hexString = new StringBuffer();
		for (int i=0;i<messageDigest.length;i++) {
		    hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
		}
		return hexString.toString();
	}
	
	
	@Override
    protected String getSpecificFilter() throws Exception
    {
       return "";
    }

    @Override
    protected String getSpecificQuery() throws Exception
    {
        return "";
    }
	    
	    

	
	

}
