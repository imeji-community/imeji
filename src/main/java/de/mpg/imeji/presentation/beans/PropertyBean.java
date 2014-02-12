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

import java.io.IOException;
import java.net.URISyntaxException;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import org.apache.commons.io.FilenameUtils;

import de.mpg.imeji.logic.util.StringHelper;
import de.mpg.imeji.presentation.util.PropertyReader;

/**
 * Java Bean where property defined in imeji.property can be access
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
@ManagedBean(name = "Property")
@ApplicationScoped
public class PropertyBean
{
    /**
     * True if Digilib is enabled
     */
    private boolean digilibEnabled = false;
    /**
     * The base of the path to the internal storage
     */
    private String internalStorageBase = "files";
    /**
     * The base of the uri of imeji objects
     */
    private static String baseURI;
    private static String applicationURL;
    
    private static String css_default;
    private static String css_alternate;

    /**
     * Default constructor
     */
    public PropertyBean()
    {
        try
        {
            this.digilibEnabled = Boolean.parseBoolean(PropertyReader.getProperty("imeji.digilib.enable"));
            this.internalStorageBase = FilenameUtils.getBaseName(FilenameUtils.normalizeNoEndSeparator(PropertyReader
                    .getProperty("imeji.storage.path")));
            applicationURL = StringHelper.normalizeURI(PropertyReader.getProperty("escidoc.imeji.instance.url"));
            this.css_default = PropertyReader.getProperty("imeji.layout.css_default");
            this.css_alternate = PropertyReader.getProperty("imeji.layout.css_alternate");
            readBaseUri();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error reading properties: ", e);
        }
    }

    /**
     * Read in the property the base Uri
     */
    private void readBaseUri()
    {
        try
        {
            baseURI = StringHelper.normalizeURI(PropertyReader.getProperty("imeji.jena.resource.base_uri"));
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error reading properties: ", e);
        }
        if (baseURI == null || baseURI.trim().equals("/"))
        {
            baseURI = applicationURL;
        }
        if (baseURI == null)
        {
            throw new RuntimeException("Error in properties. Check property: escidoc.imeji.instance.url");
        }
    }

    /**
     * @return the magickEnabled
     */
    public boolean isDigilibEnabled()
    {
        return digilibEnabled;
    }

    /**
     * @return the internalStorageBase
     * @throws URISyntaxException
     * @throws IOException
     */
    public String getInternalStorageBase() throws IOException, URISyntaxException
    {
        this.internalStorageBase = FilenameUtils.getBaseName(FilenameUtils.normalizeNoEndSeparator(PropertyReader
                .getProperty("imeji.storage.path")));
        return internalStorageBase;
    }

    /**
     * @return the baseURI
     */
    public String getBaseURI()
    {
        return baseURI;
    }

    /**
     * Static getter
     * 
     * @return
     */
    public static String baseURI()
    {
        return baseURI;
    }

    /**
     * @return the applicationURL
     */
    public static String getApplicationURL()
    {
        return applicationURL;
    }

    /**
     * Static getter
     * 
     * @return
     */
    public static String applicationURL()
    {
        return applicationURL;
    }
    
    public static String getCss_default() {
		return css_default;
	}

	public static void setCss_default(String css_default) {
		PropertyBean.css_default = css_default;
	}

	public static String getCss_alternate() {
		return css_alternate;
	}

	public static void setCss_dark(String css_alternate) {
		PropertyBean.css_alternate = css_alternate;
	}
}
