package de.mpg.escidoc.faces.metastore.util;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.rpc.ServiceException;

import org.apache.xmlbeans.XmlException;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.DC;

import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ComponentNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.schemas.item.x09.ItemDocument;
import de.escidoc.www.services.om.ItemHandler;
import de.mpg.escidoc.faces.metastore.DataFactory;
import de.mpg.escidoc.faces.metastore.ResourceHandler;
import de.mpg.escidoc.faces.metastore.URIS;
import de.mpg.escidoc.faces.metastore.vocabulary.FACES4;
import de.mpg.escidoc.services.framework.ServiceLocator;

public class MetastoreTest implements URIS
{
    static Resource emotion = null;
    static Resource pic_group = null;
    static Resource age_group = null;
    static Resource gender = null;
    static Literal identifier = null;
    static Literal age = null;
    
    public static void main(String[] args)
    {
        //DataFactory.removeData(BASE_MODEL);
        //DataFactory.removeData(FACE_MODEL);
        //create();
        //getResource();
        //all();
        //getValues("114788");
        updateValue();
    }
    
    public static void create()
    {
        ItemHandler ih;
        try
        {
            ih = ServiceLocator.getItemHandler(new URL("http://dev-coreservice.mpdl.mpg.de"));
            String itemXml = ih.retrieve("escidoc:111992");
            ItemDocument iDoc = ItemDocument.Factory.parse(itemXml);

            emotion = FACES4.Emotion.disgust;
            pic_group = FACES4.PictureGroup.a;
            age_group = FACES4.AgeGroup.middle;
            gender = FACES4.Gender.female;
            identifier = ResourceFactory.createTypedLiteral(iDoc.getItem().getObjid());
            age = ResourceFactory.createTypedLiteral(54);
            
            ResourceHandler rh = new ResourceHandler();
            HashMap<Property, RDFNode> map = rh.facesMetadataMap(emotion, pic_group, identifier, age, age_group, gender);
            for (Entry<Property, RDFNode> e : map.entrySet())
            {
                System.out.println(e.getKey() + "  " + e.getValue());
            }
            //rh.createFaceItem(iDoc, true, map);
            //rh.createFaceItem(iDoc, false, null);
            rh.createFaceMetadata("111992", map);
        }
        catch (ServiceException e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        catch (URISyntaxException e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        catch (ComponentNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (ItemNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (MissingMethodParameterException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (AuthorizationException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (AuthenticationException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (SystemException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (RemoteException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (XmlException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (MalformedURLException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public static void getResource()
    {
        ResourceHandler rh = new ResourceHandler();
        String[] ids = new String[]{"114216", "114200", "114788", "111725"};
        rh.retrieveFaceItemRDF(ids, true, false);
    }
    
    public static void getValues(String id)
    {
        ResourceHandler rh = new ResourceHandler();
        ArrayList<String[]> values = rh.retrieveFaceItemValues(id, true, true);
        for (String[] e : values)
        {
            System.out.println(e[0] + ":   " + e[1]);
        }
    }
    
    public static void all()
    {
        Model m = DataFactory.model(BASE_MODEL);
        Model f = DataFactory.model(FACE_MODEL);
        m.begin();
        f.begin();
        DataFactory.removeResource(BASE_MODEL, BASE_URI, "123456", null);
        f.removeAll(ResourceFactory.createResource(FACE_MD_URI + "123456"), null, null);
        m.commit();
        f.commit();
        m.close();
        f.close();
        m.write(System.out, "RDF/XML-ABBREV");
        f.write(System.out, "RDF/XML-ABBREV");
        
    }
    
    public static void updateValue()
    {
        Model base = DataFactory.model(BASE_MODEL);
        ResIterator resi = base.listSubjectsWithProperty(ResourceFactory.createProperty(BASE_URI, "metadata"));
        ArrayList<String> idList = new ArrayList<String>();
        for (; resi.hasNext(); )
        {
            Resource res = resi.nextResource();
            idList.add(res.getURI().substring(res.getURI().lastIndexOf("/") + 1));
        }
        Property prop = DC.identifier;
        ResourceHandler rh = new ResourceHandler();

        for (String id : idList)
        {
            Resource res = ResourceFactory.createResource(BASE_URI + id);
            rh.updateFacesMetadataValue(id, prop, res);
        }
    }
}
