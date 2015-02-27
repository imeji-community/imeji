package de.mpg.imeji.rest.api;

import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.exceptions.NotFoundException;
import de.mpg.imeji.exceptions.UnprocessableError;
import de.mpg.imeji.logic.controller.AlbumController;
import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.controller.ProfileController;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.Album;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.rest.process.CommonUtils;
import de.mpg.imeji.rest.process.TransferObjectFactory;
import de.mpg.imeji.rest.to.AlbumTO;
import de.mpg.imeji.rest.to.CollectionTO;
import de.mpg.imeji.rest.to.CollectionProfileTO.METHOD;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;
import static de.mpg.imeji.rest.process.ReverseTransferObjectFactory.TRANSFER_MODE.CREATE;
import static de.mpg.imeji.rest.process.ReverseTransferObjectFactory.TRANSFER_MODE.UPDATE;
import static de.mpg.imeji.rest.process.ReverseTransferObjectFactory.transferAlbum;
import static de.mpg.imeji.rest.process.ReverseTransferObjectFactory.transferCollection;

public class AlbumService implements API<AlbumTO>{

	private static final Logger LOGGER = LoggerFactory.getLogger(AlbumService.class);
	
	private AlbumTO getAlbumTO (AlbumController controller, String id, User u) throws ImejiException {
		AlbumTO to = new AlbumTO();
		Album vo = controller.retrieve(ObjectHelper.getURI(Album.class, id), u);
		TransferObjectFactory.transferAlbum(vo, to);
		return to;
	}
	
	@Override
	public AlbumTO read(String id, User u) throws ImejiException {
		AlbumController controller = new AlbumController();
		return getAlbumTO(controller, id, u);
	}
	
	@Override
	public AlbumTO create(AlbumTO o, User u) throws ImejiException {
		return createAskValidate(o, u, true);
//		return null;
	}

	public AlbumTO createNoValidate(AlbumTO to, User u) throws ImejiException {
		return createAskValidate(to, u, false);
	}
	
	private AlbumTO createAskValidate(AlbumTO to, User u, boolean validate ) throws ImejiException{
		AlbumController ac = new AlbumController();
		Album vo = new Album();
		transferAlbum(to, vo, CREATE);
		URI albumURI;
		albumURI = ac.create(vo, u);	
		return read(CommonUtils.extractIDFromURI(albumURI), u);
	}

	@Override
	public AlbumTO update(AlbumTO to, User u) throws ImejiException {
        AlbumController ac = new AlbumController();

        Album vo = ac.retrieve(ObjectHelper.getURI(Album.class, to.getId()), u);
        if(vo == null)
        	throw new UnprocessableError("Album not found");

        transferAlbum(to, vo, UPDATE);
        AlbumTO newTO = new AlbumTO();
        TransferObjectFactory.transferAlbum(ac.update(vo, u), newTO);
        return newTO;
	}

	@Override
	public boolean delete(String id, User u) throws ImejiException {
		AlbumController controller = new AlbumController();
		Album vo = controller.retrieve(ObjectHelper.getURI(Album.class, id), u);
		controller.delete(vo, u);
		return true;
	}

	@Override
	public AlbumTO release(String id, User u) throws ImejiException {
		AlbumController controller = new AlbumController();
		Album vo = controller.retrieve(ObjectHelper.getURI(Album.class, id), u);
		controller.release(vo, u);

		//Now Read the album and return it back
		return getAlbumTO(controller, id, u);
	}

	@Override
	public AlbumTO withdraw(String id, User u, String discardComment)
			throws ImejiException {
		AlbumController controller = new AlbumController();
		Album vo = controller.retrieve(ObjectHelper.getURI(Album.class, id), u);
		vo.setDiscardComment(discardComment);
		controller.withdraw(vo, u);

		//Now Read the withdrawn album and return it back
		return getAlbumTO(controller, id, u);
	}

	@Override
	public void share(String id, String userId, List<String> roles, User u)
			throws ImejiException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unshare(String id, String userId, List<String> roles, User u)
			throws ImejiException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<String> search(String q, User u) throws ImejiException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public List<String> addItem(String id, User u, List<String> itemIds) throws ImejiException {
		AlbumController controller = new AlbumController();
		Album vo = controller.retrieve(ObjectHelper.getURI(Album.class, id), u);
		List<String> itemUris = new ArrayList<>();
		
		//Convert Ids to Uris
		for(String itemId : itemIds){
			itemUris.add(ObjectHelper.getURI(Item.class, itemId).toASCIIString());
		}
		List<String> ids = new ArrayList<String>();
		for(URI itemURI : controller.addToAlbum(vo, itemUris, u))
			ids.add(CommonUtils.extractIDFromURI(itemURI));
		return ids;
	}

}
