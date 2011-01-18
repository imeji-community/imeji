package de.mpg.imeji.metadata;

import java.util.List;

import de.mpg.jena.vo.Image;
import de.mpg.jena.vo.MetadataProfile;
import de.mpg.jena.vo.Statement;

public class MetadataMultipleEditor extends MetadataEditor
{
	public MetadataMultipleEditor(List<Image> images, MetadataProfile profile,Statement statement) 
	{
		super(images, profile, statement);
	}

	@Override
	public void initialize(List<Image> images) 
	{
		for (Image im : images) 
		{
			if (hasProfile(im)) 
			{
				//this.before.add(im);
				boolean empty = true;
				for (int i = 0; i < im.getMetadata().size(); i++) 
				{
					if (im.getMetadata().get(i).getName().equals(statement.getName()))
					{
						empty = false;
					}
				}
				if (empty) addMetadata(im, 0);
				this.images.add(im);
			}
		}
	}
	
	@Override
	public void prepareUpdate() {
		// TODO Auto-generated method stub
		
	}

	public void addMetadata(int imagePos, int metadataPos)
    {
        if (imagePos < images.size()) 
        {
			addMetadata(images.get(imagePos), metadataPos);
		}
    }
	
	public void addMetadata(Image image, int metadataPos)
	{
		if (metadataPos <= image.getMetadata().size()) 
		{
			image.getMetadata().add(metadataPos, newMetadata());
		}
	}
	 
	public void removeMetadata(int imagePos, int metadataPos)
	{
		if (imagePos < images.size()) 
		{
			removeMetadata(images.get(imagePos), metadataPos);
		}
	}
	
	public void removeMetadata(Image image, int metadataPos)
	{
		if (metadataPos <image.getMetadata().size()) 
		{
			image.getMetadata().remove(metadataPos);
		}
	}

}
