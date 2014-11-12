package de.mpg.imeji.rest.api;

import java.net.URI;
import java.util.List;

import javax.ws.rs.NotSupportedException;

import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.auth.exception.NotAllowedError;
import de.mpg.imeji.logic.controller.ProfileController;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.User;
import de.mpg.j2j.exceptions.NotFoundException;



public class ProfileService implements API<MetadataProfile>{
	
	public MetadataProfile read(URI uri) throws Exception{
		ProfileController pcon = new ProfileController();
		return pcon.retrieve(uri, Imeji.adminUser);
	}


	@Override
	public MetadataProfile create(MetadataProfile o, User u) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MetadataProfile read(String id, User u) throws NotFoundException,
			NotAllowedError, NotSupportedException, Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MetadataProfile update(MetadataProfile o, User u)
			throws NotFoundException, NotAllowedError, NotSupportedException,
			Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean delete(MetadataProfile o, User u) throws NotFoundException,
			NotAllowedError, NotSupportedException, Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void release(MetadataProfile o, User u) throws NotFoundException,
			NotAllowedError, NotSupportedException, Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void withdraw(MetadataProfile o, User u) throws NotFoundException,
			NotAllowedError, NotSupportedException, Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void share(String id, String userId, List<String> roles, User u)
			throws NotFoundException, NotAllowedError, NotSupportedException,
			Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unshare(String id, String userId, List<String> roles, User u)
			throws NotFoundException, NotAllowedError, NotSupportedException,
			Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<String> search(String q, User u) throws NotSupportedException,
			Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
