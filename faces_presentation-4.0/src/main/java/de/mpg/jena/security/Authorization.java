package de.mpg.jena.security;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import de.mpg.jena.vo.Container;
import de.mpg.jena.vo.Grant;
import de.mpg.jena.vo.Image;
import de.mpg.jena.vo.User;
import de.mpg.jena.vo.Grant.GrantType;

/**
 * 			Imeji Authorization. Based on Imeji Grants:
 * <br/>
 * 			SYSADMIN, CONTAINER_ADMIN, CONTAINER_EDITOR, IMAGE_UPLOADER, IMAGE_EDITOR, PRIVILEGED_VIEWER
 * <br/> 	See: http://colab.mpdl.mpg.de/mediawiki/Imeji_User_Management
 * @author saquet
 *
 */
public class Authorization
{	
	/**
	 * Generic Authorization
	 * @param gt
	 * @param user
	 * @param uri
	 * @return
	 */
	public boolean is(GrantType gt, User user, URI uri)
	{
		for (Grant g :  getGrantsForURI(user, uri))
		{
			if (gt.equals(g.getGrantType())) return true;
		}
		return false;
	}
	
	
	public boolean isSysAdmin(User user)
	{
		if (user == null) return false;
		for (Grant g : user.getGrants())
		{
			if (GrantType.SYSADMIN.equals(g.getGrantType())) return true;
		}
		return false;
	}
	
	public boolean isContainerAdmin(User user, Container container) 
	{
		for (Grant g :  getGrantsForObject(user, container))
		{
			if (GrantType.CONTAINER_ADMIN.equals(g.getGrantType())) return true;
		}

		return isSysAdmin(user);
	}

	public boolean isContainerAdmin(User user, Image image) 
	{
		for (Grant g :  getGrantsForURI(user, image.getCollection()))
		{
			if (GrantType.CONTAINER_ADMIN.equals(g.getGrantType())) return true;
		}
		return isSysAdmin(user);
	}

	
	public boolean isContainerEditor(User user, Container container)
	{
		for (Grant g :  getGrantsForObject(user, container))
		{
			if (GrantType.CONTAINER_EDITOR.equals(g.getGrantType())) return true;
		}
		return isSysAdmin(user);
	}
	
	public boolean isContainerEditor(User user, Image image)
	{
		for (Grant g :  getGrantsForURI(user, image.getCollection()))
		{
			if (GrantType.CONTAINER_EDITOR.equals(g.getGrantType())) return true;
		}
		return isSysAdmin(user);
	}
	
	public boolean isPictureEditor(User user, Container container)
	{
		for (Grant g :  getGrantsForObject(user, container))
		{
			if (GrantType.IMAGE_EDITOR.equals(g.getGrantType())) return true;
		}
		return isSysAdmin(user);
	}
	
	public boolean isPictureEditor(User user, Image image)
	{
		for (Grant g :  getGrantsForURI(user, image.getCollection()))
		{
			if (GrantType.IMAGE_EDITOR.equals(g.getGrantType())) return true;
		}
		return isSysAdmin(user);
	}

	public boolean isViewerFor(User user, Container container)
	{
		for (Grant g :  getGrantsForObject(user, container))
		{
			if (GrantType.PRIVILEGED_VIEWER.equals(g.getGrantType())) return true;
		}
		return isSysAdmin(user);
	}
	
	public boolean isViewerFor(User user, Image image)
	{
		for (Grant g : getGrantsForURI(user, image.getCollection()))
		{
			if (GrantType.PRIVILEGED_VIEWER.equals(g.getGrantType())) return true;
		}
		return isSysAdmin(user);
	}

	public boolean isUploaderFor(User user, Container container) 
	{
		for (Grant g :  getGrantsForObject(user, container))
		{
			if (GrantType.IMAGE_UPLOADER.equals(g.getGrantType())) return true;
		}
		return isSysAdmin(user);
	}
	
	private List<Grant> getGrantsForObject(User user, Container container)
	{
		if (user == null || container == null) return new ArrayList<Grant>();
		return getGrantsForURI(user, container.getId());
	}
	
	private List<Grant> getGrantsForURI(User user, URI uri)
	{
		List<Grant> grants = new ArrayList<Grant>();
		
		if (user == null) return grants;
		
		for (Grant g : user.getGrants())
		{
			if(g.getGrantType().equals(GrantType.SYSADMIN) || g.getGrantFor().equals(uri))
			{
				grants.add(g);
			}
		}
		return grants;
	}
}
