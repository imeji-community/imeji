package de.mpg.jena;

import java.lang.annotation.Annotation;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.tdb.TDBFactory;

import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.jena.vo.Album;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.Image;
import de.mpg.jena.vo.MetadataProfile;
import de.mpg.jena.vo.User;

public class ImejiJena
{
	public static String tdbPath = null;
	
	public static Model collectionModel;
	public static Model albumModel;
	public static Model imageModel;
	public static Model userModel;
	public static Model profileModel;
	public static Dataset imejiDataSet;
	
	private static Logger logger = Logger.getLogger(ImejiJena.class);
	
	public static void init()
	{
		logger.info("Initializing Jena models...");
		try 
		{
			tdbPath = PropertyReader.getProperty("imeji.tdb.path");
			//FOR TESTING: SHOULD BE OUTCOMMENTED FOR DEPLOYMENT
			//tdbPath = "R://imeji_tdb//imeji_data";
		} 
		catch (Exception e) 
		{
			throw new RuntimeException("Error reading property imeji.tdb.path", e);
		}
		imejiDataSet = TDBFactory.createDataset(tdbPath);
		
		collectionModel = ImejiJena.initModel(getModelName(CollectionImeji.class));
		albumModel = ImejiJena.initModel(getModelName(Album.class));
		imageModel =ImejiJena.initModel(getModelName(Image.class));
		userModel = ImejiJena.initModel(getModelName(User.class));
		profileModel = ImejiJena.initModel(getModelName(MetadataProfile.class));
	
		logger.info("... done!");
	}
	
	private static Model initModel(String name)
	{
		//String filename = tdbPath + "/" + name;
		//(new File(filename)).mkdirs();
		TDBFactory.createNamedModel(name, tdbPath);
		return imejiDataSet.getNamedModel(name);
	}
	
	public static String getModelName(Class<?> voClass)
	{
		 Annotation rdfTypeAnn = voClass.getAnnotation(thewebsemantic.RdfType.class);
	     return rdfTypeAnn.toString().split("@thewebsemantic.RdfType\\(value=")[1].split("\\)")[0];
	}
}
