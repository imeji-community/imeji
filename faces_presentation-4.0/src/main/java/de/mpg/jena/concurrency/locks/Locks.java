package de.mpg.jena.concurrency.locks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;


public class Locks
{
	// Lock on User Event
	private static Map<String, Lock> userLocks; 
	// Locks on System event
	private static Map<String, Lock> sysLocks; 
	// Life time for a lock. After that limit, the lock is destroyed.
	private static final long LOCK_MAX_TIME = 900000;// 900s = 15min 
	
	private static Logger logger = Logger.getLogger(Locks.class);
	
	public static Map<String, Lock> getUserLocks()
	{
		if (userLocks == null) userLocks = new HashMap<String, Lock>();
		return userLocks;
	}
	
	public static Map<String, Lock> getSystemLocks()
	{
		if (sysLocks == null) sysLocks = new HashMap<String, Lock>();
		return sysLocks;
	}
	
	/**
	 * return true if the uri (i.e object) is locked for the user.
	 * 
	 * <br/> Check first if uri is locked by system.
	 * <br/> Then check if uri is locked by one other user.
	 * @param uri
	 * @param email
	 * @return
	 */
	public static boolean isLocked(String uri, String email)
	{
		Lock syslock = getSystemLocks().get(uri);
		if (syslock != null)
		{
			return true;
		}
		else
		{
			Lock userLock = getUserLocks().get(uri);
			if (userLock != null && email != null && !userLock.getEmail().equals(email)) 
			{
				return true;
			}
			return false;
		}
	}
	
	/**
	 * If lock doesn't already exist, then add a lock to:
	 * 
	 *  <br/> System locks if email is null in Lock
	 *  <br/> User locks if email is defined. 
	 * @param lock
	 */
	public static void lock(Lock lock)
	{
		if(!isLocked(lock.getUri(), lock.getEmail()))
		{
			if (lock.getEmail() == null)
			{
				logger.debug(lock.getUri()+ " locked by system");
				getSystemLocks().put(lock.getUri(), lock);
			}
			else 
			{
				logger.debug(lock.getUri()+ " locked by " + lock.getEmail());
				getUserLocks().put(lock.getUri(), lock);
			}
		}
	}
	
	/**
	 * Unlock the lock.
	 * @param lock
	 */
	public static void unLock(Lock lock)
	{
		if (lock.getEmail() == null && getSystemLocks().get(lock.getUri()) != null)
		{
			logger.debug(lock.getUri()+ " unlocked by system");
			getSystemLocks().remove(lock.getUri());
		}
		else if(lock.getEmail() != null && getUserLocks().get(lock.getUri()) != null)
		{
			logger.debug(lock.getUri()+ " unlocked by " + lock.getEmail());
			getUserLocks().remove(lock.getUri());
		}
	}
	
	/**
	 * Unlock all locks for one User
	 * @param email
	 */
	public static void unlockAll(String email)
	{
		List<Lock> toUnlock = new ArrayList<Lock>();
		if (!getUserLocks().isEmpty() && email != null)
		{
			for (Lock l :getUserLocks().values())
			{
				if (email.equals(l.getEmail())) toUnlock.add(l);
			}
		}
		for (Lock l : toUnlock) {unLock(l);}
	}
	
	/**
	 * Retreive a list of all locks which are expired
	 * @return
	 */
	public static List<Lock> getExpiredLocks()
	{
		List<Lock> list = new ArrayList<Lock>();
		if (!getUserLocks().isEmpty())
		{
			List<Lock> l1 = new ArrayList<Lock>(userLocks.values());
			for (Lock lock : l1)
			{
				long created = lock.getCreateTime();
				long current = System.currentTimeMillis();
				if (current > (LOCK_MAX_TIME + created))
				{
					list.add(lock);
				}
			}
		}
		if (!getSystemLocks().isEmpty())
		{
			List<Lock> l1 = new ArrayList<Lock>(sysLocks.values());
			for (Lock lock : l1)
			{
				long created = lock.getCreateTime();
				long current = System.currentTimeMillis();
				if (current > (LOCK_MAX_TIME + created))
				{
					list.add(lock);
				}
			}
		}
		return list;
	}
}
