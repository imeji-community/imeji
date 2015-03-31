package de.mpg.imeji.rest.api;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import de.mpg.imeji.exceptions.BadRequestException;
import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.rest.process.ReverseTransferObjectFactory;
import de.mpg.imeji.rest.process.TransferObjectFactory;
import de.mpg.imeji.rest.to.ItemTO;
import de.mpg.imeji.rest.to.ItemWithFileTO;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ObjectUtils;

import java.io.IOException;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;
import static de.mpg.imeji.rest.process.ReverseTransferObjectFactory.TRANSFER_MODE.CREATE;
import static de.mpg.imeji.rest.process.ReverseTransferObjectFactory.TRANSFER_MODE.UPDATE;

public class ItemService implements API<ItemTO> {

	private static ItemController controller = new ItemController();

	@Override
	public ItemTO create(ItemTO to, User u) throws ImejiException {
		if (to instanceof ItemWithFileTO) {
			// get newFilename
			String filename = getFilename((ItemWithFileTO) to);

			// transfer TO into item
			Item item = new Item();
			
			
			ReverseTransferObjectFactory.transferItem(to, item, u, CREATE);
			
			item = controller.create(item, ((ItemWithFileTO) to).getFile(), filename, u, ((ItemWithFileTO) to).getFetchUrl(), ((ItemWithFileTO) to).getReferenceUrl());
			// transfer item into ItemTO
			ItemTO itemTO = new ItemTO();
			TransferObjectFactory.transferItem(item, itemTO);
			return itemTO;
		}
		else
		{
			throw new BadRequestException("A file must be uploaded, referenced or fetched from external location.");
		}
}

	@Override
	public ItemTO read(String id, User u) throws ImejiException {
		ItemTO to = new ItemTO();
		Item item = controller.retrieve(ObjectHelper.getURI(Item.class, id), u);
		TransferObjectFactory.transferItem(item, to);
		return to;
	}

	public List<ItemTO> readItems(User u, String q) throws ImejiException, IOException {
		return Lists.transform(new ItemController().retrieve(u, q),
				new Function<Item, ItemTO>() {
					@Override
					public ItemTO apply(Item vo) {
						ItemTO to = new ItemTO();
						TransferObjectFactory.transferItem(vo, to);
						return to;
					}
				}
		);
	}

	@Override
	public ItemTO update(ItemTO to, User u) throws ImejiException {
		Item item = controller.retrieve(
				ObjectHelper.getURI(Item.class, to.getId()), u);
		ReverseTransferObjectFactory.transferItem(to, item, u, UPDATE);
		if (to instanceof ItemWithFileTO) {
			ItemWithFileTO tof = (ItemWithFileTO) to;
			String url = getExternalFileUrl(tof);
			if(tof.getFile() != null){
				item = controller.updateFile(item, tof.getFile(), u);
			}
			else if (url != null) {
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
	public boolean delete(String id, User u) throws ImejiException {
			controller.delete(id, u);
			return true;
	}

	@Override
	public ItemTO release(String id, User u) throws ImejiException {
		// TODO Auto-generated method stub
		return null;

	}

	@Override
	public ItemTO withdraw(String id, User u, String discardComment) throws ImejiException {
		// TODO Auto-generated method stub
		return null;

	}

	@Override
	public void share(String id, String userId, List<String> roles, User u)
			throws ImejiException {
		// TODO Auto-generated method stub

	}

	@Override
	public void unshare(String id, String userId, List<String> roles, User u)
			throws  ImejiException {
		// TODO Auto-generated method stub

	}

	@Override
	public List<String> search(String q, User u) throws ImejiException {
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
				(to.getFile()!=null)?FilenameUtils.getName(to.getFile().getName()):"",
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
