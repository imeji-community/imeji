package de.mpg.imeji.rest.crud;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.User;

public class ItemCRUD implements CRUDInterface<Item> {

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
	public Item read(Item o, User u) {

		ItemController controller = new ItemController();

		try {
			return controller.retrieve(o.getId(), u);
		} catch (Exception e) {
			throw new RuntimeException("Error reading item: " + e.getMessage(),
					e);
		}

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

}
