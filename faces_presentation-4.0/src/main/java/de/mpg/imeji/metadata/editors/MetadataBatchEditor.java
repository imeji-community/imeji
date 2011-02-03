package de.mpg.imeji.metadata.editors;

import java.util.ArrayList;
import java.util.List;

import de.mpg.imeji.util.ProfileHelper;
import de.mpg.jena.vo.Image;
import de.mpg.jena.vo.ImageMetadata;
import de.mpg.jena.vo.MetadataProfile;
import de.mpg.jena.vo.Statement;
import de.mpg.jena.vo.complextypes.util.ComplexTypeHelper;

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
	public boolean prepareUpdate() 
	{
		if (images.size() == 0)
		{
			return false;
		}
		ImageMetadata md = this.images.get(0).getMetadata().get(0);
		for (Image im: originalImages)
		{
			if (erase) 
			{
				 im = eraseOldMetadata(im);
			}
			im.getMetadata().add(md);
		}
		images = originalImages;
		return true;
	}
	

	@Override
	public boolean validateMetadataofImages() 
	{
//		for (Image im : images)
//		{
//			validator = new Validator(im.getMetadata(), profile);
//			if (!(validator.valid()))
//			{
//				return false;
//			}
//		}
		return true;
	}
	
	private Image eraseOldMetadata(Image im)
	{
		for (int i=0; i<im.getMetadata().size(); i++)
		{
			if (im.getMetadata().get(i).getName().equals(statement.getName()))
			{
				im.getMetadata().remove(i);
				i = 0;
			}
		}
		return im;
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
