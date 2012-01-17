/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.imeji.util;

import thewebsemantic.LocalizedString;

public class LocalizedStringHelper
{
	private String string = null;
	private String lang = "eng";
	
	
	public LocalizedStringHelper(LocalizedString locString)
	{
		string = locString.toString();
		lang = locString.getLang();
	}
	
	public LocalizedString getAsLocalizedString()
	{
		return new LocalizedString(string, lang);
	}
	
	public String getString() 
	{
		return string;
	}
	
	public void setString(String string) 
	{
		this.string = string;
	}
	
	public String getLang() 
	{
		return lang;
	}
	
	public void setLang(String lang) 
	{
		this.lang = lang;
	}
}
