package de.mpg.escidoc.faces.mdProfile;

import gov.loc.zing.srw.RecordType;
import gov.loc.zing.srw.RecordsType;
import gov.loc.zing.srw.SearchRetrieveResponseDocument;
import gov.loc.zing.srw.SearchRetrieveResponseType;

import java.io.StringWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.namespace.QName;

import org.apache.axis.encoding.Base64;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.impl.xb.xsdschema.Element;
import org.apache.xmlbeans.impl.xb.xsdschema.ExplicitGroup;
import org.apache.xmlbeans.impl.xb.xsdschema.NoFixedFacet;
import org.apache.xmlbeans.impl.xb.xsdschema.SchemaDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.RestrictionDocument.Restriction;
import org.apache.xmlbeans.impl.xb.xsdschema.SchemaDocument.Schema;
import org.w3c.dom.Node;

import de.escidoc.schemas.item.x09.ItemDocument;
import de.escidoc.schemas.item.x09.ItemDocument.Item;
import de.escidoc.schemas.itemlist.x09.ItemListDocument.ItemList;
import de.escidoc.schemas.metadatarecords.x05.MdRecordDocument.MdRecord;
import de.mpg.escidoc.faces.metadata.Metadata;
import de.mpg.escidoc.faces.util.UserHelper;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;

public class MdProfileController
{
	private static Logger logger = Logger.getLogger(MdProfileController.class);
    private XmlTransforming xmlTransforming = null;
    
    public MdProfileController()
    {
	try
	{
	    InitialContext context = new InitialContext();
	    xmlTransforming  = (XmlTransforming) context.lookup(XmlTransforming.SERVICE_NAME);
	} 
	catch (NamingException e)
	{
	    throw new RuntimeException("MdProfileController: Error initialization: " + e);
	}
    }

    
    public MdProfileVO create(MdProfileVO profile, String userHandle)
    {
	try
	{
	   
		//Workaround until content models can be created by other user than admin
		//and are searchable
		
	    ItemDocument itemDocument = ItemDocument.Factory.newInstance();
	    itemDocument.addNewItem();
	    itemDocument.getItem().addNewProperties();
	    itemDocument.getItem().getProperties().addNewContentModel();
	    itemDocument.getItem().getProperties().getContentModel().setObjid("escidoc:194296");
	    itemDocument.getItem().getProperties().addNewContext();
	    itemDocument.getItem().getProperties().getContext().setObjid(PropertyReader.getProperty("escidoc.faces.context.id"));
	    MdRecord mdRec = itemDocument.getItem().addNewMdRecords().addNewMdRecord();
	    mdRec.setName("escidoc");
	    
	    XmlCursor mdRecCursor = mdRec.newCursor();
	    mdRecCursor.toEndToken();
	    XmlCursor schemaCursor = transformToXml(profile).newCursor();
	    schemaCursor.copyXmlContents(mdRecCursor);
	    mdRecCursor.dispose();
	    schemaCursor.dispose();
	    
	    String createdItem = ServiceLocator.getItemHandler(userHandle).create(itemDocument.xmlText());
	    logger.info(ItemDocument.Factory.parse(createdItem).getItem().getObjid() + " - Metadata Profile created");
	    
	    ItemDocument.Factory.parse(createdItem);
	    
	    
	    
		
	    //Create content-model with reference to previous item component
	    /*
	    de.escidoc.schemas.contentmodel.x01.ContentModelDocument cmd = de.escidoc.schemas.contentmodel.x01.ContentModelDocument.Factory.newInstance();
	    cmd.addNewContentModel();
	    cmd.getContentModel().addNewProperties();
	    cmd.getContentModel().getProperties().setName(profile.getName());
	    cmd.getContentModel().getProperties().setDescription("Content model for " + profile.getName() + " profile");
	    cmd.getContentModel().addNewContentStreams();
	    cmd.getContentModel().getContentStreams().addNewContentStream();
	    cmd.getContentModel().getContentStreams().getContentStreamArray(0).setMimeType(XmlAnySimpleType.Factory.newValue("text/xml"));
	    cmd.getContentModel().getContentStreams().getContentStreamArray(0).setName(profile.getName() + "xsd");
	    cmd.getContentModel().getContentStreams().getContentStreamArray(0).setStorage(XmlAnySimpleType.Factory.newValue("internal-managed"));
	    
	    //copy schema into content stream of content model
	    XmlCursor contentModelCursor = cmd.getContentModel().getContentStreams().getContentStreamArray(0).newCursor();
	    contentModelCursor.toEndToken();
	    XmlCursor schemaCursor = transformToXml(profile).newCursor();
	    schemaCursor.copyXmlContents(contentModelCursor);
	    contentModelCursor.dispose();
	    schemaCursor.dispose();

	    System.out.println("content model GENERATED:");
	    System.out.println(cmd.xmlText());
	    System.out.println("END content model");
	    */
	    //String createdContentModel = ServiceLocator.getContentModelHandler(userHandle).create(cmd.xmlText());
	    

	    return profile;
	} 
	catch (Exception e)
	{
	    throw new RuntimeException("Error creating Md Profile", e);
	}
    }
    
    public List<MdProfileVO> retrieveMdProfiles(String userHandle) throws Exception
    {
    	
    	HashMap<String, String[]> filterParams = new HashMap<String, String[]>();
    	filterParams.put("operation",new String[]{"searchRetrieve"});
    	filterParams.put("version", new String[]{"1.1"});
    	filterParams.put("query", new String[]{"\"/properties/content-model/id\"=escidoc:194296"});
    	
    	String itemListXml = ServiceLocator.getItemHandler(userHandle).retrieveItems(filterParams);
    	
    	SearchRetrieveResponseDocument il = SearchRetrieveResponseDocument.Factory.parse(itemListXml);
    	List<MdProfileVO> mdProfileList = new ArrayList<MdProfileVO>(); 
    	

    	if(il.getSearchRetrieveResponse().getNumberOfRecords().intValue() > 0)
    	{
	    	for (RecordType rec : il.getSearchRetrieveResponse().getRecords().getRecordArray())
	    	{
	    		ItemDocument item = ItemDocument.Factory.parse(rec.getRecordData().getDomNode().getFirstChild());
	    		XmlObject schemaNode = item.getItem().getMdRecords().getMdRecordArray(0).selectChildren(new QName("http://www.w3.org/2001/XMLSchema","schema"))[0];
	    		SchemaDocument schemaDoc = SchemaDocument.Factory.parse(schemaNode.getDomNode());
	    		logger.info("Found schema doc : " +schemaDoc.xmlText());
	    		mdProfileList.add(transformToMdProfileVO(item.getItem().getObjid(), schemaDoc));
	    	}
    	}

    	return mdProfileList;  

    	
    }
    
    
    
    
    /**
     * Helper method that transforms an MDProfileVO object into a SchemaDocument (XMLBeans)
     * @param mdProfileVO
     * @return
     * @throws Exception
     */
    private static SchemaDocument transformToXml(MdProfileVO mdProfileVO) throws Exception
    {
    	SchemaDocument schemaDoc = SchemaDocument.Factory.newInstance();
    	Schema schema = schemaDoc.addNewSchema();
    	Element rootElement = schema.addNewElement();
    	rootElement.setName(mdProfileVO.getName());
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
    
    private static MdProfileVO transformToMdProfileVO(String id, SchemaDocument schemaDoc) throws Exception
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
    	mdProfile.setId(id);
    	return mdProfile;
    }
    
    
}
