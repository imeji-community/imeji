package de.mpg.j2j.transaction;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.tdb.TDBFactory;

import de.mpg.imeji.logic.ImejiJena;

public abstract class Transaction extends Thread
{
    private String modelURI = null;
    private boolean running = false;
    private Exception exception;
    private boolean isException = false;
    protected static Logger logger = Logger.getLogger(Transaction.class);

    public Transaction(String modelURI)
    {
        super();
        this.modelURI = modelURI;
    }

    @Override
    public synchronized void start()
    {
        running = true;
        logger.info("Start: " + this.getId() + " " + getLockType());
        super.start();
    }

    @Override
    public void run()
    {
        Dataset dataset = TDBFactory.createDataset(ImejiJena.tdbPath);
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
        }
        finally
        {
            dataset.end();
            running = false;
        }
    }

    public void waitForEnd()
    {
        while (running)
        {
            try
            {
                Thread.sleep(1);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
                running = false;
            }
        }
    }

    protected Model getModel(Dataset dataset)
    {
        if (modelURI != null)
        {
            return dataset.getNamedModel(modelURI);
        }
        else
        {
            return dataset.getDefaultModel();
        }
    }

    protected abstract void execute(Dataset ds) throws Exception;

    protected abstract ReadWrite getLockType();

    public void setRunning(boolean running)
    {
        this.running = running;
    }

    public boolean isRunning()
    {
        return running;
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
