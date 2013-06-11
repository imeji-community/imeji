/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.metadata.editors;

import java.util.List;

import de.mpg.imeji.logic.util.MetadataFactory;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.presentation.metadata.EditorItemBean;
import de.mpg.imeji.presentation.metadata.SuperMetadataBean;
import de.mpg.imeji.presentation.metadata.util.MetadataHelper;

/**
 * Editor for multiple edit (edit selected items or edit all item of a collection)
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class MetadataMultipleEditor extends MetadataEditor
{
    /**
     * Editor for multiple edit (edit selected items or edit all items of a collection)
     * 
     * @param items
     * @param profile
     * @param statement
     */
    public MetadataMultipleEditor(List<Item> items, MetadataProfile profile, Statement statement)
    {
        super(items, profile, statement);
    }

    @Override
    public void initialize()
    {
        boolean hasStatement = (statement != null);
        for (EditorItemBean eib : items)
        {
            boolean empty = true;
            for (SuperMetadataBean smdb : eib.getMetadata())
            {
                if (hasStatement && smdb.getStatement() != null && smdb.getStatement().equals(statement.getId()))
                {
                    empty = false;
                }
            }
            if (empty && hasStatement)
            {
                addMetadata(eib, 0);
            }
        }
    }

    @Override
    public boolean prepareUpdate()
    {
        for (EditorItemBean eib : items)
        {
            for (int i = 0; i < eib.getMetadata().size(); i++)
            {
                if (MetadataHelper.isEmpty(eib.getMetadata().get(i).asMetadata()))
                {
                    eib.getMetadata().remove(i);
                }
                else
                    eib.getMetadata().get(i).setPos(i);
            }
        }
        if (items.size() == 0)
        {
            return false;
        }
        return true;
    }

    @Override
    public boolean validateMetadataofImages()
    {
        // Validate only first image since all images get the same metadata
        // validator = new Validator(images.get(0).getMetadata(), profile);
        // return validator.valid();
        return true;
    }

    @Override
    public void addMetadata(int imagePos, int metadataPos)
    {
        if (imagePos < items.size())
        {
            addMetadata(items.get(imagePos), metadataPos);
        }
    }

    @Override
    public void addMetadata(EditorItemBean eib, int metadataPos)
    {
        if (metadataPos <= eib.getMetadata().size())
        {
            Metadata md = MetadataFactory.createMetadata(getStatement());
            eib.getMetadata().add(metadataPos, new SuperMetadataBean(md, getStatement()));
        }
    }

    @Override
    public void removeMetadata(int imagePos, int metadataPos)
    {
        if (imagePos < items.size())
        {
            removeMetadata(items.get(imagePos), metadataPos);
        }
    }

    @Override
    public void removeMetadata(EditorItemBean eib, int metadataPos)
    {
        if (metadataPos < eib.getMetadata().size())
        {
            eib.getMetadata().remove(metadataPos);
        }
    }
}
