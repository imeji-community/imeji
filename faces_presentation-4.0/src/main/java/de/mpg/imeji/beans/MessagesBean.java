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
* Copyright 2006-2010 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.imeji.beans;

import java.util.Iterator;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

public class MessagesBean {

	 public boolean getHasErrorMessages()
	    {
	        for (Iterator<FacesMessage> i = FacesContext.getCurrentInstance().getMessages(); i.hasNext();)
	        {
	            FacesMessage fm = i.next();
	            if (fm.getSeverity().equals(FacesMessage.SEVERITY_ERROR) || fm.getSeverity().equals(FacesMessage.SEVERITY_WARN) || fm.getSeverity().equals(FacesMessage.SEVERITY_FATAL))
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
