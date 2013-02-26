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
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

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
    private static AtomicInteger counter = new AtomicInteger();
    private static Random rand = new Random();
    /**
     * When this value is reached, initialize the counter to 0. Since id use timestamp, the id will still be unique as
     * the timestamp will have changed in the meantime
     */
    private static final int COUNTER_MAXIMUM_VALUE = 100000;
    /**
     * The counter identifier is composed with a first random part, to avoid easy identifier guess
     */
    private static final int COUNTER_PREFIX_RANGE = 1000;

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
        else if ("random".equals(method))
        {
            return newRandomId();
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
        int value = counter.getAndIncrement();
        if (value > COUNTER_MAXIMUM_VALUE)
        {
            // Set to the minimum value
            counter.set(0);
        }
        return Integer.toString(rand.nextInt(COUNTER_PREFIX_RANGE), Character.MAX_RADIX) + "-"
                + Long.toString(System.currentTimeMillis(), Character.MAX_RADIX) + "-"
                + Long.toString(value, Character.MAX_RADIX);
    }

    /**
     * Create a random id. No assurance of uniqueness, even if probability is small. Generated id are smaller...
     * 
     * @return
     */
    public static String newRandomId()
    {
        return Long.toString(rand.nextLong(), Character.MAX_RADIX);
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
