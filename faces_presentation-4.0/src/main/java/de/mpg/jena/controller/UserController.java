package de.mpg.jena.controller;

import java.net.URI;
import java.security.MessageDigest;
import java.util.Collection;

import thewebsemantic.RDF2Bean;
import de.mpg.jena.ImejiBean2RDF;
import de.mpg.jena.ImejiJena;
import de.mpg.jena.ImejiRDF2Bean;
import de.mpg.jena.vo.User;

public class UserController extends ImejiController
{
	private static ImejiRDF2Bean imejiRDF2Bean = new ImejiRDF2Bean(ImejiJena.userModel);
	private static ImejiBean2RDF imejiBean2RDF = new ImejiBean2RDF(ImejiJena.userModel);
	
	public UserController(User user)
	{
		super(user);
	}
	
	public void create(User newUser) throws Exception
	{
		imejiBean2RDF = new ImejiBean2RDF(ImejiJena.userModel);
		imejiBean2RDF.create(newUser, user);
	}
	
	public void delete(User user) throws Exception
	{
		imejiBean2RDF = new ImejiBean2RDF(ImejiJena.userModel);
		imejiBean2RDF.delete(user, this.user);
	}
	
	public User retrieve(String email)
	{
		imejiRDF2Bean = new ImejiRDF2Bean(ImejiJena.userModel);
		return (User) imejiRDF2Bean.load(User.class, email);
	}
	
	/**
	 * @deprecated
	 * @return
	 */
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
	
	public void update(User user) throws Exception
	{
		imejiBean2RDF = new ImejiBean2RDF(ImejiJena.userModel);
		imejiBean2RDF.saveDeep(user, this.user);
		cleanGraph();
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
