package de.mpg.j2j.transaction;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.Jena;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.tdb.TDBFactory;

import de.mpg.imeji.logic.ImejiJena;

/**
 * Run a {@link Transaction} in a new {@link Thread}. A new {@link Dataset} is created for this thread <br/>
 * Necessary to follow the {@link Jena} per {@link Thread} Readers–writer lock
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class ThreadedTransaction extends Thread
{
    private boolean running = false;
    private Transaction transaction;
    protected static Logger logger = Logger.getLogger(ThreadedTransaction.class);

    /**
     * Construct a new {@link ThreadedTransaction} for one {@link Transaction}
     * 
     * @param transaction
     */
    public ThreadedTransaction(Transaction transaction)
    {
        this.transaction = transaction;
    }

    @Override
    public synchronized void start()
    {
        running = true;
        super.start();
    }

    @Override
    public void run()
    {
        try
        {
            transaction.start(TDBFactory.createDataset(ImejiJena.tdbPath));
        }
        finally
        {
            running = false;
        }
    }

    /**
     * Method waiting for the {@link Thread} to be finished. Used when synchronization needed
     */
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

    /**
     * setter
     * 
     * @param running
     */
    public void setRunning(boolean running)
    {
        this.running = running;
    }

    /**
     * getter
     * 
     * @return
     */
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
        transaction.throwException();
    }
}
