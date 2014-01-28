/*
 *
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License"). You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE
 * or http://www.escidoc.de/license.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at license/ESCIDOC.LICENSE.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */
/*
 * Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
 * für wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 */
package de.mpg.imeji.presentation.user;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.controller.UserGroupController;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.logic.vo.UserGroup;
import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.util.BeanHelper;

/**
 * Bean to create a
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
@ManagedBean(name = "UserGroup")
@ViewScoped
public class UserGroupBean implements Serializable
{
    private static final long serialVersionUID = -6501626930686020874L;
    private UserGroup userGroup = new UserGroup();
    private Collection<User> users;
    @ManagedProperty(value = "#{SessionBean.user}")
    private User sessionUser;
    private static Logger logger = Logger.getLogger(UserGroupsBean.class);

    @PostConstruct
    public void init()
    {
        String groupId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id");
        if (groupId != null)
        {
            UserGroupController c = new UserGroupController();
            try
            {
                userGroup = c.read(groupId, sessionUser);
                setUsers(loadUsers(userGroup));
            }
            catch (Exception e)
            {
                BeanHelper.error("Error reading user group " + groupId);
                logger.error(e);
            }
        }
    }

    /**
     * Load the {@link User} of a {@link UserGroup}
     * 
     * @param subject
     * @param f
     * @param object
     * @param position
     * @return
     */
    public Collection<User> loadUsers(UserGroup group) throws Exception
    {
        Collection<User> users = new ArrayList<User>();
        UserController c = new UserController(sessionUser);
        for (URI uri : userGroup.getUsers())
        {
            users.add(c.retrieve(uri));
        }
        return users;
    }

    /**
     * Remove a {@link User} from a {@link UserGroup}
     * 
     * @param remove
     * @return
     * @throws IOException 
     */
    public void removeUserGromGroup(URI remove) throws IOException
    {
        userGroup.getUsers().remove(remove);
        save();
        Navigation nav = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
        FacesContext.getCurrentInstance().getExternalContext().redirect(nav.getApplicationUrl() + "usergroup?id=" + userGroup.getId());
    }

    /**
     * Create a new {@link UserGroup}
     */
    public String create()
    {
        UserGroupController c = new UserGroupController();
        try
        {
            c.create(userGroup, sessionUser);
        }
        catch (Exception e)
        {
            BeanHelper.error("Error creating user group");
        }
        return "pretty:userGroups";
    }

    /**
     * Update the current {@link UserGroup}
     */
    public String save()
    {
        UserGroupController c = new UserGroupController();
        try
        {
            c.update(userGroup, sessionUser);
        }
        catch (Exception e)
        {
            BeanHelper.error("Error updating user group");
        }
        return "pretty:userGroups";
    }

    /**
     * @return the userGroup
     */
    public UserGroup getUserGroup()
    {
        return userGroup;
    }

    /**
     * @param userGroup the userGroup to set
     */
    public void setUserGroup(UserGroup userGroup)
    {
        this.userGroup = userGroup;
    }

    /**
     * @return the sessionUser
     */
    public User getSessionUser()
    {
        return sessionUser;
    }

    /**
     * @param sessionUser the sessionUser to set
     */
    public void setSessionUser(User sessionUser)
    {
        this.sessionUser = sessionUser;
    }

    /**
     * @return the users
     */
    public Collection<User> getUsers()
    {
        return users;
    }

    /**
     * @param users the users to set
     */
    public void setUsers(Collection<User> users)
    {
        this.users = users;
    }
}
