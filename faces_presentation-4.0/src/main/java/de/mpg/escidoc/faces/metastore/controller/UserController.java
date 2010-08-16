package de.mpg.escidoc.faces.metastore.controller;

import java.net.URI;
import java.security.MessageDigest;

import de.mpg.escidoc.faces.metastore.vo.User;

public class UserController extends ImejiController{
	
	public UserController(User user)
	{
		super(user);
	}
	
	public void create (User user)
	{
		base.begin();
		bean2RDF.saveDeep(user);
		base.commit();
		base.close();
	}
	

	public User retrieve(String email)
	{
		return rdf2Bean.load(User.class, email);
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

	
	

}
