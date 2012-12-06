package de.mpg.imeji.presentation.ingest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import de.mpg.imeji.logic.ingest.controller.IngestController;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.presentation.beans.SessionBean;
import de.mpg.imeji.presentation.collection.ViewCollectionBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.UrlHelper;

public class IngestBean
{
    private File itemListXmlFile;
    private File profileXmlFile;
    private SessionBean session = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
    private String collectionId;
    private CollectionImeji collection;

    public String ingest()
    {
        IngestController ic = new IngestController(session.getUser(), collection);
        try
        {
            ic.ingest(itemListXmlFile, profileXmlFile);
        }
        catch (Exception e)
        {
        	itemListXmlFile = null;
        	profileXmlFile = null;
            BeanHelper.error(session.getMessage("error_ingest"));
            BeanHelper.error(e.getMessage());
            e.printStackTrace();
        }
        BeanHelper.info(session.getMessage("success_ingest"));
        BeanHelper.info(session.getMessage("success_ingest_images_into_collection") + " " + collection.getMetadata().getTitle());
        return "pretty:";
    }

    public void itemListXmlListener(UploadEvent event) throws Exception
    {
        UploadItem item = event.getUploadItem();
        itemListXmlFile = item.getFile();
    }

    public void profileXmlListener(UploadEvent event) throws Exception
    {
        UploadItem item = event.getUploadItem();
        profileXmlFile = item.getFile();
    }

    public void loadCollection()
    {
        if (collectionId != null)
        {
            ((ViewCollectionBean)BeanHelper.getSessionBean(ViewCollectionBean.class)).setId(collectionId);
            ((ViewCollectionBean)BeanHelper.getSessionBean(ViewCollectionBean.class)).init();
            collection = ((ViewCollectionBean)BeanHelper.getSessionBean(ViewCollectionBean.class)).getCollection();
        }
        else
        {
            BeanHelper.error(session.getLabel("error") + " No ID in URL");
        }
    }

    @Deprecated
    private String readFile(File file) throws IOException
    {
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String fileString = "";
        String s;
        while ((s = br.readLine()) != null)
        {
            fileString += s;
        }
        fr.close();
        return fileString;
    }

    public void status()
    {
        if (UrlHelper.getParameterBoolean("init"))
        {
            loadCollection();
        }
    }

    public CollectionImeji getCollection()
    {
        return collection;
    }

    public void setCollection(CollectionImeji collection)
    {
        this.collection = collection;
    }

    public String getCollectionId()
    {
        return collectionId;
    }

    public void setCollectionId(String collectionId)
    {
        this.collectionId = collectionId;
    }
}
