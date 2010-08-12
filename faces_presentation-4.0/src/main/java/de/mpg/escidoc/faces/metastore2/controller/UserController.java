package de.mpg.escidoc.faces.metastore2.controller;

import de.mpg.escidoc.faces.metastore2.vo.User;

public class UserController extends ImejiController{
	
	
	public void create (User user)
	{
		base.begin();
		bean2RDF.saveDeep(user);
		base.commit();
		base.close();
	}

	public User retrieve(String email, User user)
	{
		return rdf2Bean.load(User.class, email);
	}

	
	

}
