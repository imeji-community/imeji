package de.mpg.j2j.transaction;

import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.Jena;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.tdb.TDBFactory;

import de.mpg.imeji.logic.Imeji;

/**
 * Run a {@link Transaction} in a new {@link Thread}. A new {@link Dataset} is created for this thread <br/>
 * Necessary to follow the {@link Jena} per {@link Thread} Readers–writer lock
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class ThreadedTransaction implements Callable<Integer>
{
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

    /*
     * (non-Javadoc)
     * @see java.util.concurrent.Callable#call()
     */
    @Override
    public Integer call() throws Exception
    {
        transaction.start(TDBFactory.createDataset(Imeji.tdbPath));
        return 1;
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
