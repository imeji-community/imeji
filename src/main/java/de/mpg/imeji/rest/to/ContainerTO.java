package de.mpg.imeji.rest.to;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ContainerTO extends PropertiesTO implements Serializable {

  private static final long serialVersionUID = -3159018504356059712L;

  private String title;

  private String description;

  private List<PersonTO> contributors = new ArrayList<PersonTO>();

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public List<PersonTO> getContributors() {
    return contributors;
  }

  public void setContributors(List<PersonTO> contributors) {
    this.contributors = contributors;
  }


}
