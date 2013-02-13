/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.security.operations;

import de.mpg.imeji.logic.security.Authorization;
import de.mpg.imeji.logic.security.Operations;
import de.mpg.imeji.logic.vo.Grant.GrantType;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Properties.Status;
import de.mpg.imeji.logic.vo.User;

/**
 * {@link Operations} for {@link MetadataProfile}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class OperationsProfile implements Operations
{
    private Authorization auth = new Authorization();

    /**
     * Create {@link MetadataProfile}<br/>
     * Allowed for:<br/>
     * - all
     */
    @Override
    public boolean create(User user, Object object)
    {
        return true;
    }

    /**
     * Read {@link MetadataProfile}<br/>
     * Allowed for:<br/>
     * - Profile is Released<br/>
     * - Profile is withdrawn<br/>
     * - Profile Admin<br/>
     * - Profile Editor<br/>
     * - Profile Viewer
     */
    @Override
    public boolean read(User user, Object object)
    {
        return (Status.RELEASED.equals(((MetadataProfile)object).getStatus())
                || Status.WITHDRAWN.equals(((MetadataProfile)object).getStatus())
                || auth.is(GrantType.PROFILE_VIEWER, user, ((MetadataProfile)object).getId())
                || auth.is(GrantType.PROFILE_ADMIN, user, ((MetadataProfile)object).getId()) || auth.is(
                GrantType.PROFILE_EDITOR, user, ((MetadataProfile)object).getId()));
    }

    /**
     * Update {@link MetadataProfile}<br/>
     * Allowed for:<br/>
     * - Profile Admin<br/>
     * - Profile Editor
     */
    @Override
    public boolean update(User user, Object object)
    {
        return (user != null && (auth.is(GrantType.PROFILE_ADMIN, user, ((MetadataProfile)object).getId()) || auth.is(
                GrantType.PROFILE_EDITOR, user, ((MetadataProfile)object).getId())));
    }

    /**
     * Delete {@link MetadataProfile}<br/>
     * Allowed for:<br/>
     * - Profile Admin
     */
    @Override
    public boolean delete(User user, Object object)
    {
        return (auth.is(GrantType.PROFILE_ADMIN, user, ((MetadataProfile)object).getId()));
    }
}
