package de.mpg.jena;

import java.lang.annotation.Annotation;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import thewebsemantic.Bean2RDF;
import thewebsemantic.NotFoundException;
import thewebsemantic.RDF2Bean;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.tdb.sys.SystemTDB;

import de.mpg.imeji.util.PropertyReader;
import de.mpg.jena.util.Counter;
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
		try 
		{
			tdbPath = PropertyReader.getProperty("imeji.tdb.path");
		} 
		catch (Exception e) 
		{
			throw new RuntimeException("Error reading property imeji.tdb.path", e);
		}
		init(tdbPath);
	}
	
	public static void init(String path)
	{
		logger.info("Initializing Jena models...");
		tdbPath = path;
		
		imejiDataSet = TDBFactory.createDataset(tdbPath);

		collectionModel = ImejiJena.initModel(getModelName(CollectionImeji.class));
		albumModel = ImejiJena.initModel(getModelName(Album.class));
		imageModel =ImejiJena.initModel(getModelName(Image.class));
		userModel = ImejiJena.initModel(getModelName(User.class));
		profileModel = ImejiJena.initModel(getModelName(MetadataProfile.class));

		logger.info("... done!");
		
		// Counter init
		logger.info("Initializing counter...");
		initCounter();
		logger.info("... done!");
		
		logger.info("Jena file access : " + SystemTDB.fileMode().name());
		logger.info("Jena is 64 bit system : " + SystemTDB.is64bitSystem);
	}

	private static Model initModel(String name)
	{
		TDBFactory.createNamedModel(name, tdbPath);
		Model m = imejiDataSet.getNamedModel(name);
		ImejiBean2RDF imejiBean2RDF = new ImejiBean2RDF(m);
		imejiBean2RDF.cleanGraph();
		return m;
	}

	public static String getModelName(Class<?> voClass)
	{
		Annotation rdfTypeAnn = voClass.getAnnotation(thewebsemantic.RdfType.class);
		return rdfTypeAnn.toString().split("@thewebsemantic.RdfType\\(value=")[1].split("\\)")[0];
	}

	private static void initCounter()
	{
		int counterFirstValue = 0;
		try 
		{
			counterFirstValue = Integer.parseInt(PropertyReader.getProperty("imeji.counter.first.value"));
		} 
		catch (Exception e) 
		{
			logger.warn("Property imeji.counter.first.value not found!!! Add property to your property file. (IGNORE BY UNIT TESTS)");
		} 

		Counter c = new Counter();
		try
		{
			c = new RDF2Bean(ImejiJena.imejiDataSet.getDefaultModel()).load(Counter.class, 0);
			if (c.getCounter() < counterFirstValue ) createNewCouter(c, counterFirstValue);
			logger.info("Counter found with value : " + c.getCounter());
		}
		catch (NotFoundException e)
		{
			logger.warn("Counter not found, creating a new one...");
			createNewCouter(c, counterFirstValue);
		}
	}
	
	private static void createNewCouter(Counter c, int counterFirstValue)
	{
		c.setCounter(counterFirstValue);
		Bean2RDF bean2RDF = new Bean2RDF(ImejiJena.imejiDataSet.getDefaultModel());
		bean2RDF.save(c);
		logger.info("New Counter created");
	}

}
