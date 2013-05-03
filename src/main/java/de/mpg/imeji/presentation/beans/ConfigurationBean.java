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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import de.mpg.imeji.presentation.util.PropertyReader;

/**
 * JavaBean managing the imeji configuration which is made directly by the administrator from the web (i.e. not in the
 * property file)
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
@ManagedBean(name = "Configuration")
@ApplicationScoped
public class ConfigurationBean
{
    private enum CONFIGURATION
    {
        SNIPPET;
    }

    private Properties config;
    private File configFile;

    /**
     * Constructor, create the file if not existing
     * 
     * @throws URISyntaxException
     * @throws IOException
     */
    public ConfigurationBean() throws IOException, URISyntaxException
    {
        configFile = new File(PropertyReader.getProperty("imeji.tdb.path") + "/conf");
        if (!configFile.exists())
        {
            configFile.createNewFile();
        }
        loadConfig();
    }

    /**
     * Load the imeji configuration from a {@link File}
     * 
     * @param f
     * @throws IOException
     */
    private void loadConfig() throws IOException
    {
        config = new Properties();
        FileInputStream in = new FileInputStream(configFile);
        config.loadFromXML(in);
    }

    /**
     * Save the configuration in the config file
     */
    public void saveConfig()
    {
        try
        {
            config.storeToXML(new FileOutputStream(configFile), "imeji configuration File");
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Set the value of a configuration property, and save it on disk
     * 
     * @param name
     * @param value
     */
    private void setProperty(String name, String value)
    {
        config.setProperty(name, value);
    }

    /**
     * Set the Snippet in the configuration
     * 
     * @param str
     */
    public void setSnippet(String str)
    {
        setProperty(CONFIGURATION.SNIPPET.name(), str);
    }

    /**
     * Read the snippet from the configuration
     * 
     * @return
     */
    public String getSnippet()
    {
        return (String)config.get(CONFIGURATION.SNIPPET.name());
    }
}
