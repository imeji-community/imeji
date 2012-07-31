/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.metadata.editors;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.util.MetadataFactory;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.presentation.beans.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;

public abstract class MetadataEditor
{
    protected List<Item> items = new ArrayList<Item>();
    protected Statement statement;
    protected MetadataProfile profile;
    // protected Validator validator;
    private static Logger logger = Logger.getLogger(MetadataEditor.class);

    /**
     * Editor: Edit a list of images for one statement.
     * 
     * @param items
     * @param statement
     */
    public MetadataEditor(List<Item> items, MetadataProfile profile, Statement statement)
    {
        this.statement = statement;
        this.profile = profile;
        this.items = items;
        initialize();
    }

    public void reset()
    {
        items = new ArrayList<Item>();
        statement = null;
        profile = null;
    }

    public void save()
    {
        SessionBean sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        ItemController ic = new ItemController(sb.getUser());
        logger.info("Start Save update");
        try
        {
            if (prepareUpdate())
            {
                logger.info("update prepared");
                if (validateMetadataofImages())
                {
                    logger.info("update validate");
                    try
                    {
                        addPositionToMetadata();
                        logger.info("update position md added");
                        ic.update(items);
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
            throw new RuntimeException(sb.getMessage("error_metadata_edit") + " " + e);
        }
    }

    /**
     * enable ordering for metadata values
     */
    private void addPositionToMetadata()
    {
        for (Item im : items)
        {
            int pos = 0;
            for (Metadata md : im.getMetadataSet().getMetadata())
            {
                md.setPos(pos);
                pos++;
            }
        }
    }

    public abstract void initialize();

    public abstract boolean prepareUpdate();

    public abstract boolean validateMetadataofImages();

    public abstract void addMetadata(int imagePos, int metadataPos);

    public abstract void addMetadata(Item item, int metadataPos);

    public abstract void removeMetadata(int imagePos, int metadataPos);

    public abstract void removeMetadata(Item item, int metadataPos);

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

    public List<Item> getImages()
    {
        return items;
    }

    public void setImages(List<Item> items)
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
