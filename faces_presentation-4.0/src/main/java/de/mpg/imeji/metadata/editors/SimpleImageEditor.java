package de.mpg.imeji.metadata.editors;

import java.util.List;

import de.mpg.jena.util.MetadataFactory;
import de.mpg.jena.vo.Image;
import de.mpg.jena.vo.ImageMetadata;
import de.mpg.jena.vo.MetadataProfile;
import de.mpg.jena.vo.Statement;

public class SimpleImageEditor extends MetadataEditor
{
	public SimpleImageEditor(List<Image> images, MetadataProfile profile,
			Statement statement) 
	{
		super(images, profile, statement);
		this.images = images;
	}

	@Override
	public void initialize(List<Image> images) 
	{

	}
	
	@Override
	public boolean prepareUpdate() {
		return true;
	}

	@Override
	public boolean validateMetadataofImages() {
		// TODO Auto-generated method stub
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
