package de.mpg.imeji.metadata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.richfaces.json.JSONCollection;
import org.richfaces.json.JSONException;

import thewebsemantic.LocalizedString;

import com.hp.hpl.jena.rdf.arp.StatementHandler;

import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.image.ImageBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.util.ProfileHelper;
import de.mpg.jena.controller.ImageController;
import de.mpg.jena.vo.Image;
import de.mpg.jena.vo.ImageMetadata;
import de.mpg.jena.vo.MetadataProfile;
import de.mpg.jena.vo.Statement;
import de.mpg.jena.vo.complextypes.util.ComplexTypeHelper;

public abstract class  MetadataEditor 
{	
	// Images before Edit
	private List<Image> before = new ArrayList<Image>();
	// Images used by Editor
	protected List<Image> images = new ArrayList<Image>();
	protected Statement statement;
	protected MetadataProfile profile;
	protected boolean overwrite = false;
	
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
			prepareUpdate();
        	ic.update(images);
		} 
        catch (Exception e) 
		{
			throw new RuntimeException("Metadata Editor error (Update images): " + e);
		}
	}

	public abstract void initialize(List<Image> images);
	
	public abstract void prepareUpdate();
	
	public abstract void addMetadata(int imagePos, int metadataPos);   
	
	public abstract void addMetadata(Image image, int metadataPos);

	public abstract void removeMetadata(int imagePos, int metadataPos);
	
	public abstract void removeMetadata(Image image, int metadataPos);
	
	public boolean getDoAutoComplete()
	{
		if (statement != null) 
        {
        	 if ( statement.getLiteralConstraints() != null && statement.getLiteralConstraints().size() > 0)
        	 {
        		 return true;
        	 }
             if (statement.getVocabulary() != null)
             {
            	 return true;
             }
        }
        return false;
	}
	
	public List<Object> autoComplete(Object suggest)
    {
        if (statement.getLiteralConstraints() != null && statement.getLiteralConstraints().size() > 0)
        {
            List<String> suggestions = new ArrayList<String>();
            List<String> literals = new ArrayList<String>();
            for (LocalizedString str : statement.getLiteralConstraints())
                literals.add(str.toString());
            for (String str : literals)
                if (str.toLowerCase().contains(suggest.toString().toLowerCase()))
                    suggestions.add(str);
            String json = "[";
            for (String str : suggestions)
                json += "{\"http_purl_org_dc_elements_1_1_title\" : \"" + str + "\"},";
            json = json.substring(0, json.length() - 1) + "]";
            JSONCollection jsc;
            try
            {
                jsc = new JSONCollection(json);
            }
            catch (JSONException e)
            {
                return null;
            }
            return Arrays.asList(jsc.toArray());
        }
        else if (statement.getVocabulary() != null)
        {
            if (suggest.toString().isEmpty())
                suggest = "a";
            if (!suggest.toString().isEmpty())
            {
                try
                {
                    HttpClient client = new HttpClient();
                    GetMethod getMethod = new GetMethod(statement.getVocabulary().toString()
                            + "?format=json&n=10&m=full&q=" + suggest);
                    client.executeMethod(getMethod);
                    JSONCollection jsc = new JSONCollection(getMethod.getResponseBodyAsString());
                    return Arrays.asList(jsc.toArray());
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
            }
        }
        return null;
    }
	
	 /**
	  * Create a new Metadata according to current Editor configuration.
	  * @return
	  */
	protected ImageMetadata newMetadata()
	{
		ImageMetadata md = new ImageMetadata(statement.getName(), ComplexTypeHelper.newComplexType(statement.getType()));
		return md;
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
	
}
