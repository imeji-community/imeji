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
package de.mpg.imeji.logic.export.format;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

import org.apache.http.client.HttpResponseException;

import de.mpg.imeji.logic.ImejiJena;
import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.export.Export;
import de.mpg.imeji.logic.search.SearchResult;
import de.mpg.imeji.logic.storage.StorageController;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;

/**
 * {@link Export} images in zip
 * 
 * @author kleinfercher (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class ZIPExport extends Export
{
    protected List<String> filteredResources = new ArrayList<String>();
    protected String modelURI;

    /**
     * @param type
     * @return
     * @throws HttpResponseException
     */
    public ZIPExport(String type) throws HttpResponseException
    {
        boolean supported = false;
        if ("image".equalsIgnoreCase(type))
        {
            modelURI = ImejiJena.imageModel;
            supported = true;
        }
        if (!supported)
        {
            throw new HttpResponseException(400, "Type " + type + " is not supported.");
        }
    }

    @Override
    public void init()
    {
    }

    @Override
    public void export(OutputStream out, SearchResult sr)
    {
        try
        {
            exportAllImages(sr, out);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
     * @see de.mpg.imeji.logic.export.Export#getContentType()
     */
    @Override
    public String getContentType()
    {
        return "application/zip";
    }

    protected void filterResources(SearchResult sr, User user)
    {
        // TODO Auto-generated method stub
    }

    /**
     * This method exports all images of the current browse page as a zip file
     * 
     * @throws Exception
     * @throws URISyntaxException
     */
    public void exportAllImages(SearchResult sr, OutputStream out) throws URISyntaxException, Exception
    {
        List<String> source = sr.getResults();
        try
        {
            // Create the ZIP file
            ZipOutputStream zip = new ZipOutputStream(out);
            for (int i = 0; i < source.size(); i++)
            {
                SessionBean session = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
                ItemController ic = new ItemController(session.getUser());
                Item item = ic.retrieve(new URI(source.get(i)));
                StorageController sc = new StorageController();
                try
                {
                    zip.putNextEntry(new ZipEntry(item.getFilename()));
                    sc.read(item.getFullImageUrl().toString(), zip, false);
                    // Complete the entry
                    zip.closeEntry();
                }
                catch (ZipException ze)
                {
                    if (ze.getMessage().contains("duplicate entry"))
                    {
                        String name = i + "_" + item.getFilename();
                        zip.putNextEntry(new ZipEntry(name));
                        sc.read(item.getFullImageUrl().toString(), zip, false);
                        // Complete the entry
                        zip.closeEntry();
                    }
                }
            }
            // Complete the ZIP file
            zip.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
