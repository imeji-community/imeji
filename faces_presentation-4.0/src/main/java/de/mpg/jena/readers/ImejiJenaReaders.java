package de.mpg.jena.readers;

import thewebsemantic.RDF2Bean;

import com.hp.hpl.jena.rdf.model.Model;

import de.mpg.jena.ImejiJena;

public class ImejiJenaReaders 
{
	public static RDF2Bean imageReader = new RDF2Bean(ImejiJena.imageModel);
	public static RDF2Bean collectionReader = new RDF2Bean(ImejiJena.collectionModel);
	public static RDF2Bean profileReader = new RDF2Bean(ImejiJena.profileModel);
	public static RDF2Bean albumReader = new RDF2Bean(ImejiJena.albumModel);
	public static RDF2Bean userReader = new RDF2Bean(ImejiJena.userModel);
	
	
	public static RDF2Bean getReader(Model m)
	{
		
		if (ImejiJena.imageModel.equals(m))
		{
			return imageReader;
		}
		else if (ImejiJena.collectionModel.equals(m))
		{
			return collectionReader;
		}
		else if (ImejiJena.profileModel.equals(m))
		{
			return profileReader;
		}
		else if (ImejiJena.albumModel.equals(m))
		{
			return albumReader;
		}
		else if (ImejiJena.userModel.equals(m))
		{
			return userReader;
		}
		return null;
	}
}
