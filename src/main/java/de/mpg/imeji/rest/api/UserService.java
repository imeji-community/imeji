package de.mpg.imeji.rest.api;

import java.net.URI;
import java.util.List;

import javax.ws.rs.NotSupportedException;

import de.mpg.imeji.exceptions.NotAllowedError;
import de.mpg.imeji.exceptions.NotFoundException;
import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.vo.User;



public class UserService implements API<User>{

	@Override
	public User create(User o, User u) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User read(String id, User u) throws NotFoundException,
			NotAllowedError, NotSupportedException, Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User update(User o, User u) throws NotFoundException,
			NotAllowedError, NotSupportedException, Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean delete(String id, User u) throws NotFoundException,
			NotAllowedError, NotSupportedException, Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public User release(String id, User u) throws NotFoundException,
			NotAllowedError, NotSupportedException, Exception {
		// TODO Auto-generated method stub
		throw new NotSupportedException();
		
		
	}

	@Override
	public User withdraw(String id, User u, String discardComment) throws NotFoundException,
			NotAllowedError, NotSupportedException, Exception {
		// TODO Auto-generated method stub
		throw new NotSupportedException();
		
	}

	@Override
	public void share(String id, String userId, List<String> roles, User u)
			throws NotFoundException, NotAllowedError, NotSupportedException,
			Exception {
		// TODO Auto-generated method stub
		throw new NotSupportedException();
		
		
	}

	@Override
	public void unshare(String id, String userId, List<String> roles, User u)
			throws NotFoundException, NotAllowedError, NotSupportedException,
			Exception {
		// TODO Auto-generated method stub
		throw new NotSupportedException();
		
		
	}

	@Override
	public List<String> search(String q, User u) throws NotSupportedException,
			Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	public User read(URI uri) throws Exception{
		UserController con = new UserController(Imeji.adminUser);
		return con.retrieve(uri);
	}

}
