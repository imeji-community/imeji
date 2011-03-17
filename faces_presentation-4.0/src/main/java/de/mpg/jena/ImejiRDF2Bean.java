package de.mpg.jena;

import java.awt.List;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import thewebsemantic.RDF2Bean;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;

import de.mpg.jena.controller.DataFactory;
import de.mpg.jena.security.Operations.OperationsType;
import de.mpg.jena.security.Security;
import de.mpg.jena.util.ObjectHelper;
import de.mpg.jena.vo.Image;
import de.mpg.jena.vo.User;

public class ImejiRDF2Bean 
{
	private RDF2Bean rdf2Bean;
	private Model model;
	private static Logger logger = Logger.getLogger(ImejiBean2RDF.class);
	
	public ImejiRDF2Bean(Model model) 
	{
		rdf2Bean = new RDF2Bean(model);
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
