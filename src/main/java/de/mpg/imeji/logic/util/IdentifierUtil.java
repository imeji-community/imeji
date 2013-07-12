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
 * Provides A utility Method to create identifers in imeji
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
     * Array of all possible {@link String} characters which are used to generate a random Id
     */
    private static final String[] RANDOM_ID_CHARSET = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l",
            "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G",
            "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "0", "1",
            "2", "3", "4", "5", "6", "7", "8", "9", "_", "" };
    /**
     * The size of the random id. Since the RANDOM_ID_CHARSET has 64 elements, to calculate the number of possible id,
     * do 64^RANDOM_ID_SIZE with: - 1: 6 bits - 10: 60 bits - 12: 72 bits - 15: 90bits
     */
    private static final int RANDOM_ID_SIZE = 16;

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
        else if ("counter".equals(method))
        {
            return newLocalUniqueId();
        }
        else
        {
            return newRandomId();
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
        String id = "";
        for (int i = 0; i < RANDOM_ID_SIZE; i++)
        {
            id += RANDOM_ID_CHARSET[rand.nextInt(RANDOM_ID_CHARSET.length)];
        }
        return id;
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
