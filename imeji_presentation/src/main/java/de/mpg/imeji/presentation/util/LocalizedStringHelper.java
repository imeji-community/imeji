/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.util;

import de.mpg.imeji.logic.vo.Grant;
import de.mpg.imeji.logic.vo.Grant.GrantType;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.j2j.misc.LocalizedString;

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
