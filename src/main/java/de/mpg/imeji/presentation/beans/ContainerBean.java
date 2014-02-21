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

import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.vo.Container;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.album.AlbumBean;
import de.mpg.imeji.presentation.collection.CollectionBean;

/**
 * Super Java Bean for containers bean {@link AlbumBean} and {@link CollectionBean}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public abstract class ContainerBean
{
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
     * Return the {@link User} having uploaded the file for this item
     * 
     * @return
     * @throws Exception
     */
    public User getCreator() throws Exception
    {
        User user = null;
        UserController uc = new UserController();
        user = uc.retrieve(getContainer().getCreatedBy());
        return user;
    }
}
