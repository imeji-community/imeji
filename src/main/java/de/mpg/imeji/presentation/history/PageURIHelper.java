/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.history;

import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.Album;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.logic.vo.UserGroup;

/**
 * Helper for {@link URI} of {@link Page}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class PageURIHelper
{
    private static Pattern findItemId = Pattern.compile(".*/item/(\\w+)");
    private static Pattern findCollectionId = Pattern.compile(".*/collection/(\\w+)");
    private static Pattern findAlbumId = Pattern.compile(".*/album/(\\w+)");
    private static Pattern findUserGroupId = Pattern.compile(".*/userGroup/(\\w+)&?");
    private static Pattern findUserId = Pattern.compile(".*/user\\?id=(.*)&?");

    /**
     * Extract the id of an imeji object from the url
     **/
    public static URI extractId(String url)
    {
        Matcher m = findItemId.matcher(url);
        if (m.find())
            return ObjectHelper.getURI(Item.class, m.group(1));
        m = findCollectionId.matcher(url);
        if (m.find())
            return ObjectHelper.getURI(CollectionImeji.class, m.group(1));
        m = findAlbumId.matcher(url);
        if (m.find())
            return ObjectHelper.getURI(Album.class, m.group(1));
        m = findUserId.matcher(url);
        if (m.find())
            return URI.create(m.group(1));
        m = findUserGroupId.matcher(url);
        if (m.find())
            return ObjectHelper.getURI(UserGroup.class, m.group(1));
        return null;
    }

    /**
     * Return the label of a page according to its url
     **/
    public static String getPageLabel(String url)
    {
        if (url == null)
            return "history_home";
        // item pages
        if (url.equals("/browse"))
        {
            return "history_images";
        }
        if (url.matches(".*/item/\\w+$"))
        {
            // same label for all item view pages
            return "history_image";
        }
        // Collections pages
        if (url.equals("/collections"))
        {
            return "history_collections";
        }
        if (url.matches("^/collection/\\w+/browse$"))
        {
            return "history_images_collection";
        }
        if (url.matches("^/collection/\\w+/upload$"))
        {
            return "history_upload";
        }
        if (url.matches("^/collection/\\w+/infos$"))
        {
            return "history_collection_info";
        }
        if (url.matches("^/collection/.+$"))
        {
            return "collection";
        }
        // Album pages
        if (url.equals("/albums"))
        {
            return "albums";
        }
        if (url.matches("^/album/\\w+/browse$"))
        {
            return "history_images_album";
        }
        if (url.matches("^/album/\\w+/infos$"))
        {
            return "history_album_info";
        }
        if (url.matches("^/album/\\w+$"))
        {
            return "history_album";
        }
        // Other pages
        if (url.equals("/admin"))
        {
            return "admin";
        }
        if (url.equals("/user"))
        {
            return "user";
        }
        if (url.equals("/users"))
        {
            return "admin_info_users";
        }
        if (url.equals("/createuser"))
        {
            return "admin_user_new";
        }
        if (url.equals("/help"))
        {
            return "help";
        }
        if (url.equals("/search"))
        {
            return "history_advanced_search";
        }
        if (url.equals("/edit"))
        {
            return "edit_images";
        }
        if (url.equals("/createusergroup"))
        {
            return "admin_userGroup_new";
        }
        if (url.equals("/usergroup"))
        {
            return "admin_userGroup";
        }
        if (url.equals("/usergroups"))
        {
            return "admin_userGroups_view";
        }
        return "history_home";
    }
}
