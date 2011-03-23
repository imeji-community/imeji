package de.mpg.jena;

import java.net.URI;

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
	
	
	public Object load(String uri, User user)
	{
		try 
		{
			Security security = new Security();
			Object o = rdf2Bean.load(uri);
			boolean b = security.check(OperationsType.READ, user, o);
			if (!security.check(OperationsType.READ, user, o)) 
			{
				if (o instanceof Image) removePrivateImages((Image)o);
			}
			return ObjectHelper.castAllHashSetToList(o);
		} 
		catch (thewebsemantic.NotFoundException e) 
		{
			throw e;
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return null;
	
	}
	//TODO
	public <T> T load(Class<T> c, String id)
	{
		try 
		{
			return (T) ObjectHelper.castAllHashSetToList(rdf2Bean.loadDeep(c, id));
		} 
		catch (thewebsemantic.NotFoundException e) 
		{
			logger.error(id + " not found for class " + c);
		}
		catch (Exception e) 
		{
			logger.error("Error loading object:" + e);
		}
		return null;
		
	}
	
	public <T> T load(Class<T> c)
	{
		return (T) rdf2Bean.loadDeep(c);
	}
	
	public void removePrivateImages(Image im)
	{
		im.setThumbnailImageUrl(URI.create("private"));
		im.setWebImageUrl(URI.create("private"));
		im.setFullImageUrl(URI.create("private"));
	}
	
}
