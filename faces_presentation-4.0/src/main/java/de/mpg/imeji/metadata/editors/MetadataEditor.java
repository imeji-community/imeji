/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.imeji.metadata.editors;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.util.ProfileHelper;
import de.mpg.jena.controller.ImageController;
import de.mpg.jena.vo.Image;
import de.mpg.jena.vo.ImageMetadata;
import de.mpg.jena.vo.MetadataProfile;
import de.mpg.jena.vo.Statement;

public abstract class  MetadataEditor 
{	
	protected List<Image> images = new ArrayList<Image>();
	protected Statement statement;
	protected MetadataProfile profile;
	//protected Validator validator;
	
	private static Logger logger = Logger.getLogger(MetadataEditor.class);
	
	/**
	 * Editor: Edit a list of images for one statement.
	 * @param images
	 * @param statement
	 */
	public MetadataEditor(List<Image> images, MetadataProfile profile, Statement statement)
	{
		this.statement = statement;
		this.profile = profile;
		this.images = images;
		initialize();
	}
	
	public void save()
	{
		SessionBean sb = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
		ImageController ic = new ImageController(sb.getUser());
        try 
        {
			if (prepareUpdate())
			{
				if (validateMetadataofImages()) 
				{
					try 
					{
						addPositionToMetadata();
						ic.update(images);
						BeanHelper.info(sb.getMessage("success_editor_edit"));
						String str = images.size() +" " + sb.getMessage("success_editor_images");
						if (images.size() == 1) str = sb.getMessage("success_editor_image");
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
		for (Image im : images)
		{
			int pos = 0;
			for (ImageMetadata md : im.getMetadataSet().getMetadata())
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
	
	public abstract void addMetadata(Image image, int metadataPos);

	public abstract void removeMetadata(int imagePos, int metadataPos);
	
	public abstract void removeMetadata(Image image, int metadataPos);
	
	 /**
	  * Create a new Metadata according to current Editor configuration.
	  * @return
	  */
	protected ImageMetadata newMetadata()
	{
		if (statement != null)
		{
//			return MetadataFactory.newMetadata(statement);
		}
		return null;
	}

	public List<Image> getImages() {
		return images;
	}

	public void setImages(List<Image> images) 
	{
		this.images = images;
	}

	public Statement getStatement() {
		return statement;
	}

	public void setStatement(Statement statement) {
		this.statement = statement;
	}

	public MetadataProfile getProfile() {
		return profile;
	}

	public void setProfile(MetadataProfile profile) {
		this.profile = profile;
	}
	
}
