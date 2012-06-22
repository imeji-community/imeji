/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.query.ReadWrite;

import de.mpg.imeji.logic.security.Operations.OperationsType;
import de.mpg.imeji.logic.security.Security;
import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.MetadataSet;
import de.mpg.imeji.logic.vo.User;
import de.mpg.j2j.controler.ResourceController;
import de.mpg.j2j.exceptions.NotFoundException;

/**
 * Interface for read operations from Jena. Support security and transaction.
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class ImejiRDF2Bean
{
    private ResourceController resourceController;
    private static Logger logger = Logger.getLogger(ImejiRDF2Bean.class);
    private String modelURI;

    public ImejiRDF2Bean(String modelURI)
    {
        this.modelURI = modelURI;
    }

    public Object load(String uri, User user, Object o) throws NotFoundException
    {
        ImejiJena.imejiDataSet.begin(ReadWrite.READ);
        try
        {
            Security security = new Security();
            resourceController = new ResourceController(modelURI);
            o = resourceController.read(URI.create(uri), o);
            if (!security.check(OperationsType.READ, user, o))
            {
                if (o instanceof Item)
                {
                    removePrivateImages((Item)o, user);
                }
                else
                {
                    if (user != null)
                    {
                        throw new RuntimeException("Security Exception: " + user.getEmail()
                                + " is not allowed to view " + uri);
                    }
                    else
                    {
                        throw new RuntimeException("Security Exception: You need to log in to view " + uri);
                    }
                }
            }
            if (o instanceof Item)
            {
                sortMetadataAccordingToPosition((Item)o);
            }
            // return ObjectHelper.castAllHashSetToList(o);
            return o;
        }
        catch (NotFoundException e)
        {
            throw e;
        }
        finally
        {
            ImejiJena.imejiDataSet.commit();
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
