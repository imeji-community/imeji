package de.mpg.imeji.rest.api;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.controller.ProfileController;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.rest.process.TransferObjectFactory;
import de.mpg.imeji.rest.to.MetadataProfileTO;

import java.net.URI;
import java.util.List;



public class ProfileService implements API<MetadataProfileTO>{

    public static final String DEFAULT_METADATA_PROFILE_ID = "default";

    public MetadataProfile read(URI uri) throws ImejiException{
		ProfileController pcon = new ProfileController();
		return pcon.retrieve(uri, Imeji.adminUser);
	}


	@Override
	public MetadataProfileTO create(MetadataProfileTO o, User u) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MetadataProfileTO read(String id, User u) throws ImejiException {
		ProfileController pcontroller = new ProfileController();
		MetadataProfileTO to = new MetadataProfileTO();
		MetadataProfile vo = DEFAULT_METADATA_PROFILE_ID.equals(id) ? Imeji.defaultMetadataProfile :
                pcontroller.retrieve(ObjectHelper.getURI(MetadataProfile.class, id), u);
		TransferObjectFactory.transferMetadataProfile(vo, to);
		return to;
	}

	@Override
	public MetadataProfileTO update(MetadataProfileTO o, User u)
			throws ImejiException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean delete(String id, User u) throws ImejiException {
		ProfileController pcontroller = new ProfileController();
		pcontroller.delete(pcontroller.retrieve(id, u), u);
		return true;
	}

	@Override
	public MetadataProfileTO release(String id, User u) throws ImejiException {
		ProfileController pcontroller = new ProfileController();
		pcontroller.release(pcontroller.retrieve(id, u), u);
		
		return getMetadataProfileTO(pcontroller, id, u);
	}

	@Override
	public MetadataProfileTO withdraw(String id, User u, String discardComment) throws ImejiException {

			ProfileController controller = new ProfileController();
			MetadataProfile vo = controller.retrieve(
					ObjectHelper.getURI(MetadataProfile.class, id), u);
			vo.setDiscardComment(discardComment);
			controller.withdraw(vo, u);

			//Now Read the withdrawn collection and return it back
			return getMetadataProfileTO(controller, id, u);
		
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

	public List<MetadataProfileTO> readAll(User u, String q) throws ImejiException {
	        ProfileController cc = new ProfileController();
	        return Lists.transform(cc.search(u, q),
	                new Function<MetadataProfile, MetadataProfileTO>() {
	                    @Override
	                    public MetadataProfileTO apply(MetadataProfile vo) {
	                        MetadataProfileTO to = new MetadataProfileTO();
	                        TransferObjectFactory.transferMetadataProfile(vo, to);
	                        return to;
	                    }
	                }
	        );
	    }
	
	private MetadataProfileTO getMetadataProfileTO (ProfileController cc, String id, User u) throws ImejiException {
		MetadataProfileTO to = new MetadataProfileTO();
        TransferObjectFactory.transferMetadataProfile(getMetadataProfileVO(cc, id, u), to);
		return to;
	}
	
	private MetadataProfile getMetadataProfileVO (ProfileController cc, String id, User u) throws ImejiException {
		return cc.retrieve(ObjectHelper.getURI(MetadataProfile.class, id), u);
	}

}
