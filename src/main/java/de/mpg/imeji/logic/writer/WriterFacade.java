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
package de.mpg.imeji.logic.writer;

import java.net.URI;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.auth.Authorization;
import de.mpg.imeji.logic.auth.exception.NotAllowedError;
import de.mpg.imeji.logic.auth.util.AuthUtil;
import de.mpg.imeji.logic.vo.Container;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.logic.vo.UserGroup;
import de.mpg.imeji.logic.vo.Grant.GrantType;

/**
 * Facade implementing Writer {@link Authorization}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class WriterFacade implements Writer
{
    private static final Logger logger = Logger.getLogger(WriterFacade.class);
    private Writer writer;

    /**
     * Constructor for one model
     */
    public WriterFacade(String modelURI)
    {
        this.writer = WriterFactory.create(modelURI);
    }

    /*
     * (non-Javadoc)
     * @see de.mpg.imeji.logic.writer.Writer#create(java.util.List, de.mpg.imeji.logic.vo.User)
     */
    @Override
    public void create(List<Object> objects, User user) throws Exception
    {
        checkSecurity(objects, user, GrantType.CREATE);
        writer.create(objects, user);
    }

    /*
     * (non-Javadoc)
     * @see de.mpg.imeji.logic.writer.Writer#delete(java.util.List, de.mpg.imeji.logic.vo.User)
     */
    @Override
    public void delete(List<Object> objects, User user) throws Exception
    {
        checkSecurity(objects, user, GrantType.DELETE);
        writer.delete(objects, user);
    }

    /*
     * (non-Javadoc)
     * @see de.mpg.imeji.logic.writer.Writer#update(java.util.List, de.mpg.imeji.logic.vo.User)
     */
    @Override
    public void update(List<Object> objects, User user) throws Exception
    {
        checkSecurity(objects, user, GrantType.UPDATE);
        writer.update(objects, user);
    }

    /*
     * (non-Javadoc)
     * @see de.mpg.imeji.logic.writer.Writer#updateLazy(java.util.List, de.mpg.imeji.logic.vo.User)
     */
    @Override
    public void updateLazy(List<Object> objects, User user) throws Exception
    {
        checkSecurity(objects, user, GrantType.UPDATE);
        writer.updateLazy(objects, user);
    }

    /**
     * Check {@link Security} for WRITE operations
     * 
     * @param list
     * @param user
     * @param opType
     * @throws NotAllowedError
     */
    private void checkSecurity(List<Object> list, User user, GrantType gt) throws NotAllowedError
    {
        for (Object o : list)
        {
            switch (gt)
            {
                case CREATE:
                    throwAuthorizationException(AuthUtil.staticAuth().createNew(user, o), user.getEmail()
                            + " not allowed to create " + extractID(o));
                    break;
                case DELETE:
                    throwAuthorizationException(AuthUtil.staticAuth().delete(user, o), user.getEmail()
                            + " not allowed to delete " + extractID(o));
                    break;
                case UPDATE:
                    throwAuthorizationException(AuthUtil.staticAuth().update(user, o), user.getEmail()
                            + " not allowed to update " + extractID(o));
                    break;
                default:
                    throw new RuntimeException("Wrong Grant type: " + gt);
            }
        }
    }

    /**
     * Extract the id (as {@link URI}) of an imeji {@link Object},
     * 
     * @param o
     * @return
     */
    public static URI extractID(Object o)
    {
        if (o instanceof Item)
        {
            return ((Item)o).getId();
        }
        else if (o instanceof Container)
        {
            return ((Container)o).getId();
        }
        else if (o instanceof MetadataProfile)
        {
            return ((MetadataProfile)o).getId();
        }
        else if (o instanceof User)
        {
            return URI.create(((User)o).getEmail());
        }
        else if (o instanceof UserGroup)
        {
            return ((UserGroup)o).getId();
        }
        return null;
    }

    /**
     * If false, throw a {@link NotAllowedError}
     * 
     * @param b
     * @param message
     * @throws NotAllowedError
     */
    private void throwAuthorizationException(boolean allowed, String message) throws NotAllowedError
    {
        if (!allowed)
        {
            NotAllowedError e = new NotAllowedError(message);
            logger.error(e);
            throw e;
        }
    }

    /**
     * Transform a single {@link Object} into a {@link List} with one {@link Object}
     * 
     * @param o
     * @return
     */
    public static List<Object> toList(Object o)
    {
        List<Object> list = new ArrayList<Object>();
        list.add(o);
        return list;
    }
}
