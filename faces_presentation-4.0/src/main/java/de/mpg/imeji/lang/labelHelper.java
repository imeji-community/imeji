/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.imeji.lang;

import java.util.Iterator;

import thewebsemantic.LocalizedString;

public class labelHelper 
{
	public static String getDefaultLabel(Iterator<LocalizedString> labels)
	{
		String l = "";
		if (labels.hasNext()) l =  ((LocalizedString) labels.next()).toString();
		while (labels.hasNext()) 
		{
			LocalizedString ls = (LocalizedString) labels.next();
			if (ls.getLang().equals("eng"))
			{
				l = ls.toString();
			}
		}
		return l;
	}
}
