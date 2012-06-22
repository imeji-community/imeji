/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.imeji.presentation.metadata.editors;

import java.net.URI;
import java.util.List;

import de.mpg.imeji.logic.util.MetadataFactory;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.presentation.metadata.util.MetadataHelper;

public class MetadataMultipleEditor extends MetadataEditor
{
	public MetadataMultipleEditor(List<Item> items, MetadataProfile profile, Statement statement) 
	{
		super(items, profile, statement);
	}

	@Override
	public void initialize() 
	{
		boolean hasStatement = (statement != null);
		for (Item im : items) 
		{
			boolean empty = true;
			for(Metadata md : im.getMetadataSet().getMetadata())
			{
				if (hasStatement && md.getStatement()!= null && md.getStatement().equals(statement.getId()))
				{
					empty = false;
				}
			}
			if (empty && hasStatement)
			{
				addMetadata(im, 0);
			}
		}
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
				else 
				{
					((List<Metadata>)im.getMetadataSet().getMetadata()).get(i).setPos(i);
					MetadataHelper.setConeID(((List<Metadata>)im.getMetadataSet().getMetadata()).get(i));
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
		// Validate only first image since all images get the same metadata
//		validator = new Validator(images.get(0).getMetadata(), profile);
//		return validator.valid();
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
		    Metadata md = MetadataFactory.createMetadata(getStatement());
		    md.setId(URI.create(item.getMetadataSet().getId() + "/" + metadataPos));
			((List<Metadata>)item.getMetadataSet().getMetadata()).add(metadataPos, md);
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
