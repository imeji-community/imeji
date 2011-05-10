package de.mpg.jena;

import java.net.URI;
import java.util.Collection;

import org.apache.log4j.Logger;

import thewebsemantic.RDF2Bean;

import com.hp.hpl.jena.rdf.model.Model;

import de.mpg.jena.readers.ImejiJenaReaders;
import de.mpg.jena.security.Operations.OperationsType;
import de.mpg.jena.security.Security;
import de.mpg.jena.util.ObjectHelper;
import de.mpg.jena.vo.Image;
import de.mpg.jena.vo.User;

public class ImejiRDF2Bean 
{
	private RDF2Bean rdf2Bean;
	private Model model;
	private static Logger logger = Logger.getLogger(ImejiRDF2Bean.class);
	
	public ImejiRDF2Bean(Model model) 
	{
		rdf2Bean = ImejiJenaReaders.getReader(model);
	}
	
	public Object load(String uri, User user) throws Exception
	{
		try 
		{
			Security security = new Security();
			Object o = rdf2Bean.load(uri);
			if (!security.check(OperationsType.READ, user, o)) 
			{
				if (o instanceof Image) 
				{
					removePrivateImages((Image)o, user);
				}
				else
				{
					throw new RuntimeException("Security Exception: " + user.getEmail() + " is not allowed to view " + uri);
				}
			}
			return ObjectHelper.castAllHashSetToList(o);
		} 
		catch (thewebsemantic.NotFoundException e) 
		{
			throw e;
		}
	}

	/**
	 * This method does not check security.
	 * 
	 * @deprecated
	 * @param <T>
	 * @param c
	 * @param id
	 * @return
	 */
	public <T> T load(Class<T> c, String id)
	{
		try 
		{
			return (T) ObjectHelper.castAllHashSetToList(rdf2Bean.loadDeep(c, id));
		} 
		catch (thewebsemantic.NotFoundException e) 
		{
			throw e;
		}
		catch (Exception e) 
		{
			logger.error("Error loading object:" + e);
		}
		return null;
		
	}
	
	public <T> Collection<T> load(Class<T> c)
	{
		return  rdf2Bean.loadDeep(c);
	}
	
	public void removePrivateImages(Image im, User user)
	{
		im.setThumbnailImageUrl(URI.create("private"));
		im.setWebImageUrl(URI.create("private"));
		im.setFullImageUrl(URI.create("private"));
		logger.error("User " + user.getEmail() +  " is not allowed to see image " + im.getId());
	}
	
}
