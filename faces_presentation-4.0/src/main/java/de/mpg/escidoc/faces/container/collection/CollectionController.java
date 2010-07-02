package de.mpg.escidoc.faces.container.collection;

import java.net.URISyntaxException;

import javax.xml.rpc.ServiceException;

import de.escidoc.core.x01.properties.OrganizationalUnitsDocument.OrganizationalUnits;
import de.escidoc.schemas.contentmodel.x01.ContentModelDocument;
import de.escidoc.schemas.context.x07.ContextDocument;
import de.escidoc.schemas.context.x07.ContextDocument.Context;
import de.escidoc.schemas.context.x07.PropertiesDocument.Properties;
import de.escidoc.schemas.result.x01.ResultDocument;
import de.escidoc.schemas.result.x01.ResultDocument.Result;
import de.escidoc.schemas.searchresult.x08.SearchResultRecordDocument.SearchResultRecord;
import de.mpg.escidoc.faces.container.FacesContainerController;
import de.mpg.escidoc.faces.container.FacesContainerVO;
import de.mpg.escidoc.faces.util.ContentModelHelper;
import de.mpg.escidoc.faces.util.LoginHelper;
import de.mpg.escidoc.faces.util.UserHelper;
import de.mpg.escidoc.services.common.referenceobjects.ContextRO;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.ContextVO;
import de.mpg.escidoc.services.common.valueobjects.GrantVO;
import de.mpg.escidoc.services.common.valueobjects.TaskParamVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.IdentifierVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.IdentifierVO.IdType;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;

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
	 * Create a a new context for the collection before to create the collection itself
	 */
	public FacesContainerVO create(CollectionVO collectionVO, String userHandle) throws Exception
    {
		AccountUserVO accountUserVO = UserHelper.getAccounUserVO(userHandle);
		
		String contextId = addNewContext(collectionVO.getMdRecord().getTitle().getValue()
			, collectionVO.getMdRecord().getAbstracts().get(0).getValue()
			, userHandle, "escidoc:persistent13");
		
		collectionVO.setContext(new ContextRO(contextId));
    	
    	collectionVO.getMdRecord().getIdentifiers().add(
    			new IdentifierVO(IdType.URI, 
    					ServiceLocator.getFrameworkUrl() +  "/cmm/content-model/" + collectionVO.getMdProfileId()));

    	UserHelper.addGrantToUser(accountUserVO, new GrantVO(GrantVO.PredefinedRoles.DEPOSITOR.frameworkValue(), contextId), userHandle);
    	UserHelper.addGrantToUser(accountUserVO, new GrantVO(GrantVO.PredefinedRoles.MODERATOR.frameworkValue(), contextId), userHandle);
    	UserHelper.addGrantToUser(accountUserVO, new GrantVO(GrantVO.PredefinedRoles.PRIVILEGEDVIEWER.frameworkValue(), contextId), userHandle);
		
        return  super.create(collectionVO, userHandle);
    }
	
	/**
	 * Create a new context in FW
	 * @param name
	 * @param description
	 * @param userhandle
	 * @return
	 */
	public String addNewContext(String name, String description, String userHandle, String organizationnalUnitId)
	{
		ContextDocument contextDocument = ContextDocument.Factory.newInstance();
		contextDocument.setContext(Context.Factory.newInstance());
		contextDocument.getContext().setProperties(Properties.Factory.newInstance());
		contextDocument.getContext().getProperties().setName(name);
		contextDocument.getContext().getProperties().setDescription(description);
		contextDocument.getContext().getProperties().setType("faces");
		contextDocument.getContext().getProperties().setOrganizationalUnits(OrganizationalUnits.Factory.newInstance());
		contextDocument.getContext().getProperties().getOrganizationalUnits().addNewOrganizationalUnit();
		contextDocument.getContext().getProperties().getOrganizationalUnits().getOrganizationalUnitArray(0).setObjid(organizationnalUnitId);
		
		try 
		{
			String contextXml = ServiceLocator.getContextHandler(LoginHelper.loginSystemAdmin()).create(contextDocument.xmlText());
			contextDocument = ContextDocument.Factory.parse(contextXml);
			
			TaskParamVO taskParamVO = new TaskParamVO(contextDocument.getContext().getLastModificationDate().getTime());
			
			String taskParam = xmlTransforming.transformToTaskParam(taskParamVO);
			
			ServiceLocator.getContextHandler(LoginHelper.loginSystemAdmin()).open(contextDocument.getContext().getObjid(), taskParam);
		} 
		catch (Exception e) 
		{
			throw new RuntimeException("Error creating new context", e);
		}
		
		return contextDocument.getContext().getObjid();
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
