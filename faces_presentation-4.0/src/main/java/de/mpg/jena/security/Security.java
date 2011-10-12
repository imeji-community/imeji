package de.mpg.jena.security;

import org.apache.log4j.Logger;

import de.mpg.jena.security.Operations.OperationsType;
import de.mpg.jena.security.operations.OperationsContainer;
import de.mpg.jena.security.operations.OperationsImage;
import de.mpg.jena.security.operations.OperationsProfile;
import de.mpg.jena.vo.Container;
import de.mpg.jena.vo.Image;
import de.mpg.jena.vo.MetadataProfile;
import de.mpg.jena.vo.User;

public class Security
{
	private static Logger logger = Logger.getLogger(Security.class);

	public boolean check(OperationsType op, User user, Object object)
	{
		try 
		{
			if (isSysAdmin(user)) return true;

			if (object == null) return true;

			// TODO no rules for users defined so far
			if(object instanceof User) return true;

			Operations operation = instantiateOperation(object);

			switch (op) 
			{
			case CREATE: return operation.create(user, object);
			case READ: return operation.read(user, object);
			case UPDATE: return operation.update(user, object);
			case DELETE: return operation.delete(user, object);
			}
		} 
		catch (Exception e) 
		{
			logger.error("Error in security", e);
		}
		return false;
	}

	public boolean isSysAdmin(User user)
	{
		Authorization auth = new Authorization();
		if (user != null) return auth.isSysAdmin(user);
		return false;
	}

	private Operations instantiateOperation(Object object)
	{
		if (object instanceof Image)
		{
			return new OperationsImage();
		}
		else if (object instanceof Container) 
		{
			return new OperationsContainer();
		}
		else if (object instanceof MetadataProfile)
		{
			return new OperationsProfile();
		}
		return null;
	}
}
