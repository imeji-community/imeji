/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.album;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;

import de.mpg.imeji.logic.controller.AlbumController;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.Album;
import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.beans.SessionBean;
import de.mpg.imeji.presentation.image.ImageBean;
import de.mpg.imeji.presentation.image.SingleImageBrowse;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.ObjectLoader;

public class AlbumImageBean extends ImageBean
{
    private String albumId;
    private Navigation navigation;
    private SessionBean session = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);

    public AlbumImageBean() throws Exception
    {
        super();
        this.prettyLink = "pretty:editImageOfAlbum";
        navigation = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
    }

    public void initBrowsing()
    {
        String tempId = (String)FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
                .get("AlbumImagesBean.id");
        setBrowse(new SingleImageBrowse((AlbumImagesBean)BeanHelper.getSessionBean(AlbumImagesBean.class), getImage(),
                "album", tempId));
    }

    private Album loadAlbum()
    {
        return ObjectLoader.loadAlbum(ObjectHelper.getURI(Album.class, albumId), session.getUser());
    }

    public String removeFromAlbum() throws Exception
    {
        AlbumController ac = new AlbumController(session.getUser());
        List<String> l = new ArrayList<String>();
        l.add(getImage().getId().toString());
        ac.removeFromAlbum(loadAlbum(), l, session.getUser());
        BeanHelper.info(session.getLabel("image") + " " + getImage().getFilename() + " "
                + session.getMessage("success_album_remove_from"));
        return "pretty:albumBrowse";
    }

    @Override
    public boolean isDeletable()
    {
        return false;
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
