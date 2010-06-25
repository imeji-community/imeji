package de.mpg.escidoc.faces.container.collection;

import java.io.IOException;
import java.net.URISyntaxException;

import de.mpg.escidoc.faces.container.FacesContainerVO;
import de.mpg.escidoc.faces.metadata.ScreenManager;
import de.mpg.escidoc.services.framework.PropertyReader;

/**
 * A collection of Faces is defined by:
 * <br> * 1 context (via ScreenManager)
 * 
 * @author saquet
 *
 */
public class CollectionVO extends FacesContainerVO
{
	
	/**
	 * Default Constructor
	 * @throws URISyntaxException 
	 * @throws IOException 
	 */
	public CollectionVO() throws IOException, URISyntaxException
	{
		super();
		this.setContentModel(PropertyReader.getProperty("escidoc.faces.collection.content-model.id"));
	}
	/**
	 * Constructor with a screenManager
	 * @param screenManager
	 * @throws URISyntaxException 
	 * @throws IOException 
	 */
	public CollectionVO(ScreenManager screenManager) throws IOException, URISyntaxException 
	{
		super();
		this.setContentModel(PropertyReader.getProperty("escidoc.faces.collection.content-model.id"));
	}
}
