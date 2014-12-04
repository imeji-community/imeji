package de.mpg.imeji.rest.api;

import java.net.URI;
import java.util.List;

import javax.ws.rs.NotSupportedException;

import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.auth.exception.NotAllowedError;
import de.mpg.imeji.logic.controller.ProfileController;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.rest.process.TransferObjectFactory;
import de.mpg.imeji.rest.to.MetadataProfileTO;
import de.mpg.j2j.exceptions.NotFoundException;



public class ProfileService implements API<MetadataProfileTO>{
	
	public MetadataProfile read(URI uri) throws Exception{
		ProfileController pcon = new ProfileController();
		return pcon.retrieve(uri, Imeji.adminUser);
	}


	@Override
	public MetadataProfileTO create(MetadataProfileTO o, User u) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MetadataProfileTO read(String id, User u) throws NotFoundException,
			NotAllowedError, NotSupportedException, Exception {
		ProfileController pcontroller = new ProfileController();
		MetadataProfileTO to = new MetadataProfileTO();
		MetadataProfile vo = pcontroller.retrieve(ObjectHelper.getURI(MetadataProfile.class, id), u);
		TransferObjectFactory.transferMetadataProfile(vo, to);
		return to;
	}

	@Override
	public MetadataProfileTO update(MetadataProfileTO o, User u)
			throws NotFoundException, NotAllowedError, NotSupportedException,
			Exception {
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
	public void release(MetadataProfileTO o, User u) throws NotFoundException,
			NotAllowedError, NotSupportedException, Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void withdraw(MetadataProfileTO o, User u) throws NotFoundException,
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
