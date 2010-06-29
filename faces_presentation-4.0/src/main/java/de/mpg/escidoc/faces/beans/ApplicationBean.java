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

package de.mpg.escidoc.faces.beans;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import de.mpg.escidoc.faces.metadata.ScreenConfiguration;
import de.mpg.escidoc.faces.statistics.StatisticsBean;
import de.mpg.escidoc.faces.util.BeanHelper;
import de.mpg.escidoc.faces.util.QueryHelper;
import de.mpg.escidoc.services.common.util.CommonUtils;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;

public class ApplicationBean
{
    private static Logger logger = Logger.getLogger(ApplicationBean.class);
    /** filename of the ear-internal property file */
    private static final String PROPERTY_FILENAME = "solution.properties";
    
    private String applicationName = null;
    
    private String licenseUrl = "";
    private String licenseImage = "";
    
    private Map<String, Map<String, Integer>> statisticsMapPrivate;
    private Map<String, Map<String, Integer>> statisticsMapPublic;

    public ApplicationBean() throws Exception
    {
        initializeStatistics();
    }
    
    public String getApplicationName()
    {
        if (applicationName == null)
        {
            try
            {
                Properties properties = CommonUtils.getProperties(PROPERTY_FILENAME);
                applicationName = properties.getProperty("appname").toUpperCase()
                    + " " + properties.getProperty("escidoc.application.version");
            }
            catch (Exception e)
            {
                logger.error("Error getting application name", e);
            }
        }
        return applicationName;
    }

    public void setApplicationName(String applicationName)
    {
        this.applicationName = applicationName;
    }
    
    public void initializeStatistics() throws Exception
    {   
        ScreenConfiguration sm = new ScreenConfiguration("search");
        statisticsMapPrivate = new HashMap<String, Map<String,Integer>>();
        statisticsMapPublic = new HashMap<String, Map<String,Integer>>();
        
        for (int i = 0; i < sm.getMdList().size(); i++)
        {
            Map<String, Integer> constraintMapPublic = new HashMap<String, Integer>();
            Map<String, Integer> constraintMapPrivate = new HashMap<String, Integer>();
            
            for (int j = 0; j < sm.getMdList().get(i).getConstraint().size(); j++)
            {
                String query = "(" + sm.getMdList().get(i).getIndex()+ "=" + sm.getMdList().get(i).getConstraint().get(j) + ")";
                constraintMapPrivate.put(sm.getMdList().get(i).getConstraint().get(j), initializeStatistic(query));
                constraintMapPublic.put(sm.getMdList().get(i).getConstraint().get(j), initializeStatistic(query, true));
            }
            
            statisticsMapPrivate.put(sm.getMdList().get(i).getIndex(), constraintMapPrivate);
            statisticsMapPublic.put(sm.getMdList().get(i).getIndex(), constraintMapPublic);
        }
    }

    private int initializeStatistic(String query) throws Exception
    {
        return initializeStatistic(query, false);
    }

    private int initializeStatistic(String query, boolean publ) throws Exception
    {
        QueryHelper queryHelper = new QueryHelper();
        String queryExtension = "(escidoc.content-model.objid=" + PropertyReader.getProperty("escidoc.faces.content-model.id") + ")";
        if (publ)
        {
            queryExtension = "(escidoc.content-model.objid=" + PropertyReader.getProperty("escidoc.faces.content-model.id") + ") and (escidoc.component.visibility=public)";
        }
        
        queryHelper.executeQueryForItems(query, 1, 1, null, queryExtension);
        return queryHelper.getTotalNumberOfItems();
    }

    public Map<String, Map<String, Integer>> getStatisticsMapPrivate()
    {
        return statisticsMapPrivate;
    }

    public void setStatisticsMapPrivate(Map<String, Map<String, Integer>> statisticsMapPrivate)
    {
        this.statisticsMapPrivate = statisticsMapPrivate;
    }

    public Map<String, Map<String, Integer>> getStatisticsMapPublic()
    {
        return statisticsMapPublic;
    }

    public void setStatisticsMapPublic(Map<String, Map<String, Integer>> statisticsMapPublic)
    {
        this.statisticsMapPublic = statisticsMapPublic;
    }

    public String getContentModel() throws IOException, URISyntaxException
    {
        String contentModel = PropertyReader.getProperty("escidoc.faces.content-model.id");
        
        return contentModel;
    }
    
    public String getLicenseUrl() 
    {
        try
        {
            //System.out.println("CC: "+ PropertyReader.getProperty("escidoc.faces.picture.license.url") );
            return PropertyReader.getProperty("escidoc.faces.picture.license.url");
        }
        catch(Exception e)
        {
            return "";
        }
    }

    public String getLicenseImage() 
    {
        try
        {
            return PropertyReader.getProperty("escidoc.faces.picture.license.image");
        }
        catch(Exception e)
        {
            return "";
        }
    }

}
