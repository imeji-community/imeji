/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.imeji.logic.security;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.security.Operations.OperationsType;
import de.mpg.imeji.logic.security.operations.OperationsContainer;
import de.mpg.imeji.logic.security.operations.OperationsImage;
import de.mpg.imeji.logic.security.operations.OperationsProfile;
import de.mpg.imeji.logic.vo.Container;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.User;

public class Security
{
	private static Logger logger = Logger.getLogger(Security.class);

	public boolean check(OperationsType op, User user, Object object)
	{
		try 
		{
			if (isSysAdmin(user)) return true;

			if (object == null) return false;

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
		if (object instanceof Item)
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
