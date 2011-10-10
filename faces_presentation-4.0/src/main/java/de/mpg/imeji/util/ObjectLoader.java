package de.mpg.imeji.util;

import java.net.URI;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import de.mpg.imeji.beans.SessionBean;
import de.mpg.jena.ImejiJena;
import de.mpg.jena.ImejiRDF2Bean;
import de.mpg.jena.controller.AlbumController;
import de.mpg.jena.controller.CollectionController;
import de.mpg.jena.controller.ImageController;
import de.mpg.jena.controller.ProfileController;
import de.mpg.jena.controller.UserController;
import de.mpg.jena.vo.Album;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.Image;
import de.mpg.jena.vo.MetadataProfile;
import de.mpg.jena.vo.Statement;
import de.mpg.jena.vo.User;

public class ObjectLoader 
{
	private static Logger logger = Logger.getLogger(ObjectLoader.class);
	public static CollectionImeji loadCollection(URI id, User user)
	{
		try 
		{
			CollectionController cl = new CollectionController(user);
			return cl.retrieve(id);
		} 
		catch (thewebsemantic.NotFoundException e) 
		{
			writeErrorNotFound("collection", id);
		}
		catch (Exception e) 
		{
			writeException(e, id.toString());
		}
		return null;
	}
	
	public static Album loadAlbum(URI id, User user)
	{
        try 
        {
        	AlbumController ac = new AlbumController(user); 
        	return ac.retrieve(id);
		} 
        catch (thewebsemantic.NotFoundException e) 
		{
			writeErrorNotFound("album", id);
		}
        catch (Exception e) 
		{
			writeException(e, id.toString());
		}
		return null;
	}
	
	
	public static Image loadImage(URI id, User user)
	{
		try 
        {
    		ImageController ic = new ImageController(user);
         	return ic.retrieve(id);
 		} 
		catch (thewebsemantic.NotFoundException e) 
		{
			writeErrorNotFound("image", id);
		}
	    catch (Exception e) 
	    {
			writeException(e, id.toString());
		}
		return null;
	}
	
	public static User loadUser(String email, User user)
	{
		try 
        {
			UserController uc = new UserController(user);
			return uc.retrieve(email);
		} 
		catch (thewebsemantic.NotFoundException e) 
		{
			writeErrorNotFound("user", URI.create(email));
		}
	    catch (Exception e) 
	    {
			writeException(e, email);
		}
	    return null;
	}
	
	public static MetadataProfile loadProfile(URI id, User user)
	{
		try 
        {
    		ProfileController pc = new ProfileController(user);
    		MetadataProfile p = pc.retrieve(id);
    		Collections.sort((List<Statement>) p.getStatements());
         	return p;
 		} 
		catch (thewebsemantic.NotFoundException e) 
		{
			writeErrorNotFound("profile", id);
		}
	    catch (Exception e) 
	    {
			writeException(e, id.toString());
		}
		return null;
	}
		
	private static void writeErrorNotFound(String objectType, URI id)
	{
		BeanHelper.error(((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getLabel(objectType) + " " + id + " "
				+ ((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getLabel("not_found"));
	}
	
	private static void writeException(Exception e, String id)
	{
		BeanHelper.error(((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getLabel("error") + " " );
		logger.error("Error Object loader for " + id, e);
	}
	
}
