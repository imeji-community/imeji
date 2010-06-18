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
* Copyright 2006-2009 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.faces.util;

import de.mpg.escidoc.services.framework.PropertyReader;

public class ConeHelper
{
    private String coneUrl = null;
    private String conePortfolioLink = null;

    public String getConeUrl() throws Exception
    {
        if (this.coneUrl == null)
        {
            setConeUrl(PropertyReader.getProperty("escidoc.cone.service.url"));
        }
        return this.coneUrl;
    }
    
    public void setConeUrl(String coneUrl)
    {
        this.coneUrl = coneUrl;
    }

    public String getConePortfolioLink() throws Exception
    {
        return this.getConeUrl() + "/html/persons/";
    }
}
