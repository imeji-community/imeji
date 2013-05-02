/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.security.operations;

import de.mpg.imeji.logic.security.Authorization;
import de.mpg.imeji.logic.security.Operations;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.logic.vo.Item.Visibility;
import de.mpg.imeji.logic.vo.Properties.Status;

/***
 * {@link Operations} for {@link Item}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class OperationsImage implements Operations
{
    private Authorization auth = new Authorization();

    /**
     * Create {@link Item} <br/>
     * Allowed for : <br/>
     * - Picture Editor <br/>
     * - Collection Editor <br/>
     * - Collection Administrator
     */
    @Override
    public boolean create(User user, Object object)
    {
        return (auth.isPictureEditor(user, (Item)object) || auth.isContainerEditor(user, ((Item)object)) || auth
                .isContainerAdmin(user, ((Item)object)));
    }

    /**
     * Read {@link Item} <br/>
     * Allowed for : <br/>
     * - everybody if image is public <br/>
     * - Collection Viewer <br/>
     * - Picture Editor <br/>
     * - Collection Editor <br/>
     * - Collection Administrator
     */
    @Override
    public boolean read(User user, Object object)
    {
        return (Visibility.PUBLIC.equals(((Item)object).getVisibility()) || auth.isViewerFor(user, (Item)object)
                || auth.isPictureEditor(user, (Item)object) || auth.isContainerEditor(user, ((Item)object)) || auth
                    .isContainerAdmin(user, ((Item)object)));
    }

    /**
     * Update {@link Item} <br/>
     * Allowed for : <br/>
     * - Picture Editor <br/>
     * - Collection Editor <br/>
     * - Collection Administrator
     */
    @Override
    public boolean update(User user, Object object)
    {
        return ( // !Status.WITHDRAWN.equals(((Image)object).getStatus()) &&
        (auth.isPictureEditor(user, (Item)object) || auth.isContainerEditor(user, ((Item)object)) || auth
                .isContainerAdmin(user, ((Item)object))));
    }

    /**
     * Delete {@link Item} (Not specified!!!!): <br/>
     * - Nobody
     */
    @Override
    public boolean delete(User user, Object object)
    {
        return ((auth.isPictureEditor(user, (Item)object) || auth.isContainerEditor(user, ((Item)object)) || auth
                .isContainerAdmin(user, ((Item)object))) && Status.PENDING.equals(((Item)object).getStatus()));
    }

    /**
     * Has privileged view role (this means, is not simple viewer, and check even if the item is public). Used to check
     * the visibility of the restricted metadata
     * 
     * @param user
     * @param object
     * @return
     */
    public boolean readRestricted(User user, Object object)
    {
        return  auth.isPictureEditor(user, (Item)object)
                || auth.isContainerEditor(user, ((Item)object)) || auth.isContainerAdmin(user, ((Item)object));
    }
}
