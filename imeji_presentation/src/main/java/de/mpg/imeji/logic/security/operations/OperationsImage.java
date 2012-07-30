/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.imeji.logic.security.operations;

import de.mpg.imeji.logic.security.Authorization;
import de.mpg.imeji.logic.security.Operations;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.logic.vo.Item.Visibility;
import de.mpg.imeji.logic.vo.Properties.Status;

public class OperationsImage implements Operations
{	
	private Authorization auth = new Authorization();
	
	/**
	 * Create images (i.e. upload images)
	 * <br/>	Allowed for :
	 * <br/>	- 	Picture Editor
	 * <br/>	- 	Collection Editor
	 * <br/> 	-	Collection Administrator
	 * 	
	 */
	public boolean create(User user, Object object) 
	{
		return (auth.isPictureEditor(user, (Item) object)
				|| auth.isContainerEditor(user, ((Item)object))
				|| auth.isContainerAdmin(user, ((Item)object)));
	}

	/**
	 * Read image (i.e View Image)
	 * <br/>	Allowed for :
	 * <br/>	- 	everybody if image is public
	 * <br/>	- 	Collection Viewer
	 * <br/>	- 	Picture Editor
	 * <br/>	- 	Collection Editor
	 * <br/> 	-	Collection Administrator
	 */
	public boolean read(User user, Object object) 
	{
		return (Visibility.PUBLIC.equals(((Item)object).getVisibility()) 
				|| auth.isViewerFor(user, (Item) object)
				|| auth.isPictureEditor(user, (Item) object)
				|| auth.isContainerEditor(user, ((Item)object))
				|| auth.isContainerAdmin(user, ((Item)object)));
	}

	/**
	 * Update Image (i.e. Edit Metadata)
	 * <br/>	Allowed for :
	 * <br/>	- 	Picture Editor
	 * <br/>	- 	Collection Editor
	 * <br/> 	-	Collection Administrator
	 */
	public boolean update(User user, Object object) 
	{	
		return ( //!Status.WITHDRAWN.equals(((Image)object).getStatus()) &&
				(
					auth.isPictureEditor(user, (Item) object)
					|| auth.isContainerEditor(user, ((Item)object))
					|| auth.isContainerAdmin(user, ((Item)object)))
				);
	}
	/**
	 * Delete Images (Not specified!!!!):
	 * <br/>	-	Nobody
	 * 
	 */
	public boolean delete(User user, Object object) 
	{
		return ((auth.isPictureEditor(user, (Item) object)
				|| auth.isContainerEditor(user, ((Item)object))
				|| auth.isContainerAdmin(user, ((Item)object)))
				&& Status.PENDING.equals(((Item)object).getStatus()));
	}


	
}
