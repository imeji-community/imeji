package de.mpg.imeji.presentation.user;

import java.util.ArrayList;
import java.util.List;

import de.escidoc.core.resources.aa.useraccount.Grants;
import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.auth.authorization.AuthorizationPredefinedRoles;
import de.mpg.imeji.logic.controller.GrantController;
import de.mpg.imeji.logic.vo.Grant;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.logic.vo.UserGroup;
import de.mpg.imeji.presentation.user.ShareBean.ShareType;

import org.apache.log4j.Logger;

public class SharedHistory
{
    private static Logger logger = Logger.getLogger(SharedHistory.class);
    private User user;
    private UserGroup group;
    private String containerUri;
    private String profileUri;
    private boolean isCollection;
    private List<String> sharedType = new ArrayList<String>();

    /**
     * Constructor with a {@link User}
     * 
     * @param user
     * @param isCollection
     * @param containerUri
     * @param profileUri
     * @param sharedType
     */
    public SharedHistory(User user, boolean isCollection, String containerUri, String profileUri,
            List<String> sharedType)
    {
        this.user = user;
        this.isCollection = isCollection;
        this.containerUri = containerUri;
        this.profileUri = profileUri;
        this.sharedType = sharedType;
    }

    /**
     * Constructor with a {@link UserGroup}
     * 
     * @param group
     * @param isCollection
     * @param containerUri
     * @param profileUri
     * @param sharedType
     */
    public SharedHistory(UserGroup group, boolean isCollection, String containerUri, String profileUri,
            List<String> sharedType)
    {
        this.setGroup(group);
        this.isCollection = isCollection;
        this.containerUri = containerUri;
        this.profileUri = profileUri;
        this.sharedType = sharedType;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public List<String> getSharedType()
    {
        return sharedType;
    }

    public boolean isCollection()
    {
        return isCollection;
    }

    public void setCollection(boolean isCollection)
    {
        this.isCollection = isCollection;
    }

    public void setSharedType(List<String> sharedType)
    {
        if (isCollection)
        {
            if (sharedType.contains("ADMIN"))
            {
                sharedType.clear();
                sharedType.add(ShareType.READ.toString());
                sharedType.add(ShareType.UPLOAD.toString());
                sharedType.add(ShareType.EDIT.toString());
                sharedType.add(ShareType.DELETE.toString());
                sharedType.add(ShareType.EDIT_COLLECTION.toString());
                sharedType.add(ShareType.EDIT_PROFILE.toString());
                sharedType.add(ShareType.ADMIN.toString());
            }
            else if (sharedType.size() > 1 && !sharedType.contains("READ"))
                sharedType.add(ShareType.READ.toString());
        }
        else
        {
            if (sharedType.contains("ADMIN"))
            {
                sharedType.clear();
                sharedType.add(ShareType.READ.toString());
                sharedType.add(ShareType.ADD.toString());
                sharedType.add(ShareType.DELETE.toString());
                sharedType.add(ShareType.EDIT_ALBUM.toString());
                sharedType.add(ShareType.ADMIN.toString());
            }
            else if (sharedType.size() > 1 && !sharedType.contains("READ"))
                sharedType.add(ShareType.READ.toString());
        }
        this.sharedType = sharedType;
    }

    /**
     * Update {@link Grants} the {@link SharedHistory} according to the new roles
     * 
     * @return
     */
    public String update()
    {
        GrantController gc = new GrantController();
        try
        {
            // Remove all Grant for the current container
            if (user != null)
                gc.removeGrants(getUser(), AuthorizationPredefinedRoles.all(containerUri, profileUri), user);
            else if (group != null)
                gc.removeGrants(group, AuthorizationPredefinedRoles.all(containerUri, profileUri), Imeji.adminUser);
            // Find all new Grants according to the shareType
            List<Grant> newGrants = new ArrayList<Grant>();
            for (String g : sharedType)
            {
                switch (g)
                {
                    case "READ":
                        newGrants.addAll(AuthorizationPredefinedRoles.read(containerUri, profileUri));
                        break;
                    case "UPLOAD":
                        newGrants.addAll(AuthorizationPredefinedRoles.upload(containerUri));
                        break;
                    case "EDIT":
                        newGrants.addAll(AuthorizationPredefinedRoles.edit(containerUri));
                        break;
                    case "DELETE":
                        newGrants.addAll(AuthorizationPredefinedRoles.delete(containerUri));
                        break;
                    case "EDIT_COLLECTION":
                        newGrants.addAll(AuthorizationPredefinedRoles.editContainer(containerUri));
                        break;
                    case "EDIT_PROFILE":
                        newGrants.addAll(AuthorizationPredefinedRoles.editProfile(profileUri));
                        break;
                    case "ADMIN":
                        newGrants.addAll(AuthorizationPredefinedRoles.admin(containerUri, profileUri));
                        break;
                }
            }
            // Save the new Grants
            if (user != null)
                gc.addGrants(user, newGrants, user);
            else if (group != null)
                gc.addGrants(group, newGrants,  Imeji.adminUser);
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new RuntimeException(e);
        }
        return "pretty:shareCollection";
    }

    /**
     * @return the group
     */
    public UserGroup getGroup()
    {
        return group;
    }

    /**
     * @param group the group to set
     */
    public void setGroup(UserGroup group)
    {
        this.group = group;
    }
}
