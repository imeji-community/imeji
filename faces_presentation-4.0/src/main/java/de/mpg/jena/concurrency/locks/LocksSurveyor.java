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
		while (!signal) 
		{
			try
			{
				List<Lock> list = new ArrayList<Lock>(Locks.getExpiredLocks());
				if (!list.isEmpty())
				{
					logger.info("Unlocking dead locks...");
					for (int i=0; i < list.size(); i++)
					{
						logger.info("on " + list.get(i).getUri() + " by " +list.get(i).getEmail());
						Locks.unLock(list.get(i));
						list = new ArrayList<Lock>(Locks.getExpiredLocks());
					}
				}
			}
			catch (Exception e) 
			{
				logger.warn("Locks Surveyor encounterd a problem: " + e.getMessage());
			}
		}
		logger.warn("Lock Surveyor stopped. It should not occurs if application still runnung!");
	}
	
	
	public void terminate()
    {
        logger.warn("Locks surveyor signaled to terminate.");
        signal = true;
    }
}
