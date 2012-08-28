/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.lang;

import java.util.Iterator;

import de.mpg.j2j.misc.LocalizedString;

public class LabelHelper
{
    public static String getDefaultLabel(Iterator<LocalizedString> labels)
    {
        String l = "";
        if (labels.hasNext())
            l = ((LocalizedString)labels.next()).toString();
        while (labels.hasNext())
        {
            LocalizedString ls = (LocalizedString)labels.next();
            if (ls.getLang().equals("eng"))
            {
                l = ls.toString();
            }
        }
        return l;
    }
}
