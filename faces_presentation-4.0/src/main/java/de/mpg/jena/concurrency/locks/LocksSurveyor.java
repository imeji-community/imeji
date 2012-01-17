/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.jena.concurrency.locks;

import java.lang.management.GarbageCollectorMXBean;
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
				synchronized (logger) 
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
			}
			catch (NegativeArraySizeException e) 
			{
				Locks.init();
				logger.error("Locks have been reinitialized. All locks have been released: " ,e);
			}
			catch (Exception e) 
			{
				logger.error("Locks Surveyor encountered a problem: " ,e);
			}
		}
		logger.error("Lock Surveyor stopped. It should not occurs if application is still running!");
	}
	
	
	public void terminate()
    {
        logger.warn("Locks surveyor signaled to terminate.");
        signal = true;
    }
}
