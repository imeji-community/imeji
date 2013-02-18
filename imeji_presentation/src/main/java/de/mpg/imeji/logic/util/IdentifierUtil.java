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
package de.mpg.imeji.logic.util;

import java.net.URI;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import de.mpg.imeji.presentation.util.PropertyReader;

/**
 * Provides A utility Method to create identifers in imeji<
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class IdentifierUtil
{
    private static String method;
    private static AtomicLong counter = null;

    /**
     * Initialize the static value for the identifier method
     */
    public IdentifierUtil()
    {
        try
        {
            method = PropertyReader.getProperty("imeji.identifier.method");
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Initialize the counter
     * 
     * @param initial
     */
    public static void initCounter(int initial)
    {
        counter = new AtomicLong(initial);
    }

    /**
     * Return an identifier according to the method set in the properties
     * 
     * @return
     */
    public static String newId()
    {
        if ("universal".equals(method))
        {
            return newUniversalUniqueId();
        }
        else
        {
            return newLocalUniqueId();
        }
    }

    /**
     * Return an {@link URI} according to the identifier creation method
     * 
     * @param c
     * @return
     */
    public static URI newURI(Class<?> c)
    {
        return ObjectHelper.getURI(c, newId());
    }

    /**
     * Create a new identifier unique for the local instance of imeji
     * 
     * @return
     */
    public static String newLocalUniqueId()
    {
        return Long.toHexString(counter.incrementAndGet());
    }

    /**
     * Create a {@link UUID}
     * 
     * @return
     */
    public static String newUniversalUniqueId()
    {
        return UUID.randomUUID().toString();
    }
}
