package de.mpg.imeji.rest.api;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.NotSupportedException;

import org.apache.commons.io.FilenameUtils;

import de.mpg.imeji.logic.auth.exception.NotAllowedError;
import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.rest.process.ReverseTransferObjectFactory;
import de.mpg.imeji.rest.process.TransferObjectFactory;
import de.mpg.imeji.rest.to.ItemTO;
import de.mpg.imeji.rest.to.ItemWithFileTO;
import de.mpg.j2j.exceptions.NotFoundException;

public class ItemService implements API<ItemTO> {

	@Override
	public ItemTO create(ItemTO to, User u) {
		if (to instanceof ItemWithFileTO) {
			// get filename
			String filename = getFilename((ItemWithFileTO) to);

			// transfer TO into item
			Item item = new Item();
			ReverseTransferObjectFactory.transferItem(to, item);

			try {
				// read collection
				CollectionController cc = new CollectionController();
				CollectionImeji collection = cc.retrieve(item.getCollection(),
						u);

				// Create Item with File
				ItemController controller = new ItemController();
				controller.create(item, ((ItemWithFileTO) to).getFile(),
						filename, collection, u);

			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}

			// transfer item into ItemTO
			ItemTO itemTO = new ItemTO();
			TransferObjectFactory.transferItem(item, itemTO);
			return itemTO;
		}
		return null;
	}

	@Override
	public ItemTO read(String id, User u) throws NotFoundException,
			NotAllowedError, Exception {

		ItemController controller = new ItemController();
		ItemTO to = new ItemTO();
		Item item = controller.retrieve(ObjectHelper.getURI(Item.class, id), u);
		TransferObjectFactory.transferItem(item, to);
		return to;
	}

	@Override
	public ItemTO update(ItemTO o, User u) {
		ItemController controller = new ItemController();
		try {
			Item item = null;// TODO:
								// ReverseTransferObjectFactory.transferItem(item,
								// o);
			item = controller.update(item, u);
			TransferObjectFactory.transferItem(item, o);
			return o;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	@Override
	public boolean delete(ItemTO o, User u) {
		ItemController controller = new ItemController();
		List<Item> items = new ArrayList<Item>();
		Item item = null;// TODO:
							// ReverseTransferObjectFactory.transferItem(item,
							// o);
		items.add(item);
		try {
			controller.delete(items, u);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void release(ItemTO o, User u) throws NotFoundException,
			NotAllowedError, NotSupportedException, Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void withdraw(ItemTO o, User u) throws NotFoundException,
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

	/**
	 * Find the correct filename if there
	 * 
	 * @param to
	 * @return
	 */
	private String getFilename(ItemWithFileTO to) {
		String filename = to.getFilename();
		if (filename == null)
			filename = FilenameUtils.getName(to.getFetchUrl());
		if (filename == null)
			filename = FilenameUtils.getName(to.getReferenceUrl());
		return filename;
	}

}
