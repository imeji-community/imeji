package de.mpg.j2j.controler;

import java.net.URI;

import com.hp.hpl.jena.rdf.model.Model;

import de.mpg.imeji.logic.ImejiBean2RDF;
import de.mpg.imeji.logic.ImejiJena;
import de.mpg.imeji.logic.ImejiRDF2Bean;
import de.mpg.j2j.exceptions.AlreadyExistsException;
import de.mpg.j2j.exceptions.NotFoundException;
import de.mpg.j2j.helper.J2JHelper;
import de.mpg.j2j.persistence.Java2Jena;
import de.mpg.j2j.persistence.Jena2Java;

/**
 * Controller for {@link RDFResource} Attention: Non transactional!!!! Don't use directly, use {@link ImejiBean2RDF} of
 * {@link ImejiRDF2Bean} instead
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class ResourceController
{
    private Model model = null;
    private Java2Jena java2rdf;
    private Jena2Java rdf2Java;

    public ResourceController(String modelURI)
    {
        if (modelURI != null)
        {
            model = ImejiJena.imejiDataSet.getNamedModel(modelURI);
        }
        else
        {
            model = ImejiJena.imejiDataSet.getDefaultModel();
        }
        this.java2rdf = new Java2Jena(model);
        this.rdf2Java = new Jena2Java(model);
    }

    /**
     * Create into Jena
     * 
     * @throws AlreadyExistsException
     * @throws InterruptedException
     */
    public void create(Object o) throws AlreadyExistsException
    {
        if (java2rdf.exists(o))
        {
            throw new AlreadyExistsException("Error creating resource " + J2JHelper.getId(o)
                    + ". Resource already exists! ");
        }
        java2rdf.write(o);
    }

    /**
     * Read the uri and write it into the {@link RDFResource}
     * 
     * @param uri
     * @param javaObject
     * @return
     * @throws NotFoundException
     */
    public Object read(URI uri, Object o) throws NotFoundException
    {
        J2JHelper.setId(o, uri);
        return read(o);
    }

    public Object read(Object o) throws NotFoundException
    {
        if (!java2rdf.exists(o))
        {
            throw new NotFoundException("Resource " + J2JHelper.getId(o) + " not found!");
        }
        o = rdf2Java.loadResource(o);
        return o;
    }

    /**
     * Update (remove and create) the complete {@link RDFResource}
     * 
     * @param o
     * @throws NotFoundException
     */
    public void update(Object o) throws NotFoundException
    {
        if (!java2rdf.exists(o))
        {
            throw new NotFoundException("Error updating resource " + J2JHelper.getId(o)
                    + ". Resource doesn't exists in model " + model.toString());
        }
        java2rdf.update(o);
    }

    /**
     * Delete a {@link RDFResource}
     * 
     * @param o
     * @throws NotFoundException
     */
    public void delete(Object o) throws NotFoundException
    {
        if (!java2rdf.exists(o))
        {
            throw new NotFoundException("Error deleting resource " + J2JHelper.getId(o) + ". Resource doesn't exists! ");
        }
        java2rdf.remove(o);
    }
}
