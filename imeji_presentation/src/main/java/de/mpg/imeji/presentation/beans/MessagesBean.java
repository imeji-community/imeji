/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.imeji.presentation.beans;

import java.util.Iterator;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

public class MessagesBean
{
    public boolean getHasErrorMessages()
    {
        for (Iterator<FacesMessage> i = FacesContext.getCurrentInstance().getMessages(); i.hasNext();)
        {
            FacesMessage fm = i.next();
            if (fm.getSeverity().equals(FacesMessage.SEVERITY_ERROR)
                    || fm.getSeverity().equals(FacesMessage.SEVERITY_WARN)
                    || fm.getSeverity().equals(FacesMessage.SEVERITY_FATAL))
            {
                return true;
            }
        }
        return false;
    }

    public int getNumberOfMessages()
    {
        int number = 0;
        for (Iterator<FacesMessage> i = FacesContext.getCurrentInstance().getMessages(); i.hasNext();)
        {
            i.next();
            number++;
        }
        return number;
    }

    public boolean getHasMessages()
    {
        return FacesContext.getCurrentInstance().getMessages().hasNext();
    }
}
