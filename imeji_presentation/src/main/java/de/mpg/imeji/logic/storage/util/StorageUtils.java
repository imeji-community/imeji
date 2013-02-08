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
package de.mpg.imeji.logic.storage.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Util class fore the storage package
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class StorageUtils
{
    public static void doHttpGet()
    {
    }

    /**
     * Transform an {@link InputStream} to a {@link Byte} array
     * 
     * @param stream
     * @return
     */
    public static byte[] toBytes(InputStream stream)
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int b;
        try
        {
            while ((b = stream.read()) != -1)
            {
                bos.write(b);
            }
            byte[] ba = bos.toByteArray();
            bos.flush();
            bos.close();
            return ba;
        }
        catch (IOException e)
        {
            throw new RuntimeException("Error transforming inputstream to bytearryoutputstream", e);
        }
    }

    /**
     * Parse the file extension from its name
     * 
     * @param filename
     * @return
     */
    public static String getFileExtension(String filename)
    {
        int i = filename.lastIndexOf('.');
        if (i > 0)
        {
            return filename.substring(i + 1);
        }
        return null;
    }
    
    /**
     * Return the Mime Type of a file according to its format (i.e. file extension)
     * 
     * @param format
     * @return
     */
    public static String getMimeType(String format)
    {
        format = format.toLowerCase();
        if ("tif".equals(format))
        {
            format = format + "f";
        }
        return "image/" + format;
    }
}
