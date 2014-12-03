package de.mpg.imeji.rest.to;

import java.io.Serializable;

/**
 * Created by vlad on 25.11.14.
 */
public class CollectionProfileTO implements Serializable{

    private static final long serialVersionUID = -5210147403244095642L;

    private String profileId;
    private String method;

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
