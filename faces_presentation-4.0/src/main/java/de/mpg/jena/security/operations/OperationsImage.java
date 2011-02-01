package de.mpg.jena.security.operations;

import de.mpg.jena.security.Authorization;
import de.mpg.jena.security.Operations;
import de.mpg.jena.vo.Image;
import de.mpg.jena.vo.Image.Visibility;
import de.mpg.jena.vo.User;

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
		return (auth.isPictureEditor(user, (Image) object)
				|| auth.isContainerEditor(user, ((Image)object))
				|| auth.isContainerAdmin(user, ((Image)object)));
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
		return (Visibility.PUBLIC.equals(((Image)object).getVisibility()) 
				|| auth.isViewerFor(user, (Image) object)
				|| auth.isPictureEditor(user, (Image) object)
				|| auth.isContainerEditor(user, ((Image)object))
				|| auth.isContainerAdmin(user, ((Image)object)));
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
		return (auth.isPictureEditor(user, (Image) object)
				|| auth.isContainerEditor(user, ((Image)object))
				|| auth.isContainerAdmin(user, ((Image)object)));
	}
	/**
	 * Delete Images (Not specified!!!!):
	 * <br/>	-	Nobody
	 * 
	 */
	public boolean delete(User user, Object object) 
	{
		return false;
	}


	
}
