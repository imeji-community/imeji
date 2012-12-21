package de.mpg.j2j.transaction;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;

import de.mpg.imeji.logic.ImejiJena;

/**
 * Transaction for Jena operations.
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public abstract class Transaction
{
    private String modelURI;
    private boolean isException;
    private Exception exception;
    private static Logger logger = Logger.getLogger(Transaction.class);

    public Transaction(String modelURI)
    {
        this.modelURI = modelURI;
    }

    public void start()
    {
        start(ImejiJena.imejiDataSet);
    }

    public void start(Dataset dataset)
    {
        try
        {
            dataset.begin(getLockType());
            execute(dataset);
            dataset.commit();
        }
        catch (Exception e)
        {
        	dataset.abort();
            isException = true;
            exception = e;
            logger.error("Exception in a transaction: has been aborted");
        }
        finally
        {
            dataset.end();
        }
    }

    protected abstract void execute(Dataset ds) throws Exception;

    protected abstract ReadWrite getLockType();

    protected Model getModel(Dataset dataset)
    {
        if (modelURI != null)
        {
            return dataset.getNamedModel(modelURI);
        }
        return null;
    }

    /**
     * If the run Method caught an Exception, throw this exception
     * 
     * @throws Exception
     */
    public void throwException() throws Exception
    {
        if (isException)
        {
            throw exception;
        }
    }
}
