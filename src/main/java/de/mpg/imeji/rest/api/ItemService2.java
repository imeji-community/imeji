package de.mpg.imeji.rest.api;

import java.net.URI;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.NotSupportedException;

import org.apache.commons.io.FilenameUtils;

import de.mpg.imeji.logic.auth.exception.NotAllowedError;
import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.storage.StorageController;
import de.mpg.imeji.logic.storage.UploadResult;
import de.mpg.imeji.logic.storage.util.StorageUtils;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.rest.process.ReverseTransferObjectFactory;
import de.mpg.imeji.rest.process.TransferObjectFactory;
import de.mpg.imeji.rest.to.ItemWithFileTO;
import de.mpg.imeji.rest.to.ItemTO;
import de.mpg.j2j.exceptions.NotFoundException;

public class ItemService2 implements API<ItemTO> {

	@Override
	public ItemTO create(ItemTO to, User u) {
		if (to instanceof ItemWithFileTO) {
			//get filename
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
			
			// transfer item into to
			TransferObjectFactory.transferItem(item, to);
			return to;
		}
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

	@Override
	public ItemTO read(String id, User u) throws NotFoundException,
			NotAllowedError, NotSupportedException, Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ItemTO update(ItemTO o, User u) throws NotFoundException,
			NotAllowedError, NotSupportedException, Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean delete(ItemTO o, User u) throws NotFoundException,
			NotAllowedError, NotSupportedException, Exception {
		// TODO Auto-generated method stub
		return false;
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

}
