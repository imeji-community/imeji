package de.mpg.imeji.metadata;

import java.util.ArrayList;
import java.util.List;

import de.mpg.jena.vo.Image;
import de.mpg.jena.vo.ImageMetadata;
import de.mpg.jena.vo.MetadataProfile;
import de.mpg.jena.vo.Statement;

public class MetadataBatchEditor extends MetadataEditor 
{
	private List<Image> originalImages;
	
	public MetadataBatchEditor(List<Image> images, MetadataProfile profile,	Statement statement) 
	{
		super(images, profile, statement);
	}

	@Override
	public void initialize(List<Image> images) 
	{
		originalImages = images;
		this.images = new ArrayList<Image>();
		this.images.add(new Image());
		this.images.get(0).getMetadata().add(newMetadata());
	}
	

	@Override
	public void prepareUpdate() 
	{
		ImageMetadata md = this.images.get(0).getMetadata().get(0);
		for (Image im: originalImages)
		{
			if (overwrite) 
			{
				 eraseOldMetadata(im);
			}
			else
			{
				im.getMetadata().add(md);
			}
		}
		images = originalImages;
	}
	
	private void eraseOldMetadata(Image im)
	{
		//TODO
	}

	@Override
	public void addMetadata(int imagePos, int metadataPos) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void addMetadata(Image image, int metadataPos)
	{
		if (metadataPos <= image.getMetadata().size()) 
		{
			image.getMetadata().add(metadataPos, newMetadata());
		}
	}

	@Override
	public void removeMetadata(int imagePos, int metadataPos) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void removeMetadata(Image image, int metadataPos) {
		// TODO Auto-generated method stub
	}
	
	

}
