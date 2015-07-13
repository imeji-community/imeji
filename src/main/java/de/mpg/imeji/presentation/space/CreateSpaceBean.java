package de.mpg.imeji.presentation.space;

import java.io.File;
import java.io.IOException;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.exceptions.UnprocessableError;
import de.mpg.imeji.logic.controller.SpaceController;
import de.mpg.imeji.presentation.util.BeanHelper;

@ManagedBean(name = "CreateSpaceBean")
@ViewScoped
public class CreateSpaceBean extends SpaceBean {
  private static final long serialVersionUID = -5469506610392004531L;

  public CreateSpaceBean() {
    setSpaceCreateMode(true);
    init();
  }

  public String save() throws Exception {
    if (createdSpace()) {
      sessionBean.setSpaceId(getSpace().getSlug());
      // Go to the home URL of the Space
      FacesContext.getCurrentInstance().getExternalContext().redirect(navigation.getHomeUrl());
    }

    return "";
  }

  public boolean createdSpace() throws ImejiException, IOException {
    try {
      SpaceController spaceController = new SpaceController();
      File spaceLogoFile =
          (sessionBean.getSpaceLogoIngestImage() != null) ? sessionBean.getSpaceLogoIngestImage()
              .getFile() : null;
      setSpace(spaceController.create(getSpace(), getSelectedCollections(), spaceLogoFile,
          sessionBean.getUser()));
      // reset the Session bean and this local, as anyway it will navigate
      // back to the home page
      // Note: check how it will work with eDit! Edit bean should be
      // implemented
      setIngestImage(null);
      BeanHelper.info(sessionBean.getMessage("success_space_create"));
      return true;
    } catch (UnprocessableError e) {
      BeanHelper.error(sessionBean.getMessage(e.getMessage()));
    }
    return false;
  }

}
