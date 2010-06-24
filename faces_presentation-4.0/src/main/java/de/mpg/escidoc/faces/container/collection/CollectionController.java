package de.mpg.escidoc.faces.container.collection;

import de.mpg.escidoc.faces.container.FacesContainerController;
import de.mpg.escidoc.faces.container.FacesContainerVO;

/**
 * Controller for {@link CollectionVO}
 * <br> Extends {@link FacesContainerController}
 * @author saquet
 *
 */
public class CollectionController extends FacesContainerController
{
	public CollectionController() 
	{
		super();
	}
	
	/**
	 * TODO: Validate the {@link CollectionVO} against it's validation rules.
	 * 
	 * @param collectionVO
	 * @return
	 */
	public boolean validate(CollectionVO collectionVO)
	{
		return false;
	}
	
	/**
	 * TODO: Create a surrogate item with the same content-model as the collection
	 */
	@Override
	public FacesContainerVO addMember(FacesContainerVO facesContainer, String item, String userHandle) throws Exception 
	{
		// TODO Auto-generated method stub
		return super.addMember(facesContainer, item, userHandle);
	}

}
