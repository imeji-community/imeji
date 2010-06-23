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

import java.lang.reflect.Method;
import java.util.Comparator;

/**
 * TODO Description
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * @param <T>
 *
 */
public class GenericFieldComparator<T> implements Comparator<T>
{

    private Method method;
    private boolean ascending;
    
    @SuppressWarnings("unchecked")
    public GenericFieldComparator(Class cls, String field, boolean ascending) throws Exception
    {
        method = cls
                .getMethod("get" + field.substring(0, 1).toUpperCase() + field.substring(1), new Class<?>[]{});
        this.ascending = ascending;
    }
    
    @SuppressWarnings("unchecked")
    public int compare(T o1, T o2)
    {
        try
        {
            Object f1 = method.invoke(o1, new Object[]{});
            Object f2 = method.invoke(o2, new Object[]{});
            if (f1 instanceof String && f2 instanceof String)
            {
                if (ascending)
                {
                    return ((String) f1).toLowerCase().compareTo(((String) f2).toLowerCase());
                }
                else
                {
                    return ((String) f2).toLowerCase().compareTo(((String) f1).toLowerCase());
                }
            }
            else if (f1 instanceof Comparable && f2 instanceof Comparable)
            {
                if (ascending)
                {
                    return ((Comparable) f1).compareTo((Comparable) f2);
                }
                else
                {
                    return ((Comparable) f2).compareTo((Comparable) f1);
                }
            }
            else
            {
                return 0;
            }
        }
        catch (Exception e)
        {
            return 0;
        }
        
    }
    
}
