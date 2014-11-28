package de.mpg.imeji.rest.api;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import javax.ws.rs.NotSupportedException;
import de.mpg.imeji.logic.auth.exception.NotAllowedError;
import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.controller.ProfileController;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.util.ImejiFactory;
import de.mpg.j2j.exceptions.NotFoundException;

public class CollectionService implements API<CollectionImeji> {

	public CollectionService() {

	}

	@Override
	public CollectionImeji read(String id, User u) throws NotFoundException,
			NotAllowedError, Exception {
		CollectionController controller = new CollectionController();
		return controller.retrieve(ObjectHelper.getURI(CollectionImeji.class, id), u);
	}

	@Override
	public CollectionImeji create(CollectionImeji c, User u) throws Exception {
		CollectionController cc = new CollectionController();
		ProfileController pc = new ProfileController();
		//MetadataProfile handling
		//TODO: change the code after CollectionImeji update for profile placeholder
		String[] prof = c.getProfile().toString().split("___");
		MetadataProfile mp = null;
		String newId = null;
		if ("copy".equals(prof[1])) {
			if ("default".equals(prof[0])) {
				mp = pc.create(ImejiFactory.newProfile(), u);
				newId = mp.getId().toString();
			} else {
				try {
					//copy metadataprofile
					//TODO: workaround for URI retrieval: use only postfix of namespaced IDs
					//ProfileController adds namespace to the IDs!
					String[] arrId = prof[0].split("/");
					mp = pc.retrieve(arrId[arrId.length-1], u);
					mp = pc.create(mp.clone(), u);
					newId = mp.getId().toString();
				} catch (Exception e) {
					//TODO: exception handling
					e.printStackTrace();
					return null;
				}
			}
		} else {
			newId = prof[0];
		}
		c.setProfile(URI.create(newId));

		try {
			URI collectionURI = cc.create(c, mp, u);
			return cc.retrieve(collectionURI, u);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public CollectionImeji update(CollectionImeji o, User u) throws NotFoundException,
			NotAllowedError, NotSupportedException, Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean delete(CollectionImeji o, User u) throws NotFoundException,
			NotAllowedError, NotSupportedException, Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void release(CollectionImeji o, User u) throws NotFoundException,
			NotAllowedError, NotSupportedException, Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void withdraw(CollectionImeji o, User u) throws NotFoundException,
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
