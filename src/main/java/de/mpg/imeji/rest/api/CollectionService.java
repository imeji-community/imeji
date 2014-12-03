package de.mpg.imeji.rest.api;

import java.net.URI;
import java.util.List;

import javax.ws.rs.NotSupportedException;

import de.mpg.imeji.logic.auth.exception.NotAllowedError;
import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.controller.ProfileController;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.util.ImejiFactory;
import de.mpg.imeji.rest.process.CommonUtils;
import de.mpg.imeji.rest.process.ReverseTransferObjectFactory;
import de.mpg.imeji.rest.process.TransferObjectFactory;
import de.mpg.imeji.rest.to.CollectionTO;
import de.mpg.j2j.exceptions.NotFoundException;

public class CollectionService implements API<CollectionTO> {

	public CollectionService() {

	}

	@Override
	public CollectionTO read(String id, User u) throws NotFoundException, NotAllowedError, Exception {
		CollectionController controller = new CollectionController();
		CollectionTO to = new CollectionTO();
		CollectionImeji vo = controller.retrieve(ObjectHelper.getURI(CollectionImeji.class, id), u);
		TransferObjectFactory.transferCollection(vo, to);
		return to;
	}

	@Override
	public CollectionTO create(CollectionTO to, User u) throws Exception {
		CollectionController cc = new CollectionController();
		ProfileController pc = new ProfileController();

		MetadataProfile mp = null;
		String profileId = to.getProfile().getProfileId();
		String method = to.getProfile().getMethod();
		String newId = null;		
		if("".equals(profileId) || profileId == null)
			mp = pc.create(ImejiFactory.newProfile(), u);
			
		else if("reference".equalsIgnoreCase(method))
			mp = pc.retrieve(profileId, u);

		else if("copy".equalsIgnoreCase(method) && "" != profileId && profileId != null){
				mp = pc.retrieve(profileId, u);
				mp = pc.create(mp.clone(), u);
				pc.update(mp, u);
		}
		CollectionImeji vo = new CollectionImeji();
		ReverseTransferObjectFactory.transferCollection(to, vo);
//		vo.setProfile(URI.create(newId));
		try {
			URI collectionURI = cc.create(vo, mp, u);
			return read(CommonUtils.extractIDFromURI(collectionURI), u);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public CollectionTO update(CollectionTO o, User u) throws NotFoundException,
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
	public void release(CollectionTO o, User u) throws NotFoundException,
			NotAllowedError, NotSupportedException, Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void withdraw(CollectionTO o, User u) throws NotFoundException,
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
