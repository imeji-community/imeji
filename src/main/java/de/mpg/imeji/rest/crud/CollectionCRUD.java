package de.mpg.imeji.rest.crud;

import de.mpg.imeji.logic.auth.exception.NotAllowedError;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.User;
import de.mpg.j2j.exceptions.NotFoundException;

public class CollectionCRUD implements CRUDInterface<CollectionImeji>{

	@Override
	public CollectionImeji create(CollectionImeji o, User u) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CollectionImeji read(CollectionImeji o, User u) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CollectionImeji update(CollectionImeji o, User u) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean delete(CollectionImeji o, User u) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public CollectionImeji read(String id, User u) throws NotFoundException,
			NotAllowedError, Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
