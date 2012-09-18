/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.album;

import javax.faces.context.FacesContext;

import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.image.ImageBean;
import de.mpg.imeji.presentation.image.SingleImageBrowse;
import de.mpg.imeji.presentation.util.BeanHelper;

public class AlbumImageBean extends ImageBean
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
    	String tempId=(String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("AlbumImagesBean.id");
    	setBrowse(new SingleImageBrowse((AlbumImagesBean)BeanHelper.getSessionBean(AlbumImagesBean.class), getImage(),"album",tempId));
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
        return navigation.getAlbumUrl() + albumId + "/" + navigation.ITEM.getPath() + "/" + getId();
    }

    public String getNavigationString()
    {
        return "pretty:albumItem";
    }
}
