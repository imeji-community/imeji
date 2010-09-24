package de.mpg.imeji.escidoc;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;

import de.escidoc.schemas.item.x09.ItemDocument;
import de.escidoc.schemas.item.x09.ItemDocument.Item;
import de.escidoc.schemas.metadatarecords.x05.MdRecordDocument;
import de.escidoc.schemas.metadatarecords.x05.MdRecordDocument.MdRecord;
import de.escidoc.schemas.metadatarecords.x05.MdRecordsDocument.MdRecords;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.imeji.upload.helper.ImageHelper;

public class ItemVO
{
    //protected MdsImejiItemVO mdRecords = null;
    protected ItemDocument itemDoc = null;

    public ItemVO(String title, String description, String context) throws IOException, URISyntaxException
    {
        //mdRecords = new MdsImejiItemVO(title, description);
        itemDoc = ItemDocument.Factory.newInstance();
        Item item = itemDoc.addNewItem();
        System.out.println(item.getDomNode().getNamespaceURI());
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
        mdRecs.setMdRecordArray(0, mdRec);
        item.setMdRecords(mdRecs);
        item.addNewRelations();
        item.addNewProperties().addNewContext();
        item.getProperties().getContext().setObjid(context);
        item.getProperties().addNewContentModel();
        item.getProperties().getContentModel().setObjid(PropertyReader.getProperty("escidoc.faces.content-model.id"));
    }

    /**
     * This method creates the three components (org, web, thumb resolution) for an imegji item
     * 
     * @param inputStream
     * @param fileName
     * @param mimetype
     * @param userHandle
     * @throws Exception
     */
    public void attachFile(InputStream inputStream, String fileName, String mimetype, String format,
            String userHandle) throws Exception
    {
        itemDoc.getItem().addNewContentStreams();
       ByteArrayOutputStream bos = new ByteArrayOutputStream();
       int b;
        while((b = inputStream.read()) != -1)
        {
            bos.write(b);
        }
        byte[] imageStream = bos.toByteArray();
        bos.flush();
        bos.close();
        
        this.itemDoc.setItem(ImageHelper.setComponent(ImageHelper.getOrig(), itemDoc.getItem(), imageStream,
                fileName, mimetype, format, userHandle));
        this.itemDoc.setItem(ImageHelper.setComponent(ImageHelper.getWeb(), itemDoc.getItem(), imageStream, fileName,
                mimetype, format, userHandle));
        this.itemDoc.setItem(ImageHelper.setComponent(ImageHelper.getThumb(), itemDoc.getItem(), imageStream,
                fileName, mimetype, format, userHandle));
    }

//    public MdsImejiItemVO getMdRecords()
//    {
//        return mdRecords;
//    }
//
//    public void setMdRecords(MdsImejiItemVO mdRecords)
//    {
//        this.mdRecords = mdRecords;
//    }

    public ItemDocument getItemDocument()
    {
        return itemDoc;
    }

    public void setItem(ItemDocument item)
    {
        this.itemDoc = item;
    }
}
