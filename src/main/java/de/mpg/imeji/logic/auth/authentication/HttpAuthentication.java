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

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;

import de.mpg.imeji.exceptions.AuthenticationError;
import de.mpg.imeji.logic.auth.Authentication;
import de.mpg.imeji.logic.vo.User;

/**
 * {@link Authentification} for {@link HttpServletRequest}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class HttpAuthentication implements Authentication {
  /**
   * The content of the http header
   */
  private String usernamePassword = null;
  private String apiKey = null;

  /**
   * Constructor
   */
  public HttpAuthentication(HttpServletRequest request) {
    usernamePassword = getUsernamePassword(request);
    apiKey = getApiKey(request);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.imeji.logic.auth.Authentification#doLogin()
   */
  @Override
  public User doLogin() throws AuthenticationError {
    if (apiKey != null) {
      APIKeyAuthentication keyAuthentication = new APIKeyAuthentication(apiKey);
      return keyAuthentication.doLogin();
    } else if (usernamePassword != null) {
      int p = usernamePassword.indexOf(":");
      if (p != -1) {
        SimpleAuthentication simpleAuthentification =
            new SimpleAuthentication(getUserLogin(), getUserPassword());
        return simpleAuthentification.doLogin();
      }
    }

    return null;
  }

  /**
   * Utility method to read the username and password in the {@link HttpServletRequest} (separated
   * by a colon).
   * 
   * @param request
   * @return The username and password combination
   */
  private String getUsernamePassword(HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");
    if (authHeader != null && authHeader.contains("Basic")) {
      return new String(Base64.decodeBase64(authHeader.replace("Basic ", "").trim().getBytes()));
    }
    return null;
  }

  /**
   * Return the api Key
   * 
   * @param request
   * @return
   */
  private String getApiKey(HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");
    if (authHeader != null && authHeader.contains("Bearer ")) {
      return new String(Base64.decodeBase64(authHeader.replace("Bearer ", "").trim().getBytes()));
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
    if (usernamePassword != null) {
      int p = usernamePassword.indexOf(":");
      if (p != -1) {
        return usernamePassword.substring(0, p);
      }
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.imeji.logic.auth.Authentification#getUserPassword()
   */
  @Override
  public String getUserPassword() {
    if (usernamePassword != null) {
      int p = usernamePassword.indexOf(":");
      if (p != -1) {
        return usernamePassword.substring(p + 1);
      }
    }
    return null;
  }

  /**
   * True if the request has informations about the login (user and password)
   * 
   * @return
   */
  public boolean hasLoginInfos() {
    return usernamePassword != null;
  }
}
