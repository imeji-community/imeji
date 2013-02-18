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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
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
    private static AtomicInteger counter = new AtomicInteger();
    private static Random rand = new Random();
    /**
     * When this value is reached, initialize the conter to 0. Since id use timestamp, the id will still be unique
     */
    private static final int COUNTER_MAXIMUM_VALUE = 1000000;

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

    // public static void main(String[] a)
    // {
    // String start = Long.toHexString(System.currentTimeMillis());
    // String current = start;
    // int count = 0;
    // while (start.equals(current))
    // {
    // newLocalUniqueId();
    // current = Long.toHexString(System.currentTimeMillis());
    // count++;
    // }
    // System.out.println(current + " - " + start + " time has changed after " + count);
    // counter = new AtomicInteger(0);
    // long t1 = System.currentTimeMillis();
    // List<String> l = new ArrayList<String>();
    // for (int i = 0; i < 20000; i++)
    // {
    // l.add(newLocalUniqueId());
    // }
    // long t2 = System.currentTimeMillis();
    // int duplicate = 0;
    // List<String> l2 = new ArrayList<String>();
    // int i = 0;
    // while (duplicate == 0 && i < l.size())
    // {
    // if (l2.contains(l.get(i)))
    // {
    // System.out.println("Duplicate: " + l.get(i) + " at " + i);
    // duplicate++;
    // }
    // l2.add(l.get(i));
    // i++;
    // }
    // System.out.println(l.get(0));
    // System.out.println(l.get(l.size() - 1));
    // System.out.println(duplicate + " duplicates in " + (t2 - t1));
    // }
    /**
     * Create a new identifier unique for the local instance of imeji
     * 
     * @return
     */
    public static String newLocalUniqueId()
    {
        counter.compareAndSet(100000, 0);
        return Long.toHexString(System.currentTimeMillis()) + "-" + Long.toHexString(counter.getAndIncrement());
    }

    /**
     * Create a random id. No assurance of uniqueness, even if probability is small. Generated id are smaller...
     * 
     * @return
     */
    public static String newRandomId()
    {
        return Long.toHexString(rand.nextLong());
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
