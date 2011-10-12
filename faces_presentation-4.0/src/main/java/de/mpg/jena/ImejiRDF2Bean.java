package de.mpg.jena;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import thewebsemantic.NotBoundException;
import thewebsemantic.RDF2Bean;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Selector;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

import de.mpg.jena.readers.ImejiJenaReaders;
import de.mpg.jena.security.Operations.OperationsType;
import de.mpg.jena.security.Security;
import de.mpg.jena.util.ObjectHelper;
import de.mpg.jena.vo.Image;
import de.mpg.jena.vo.ImageMetadata;
import de.mpg.jena.vo.User;

public class ImejiRDF2Bean 
{
	private RDF2Bean rdf2Bean;
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
					if (user != null )
					{
						throw new RuntimeException("Security Exception: " + user.getEmail() + " is not allowed to view " + uri);
					}
					else
					{
						throw new RuntimeException("Security Exception: You need to log in to view " + uri);
					}
				}
			}
			
			if (o instanceof Image) 
			{
				sortMetadataAccordingToPosition((Image)o);
			}
			
			return ObjectHelper.castAllHashSetToList(o);
		} 
		catch (thewebsemantic.NotFoundException e) 
		{
			throw e;
		}
		catch (NotBoundException e) 
		{
			logger.warn(uri + " NOT BOUND! ", e);
			//test(uri);
			//logger.warn("...DONE");
		}
		return null;
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
	
	private void removePrivateImages(Image im, User user)
	{
		im.setThumbnailImageUrl(URI.create("private"));
		im.setWebImageUrl(URI.create("private"));
		im.setFullImageUrl(URI.create("private"));
	}
	
	private  void sortMetadataAccordingToPosition(Image im)
    {
    	List<ImageMetadata> mdSorted = new ArrayList<ImageMetadata>();
    	for (ImageMetadata md : im.getMetadataSet().getMetadata())
		{
    		if (md.getPos() < mdSorted.size()) mdSorted.add(md.getPos(), md);
    		else  mdSorted.add(md);
		}
    	im.getMetadataSet().setMetadata(mdSorted);
    }
	
	
	private void test(String uri)
	{
		 Resource r = rdf2Bean.getModel().getResource(uri);
		 
		 if (!hasMetadataSet(r))
		 {
			 Statement s = findMetadataSetPredicate(r);
			 
			 List<Statement> l =  new ArrayList<Statement>();//(s.getResource());
			 l.add(s);
			 for(Statement st : l)
			 {
				 System.out.println("Will remove " + st);
				 //System.out.println("with " + find(st.getResource()));
			 }
			 System.out.println("Removing... ");
			 rdf2Bean.getModel().remove(l);
			 System.out.println("... done!");
			 
		 }
	}
	
	private List<Statement> find(Resource r)
	{
		Selector selector = new SimpleSelector(r, null, (Resource)null);
		StmtIterator iter = rdf2Bean.getModel().listStatements(selector);
		while (iter.hasNext())
		{
			try{
				Statement s1 = iter.nextStatement();
				System.out.println(s1);
				find(s1.getResource());
			 }
			 catch (Exception e) {/*Do nothing*/}
		}
		return null;
	}
	
	private boolean hasMetadataSet(Resource r)
	{
		Selector selector = new SimpleSelector(r, null, (Resource)null);
		StmtIterator iter = rdf2Bean.getModel().listStatements(selector);
		int limit = 0;
		boolean hasMdSet = false;
		while (iter.hasNext() && limit < 500)
		{
			limit++;
			try{
				Statement s1 = iter.nextStatement();
				if (!hasMdSet)
				{
					hasMdSet = (hasMetadataSet(s1.getResource()) || s1.getResource().toString().equals("http://imeji.mpdl.mpg.de/metadataSet"));
				}
			 }
			 catch (Exception e) {/*Do nothing*/}
		}
		return hasMdSet;
	}
	
	private Statement findMetadataSetPredicate(Resource r)
	{
		Selector selector = new SimpleSelector(r, null, (Resource)null);
		StmtIterator iter = rdf2Bean.getModel().listStatements(selector);
		int limit = 0;
		
		while (iter.hasNext() && limit < 500)
		{
			limit++;
			try{
				Statement s1 = iter.nextStatement();
				if (s1.getPredicate().toString().equals("http://imeji.mpdl.mpg.de/metadataSet"))
				{
					return s1;
				}
				Statement st = findMetadataSetPredicate(s1.getResource());
				if (st != null) return st;
			 }
			 catch (Exception e) {/*Do nothing*/}
		}
		return null;
	}
	
	private List<Statement> findAllStatements(Resource r)
	{
		Selector selector = new SimpleSelector(r, null, (Resource)null);
		StmtIterator iter = rdf2Bean.getModel().listStatements(selector);
		int limit = 0;
		List<Statement> list = new ArrayList<Statement>();
		while (iter.hasNext() && limit < 500)
		{
			limit++;
			try{
				Statement s = iter.nextStatement();
				if (!s.getPredicate().toString().equals("http://imeji.mpdl.mpg.de/profile"))
				{
					list.add(s);
					list.addAll(findAllStatements(s.getResource()));
				}
			 }
			 catch (Exception e) {/*Do nothing*/}
		}
		return list;
	}
	
//	private Statement findMetadataSet()
//	{
//		Selector selector = new SimpleSelector(r, null, (Resource)null);
//		StmtIterator iter = rdf2Bean.getModel().listStatements(selector);
//		int limit = 0;
//		Resource metadataset = null;
//		while (iter.hasNext() && limit < 500)
//		{
//			limit++;
//			try{
//				
//				 Statement s1 = iter.nextStatement();
//				 //System.out.println( s1.getPredicate());
//				 if (s1.getPredicate().getURI().equals("http://imeji.mpdl.mpg.de/metadataSet"))
//				 {
//					 metadataset = s1.getResource();
//				 }
//				 //System.out.println(s1.getResource().toString());
//				 if (s1.getResource().toString().equals("http://imeji.mpdl.mpg.de/metadataSet"))
//				 {
//					 return state System.out.println("MDSET");
//				 }
//				 findMetadataSet(s1.getResource());
//			 }
//			 catch (Exception e) {
//				// if (e.getMessage() != null)
//				//System.out.println(r.getURI() + " - " + e);
//				// System.out.println("***************");
//			}
//		}
//		return metadataset;
//	}
	
	
	private void deleteObjects(String uri)
	    {
	        Resource r = rdf2Bean.getModel().getResource(uri);
	        StmtIterator it = r.listProperties(RDF.type);
	        while (it.hasNext()) {
				Resource r1 = it.nextStatement().getResource();
	        }

	        if (rdf2Bean.getModel().containsResource(r))
	        {
	            logger.info("Forced Delete of " + uri);
	            try{
		            Selector selector = new SimpleSelector(null, null, r);
		            StmtIterator iter = rdf2Bean.getModel().listStatements(selector);
		            while (iter.hasNext())
		            {
		                Statement mdToDelete = iter.nextStatement();
		                Resource sub = mdToDelete.getSubject();
		                Selector selector2 = new SimpleSelector(null, null, sub);
		                StmtIterator iter2 = rdf2Bean.getModel().listStatements(selector2);
		                while (iter2.hasNext())
		                {
		                    Statement imageWithMd = iter2.nextStatement();
		                    Selector selector3 = new SimpleSelector(null, imageWithMd.getPredicate(), (Resource)null);
		                    StmtIterator iter3 = rdf2Bean.getModel().listStatements(selector3);
		                    while (iter3.hasNext())
		                    {
		                        Statement statementToDelete = iter3.nextStatement();
		                        if (mdToDelete.getSubject().getId().equals(statementToDelete.getResource().getId()))
		                        {
		                            try
		                            {
		                            	rdf2Bean.getModel().remove(statementToDelete);
		                            }
		                            catch (Exception e) 
		                            {	
		                            	logger.warn("Error deleting object" + statementToDelete.getResource().getId());
		                            }
		                            iter3 = rdf2Bean.getModel().listStatements(selector3);
		                        }
		                    }
		                }
		                try
		                {
		                	rdf2Bean.getModel().remove(mdToDelete);
		                }
		                catch (Exception e) 
		                {	
		                	logger.warn("Error deleting object" + mdToDelete.getResource().getId());
		                }
		            }
	            }
	            catch (Exception e) {
					logger.error("PROBLEM by Forced delete!" + e.getMessage());
				}
	        }
	        else
	        {
	            logger.warn("Error forced Delete of " + uri + ". Resource was not found.");
	        }
	    }
	
}
