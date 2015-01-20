package de.mpg.imeji.rest.api;

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
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ObjectUtils;

import javax.ws.rs.NotSupportedException;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;
import static de.mpg.imeji.rest.process.ReverseTransferObjectFactory.TRANSFER_MODE.CREATE;
import static de.mpg.imeji.rest.process.ReverseTransferObjectFactory.TRANSFER_MODE.UPDATE;

public class ItemService implements API<ItemTO> {

	private static ItemController controller = new ItemController();

	@Override
	public ItemTO create(ItemTO to, User u) throws Exception {
		if (to instanceof ItemWithFileTO) {
			// get newFilename
			String filename = getFilename((ItemWithFileTO) to);

			// transfer TO into item
			Item item = new Item();
			ReverseTransferObjectFactory.transferItem(to, item, CREATE);

			// read collection
			CollectionController cc = new CollectionController();
			CollectionImeji collection = cc.retrieve(item.getCollection(), u);
			// Create Item with File
			if (((ItemWithFileTO) to).getFile() != null) {
				// If TO has attribute File, then upload it
				controller.createWithFile(item,
						((ItemWithFileTO) to).getFile(), filename, collection,
						u);
			} else if (getExternalFileUrl((ItemWithFileTO) to) != null) {
				// If no file, but either a fetchUrl or a referenceUrl
				controller.createWithExternalFile(item, collection,
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
		Item item = controller.retrieve(
				ObjectHelper.getURI(Item.class, to.getId()), u);
		ReverseTransferObjectFactory.transferItem(to, item, UPDATE);
		if (to instanceof ItemWithFileTO) {
			ItemWithFileTO tof = (ItemWithFileTO) to;
			String url = getExternalFileUrl(tof);
//			if (url != null)
//				item = controller.updateWithExternalFile(item,
//						getExternalFileUrl(tof), to.getFilename(),
//						downloadFile(tof), u);
//			else
//				item = controller.updateFile(item, tof.getFile(), u);
//			tof.setFile(null);
			if(tof.getFile() !=null){
				item = controller.updateFile(item, tof.getFile(), u);
			}
			else{
				if (url != null)
					item = controller.updateWithExternalFile(item,
							getExternalFileUrl(tof), to.getFilename(),
							downloadFile(tof), u);
			}
		} else {
			item = controller.update(item, u);
		}
		to = new ItemTO();
		TransferObjectFactory.transferItem(item,to);
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
		return ObjectUtils.firstNonNull(to.getFilename(),
				FilenameUtils.getName(to.getFile().getName()),
				FilenameUtils.getName(to.getFetchUrl()),
				FilenameUtils.getName(to.getReferenceUrl()));
	}

	/**
	 * Return the external Url
	 * 
	 * @param to
	 * @return
	 */
	private String getExternalFileUrl(ItemWithFileTO to) {
		return firstNonNullOrEmtpy(to.getFetchUrl(), to.getReferenceUrl());
	}

	private String firstNonNullOrEmtpy(String... strs) {
		if (strs == null)
			return null;
		for (String str : strs)
			if (str != null && !"".equals(str.trim()))
				return str;
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
