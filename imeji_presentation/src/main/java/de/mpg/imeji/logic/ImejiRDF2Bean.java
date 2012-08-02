/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.security.Operations.OperationsType;
import de.mpg.imeji.logic.security.Security;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.imeji.logic.vo.MetadataSet;
import de.mpg.imeji.logic.vo.User;
import de.mpg.j2j.helper.J2JHelper;
import de.mpg.j2j.transaction.CRUDTransaction;
import de.mpg.j2j.transaction.Transaction;

/**
 * Interface for read operations from Jena. Implements security and transaction.
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class ImejiRDF2Bean
{
    private static Logger logger = Logger.getLogger(ImejiRDF2Bean.class);
    private String modelURI;
    private boolean lazy = false;
    private boolean asynchrone = false;
    private Security security = null;

    public ImejiRDF2Bean(String modelURI)
    {
        this.modelURI = modelURI;
        security = new Security();
    }

    public Object loadLazy(String uri, User user, Object o) throws Exception
    {
        this.lazy = true;
        return load(uri, user, o);
    }

    public Object load(String uri, User user, Object o) throws Exception
    {
        J2JHelper.setId(o, URI.create(uri));
        List<Object> objects = new ArrayList<Object>();
        objects.add(o);
        List<Object> l = load(objects, user);
        if (l.size() > 0)
            return l.get(0);
        return null;
    }

    public List<Object> load(List<Object> objects, User user) throws Exception
    {
        Transaction transaction = new CRUDTransaction(objects, OperationsType.READ, modelURI, lazy);
        transaction.start();
        transaction.waitForEnd();
        transaction.throwException();
        checkSecurity(objects, user, OperationsType.READ);
        return objects;
    }

    public List<Object> loadLazy(List<Object> objects, User user) throws Exception
    {
        this.lazy = true;
        return load(objects, user);
    }

    private void checkSecurity(List<Object> list, User user, OperationsType opType)
    {
        for (int i = 0; i < list.size(); i++)
        {
            if (!security.check(opType, user, list.get(i)))
            {
                String id = J2JHelper.getId(list.get(i)).toString();
                String email = "Not logged in";
                if (user != null)
                    email = user.getEmail();
                throw new RuntimeException("Imeji Security exception: " + email + " not allowed to " + opType.name()
                        + " " + id);
            }
        }
    }

    private void removePrivateImages(Item im, User user)
    {
        im.setThumbnailImageUrl(URI.create("private"));
        im.setWebImageUrl(URI.create("private"));
        im.setFullImageUrl(URI.create("private"));
    }

    private void sortMetadataAccordingToPosition(Item im)
    {
        List<Metadata> mdSorted = new ArrayList<Metadata>();
        for (MetadataSet mds : im.getMetadataSets())
        {
            for (Metadata md : mds.getMetadata())
            {
                if (md.getPos() < mdSorted.size())
                    mdSorted.add(md.getPos(), md);
                else
                    mdSorted.add(md);
            }
            mds.setMetadata(mdSorted);
        }
    }
}
