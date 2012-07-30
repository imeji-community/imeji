/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.imeji.logic.security.operations;

import de.mpg.imeji.logic.security.Authorization;
import de.mpg.imeji.logic.security.Operations;
import de.mpg.imeji.logic.vo.Container;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.logic.vo.Grant.GrantType;
import de.mpg.imeji.logic.vo.Properties.Status;

public class OperationsProfile implements Operations
{
	private Authorization auth = new Authorization();
	
	public boolean create(User user, Object object) 
	{
		return true;
	}

	public boolean read(User user, Object object) {
		return ( Status.RELEASED.equals(((MetadataProfile) object).getStatus())
				|| Status.WITHDRAWN.equals(((MetadataProfile) object).getStatus())
				||auth.is(GrantType.PROFILE_VIEWER, user, ((MetadataProfile) object).getId())
				||auth.is(GrantType.PROFILE_ADMIN, user, ((MetadataProfile) object).getId())
				|| auth.is(GrantType.PROFILE_EDITOR, user, ((MetadataProfile) object).getId()));
	}

	public boolean update(User user, Object object) {
		return (user != null
				&& 	(	auth.is(GrantType.PROFILE_ADMIN, user, ((MetadataProfile) object).getId())
						|| auth.is(GrantType.PROFILE_EDITOR, user, ((MetadataProfile) object).getId()))
					);
	}

	public boolean delete(User user, Object object) {
		return (auth.is(GrantType.PROFILE_ADMIN, user, ((MetadataProfile) object).getId()));
	}
}
