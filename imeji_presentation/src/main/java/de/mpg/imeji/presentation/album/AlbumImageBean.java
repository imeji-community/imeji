/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.imeji.presentation.album;

import java.io.Serializable;

import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.image.ImageBean;
import de.mpg.imeji.presentation.image.SingleImageBrowse;
import de.mpg.imeji.presentation.util.BeanHelper;

public class AlbumImageBean extends ImageBean implements Serializable
{
    private String albumId;
    private Navigation navigation;

    public AlbumImageBean() throws Exception
    {
        super();
        this.prettyLink = "pretty:editImageOfAlbum";
        navigation = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
    }
    
    public void initBrowsing()
    {
    	setBrowse(new SingleImageBrowse((AlbumImagesBean) BeanHelper.getSessionBean(AlbumImagesBean.class), getImage()));
    }

    public String getAlbumId()
    {
        return albumId;
    }

    public void setAlbumId(String albumId)
    {
        this.albumId = albumId;
    }

    public String getPageUrl()
    {
        return navigation.getApplicationUrl() + "album/" + this.albumId + "/image/" + this.getId();
    }
    
    public String getNavigationString()
    {
        return "pretty:viewImageAlbum";
    }
}
