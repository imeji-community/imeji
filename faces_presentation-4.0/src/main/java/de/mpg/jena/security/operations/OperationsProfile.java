package de.mpg.jena.security.operations;

import de.mpg.jena.security.Authorization;
import de.mpg.jena.security.Operations;
import de.mpg.jena.vo.MetadataProfile;
import de.mpg.jena.vo.Properties.Status;
import de.mpg.jena.vo.User;
import de.mpg.jena.vo.Grant.GrantType;

public class OperationsProfile implements Operations
{
	private Authorization auth = new Authorization();
	
	public boolean create(User user, Object object) 
	{
		return true;
	}

	public boolean read(User user, Object object) {
		return (auth.is(GrantType.PROFILE_ADMIN, user, ((MetadataProfile) object).getId())
				|| auth.is(GrantType.PROFILE_EDITOR, user, ((MetadataProfile) object).getId())
				|| Status.RELEASED.equals(((MetadataProfile) object).getProperties().getStatus()));
	}

	public boolean update(User user, Object object) {
		return (auth.is(GrantType.PROFILE_ADMIN, user, ((MetadataProfile) object).getId())
				|| auth.is(GrantType.PROFILE_EDITOR, user, ((MetadataProfile) object).getId()));
	}

	public boolean delete(User user, Object object) {
		return (auth.is(GrantType.PROFILE_ADMIN, user, ((MetadataProfile) object).getId()));
	}
}
