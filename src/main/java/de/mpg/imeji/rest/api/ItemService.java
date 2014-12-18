package de.mpg.imeji.rest.api;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.NotAllowedException;
import javax.ws.rs.NotSupportedException;

import com.google.common.base.Strings;
import org.apache.commons.io.FilenameUtils;

import de.mpg.imeji.logic.auth.exception.NotAllowedError;
import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.util.ImejiFactory;
import de.mpg.imeji.rest.process.ReverseTransferObjectFactory;
import de.mpg.imeji.rest.process.TransferObjectFactory;
import de.mpg.imeji.rest.to.ItemTO;
import de.mpg.imeji.rest.to.ItemWithFileTO;
import de.mpg.j2j.exceptions.NotFoundException;

import static com.google.common.base.Strings.*;

public class ItemService implements API<ItemTO> {

	private static ItemController controller = new ItemController();

	@Override
	public ItemTO create(ItemTO to, User u) throws Exception {
		if (to instanceof ItemWithFileTO) {
			// get newFilename
			String filename = getFilename((ItemWithFileTO) to);

			// transfer TO into item
			Item item = new Item();
			ReverseTransferObjectFactory.transferItem(to, item);

			// read collection
			CollectionController cc = new CollectionController();
			CollectionImeji collection = cc.retrieve(item.getCollection(), u);

			// Create Item with File
			ItemController ic = new ItemController();
			if (((ItemWithFileTO) to).getFile() != null) {
				// If TO has attribute File, then upload it
				ic.createWithFile(item, ((ItemWithFileTO) to).getFile(),
						filename, collection, u);
			} else if (getExternalFileUrl((ItemWithFileTO) to) != null) {
				// If no file, but either a fetchUrl or a referenceUrl
				ic.createWithExternalFile(item, collection,
						getExternalFileUrl((ItemWithFileTO) to), filename,
						downloadFile((ItemWithFileTO) to), u);
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

		ItemTO to = new ItemTO();
		Item item = controller.retrieve(ObjectHelper.getURI(Item.class, id), u);
		TransferObjectFactory.transferItem(item, to);
		return to;
	}

	@Override
	public ItemTO update(ItemTO to, User u) throws Exception {
		Item item = controller.retrieve(ObjectHelper.getURI(Item.class, to.getId()), u);
		ReverseTransferObjectFactory.transferItem(to, item);
		item = controller.update(item, u);
		TransferObjectFactory.transferItem(item, to);
		return to;
	}
	
	public ItemTO update(ItemWithFileTO to, User u) throws Exception {
		Item item = controller.retrieve(ObjectHelper.getURI(Item.class, to.getId()), u);
		ReverseTransferObjectFactory.transferItem(to, item);
		item = controller.updateFile(item, to.getFile(), u);
		TransferObjectFactory.transferItem(item, to);
		return to;
	}

	@Override
	public boolean delete(String id, User u) throws NotFoundException,
			NotAllowedError {
		List<Item> items = new ArrayList<Item>();
		Item item = new Item();
		try {
			item = controller.retrieve(ObjectHelper.getURI(Item.class, id), u);
		} catch (Exception e1) {
			throw new NotFoundException(e1.getMessage());
		}
		items.add(item);
		try {
			controller.delete(items, u);
			return true;
		} catch (Exception e) {
			throw new NotAllowedError(e.getMessage());
		}
	}

	@Override
	public void release(String id, User u) throws NotFoundException,
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
	 **/
	private String getFilename(ItemWithFileTO to) {
		String filename = to.getFilename();
		if (filename == null)
			filename = FilenameUtils.getName(to.getFile().getName());
		if (filename == null)
			filename = FilenameUtils.getName(to.getFetchUrl());
		if (filename == null)
			filename = FilenameUtils.getName(to.getReferenceUrl());
		return filename;
	}

	/**
	 * Return the external Url
	 * 
	 * @param to
	 * @return
	 */
	private String getExternalFileUrl(ItemWithFileTO to) {
		if (!isNullOrEmpty(to.getFetchUrl()))
			return to.getFetchUrl();
		else if (!isNullOrEmpty(to.getReferenceUrl()))
			return to.getReferenceUrl();
		return null;
	}

	/**
	 * True if the file must be download in imeji (i.e fetchurl is defined)
	 * 
	 * @param to
	 * @return
	 */
	private boolean downloadFile(ItemWithFileTO to) {
		return !isNullOrEmpty(to.getFetchUrl());
	}

}
