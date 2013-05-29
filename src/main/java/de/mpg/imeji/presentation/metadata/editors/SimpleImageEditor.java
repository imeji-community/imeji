/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.metadata.editors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.mpg.imeji.logic.util.MetadataFactory;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.presentation.metadata.EditorItemBean;
import de.mpg.imeji.presentation.metadata.SuperMetadataBean;
import de.mpg.imeji.presentation.metadata.util.MetadataHelper;
import de.mpg.imeji.presentation.util.ProfileHelper;

/**
 * Editor for one item (by the item detail page)
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class SimpleImageEditor extends MetadataEditor
{
    private List<Statement> statementChilds = new ArrayList<Statement>();

    /**
     * Editor for one item (by the item detail page)
     * 
     * @param items
     * @param profile
     * @param statement
     */
    public SimpleImageEditor(List<Item> items, MetadataProfile profile, Statement statement)
    {
        super(items, profile, statement);
    }

    /**
     * Convenient constructor for one {@link Item}
     * 
     * @param item
     * @param profile
     * @param statement
     */
    public SimpleImageEditor(Item item, MetadataProfile profile, Statement statement)
    {
        super(Arrays.asList(item), profile, statement);
    }

    @Override
    public void initialize()
    {
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
                {
                   // eib.getMetadata().get(i).setPos(i);
                }
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
        // TODO Auto-generated method stub
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
        statementChilds = ProfileHelper.getChilds(getStatement(), profile, false);
        if (metadataPos + statementChilds.size() <= eib.getMetadata().size())
        {
            metadataPos = metadataPos + statementChilds.size() + 1;
            Metadata md = MetadataFactory.createMetadata(getStatement());
            md.setPos(metadataPos);
            eib.getMetadata().add(metadataPos, new SuperMetadataBean(md, getStatement()));
            addChilds(eib, metadataPos);
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

    /**
     * Add all {@link Metadata} that are defined as a child of the current {@link Statement}
     * 
     * @param eib
     * @param metadataPos
     */
    private void addChilds(EditorItemBean eib, int metadataPos)
    {
        for (Statement st : statementChilds)
        {
            metadataPos = metadataPos + 1;
            Metadata md = MetadataFactory.createMetadata(st);
            md.setPos(metadataPos);
            eib.getMetadata().add(metadataPos, new SuperMetadataBean(md, getStatement()));
        }
    }
}
