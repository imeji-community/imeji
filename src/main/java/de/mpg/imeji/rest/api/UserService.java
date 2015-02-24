package de.mpg.imeji.rest.api;

import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.exceptions.NotSupportedMethodException;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.vo.User;

import java.net.URI;
import java.util.List;

import static de.mpg.imeji.logic.Imeji.adminUser;


public class UserService implements API<User>{

	@Override
	public User create(User o, User u) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User read(String id, User u) throws ImejiException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User update(User o, User u) throws ImejiException {
		// TODO Auto-generated method stub
		throw new NotSupportedMethodException();
	}

	@Override
	public boolean delete(String id, User u) throws ImejiException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public User release(String id, User u) throws ImejiException {
		// TODO Auto-generated method stub
		throw new NotSupportedMethodException();
	}

	@Override
	public User withdraw(String id, User u, String discardComment) throws ImejiException {
		// TODO Auto-generated method stub
		throw new NotSupportedMethodException();
	}

	@Override
	public void share(String id, String userId, List<String> roles, User u)
			throws ImejiException {
		// TODO Auto-generated method stub
		throw new NotSupportedMethodException();
		
		
	}

	@Override
	public void unshare(String id, String userId, List<String> roles, User u)
			throws ImejiException {
		// TODO Auto-generated method stub
		throw new NotSupportedMethodException();
		
		
	}

	@Override
	public List<String> search(String q, User u) throws ImejiException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public User read(URI uri) throws ImejiException {
        //TODO: admin cannot read itself???!!! workaround:
        return adminUser.getId().equals(uri) ? adminUser :
                new UserController(adminUser).retrieve(uri);
	}

	public User read(String email) throws ImejiException {
        return adminUser.getEmail().equals(email) ? adminUser :
                new UserController(adminUser).retrieve(email);
	}

}
