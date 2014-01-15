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
package de.mpg.imeji.logic.auth.authorization;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import de.mpg.imeji.logic.vo.Album;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Grant;
import de.mpg.imeji.logic.vo.Grant.GrantType;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.beans.PropertyBean;

/**
 * Defines the predefined roles (for instance the creator of collection) with a {@link List} of {@link Grant}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class AuthorizationPredefinedRoles
{
    /**
     * The default {@link User} role in imeji can create (collection/album) in imeji
     * 
     * @param uri
     * @return
     */
    public static List<Grant> defaultUser(String uri)
    {
        // Add the Grant to create a collection
        GrantType[] g = { GrantType.CREATE };
        List<Grant> l = toGrantList(g, PropertyBean.baseURI());
        l.addAll(restrictedUser(uri));
        return l;
    }

    /**
     * This user can not create a collection in imeji. He only has the {@link Grant} on his account
     * 
     * @param uri
     * @return
     */
    public static List<Grant> restrictedUser(String uri)
    {
        GrantType[] g = { GrantType.CREATE, GrantType.READ, GrantType.UPDATE, GrantType.DELETE, GrantType.ADMIN };
        return toGrantList(g, uri);
    }

    /**
     * Return the {@link Grant} of a {@link User} who is an imeji system administrator
     * 
     * @return
     */
    public static List<Grant> imejiAdministrator(String uri)
    {
        GrantType[] g = { GrantType.ADMIN };
        List<Grant> l = toGrantList(g, PropertyBean.baseURI());
        l.addAll(defaultUser(uri));
        return l;
    }

    /**
     * Return the {@link Grant} of a {@link User} who creates a {@link CollectionImeji}
     * 
     * @param uri
     * @return
     */
    public static List<Grant> collectionCreator(String collectionUri, String profileUri)
    {
        List<Grant> l = containerCreator(collectionUri);
        l.addAll(containerCreator(profileUri));
        return l;
    }

    /**
     * Return the {@link Grant} of a {@link User} who creates an {@link Album}
     * 
     * @param albumUri
     * @return
     */
    public static List<Grant> albumCreator(String albumUri)
    {
        return containerCreator(albumUri);
    }

    /**
     * Return the {@link List} of {@link Grant} needed to create a container
     * 
     * @param uri
     * @return
     */
    private static List<Grant> containerCreator(String uri)
    {
        GrantType[] g = { GrantType.CREATE, GrantType.READ, GrantType.UPDATE, GrantType.DELETE, GrantType.ADMIN,
                GrantType.READ_CONTENT, GrantType.UPDATE_CONTENT, GrantType.DELETE_CONTENT };
        return toGrantList(g, uri);
    }

    /**
     * Transform an array of {@link GrantType} to a {@link List} of {@link Grant} for the given uri
     * 
     * @param array
     * @param uri
     * @return
     */
    private static List<Grant> toGrantList(GrantType[] array, String uri)
    {
        List<Grant> l = new ArrayList<Grant>();
        for (GrantType gt : array)
        {
            l.add(new Grant(gt, URI.create(uri)));
        }
        return l;
    }
}
