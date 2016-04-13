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
package de.mpg.imeji.presentation.album;

import java.io.IOException;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.exceptions.UnprocessableError;
import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.controller.resource.AlbumController;
import de.mpg.imeji.logic.controller.resource.UserController;
import de.mpg.imeji.logic.controller.util.ImejiFactory;
import de.mpg.imeji.logic.vo.Person;
import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.util.BeanHelper;

/**
 * Java Bean for create album page
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
@ManagedBean(name = "CreateAlbumBean")
@SessionScoped
public class CreateAlbumBean extends AlbumBean {
  private static final long serialVersionUID = -3257133789269212025L;
  private static final Logger LOGGER = Logger.getLogger(CreateAlbumBean.class);

  /**
   * DEfault constructor
   */
  public CreateAlbumBean() {
    super();
  }

  /**
   * Called when bean page is called
   */
  public void init() {
    setAlbum(ImejiFactory.newAlbum());
    ((List<Person>) getAlbum().getMetadata().getPersons()).set(0,
        sessionBean.getUser().getPerson().clone());
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.imeji.presentation.album.AlbumBean#getCancel()
   */
  @Override
  public String getCancel() {
    Navigation nav = (Navigation) BeanHelper.getApplicationBean(Navigation.class);
    return nav.getAlbumsUrl();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.imeji.presentation.album.AlbumBean#save()
   */
  @Override
  public void save() throws IOException {
    try {
      AlbumController ac = new AlbumController();
      ac.create(getAlbum(), sessionBean.getUser());
      UserController uc = new UserController(sessionBean.getUser());
      sessionBean.setUser(uc.retrieve(sessionBean.getUser().getEmail()));
      BeanHelper
          .info(Imeji.RESOURCE_BUNDLE.getMessage("success_album_create", sessionBean.getLocale()));
      Navigation nav = (Navigation) BeanHelper.getApplicationBean(Navigation.class);
      ((AlbumBean) BeanHelper.getSessionBean(AlbumBean.class)).setAlbum(getAlbum());
      sessionBean.setActiveAlbum(getAlbum());
      FacesContext.getCurrentInstance().getExternalContext()
          .redirect(nav.getAlbumUrl() + getAlbum().getIdString());
    } catch (UnprocessableError e) {
      BeanHelper.error(e, sessionBean.getLocale());
      LOGGER.error("Error creating album, e");
    } catch (ImejiException e) {
      BeanHelper
          .error(Imeji.RESOURCE_BUNDLE.getMessage("error_album_create", sessionBean.getLocale()));
      LOGGER.error("Error creating album, e");
    }
  }
}
