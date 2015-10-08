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
package de.mpg.imeji.logic.auth.authentication;

import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.auth.Authentication;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.util.StringHelper;
import de.mpg.imeji.logic.vo.User;

/**
 * Simple {@link Authentication} in the local database
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class SimpleAuthentication implements Authentication {
  private String login = null;
  private String pwd = null;

  /**
   * Constructor
   */
  public SimpleAuthentication(String login, String pwd) {
    this.login = login;
    this.pwd = pwd;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.imeji.logic.auth.Authentification#doLogin()
   */
  @Override
  public User doLogin() {
    UserController uc = new UserController(Imeji.adminUser);
    try {
      User user = uc.retrieve(login);
      if (user.getEncryptedPassword().equals(StringHelper.convertToMD5(pwd))
          || user.getEncryptedPassword().equals(pwd)) {
        return user;
      }
    } catch (Exception e) {
      logger.error("Error SimpleAuthentification", e);
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.imeji.logic.auth.Authentification#getUserLogin()
   */
  @Override
  public String getUserLogin() {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.imeji.logic.auth.Authentification#getUserPassword()
   */
  @Override
  public String getUserPassword() {
    // TODO Auto-generated method stub
    return null;
  }
}
