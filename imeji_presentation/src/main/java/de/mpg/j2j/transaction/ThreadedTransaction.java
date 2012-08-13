package de.mpg.j2j.transaction;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.tdb.TDBFactory;

import de.mpg.imeji.logic.ImejiJena;

/**
 * Run a {@link Transaction} in a dedicated {@link Thread}. A new {@link Dataset} is created for this thread.
 *
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class ThreadedTransaction extends Thread
{
    private boolean running = false;
    private Transaction transaction;
    protected static Logger logger = Logger.getLogger(TransactionOld.class);

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
        transaction.start(TDBFactory.createDataset(ImejiJena.tdbPath));
        running = false;
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
        transaction.throwException();
    }
}
