package de.mpg.escidoc.faces.item;

import java.awt.image.BufferedImage;

import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;

import de.escidoc.schemas.item.x09.ItemDocument;
import de.escidoc.schemas.item.x09.ItemDocument.Item;
import de.escidoc.schemas.metadatarecords.x05.MdRecordDocument;
import de.escidoc.schemas.metadatarecords.x05.MdRecordDocument.MdRecord;
import de.escidoc.schemas.metadatarecords.x05.MdRecordsDocument.MdRecords;
import de.escidoc.schemas.relations.x03.RelationsDocument.Relations.Relation;
import de.mpg.escidoc.faces.metadata.MdsImejiItemVO;
import de.mpg.escidoc.faces.upload.helper.ImageHelper;
import de.mpg.escidoc.services.framework.PropertyReader;


public class ImejiItemVO {
	
    protected MdsImejiItemVO mdRecords = null;
    protected ItemDocument itemDoc = null;

	public ImejiItemVO(String title, String description, String collection,String context){
		mdRecords = new MdsImejiItemVO(title, description);
    	itemDoc = ItemDocument.Factory.newInstance();
    	Item item = itemDoc.addNewItem();
    	System.out.println(item.getDomNode().getNamespaceURI());
    	
//    	MdRecord mdRec = (MdRecord)this.mdRecords;
    	MdRecordDocument mdRDoc = MdRecordDocument.Factory.newInstance();
    	mdRDoc.addNewMdRecord();
    	MdRecord mdRec = mdRDoc.getMdRecord();
    	mdRec.setName("escidoc");
    	
    	XmlObject xmlObject = XmlObject.Factory.newInstance();
    	XmlCursor xmlCursor = xmlObject.newCursor();
    	xmlCursor.toNextToken();
    	xmlCursor.beginElement("imeji-metadata");
    	xmlCursor.toStartDoc();
    	xmlCursor.toNextToken();
    	
    	XmlCursor cursor = mdRec.newCursor();
    	cursor.toEndToken();
    	xmlCursor.moveXml(cursor);
    	cursor.dispose();
    	xmlCursor.dispose();
    	
    	MdRecords mdRecs = item.addNewMdRecords();
    	
    	mdRecs.addNewMdRecord();
    	mdRecs.setMdRecordArray(0,mdRec);
    	item.setMdRecords(mdRecs);
    	
    	item.addNewRelations();

    	Relation rel = item.getRelations().addNewRelation();
    	rel.setObjid(collection);
    	rel.setPredicate(XmlAnySimpleType.Factory.newValue("http://www.escidoc.de/ontologies/mpdl-ontologies/content-relations#isMemberOf"));
    	
    	item.addNewProperties().addNewContext();
    	item.getProperties().getContext().setObjid(context);

    	item.getProperties().addNewContentModel();
    	try {
			item.getProperties().getContentModel().setObjid(PropertyReader.getProperty("escidoc.faces.content-model.id"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
	}

	/**
	 * This method creates the three components (org, web, thumb resolution) for an imegji item
	 * @param inputStream
	 * @param fileName
	 * @param mimetype
	 * @param userHandle
	 * @throws Exception
	 */
	public void attachFile(BufferedImage bufferedImage, String fileName, String mimetype, String format, String userHandle) throws Exception
	{
		ImageHelper imageHelper = new ImageHelper();
		itemDoc.getItem().addNewContentStreams();
		this.itemDoc.setItem(imageHelper.setComponent(imageHelper.getOrig(), itemDoc.getItem(), bufferedImage, fileName, mimetype, format, userHandle));
		this.itemDoc.setItem(imageHelper.setComponent(imageHelper.getWeb(), itemDoc.getItem(), bufferedImage, fileName, mimetype, format, userHandle));
		this.itemDoc.setItem(imageHelper.setComponent(imageHelper.getThumb(), itemDoc.getItem(), bufferedImage, fileName, mimetype, format, userHandle));
	}
	
	public MdsImejiItemVO getMdRecords() {
		return mdRecords;
	}

	public void setMdRecords(MdsImejiItemVO mdRecords) {
		this.mdRecords = mdRecords;
	}

	public ItemDocument getItemDocument() {
		return itemDoc;
	}

	public void setItem(ItemDocument item) {
		this.itemDoc = item;
	}
    
    
}
