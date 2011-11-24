package de.mpg.jena.controller;

import java.net.URI;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collection;

import de.mpg.jena.ImejiBean2RDF;
import de.mpg.jena.ImejiJena;
import de.mpg.jena.ImejiRDF2Bean;
import de.mpg.jena.util.ObjectHelper;
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
		imejiBean2RDF.create(imejiBean2RDF.toList(newUser), user);
	}

	public void delete(User user) throws Exception
	{
		imejiBean2RDF = new ImejiBean2RDF(ImejiJena.userModel);
		imejiBean2RDF.delete(imejiBean2RDF.toList(user), this.user);
	}

	public User retrieve(String email)
	{
		imejiRDF2Bean = new ImejiRDF2Bean(ImejiJena.userModel);
		return (User) imejiRDF2Bean.load(User.class, email);
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
		imejiBean2RDF.saveDeep(imejiBean2RDF.toList(user), this.user);
	}

	/**
	 *
	 * @return
	 */
	public Collection<User> retrieveAll()
	{
		imejiRDF2Bean = new ImejiRDF2Bean(ImejiJena.userModel);
		Collection<User> users = new ArrayList<User>();

		for (User user : (Collection<User>) imejiRDF2Bean.load(User.class))
		{
			try 
			{
				users.add((User) ObjectHelper.castAllHashSetToList(user));
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}

		return users;
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
