package de.mpg.imeji.presentation.user.util;

import de.mpg.imeji.logic.vo.Grant;
import de.mpg.imeji.logic.vo.Grant.GrantType;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;

public class GrantHelper
{
    public static String grantString(Grant grant)
    {
        String role = "";
        if (grant.getGrantFor().toString().contains("album")
                && grant.getGrantType().getFragment().equals(GrantType.CONTAINER_EDITOR.name()))
        {
            role = ((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getLabel("role_album_editor");
        }
        else
        {
            if (grant.getGrantType().getFragment().equals(GrantType.VIEWER.name()))
            {
                role = ((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getLabel("role_viewer");
            }
            if (grant.getGrantType().getFragment().equals(GrantType.CONTAINER_EDITOR.name()))
            {
                role = ((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getLabel("role_collection_editor");
            }
            if (grant.getGrantType().getFragment().equals(GrantType.IMAGE_EDITOR.name()))
            {
                role = ((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getLabel("role_image_editor");
            }
            if (grant.getGrantType().getFragment().equals(GrantType.IMAGE_UPLOADER.name()))
            {
                role = ((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getLabel("role_image_uploader");
            }
            if (grant.getGrantType().getFragment().equals(GrantType.PROFILE_EDITOR.name()))
            {
                role = ((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getLabel("role_profile_editor");
            }
            if (grant.getGrantType().getFragment().equals(GrantType.PROFILE_VIEWER.name()))
            {
                role = ((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getLabel("role_profile_viewer");
            }
            if (grant.getGrantType().getFragment().equals(GrantType.CONTAINER_ADMIN.name())
                    || grant.getGrantType().getFragment().equals(GrantType.PROFILE_ADMIN.name()))
            {
                role = ((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getLabel("role_owner");
            }
        }
        return role;
    }
}
