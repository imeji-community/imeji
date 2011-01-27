package de.mpg.jena.security;

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
	public boolean check(OperationsType op, User user, Object object)
	{
		Operations operation = instantiateOperation(object);
		
		switch (op) 
		{
			case CREATE: return operation.create(user, object);
			case READ: return operation.read(user, object);
			case UPDATE: return operation.update(user, object);
			case DELETE: return operation.delete(user, object);
		}
		
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
