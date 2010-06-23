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

package de.mpg.escidoc.faces.util;

import javax.naming.InitialContext;

import org.apache.log4j.Logger;

import de.mpg.escidoc.faces.beans.SessionBean;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.common.valueobjects.face.FaceItemVO;
import de.mpg.escidoc.services.framework.ServiceLocator;

public class ItemHelper
{
    
    private static Logger logger = Logger.getLogger(ItemHelper.class);
    private static XmlTransforming xmlTransforming;
    
    static
    {
        try
        {
            InitialContext context = new InitialContext();
            xmlTransforming = (XmlTransforming) context.lookup(XmlTransforming.SERVICE_NAME);
        }
        catch (Exception e)
        {
            logger.error("Error initializing ejbs", e);
        }
        
    }
    /**
     * Retrieve an item from the framework.
     * 
     * @param objectId The item id
     * @return A {@link FaceItemVO} value object.
     */
    public static FaceItemVO getItem(String objectId)
    {
        SessionBean sessionBean = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
        try
        {
            String itemXml;
            if (sessionBean.getUserHandle() != null)
            {
                itemXml = ServiceLocator.getItemHandler(sessionBean.getUserHandle()).retrieve(objectId);
            }
            else
            {
                itemXml = ServiceLocator.getItemHandler().retrieve(objectId);
            }
            ItemVO itemVO = xmlTransforming.transformToItem(itemXml);
            FaceItemVO faceItemVO = new FaceItemVO(itemVO);
            return faceItemVO;
        }
        catch (Exception e)
        {
            logger.error("Error retrieving item", e);
        }
        return null;
    }
    
    public static String getItemXml(String objectid)
    {
        SessionBean sessionBean = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
        try
        {
            String itemXml;
            if (sessionBean.getUserHandle() != null)
            {
                itemXml = ServiceLocator.getItemHandler(sessionBean.getUserHandle()).retrieve(objectid);
            }
            else
            {
                itemXml = ServiceLocator.getItemHandler().retrieve(objectid);
            }
            return itemXml;
        }
        catch (Exception e)
        {
            logger.error("Error retrieving item", e);
        }
        return null;
    }
    
}
