/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.metadata.editors;

import java.util.ArrayList;
import java.util.List;

import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.util.MetadataFactory;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.presentation.metadata.EditorItemBean;
import de.mpg.imeji.presentation.metadata.SuperMetadataBean;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;

/**
 * Abstract call for the {@link Metadata} editors
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public abstract class MetadataEditor
{
    protected List<EditorItemBean> items;
    protected Statement statement;
    protected MetadataProfile profile;

    // protected Validator validator;
    /**
     * Editor: Edit a list of images for one statement.
     * 
     * @param items
     * @param statement
     */
    public MetadataEditor(List<Item> itemList, MetadataProfile profile, Statement statement)
    {
        this.statement = statement;
        this.profile = profile;
        items = new ArrayList<EditorItemBean>();
        for (Item item : itemList)
        {
            items.add(new EditorItemBean(item, profile));
        }
        initialize();
    }

    /**
     * Reset all value to empty state
     */
    public void reset()
    {
        items = new ArrayList<EditorItemBean>();
        statement = null;
        profile = null;
    }

    /**
     * Save the {@link Item} and {@link Metadata} defined in the editor
     */
    public void save()
    {
        SessionBean sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        ItemController ic = new ItemController(sb.getUser());
        try
        {
            if (prepareUpdate())
            {
                if (validateMetadataofImages())
                {
                    try
                    {
                         addPositionToMetadata();
                        List<Item> itemList = new ArrayList<Item>();
                        for (EditorItemBean eib : items)
                        {
                            itemList.add(eib.asItem());
                        }
                        ic.update(itemList);
                        BeanHelper.info(sb.getMessage("success_editor_edit"));
                        String str = items.size() + " " + sb.getMessage("success_editor_images");
                        if (items.size() == 1)
                            str = sb.getMessage("success_editor_image");
                        BeanHelper.info(str);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        BeanHelper.error(sb.getMessage("error_metadata_edit") + ": " + e.getMessage());
                    }
                }
                else
                {
                    BeanHelper.error(sb.getMessage("error_metadata_validation"));
                }
            }
            else
            {
                BeanHelper.error(sb.getMessage("error_metadata_edit_no_images"));
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(sb.getMessage("error_metadata_edit") + " ", e);
        }
    }

    /**
     * enable ordering for metadata values
     */
    protected void addPositionToMetadata()
    {
        for (EditorItemBean eib : items)
        {
            int pos = 0;
            for (SuperMetadataBean smb : eib.getMetadata())
            {
                smb.setPos(pos);
                pos++;
            }
        }
    }

    public abstract void initialize();

    public abstract boolean prepareUpdate();

    public abstract boolean validateMetadataofImages();

    public abstract void addMetadata(int imagePos, int metadataPos);

    public abstract void addMetadata(EditorItemBean item, int metadataPos);

    public abstract void removeMetadata(int imagePos, int metadataPos);

    public abstract void removeMetadata(EditorItemBean item, int metadataPos);

    /**
     * Create a new Metadata according to current Editor configuration.
     * 
     * @return
     */
    protected Metadata newMetadata()
    {
        if (statement != null)
        {
            return MetadataFactory.createMetadata(statement);
        }
        return null;
    }

    public List<EditorItemBean> getItems()
    {
        return items;
    }

    public void setItems(List<EditorItemBean> items)
    {
        this.items = items;
    }

    public Statement getStatement()
    {
        return statement;
    }

    public void setStatement(Statement statement)
    {
        this.statement = statement;
    }

    public MetadataProfile getProfile()
    {
        return profile;
    }

    public void setProfile(MetadataProfile profile)
    {
        this.profile = profile;
    }
}
