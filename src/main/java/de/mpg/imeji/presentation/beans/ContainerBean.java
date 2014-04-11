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
package de.mpg.imeji.presentation.beans;

import java.util.List;

import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Container;
import de.mpg.imeji.logic.vo.Organization;
import de.mpg.imeji.logic.vo.Person;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.album.AlbumBean;
import de.mpg.imeji.presentation.collection.CollectionBean;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.ImejiFactory;

/**
 * Super Java Bean for containers bean {@link AlbumBean} and {@link CollectionBean}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public abstract class ContainerBean
{
    private int authorPosition;
    private int organizationPosition;

    /**
     * Types of containers
     * 
     * @author saquet (initial creation)
     * @author $Author$ (last modification)
     * @version $Revision$ $LastChangedDate$
     */
    public enum CONTAINER_TYPE
    {
        COLLECTION, ALBUM;
    }

    /**
     * return the {@link CONTAINER_TYPE} of the current bean
     * 
     * @return
     */
    public abstract String getType();

    /**
     * Return the container
     * 
     * @return
     */
    public abstract Container getContainer();

    /**
     * Return the String used for redirection
     * 
     * @return
     */
    protected abstract String getNavigationString();

    /**
     * Return the bundle of the message when not orga is set
     * 
     * @return
     */
    protected abstract String getErrorMessageNoAuthor();

    /**
     * Return the {@link User} having uploaded the file for this item
     * 
     * @return
     * @throws Exception
     */
    public User getCreator() throws Exception
    {
        User user = null;
        UserController uc = new UserController(null);;
        user = uc.retrieve(getContainer().getCreatedBy());
        return user;
    }

    /**
     * Add a new author to the {@link CollectionImeji}
     * 
     * @param authorPosition
     * @return
     */
    public String addAuthor(int authorPosition)
    {
        List<Person> c = (List<Person>)getContainer().getMetadata().getPersons();
        Person p = ImejiFactory.newPerson();
        p.setPos(authorPosition + 1);
        c.add(authorPosition + 1, p);
        return "";
    }

    /**
     * Remove an author of the {@link CollectionImeji}
     * 
     * @return
     */
    public String removeAuthor(int authorPosition)
    {
        List<Person> c = (List<Person>)getContainer().getMetadata().getPersons();
        if (c.size() > 1)
            c.remove(authorPosition);
        else
            BeanHelper.error(getErrorMessageNoAuthor());
        return "";
    }

    /**
     * Add an organization to an author of the {@link CollectionImeji}
     * 
     * @param authorPosition
     * @param organizationPosition
     * @return
     */
    public String addOrganization(int authorPosition, int organizationPosition)
    {
        List<Person> persons = (List<Person>)getContainer().getMetadata().getPersons();
        List<Organization> orgs = (List<Organization>)persons.get(authorPosition).getOrganizations();
        Organization o = ImejiFactory.newOrganization();
        o.setPos(organizationPosition + 1);
        orgs.add(organizationPosition + 1, o);
        return "";
    }

    /**
     * Remove an organization to an author of the {@link CollectionImeji}
     * 
     * @return
     */
    public String removeOrganization(int authorPosition, int organizationPosition)
    {
        List<Person> persons = (List<Person>)getContainer().getMetadata().getPersons();
        List<Organization> orgs = (List<Organization>)persons.get(authorPosition).getOrganizations();
        if (orgs.size() > 1)
            orgs.remove(organizationPosition);
        else
            BeanHelper.error(((SessionBean)BeanHelper.getSessionBean(SessionBean.class))
                    .getMessage("error_author_need_one_organization"));
        return "";
    }

    /**
     * getter
     * 
     * @return
     */
    public int getAuthorPosition()
    {
        return authorPosition;
    }

    /**
     * setter
     * 
     * @param pos
     */
    public void setAuthorPosition(int pos)
    {
        this.authorPosition = pos;
    }

    /**
     * @return the collectionPosition
     */
    public int getOrganizationPosition()
    {
        return organizationPosition;
    }

    /**
     * @param collectionPosition the collectionPosition to set
     */
    public void setOrganizationPosition(int organizationPosition)
    {
        this.organizationPosition = organizationPosition;
    }
}
