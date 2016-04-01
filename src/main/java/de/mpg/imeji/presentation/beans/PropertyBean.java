/*
 * 
 * CDDL HEADER START
 * 
 * The contents of this file are subject to the terms of the Common Development and Distribution
 * License, Version 1.0 only (the "License"). You may not use this file except in compliance with
 * the License.
 * 
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license.
 * See the License for the specific language governing permissions and limitations under the
 * License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License
 * file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with
 * the fields enclosed by brackets "[]" replaced with your own identifying information: Portions
 * Copyright [yyyy] [name of copyright owner]
 * 
 * CDDL HEADER END
 */
/*
 * Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft für
 * wissenschaftlich-technische Information mbH and Max-Planck- Gesellschaft zur Förderung der
 * Wissenschaft e.V. All rights reserved. Use is subject to license terms.
 */
package de.mpg.imeji.presentation.beans;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import de.mpg.imeji.logic.Imeji;

/**
 * Java Bean where property defined in imeji.property can be access
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
@ManagedBean(name = "Property")
@ApplicationScoped
public class PropertyBean {

  /**
   * Function reads each property from imeji.properties file
   * 
   * @param key - property name
   * @return String - trimmed value of key
   */
  public String getProperty(String key) {
    return Imeji.PROPERTIES.getProperty(key);
  }

  /**
   * @return the digilibEnabled
   */
  public boolean isDigilibEnabled() {
    return Imeji.PROPERTIES.isDigilibEnabled();
  }

  /**
   * @return the internalStorageBase
   * @throws URISyntaxException
   * @throws IOException
   */
  public String getInternalStorageBase() {
    return Imeji.PROPERTIES.getInternalStorageBase();
  }

  /**
   * @return the baseURI
   */
  public String getBaseURI() {
    return Imeji.PROPERTIES.getBaseURI();
  }

  /**
   * @return the applicationURL
   */
  public String getApplicationURL() {
    return Imeji.PROPERTIES.getApplicationURL();
  }
}
