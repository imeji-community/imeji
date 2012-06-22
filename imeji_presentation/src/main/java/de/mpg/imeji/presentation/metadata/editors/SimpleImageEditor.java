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
import de.mpg.imeji.presentation.metadata.util.MetadataHelper;

public class SimpleImageEditor extends MetadataEditor
{
	public SimpleImageEditor(List<Item> items, MetadataProfile profile, Statement statement) 
	{
		super(items, profile, statement);
		this.items = items;
	}

	@Override
	public void initialize() 
	{
	}
	
	@Override
	public boolean prepareUpdate() 
	{
		for (Item im : items)
		{
			for(int i=0; i< im.getMetadataSet().getMetadata().size(); i++)
			{
				if (MetadataHelper.isEmpty(((List<Metadata>)im.getMetadataSet().getMetadata()).get(i)))
				{
					((List<Metadata>)im.getMetadataSet().getMetadata()).remove(i);
				}
				((List<Metadata>)im.getMetadataSet().getMetadata()).get(i).setPos(i);
				
				MetadataHelper.setConeID(((List<Metadata>)im.getMetadataSet().getMetadata()).get(i));
			}
		}
		if (items.size() == 0)
		{
			return false;
		}
		return true;
	}

	@Override
	public boolean validateMetadataofImages() {
		// TODO Auto-generated method stub
		return true;
	}

	public void addMetadata(int imagePos, int metadataPos)
    {
        if (imagePos < items.size()) 
        {
			addMetadata(items.get(imagePos), metadataPos);
		}
    }
	
	public void addMetadata(Item item, int metadataPos)
	{
		if (metadataPos <= item.getMetadataSet().getMetadata().size()) 
		{
			((List<Metadata>)item.getMetadataSet().getMetadata()).add(metadataPos + 1, MetadataFactory.createMetadata(getStatement()));
		}
	}
	 
	public void removeMetadata(int imagePos, int metadataPos)
	{
		if (imagePos < items.size()) 
		{
			removeMetadata(items.get(imagePos), metadataPos);
		}
	}
	
	public void removeMetadata(Item item, int metadataPos)
	{
		if (metadataPos < item.getMetadataSet().getMetadata().size()) 
		{
			((List<Metadata>)item.getMetadataSet().getMetadata()).remove(metadataPos);
		}
	}

}
