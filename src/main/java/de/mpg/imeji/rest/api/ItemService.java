package de.mpg.imeji.rest.api;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.NotSupportedException;

import de.mpg.imeji.logic.auth.exception.NotAllowedError;
import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.User;
import de.mpg.j2j.exceptions.NotFoundException;

public class ItemService implements API<Item> {

	public ItemService() {

	}

	@Override
	public Item create(Item o, User u) {

		ItemController controller = new ItemController();
		URI coll = o.getCollection();
		try {
			return controller.create(o, coll, u);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	@Override
	public Item read(String id, User u) throws NotFoundException,
			NotAllowedError, Exception {

		ItemController controller = new ItemController();
		return controller.retrieve(ObjectHelper.getURI(Item.class, id), u);
	}

	@Override
	public Item update(Item o, User u) {
		ItemController controller = new ItemController();
		try {
			return controller.update(o, u);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	@Override
	public boolean delete(Item o, User u) {
		ItemController controller = new ItemController();
		List<Item> items = new ArrayList<Item>();
		items.add(o);
		try {
			controller.delete(items, u);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void release(Item o, User u) throws NotFoundException,
			NotAllowedError, NotSupportedException, Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void withdraw(Item o, User u) throws NotFoundException,
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
