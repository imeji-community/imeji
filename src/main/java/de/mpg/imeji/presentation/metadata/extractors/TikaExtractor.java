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
package de.mpg.imeji.presentation.metadata.extractors;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.jpeg.JpegParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.DefaultHandler;

import de.mpg.imeji.logic.storage.StorageController;
import de.mpg.imeji.logic.vo.Item;

/**
 * User {@link Tika} to extract metadata out of the image
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class TikaExtractor
{
    public static List<String> extract(Item item)
    {
        List<String> techMd = new ArrayList<String>();
        try
        {
            StorageController sc = new StorageController();
            ByteArrayOutputStream bous = new ByteArrayOutputStream();
            sc.read(item.getFullImageUrl().toString(), bous, true);
            ByteArrayInputStream in = new ByteArrayInputStream(bous.toByteArray());
            Metadata metadata = new Metadata();
            AutoDetectParser parser = new AutoDetectParser();
            BodyContentHandler handler = new BodyContentHandler();
            parser.parse(in, handler, metadata);
            for (String name : metadata.names())
            {
                techMd.add(name + " :  " + metadata.get(name));
                
            }
            System.err.println("Content-Type = " + metadata.get("Content-Type"));
            System.err.println("Make = " + metadata.get("Make"));
            System.err.println("Model = " + metadata.get("Model"));
            System.err.println("Artist = " + metadata.get("Artist"));
            System.err.println("Software = " + metadata.get("Software"));
            System.err.println("Image Description = " + metadata.get("Image Description"));
            System.err.println("exif:DateTimeOriginal = " + metadata.get("exif:DateTimeOriginal"));
            System.err.println("DateTimeOriginal = " + metadata.get("DateTimeOriginal"));
            System.err.println("Date/Time = " + metadata.get("Date/Time"));
            System.err.println("Color Space = " + metadata.get("ColorSpace"));
            
            
            
            
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return techMd;
    }
}
