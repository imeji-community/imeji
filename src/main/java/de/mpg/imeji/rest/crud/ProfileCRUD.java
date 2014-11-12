package de.mpg.imeji.rest.crud;

import java.net.URI;

import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.auth.exception.NotAllowedError;
import de.mpg.imeji.logic.controller.ProfileController;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.User;
import de.mpg.j2j.exceptions.NotFoundException;

public class ProfileCRUD implements CRUDInterface<MetadataProfile>{

	@Override
	public MetadataProfile create(MetadataProfile o, User u) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MetadataProfile read(MetadataProfile o, User u) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MetadataProfile update(MetadataProfile o, User u) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean delete(MetadataProfile o, User u) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public MetadataProfile read(String id, User u) throws NotFoundException,
			NotAllowedError, Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	public MetadataProfile read(URI uri) throws Exception{
		ProfileController pcon = new ProfileController();
		return pcon.retrieve(uri, Imeji.adminUser);
	}

}
