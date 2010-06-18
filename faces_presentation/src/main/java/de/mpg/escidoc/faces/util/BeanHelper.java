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

import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

public class BeanHelper
{
    
    private static Logger logger = Logger.getLogger(BeanHelper.class);

    /**
     * Return any bean stored in request scope under the specified name.
     * @param cls The bean class.
     * @return the actual or new bean instance
     */
    public static synchronized Object getRequestBean(final Class<?> cls)
    {
        String name = null;

        name = (String) cls.getSimpleName();

        Object result = FacesContext
                .getCurrentInstance()
                .getExternalContext()
                .getRequestMap()
                .get(name);
        
        logger.debug("Getting bean " + name + ": " + result);

        if (result == null)
        {
            try
            {
                logger.debug("Creating new request bean: " + name);
                Object newBean = cls.newInstance();
                FacesContext
                        .getCurrentInstance()
                        .getExternalContext()
                        .getRequestMap()
                        .put(name, newBean);
                return newBean;
            }
            catch (Exception e)
            {
                throw new RuntimeException("Error creating new bean of type " + cls, e);
            }
        }
        else
        {
            return result;
        }
    }

    /**
     * Return any bean stored in session scope under the specified name.
     * @param cls The bean class.
     * @return the actual or new bean instance
     */
    public static synchronized Object getSessionBean(final Class<?> cls)
    {

        String name = null;

        name = (String) cls.getSimpleName();

        Object result = FacesContext
                .getCurrentInstance()
                .getExternalContext()
                .getSessionMap()
                .get(name);
        
        logger.debug("Getting bean " + name + ": " + result);

        if (result == null)
        {
            try
            {
                logger.debug("Creating new session bean: " + name);
                Object newBean = cls.newInstance();
                FacesContext
                        .getCurrentInstance()
                        .getExternalContext()
                        .getSessionMap()
                        .put(name, newBean);
                return newBean;
            }
            catch (Exception e)
            {
                throw new RuntimeException("Error creating new bean of type " + cls, e);
            }
        }
        else
        {
            return result;
        }
    }

    /**
     * Return any bean stored in application scope under the specified name.
     * @param cls The bean class.
     * @return the actual or new bean instance
     */
    public static synchronized Object getApplicationBean(final Class<?> cls)
    {
        String name = null;

        name = (String) cls.getSimpleName();
        
        Object result = FacesContext
                .getCurrentInstance()
                .getExternalContext()
                .getApplicationMap()
                .get(name);
        
        logger.debug("Getting bean " + name + ": " + result);

        if (result == null)
        {
            try
            {
                logger.debug("Creating new application bean: " + name);
                Object newBean = cls.newInstance();
                FacesContext
                        .getCurrentInstance()
                        .getExternalContext()
                        .getApplicationMap()
                        .put(name, newBean);
                return newBean;
            }
            catch (Exception e)
            {
                throw new RuntimeException("Error creating new bean of type " + cls, e);
            }
        }
        else
        {
            return result;
        }
    }
}
