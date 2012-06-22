/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.imeji.logic.security.operations;

import de.mpg.imeji.logic.security.Authorization;
import de.mpg.imeji.logic.security.Operations;
import de.mpg.imeji.logic.vo.Container;
import de.mpg.imeji.logic.vo.Properties.Status;
import de.mpg.imeji.logic.vo.User;

public class OperationsContainer implements Operations
{
	private Authorization auth = new Authorization();
	
	/**
	 * Create Collection
	 * <br/>	Allowed for:
	 * <br/>	-	everybody
	 */
	public boolean create(User user, Object object) 
	{
		return true;
	}

	/**
	 * View Collection
	 * <br/>	Allowed for:
	 * <br/>	- 	everybody collection is Public
	 * <br/>	- 	Collection Viewer
	 * <br/>	- 	Picture Editor
	 * <br/>	- 	Collection Editor
	 * <br/> 	-	Collection Administrator
	 */
	public boolean read(User user, Object object) 
	{
		return (Status.RELEASED.equals(((Container)object).getProperties().getStatus())
				|| Status.WITHDRAWN.equals(((Container)object).getProperties().getStatus())
				|| auth.isViewerFor(user, (Container) object)
				|| auth.isPictureEditor(user, (Container) object)
				|| auth.isContainerEditor(user, (Container) object)
				|| auth.isContainerAdmin(user, (Container) object));
	}

	/**
	 * Update Collection (Edit collection MD, release/withdrawMD)
	 * <br/>	Allowed for:
	 * <br/>	- 	Collection Editor
	 * <br/> 	-	Collection Administrator
	 */
	public boolean update(User user, Object object) 
	{
		return (user != null
				&& (auth.isContainerEditor(user,(Container) object)
						|| auth.isContainerAdmin(user, (Container) object)));
	}

	/**
	 * Update Collection
	 * <br/>	Allowed for:
	 * <br/> 	-	Collection Administrator (if collection isn't released)
	 */
	public boolean delete(User user, Object object) 
	{
		return (!Status.RELEASED.equals(((Container)object).getProperties().getStatus())
				&& !Status.WITHDRAWN.equals(((Container)object).getProperties().getStatus())
				&& (auth.isContainerEditor(user,(Container) object)
					|| auth.isContainerAdmin(user, (Container) object)));
	}
}
