package de.mpg.imeji.presentation.beans;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 * Session Bean for the container editor (create and edit collection/album)
 * 
 * @author bastiens
 *
 */
@ManagedBean(name = "ContainerEditorSession")
@SessionScoped
public class ContainerEditorSession implements Serializable {
  private static final long serialVersionUID = -4891856418594298289L;
  private String uploadedLogoPath;

  /**
   * @return the uploadedLogoPath
   */
  public String getUploadedLogoPath() {
    return uploadedLogoPath;
  }

  /**
   * @param uploadedLogoPath the uploadedLogoPath to set
   */
  public void setUploadedLogoPath(String uploadedLogoPath) {
    this.uploadedLogoPath = uploadedLogoPath;
  }
}
