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


public class StringHelper
{
    private StringHelper()
    {
        
    }
    
    /**
     * Encodes an incoming request parameter to disable cql injection.
     */
    public static String encodeCqlParameter(String parameter) throws IllegalArgumentException
    {
        if (parameter == null)
        {
            return null;
        }
        if (parameter.contains("*") || parameter.contains(" ") || parameter.contains("(") || parameter.contains(")"))
        {
            throw new IllegalArgumentException("parameter contains illegal characters: " + parameter);
        }
        return parameter;
    }
    
    /**
     * Encode the special character contained in the string value.
     * @param value
     * @return
     */
    public static String escapeSpecialCharacter(String value)
    {
        value = value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
        return value;
    }
}
