package de.mpg.jena.concurrency.locks;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;


public class LocksSurveyor extends Thread
{
	private static Logger logger = Logger.getLogger(LocksSurveyor.class);
	private boolean signal = false;
	
	
	public void run() 
	{
		logger.info("Lock Surveyor started.");
		Locks.init();
		while (!signal) 
		{
			try
			{
				List<Lock> list = new ArrayList<Lock>(Locks.getExpiredLocks());
				if (!Locks.getExpiredLocks().isEmpty())
				{
					logger.info("Unlocking dead locks...");
					for (Lock l :Locks.getExpiredLocks())
					{
						list.add(l);
					}
					for(Lock l : list)
					{
						logger.info("on " + l.getUri() + " by " + l.getEmail());
						Locks.unLock(l);
					}
				}
				Thread.sleep(10000);
			}
			catch (NegativeArraySizeException e) 
			{
				Locks.init();
			}
			catch (Exception e) 
			{
				logger.warn("Locks Surveyor encounterd a problem: " + e.getMessage() + " " + e.getCause());
				e.printStackTrace();
			}
		}
		logger.warn("Lock Surveyor stopped. It should not occurs if application is still runnung!");
	}
	
	
	public void terminate()
    {
        logger.warn("Locks surveyor signaled to terminate.");
        signal = true;
    }
}
