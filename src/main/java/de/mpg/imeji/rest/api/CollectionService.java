package de.mpg.imeji.rest.api;

import java.util.List;
import javax.ws.rs.NotSupportedException;
import de.mpg.imeji.logic.auth.exception.NotAllowedError;
import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.User;
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
	public CollectionImeji create(CollectionImeji o, User u) {
		// TODO Auto-generated method stub
		return null;
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
