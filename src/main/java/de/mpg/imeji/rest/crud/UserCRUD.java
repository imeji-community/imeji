package de.mpg.imeji.rest.crud;

import java.net.URI;

import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.auth.exception.NotAllowedError;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.vo.User;
import de.mpg.j2j.exceptions.NotFoundException;

public class UserCRUD implements CRUDInterface<User>{

	@Override
	public User create(User o, User u) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User read(User o, User u) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User update(User o, User u) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean delete(User o, User u) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public User read(String id, User u) throws NotFoundException, NotAllowedError,
			Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	public User read(URI uri) throws Exception{
		UserController con = new UserController(Imeji.adminUser);
		return con.retrieve(uri);
	}

}
