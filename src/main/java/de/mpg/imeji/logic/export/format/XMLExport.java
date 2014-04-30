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

import org.apache.http.client.HttpResponseException;

import de.mpg.imeji.logic.export.Export;
import de.mpg.imeji.logic.export.format.xml.XMLItemsExport;
import de.mpg.imeji.logic.export.format.xml.XMLMdProfileExport;

/**
 * {@link Export} in xml
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public abstract class XMLExport extends Export
{
    /**
     * Factory for {@link XMLExport}
     * 
     * @param type
     * @return
     * @throws HttpResponseException
     */
    public static XMLExport factory(String type) throws HttpResponseException
    {
        if ("image".equals(type))
        {
            return new XMLItemsExport();
        }
        else if ("profile".equals(type))
        {
            return new XMLMdProfileExport();
        }
        throw new HttpResponseException(400, "Type " + type + " is not supported.");
    }

    /*
     * (non-Javadoc)
     * @see de.mpg.imeji.logic.export.Export#getContentType()
     */
    @Override
    public String getContentType()
    {
        return "application/xml";
    }
}
