package de.mpg.escidoc.faces.container.collection;

import java.io.IOException;
import java.net.URISyntaxException;

import de.escidoc.schemas.container.x08.ContainerDocument.Container;
import de.mpg.escidoc.faces.container.FacesContainerVO;
import de.mpg.escidoc.faces.metadata.ScreenConfiguration;
import de.mpg.escidoc.services.common.referenceobjects.ContextRO;
import de.mpg.escidoc.services.framework.PropertyReader;

/**
 * A collection of Faces is defined by:
 * <br> * 1 context 
 * 
 * @author saquet
 *
 */
public class CollectionVO extends FacesContainerVO
{
	private ScreenConfiguration screenConfiguration = null;
	private String mdProfileId = null;
	

	/**
	 * Constructor with a {@link ScreenConfiguration} 
	 * @param screeConfiguation defines the md-profile of the items of the collection
	 * @throws URISyntaxException 
	 * @throws IOException 
	 */
	public CollectionVO(ScreenConfiguration screenConfiguration) throws IOException, URISyntaxException 
	{
		super();
		this.setContentModel(PropertyReader.getProperty("escidoc.faces.collection.content-model.id"));
		this.screenConfiguration = screenConfiguration;
		// TODO: change that.
		//this.mdProfileId = "escidoc:faces50";
	}
	
	public CollectionVO(FacesContainerVO fc)
	{
		super(fc);
	}
	
	public ScreenConfiguration getScreenConfiguration() 
	{
		return screenConfiguration;
	}

	public void setScreenConfiguration(ScreenConfiguration screenConfiguration) 
	{
		this.screenConfiguration = screenConfiguration;
	}

	/**
	 * @return the mdProfileId
	 */
	public String getMdProfileId() 
	{
		return mdProfileId;
	}

	/**
	 * @param mdProfileId the mdProfileId to set
	 */
	public void setMdProfileId(String mdProfileId) 
	{
		this.mdProfileId = mdProfileId;
	}
	
	
}
