package de.mpg.j2j;

import java.net.URI;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.tdb.TDBFactory;

import de.mpg.imeji.logic.ImejiJena;
import de.mpg.imeji.logic.controller.ProfileController;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.util.StringHelper;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Grant;
import de.mpg.imeji.logic.vo.Grant.GrantType;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.util.ImejiFactory;
import de.mpg.j2j.exceptions.NotFoundException;

/**
 * Class for developer testing purpose
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class BasicTest
{
    public static void main(String[] args) throws Exception
    {
        jenaInitTest();
        imejiInitTest();
        System.out.println("all done.");
    }

    /**
     * If this test works, then imeji should be abble to run (Should be run twice)
     * 
     * @throws Exception
     */
    public static void imejiInitTest() throws Exception
    {
        System.out.println("imejiInitTest started...");
        ImejiJena.init();
        UserController uc = new UserController(ImejiJena.adminUser);
        try
        {
            uc.retrieve(getTestUser().getEmail());
        }
        catch (NotFoundException e)
        {
            uc.create(getTestUser());
        }
        ProfileController pc = new ProfileController();
        pc.create(new MetadataProfile(), getTestUser());
        ImejiJena.printModel(ImejiJena.profileModel);
        ImejiJena.printModel(ImejiJena.userModel);
        System.out.println("done...");
    }

    /**
     * Test to understand problems in imeji initialization. Should do the same thing like imejiInitTest but with simple
     * jena methods
     * 
     * @throws Exception
     */
    public static void jenaInitTest() throws Exception
    {
        System.out.println("jenaInitTest started...");
        Dataset ds = TDBFactory.createDataset("C:\\Projects\\Imeji\\tdb\\testing");
        String name1 = "http://test.com/model1";
        String name2 = "http://test.com/model2";
        initModel(ds, name1);
        initModel(ds, name2);
        for (int i = 0; i < 2; i++)
        {
            String id1 = "http://test.com/" + i;
            String id2 = "http://test.com/" + (i + 100);
            writeResource(ds, name1, id1);
            writeResource(ds, name2, id2);
            printModel(ds, name1);
            printModel(ds, name2);
        }
        System.out.println("done...");
    }

    public static void writeResource(Dataset ds, String modelName, String id)
    {
        try
        {
            ds.begin(ReadWrite.WRITE);
            Model model = ds.getNamedModel(modelName);
            Resource r = model.createResource(id);
            r.addLiteral(model.createProperty("http://test.com/property"), "value1");
            ds.commit();
        }
        finally
        {
            ds.end();
        }
    }

    public static void initModel(Dataset ds, String name)
    {
        try
        {
            ds.begin(ReadWrite.READ);
            Model m = ModelFactory.createDefaultModel();
            if (ds.containsNamedModel(name))
            {
                ds.getNamedModel(name);
            }
            else
            {
                ds.addNamedModel(name, m);
            }
            ds.commit();
        }
        finally
        {
            ds.end();
        }
    }

    public static void printModel(Dataset ds, String name)
    {
        try
        {
            System.out.println("printing " + name);
            ds.begin(ReadWrite.READ);
            Model model = ds.getNamedModel(name);
            model.write(System.out, "RDF/XML");
            ds.commit();
        }
        finally
        {
            ds.end();
        }
    }

    public static void testReadResource(Dataset ds, String modelName, String id)
    {
        try
        {
            ds.begin(ReadWrite.READ);
            Model model = ds.getNamedModel(modelName);
            Resource r = model.getResource(id);
            System.out.println(r);
            System.out.println(r.getProperty(model.createProperty("http://test.com/property")));
            ds.commit();
        }
        finally
        {
            ds.end();
        }
    }

    public static CollectionImeji getTestCollection()
    {
        CollectionImeji c = ImejiFactory.newCollection();
        return c;
    }

    public static MetadataProfile getTestProfile()
    {
        MetadataProfile p = ImejiFactory.newProfile();
        return p;
    }

    public static User getTestUser()
    {
        User adminUser = new User();
        adminUser.setEmail("test@mpdl.mpg.de");
        adminUser.setName("Imeji test");
        adminUser.setNick("test");
        try
        {
            adminUser.setEncryptedPassword(StringHelper.convertToMD5("password"));
        }
        catch (Exception e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return adminUser;
    }

    public static User getAdmin()
    {
        User adminUser = new User();
        adminUser.setEmail("admin@mpdl.mpg.de");
        adminUser.setName("Imeji Sysadmin");
        adminUser.setNick("sysadmin");
        try
        {
            adminUser.setEncryptedPassword(StringHelper.convertToMD5("password"));
        }
        catch (Exception e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        adminUser.getGrants().add(new Grant(GrantType.SYSADMIN, URI.create("http://imeji.org/")));
        return adminUser;
    }
}
