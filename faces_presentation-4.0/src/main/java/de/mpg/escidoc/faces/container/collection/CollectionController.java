package de.mpg.escidoc.faces.container.collection;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.impl.xb.xsdschema.Element;
import org.apache.xmlbeans.impl.xb.xsdschema.ExplicitGroup;
import org.apache.xmlbeans.impl.xb.xsdschema.NoFixedFacet;
import org.apache.xmlbeans.impl.xb.xsdschema.SchemaDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.RestrictionDocument.Restriction;
import org.apache.xmlbeans.impl.xb.xsdschema.SchemaDocument.Schema;

import de.escidoc.core.x01.properties.OrganizationalUnitsDocument.OrganizationalUnits;
import de.escidoc.schemas.container.x08.ContainerDocument;
import de.escidoc.schemas.context.x07.ContextDocument;
import de.escidoc.schemas.context.x07.ContextDocument.Context;
import de.escidoc.schemas.context.x07.PropertiesDocument.Properties;
import de.escidoc.schemas.metadatarecords.x05.MdRecordDocument.MdRecord;
import de.mpg.escidoc.faces.container.FacesContainerController;
import de.mpg.escidoc.faces.container.FacesContainerVO;
import de.mpg.escidoc.faces.mdProfile.MdProfileVO;
import de.mpg.escidoc.faces.metadata.Metadata;
import de.mpg.escidoc.faces.util.LoginHelper;
import de.mpg.escidoc.faces.util.UserHelper;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.ContainerVO;
import de.mpg.escidoc.services.common.valueobjects.TaskParamVO;
import de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean;
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
	 * Create a collection.
	 * The collection must have a valid context.
	 */
	public FacesContainerVO create(CollectionVO collectionVO, String userHandle) throws Exception
	{
	    String container = transformToContainerXml(collectionVO);
	    String createdCollection = ServiceLocator.getContainerHandler(userHandle).create(container); 
	    return  new CollectionVO(new FacesContainerVO(transformToContainerVO(createdCollection)));

        	
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
	 * @override
	 */
	/*
	public  CollectionVO retrieve(String id, String userHandle) throws Exception
	{
		FacesContainerVO fc = super.retrieve(id, userHandle);
		
		return new CollectionVO(fc);
	}
	*/
	
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
	
	  /**
     * Helper method that transforms an MDProfileVO object into a SchemaDocument (XMLBeans)
     * @param mdProfileVO
     * @return
     * @throws Exception
     */
	/*
    private static SchemaDocument transformToXml(MdProfileVO mdProfileVO) throws Exception
    {
    	SchemaDocument schemaDoc = SchemaDocument.Factory.newInstance();
    	Schema schema = schemaDoc.addNewSchema();
    	Element rootElement = schema.addNewElement();
    	rootElement.setName("imeji");
    	ExplicitGroup sequ = rootElement.addNewComplexType().addNewSequence();
    	
    	for(Metadata mdVO : mdProfileVO.getMetadataList())
    	{
    		Element e = sequ.addNewElement();
    		e.setName(mdVO.getName());
    		e.setRef(new QName(mdVO.getNamespace(), mdVO.getName()));
    		e.setMinOccurs(BigInteger.valueOf(mdVO.getMinOccurs()));
    		e.setMaxOccurs(BigInteger.valueOf(mdVO.getMaxOccurs()));
    		
    		if(mdVO.getConstraint()!=null && mdVO.getConstraint().size() > 0)
    		{
    			Restriction r = e.addNewSimpleType().addNewRestriction();
    			for(String constraint : mdVO.getConstraint())
    			{
    				r.addNewEnumeration().setValue(XmlAnySimpleType.Factory.newValue(constraint));
    			}
    		}
    		
    	}

	    return schemaDoc;
    	
    	
	    
	    
    }
    
    
    private static CollectionVO transformToCollectionVO(ContainerDocument containerDoc) throws Exception
    {
    	for(MdRecord mdRec : containerDoc.getContainer().getMdRecords().getMdRecordArray())
    	{
    		if(mdRec.getName().equals("md-profile"))
    		{
    			XmlCursor mdRecCursor = mdRec.newCursor();
    			mdRecCursor.toEndToken();
    			SchemaDocument schemaDoc = SchemaDocument.Factory.parse(mdRecCursor.getDomNode());
    			MdProfileVO mdProfile = transformToMdProfileVO(schemaDoc);
    			mdRecCursor.dispose();
    			
    			mdRecCursor = mdRec.newCursor();
    			mdRecCursor.removeXml();
    			mdRecCursor.dispose();
    			
    			ContainerVO container = xmlTransforming.transformToContainer(containerDoc.xmlText());
    			CollectionVO coll = new CollectionVO(new FacesContainerVO(container));
    			coll.setMdProfile(mdProfile);
    			return coll;
    			
    		}
    	}
    	return null;
    	
    }
    
    
    private static ContainerDocument transformToCollection(CollectionVO coll) throws Exception
    {
    	String facesContainerXml = xmlTransforming.transformToContainer(coll);
		
		//add md profile schema
		ContainerDocument container = ContainerDocument.Factory.parse(facesContainerXml);
		MdRecord mdRec = container.getContainer().getMdRecords().addNewMdRecord();
		mdRec.setName("md-profile");
		XmlCursor mdRecCursor = mdRec.newCursor();
	    mdRecCursor.toEndToken();
	    XmlCursor schemaCursor = transformToXml(coll.getMdProfile()).newCursor();
	    schemaCursor.copyXmlContents(mdRecCursor);
	    mdRecCursor.dispose();
	    schemaCursor.dispose();
	    
	    return container;
    	
    }
    
    
    private static MdProfileVO transformToMdProfileVO(SchemaDocument schemaDoc) throws Exception
    {
    	Element rootElem = schemaDoc.getSchema().getElementArray(0);
    	String profileName = rootElem.getName();
    	
    	List<Metadata> mdList = new ArrayList<Metadata>();
    	
    	for (Element el : rootElem.getComplexType().getSequence().getElementArray())
    	{
    		Metadata md = new Metadata(el.getName(), el.getName(), el.getDomNode().getNamespaceURI());
    		mdList.add(md);
    		
    		if (el.getSimpleType()!=null && el.getSimpleType().getRestriction()!=null)
    		{
    			List<String> constraintList = new ArrayList<String>();
    			for(NoFixedFacet constraint :  el.getSimpleType().getRestriction().getEnumerationArray())
    			{
    				constraintList.add(constraint.getValue().toString());
    			}
    			md.setConstraint(constraintList);
    		}
    	}
    	
    	
    	MdProfileVO mdProfile = new MdProfileVO(profileName, mdList);
    	return mdProfile;
    }
*/
}
