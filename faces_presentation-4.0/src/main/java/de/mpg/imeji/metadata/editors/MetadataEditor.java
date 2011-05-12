package de.mpg.imeji.metadata.editors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.richfaces.json.JSONCollection;
import org.richfaces.json.JSONException;

import thewebsemantic.LocalizedString;
import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.util.ProfileHelper;
import de.mpg.jena.controller.ImageController;
//import de.mpg.jena.util.MetadataFactory;
import de.mpg.jena.vo.Image;
import de.mpg.jena.vo.ImageMetadata;
import de.mpg.jena.vo.MetadataProfile;
import de.mpg.jena.vo.Statement;

public abstract class  MetadataEditor 
{	
	protected List<Image> images = new ArrayList<Image>();
	protected Statement statement;
	protected MetadataProfile profile;
	protected boolean erase = false;
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
		initialize(images);
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
						ic.update(images);
						BeanHelper.info("Edit done!");
						String str = images.size() +" images edited";
						if (images.size() == 1) str = "One image edited";
						BeanHelper.info(str);
					} 
					catch (Exception e) 
					{
						logger.error(e);
						BeanHelper.warn("Edit error: " + e.getMessage());
					}
				}
				else
				{
					BeanHelper.error("Validation error!");
				}
			}
			else
			{
				BeanHelper.error("No Images to edit!");
			}
		} 
        catch (Exception e) 
		{
			throw new RuntimeException("Metadata Editor error (Update images): " + e);
		}
	}

	public abstract void initialize(List<Image> images);
	
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
	
	protected boolean hasProfile(Image image)
	{
		if (profile != null && profile.getId().equals(ProfileHelper.loadProfile(image).getId()))
		{
			return true;
		}
		return false;
	}

	public List<Image> getImages() {
		return images;
	}

	public void setImages(List<Image> images) {
		this.images = images;
	}

	public Statement getStatement() {
		return statement;
	}

	public void setStatement(Statement statement) {
		this.statement = statement;
	}

	public boolean isErase() {
		return erase;
	}

	public void setErase(boolean erase) {
		this.erase = erase;
	}

	public MetadataProfile getProfile() {
		return profile;
	}

	public void setProfile(MetadataProfile profile) {
		this.profile = profile;
	}
	
}
