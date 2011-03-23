package de.mpg.imeji.metadata.editors;

import java.util.List;

import de.mpg.imeji.metadata.util.MetadataHelper;
import de.mpg.jena.util.MetadataFactory;
import de.mpg.jena.vo.ComplexType;
import de.mpg.jena.vo.ComplexType.ComplexTypes;
import de.mpg.jena.vo.Image;
import de.mpg.jena.vo.ImageMetadata;
import de.mpg.jena.vo.MetadataProfile;
import de.mpg.jena.vo.Statement;
import de.mpg.jena.vo.complextypes.Date;

public class MetadataMultipleEditor extends MetadataEditor
{
	public MetadataMultipleEditor(List<Image> images, MetadataProfile profile, Statement statement) 
	{
		super(images, profile, statement);
	}

	@Override
	public void initialize(List<Image> images) 
	{
		boolean hasStatement = (statement != null);
		for (Image im : images) 
		{
			if (hasProfile(im)) 
			{
				boolean empty = true;
				for(ImageMetadata md : im.getMetadataSet().getMetadata())
				{
					if (hasStatement && md.getNamespace()!= null && md.getNamespace().equals(statement.getName()))
					{
						empty = false;
					}
				}
				if (empty && hasStatement) addMetadata(im, 0);
				this.images.add(im);
			}
		}
	}
	
	@Override
	public boolean prepareUpdate() 
	{
		for (Image im : images)
		{
			for(int i=0; i< im.getMetadataSet().getMetadata().size(); i++)
			{
				if (MetadataHelper.isEmpty(((List<ImageMetadata>)im.getMetadataSet().getMetadata()).get(i)))
				{
					((List<ImageMetadata>)im.getMetadataSet().getMetadata()).remove(i);
				}
				else 
				{
					((List<ImageMetadata>)im.getMetadataSet().getMetadata()).get(i).setPos(i);
				}
				
			}
		}
		if (images.size() == 0)
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
        if (imagePos < images.size()) 
        {
			addMetadata(images.get(imagePos), metadataPos);
		}
    }
	
	public void addMetadata(Image image, int metadataPos)
	{
		if (metadataPos <= image.getMetadataSet().getMetadata().size()) 
		{
			((List<ImageMetadata>)image.getMetadataSet().getMetadata()).add(metadataPos, MetadataFactory.newMetadata(getStatement()));
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
		if (metadataPos < image.getMetadataSet().getMetadata().size()) 
		{
			((List<ImageMetadata>)image.getMetadataSet().getMetadata()).remove(metadataPos);
		}
	}



}
