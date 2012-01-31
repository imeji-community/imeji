/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.jena;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;

import javax.servlet.jsp.tagext.TryCatchFinally;

import org.apache.log4j.Logger;

import thewebsemantic.Bean2RDF;
import thewebsemantic.RDF2Bean;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.shared.LockMRSW;

import de.mpg.jena.concurrency.locks.Locks;
import de.mpg.jena.controller.ImejiController;
import de.mpg.jena.security.Operations.OperationsType;
import de.mpg.jena.security.Security;
import de.mpg.jena.vo.Container;
import de.mpg.jena.vo.Image;
import de.mpg.jena.vo.MetadataProfile;
import de.mpg.jena.vo.User;

/**
 * Add Security and Locking control to Bean2RDF.
 *  
 * @author saquet
 *
 */
public class ImejiBean2RDF
{
	private static Bean2RDF bean2rdf;
	private Security security;
	private static Logger logger = Logger.getLogger(ImejiBean2RDF.class);

	private Model model;

	public ImejiBean2RDF(Model model) 
	{
		this.model = model;
		bean2rdf = new Bean2RDF(model);
		security = new Security();
	}

	public List<Object> toList(Object o)
	{
		List<Object> list = new ArrayList<Object>();
		list.add(o);
		return list;
	}

	public void create(List<Object> objects, User user) throws Exception
	{
		for (Object o : objects)
		{
			if (Locks.tryLock())
			{
				try 
				{
					beginTransaction(o, user, OperationsType.CREATE);
					bean2rdf.saveDeep(o);
					commitTransaction(o, user);
				}
				finally
				{
					Locks.releaseLockForWrite();
				}
			}
		}
		cleanGraph();
	}

	public void delete(List<Object> objects, User user) throws Exception 
	{
		for (Object o : objects)
		{
			if (Locks.tryLock())
			{
				try 
				{
					beginTransaction(o, user, OperationsType.DELETE);
					bean2rdf.delete(o);
					commitTransaction(o, user);
				} 
				finally
				{
					Locks.releaseLockForWrite();
				}
			}
		}
		cleanGraph();
	}

	public Resource saveDeep(List<Object> objects, User user) throws Exception
	{		
		for (Object o : objects)
		{
			if (Locks.tryLock())
			{
				try
				{
					beginTransaction(o, user, OperationsType.UPDATE);
					bean2rdf.saveDeep(o);
					commitTransaction(o, user);
				}
				finally
				{
					Locks.releaseLockForWrite();
				}
			}
		}
		cleanGraph();
		return null;
	}


	/**
	 * Begin an Imeji transaction:
	 * <br/> Check Security.
	 * <br/> Check lockings (optimistic and pessimistics)
	 * <br/> Lock bean
	 * 
	 * @param bean
	 * @param user
	 * @throws Exception 
	 */
	private void beginTransaction(Object bean, User user, OperationsType opType) throws Exception
	{
		try
		{
			checkSecurity(bean, user, opType);
			bean2rdf = new Bean2RDF(model);
			//bean2rdf.getModel().begin();
		}
		catch (Exception e)
		{
			commitTransaction(bean, user);
			logger.error(e);
			throw e;
		}
	}

	private void commitTransaction(Object bean, User user)
	{
		//bean2rdf.getModel().commit();
	}

	private void checkSecurity(Object bean, User user, OperationsType opType)
	{
		if (!security.check(opType, user, bean) )
		{
			throw new RuntimeException("Imeji Security exception: " +  user.getEmail() +" not allowed to " +  opType.name() + " " +  extractID(bean));
		}
	}

	public void cleanGraph()
	{
		if (Locks.tryLock())
		{
			try
			{
				//bean2rdf.getModel().enterCriticalSection(com.hp.hpl.jena.shared.Lock.WRITE);
				String q = "SELECT DISTINCT ?s WHERE { ?s ?p ?o . OPTIONAL {?s2 ?p2 ?s} . FILTER (isBlank(?s) && !bound(?s2))}";
				Query queryObject = QueryFactory.create(q);
				QueryExecution qe = QueryExecutionFactory.create(queryObject, bean2rdf.getModel());
				ResultSet results = qe.execSelect();
				while (results.hasNext())
				{
					QuerySolution qs = results.next();
					Resource s = qs.getResource("?s");
					s.removeProperties();
				}
				qe.close();
			}
			catch (Exception e) 
			{
				logger.error("Error cleaning graph", e);
			}
			finally
			{
				Locks.releaseLockForWrite();
				//bean2rdf.getModel().leaveCriticalSection();
			}
		}

	}

	private void activeLazyList(Object o)
	{
		if (o instanceof Image)
		{
			((Image) o).getMetadataSet().getMetadata().size();
		}
		else if (o instanceof Container)
		{
			((Container) o).getImages().size();
		}
	}

	private void setLastModificationDate(Object o, User user)
	{
		if (o instanceof Image)
		{
			ImejiController.writeUpdateProperties(((Image) o).getProperties(), user);
		}
		else if (o instanceof Container)
		{
			ImejiController.writeUpdateProperties(((Container) o).getProperties(), user);
		}
	}

	private Date getLastModificationDate(Object o)
	{
		if (o instanceof Image)
		{
			return ((Image) o).getProperties().getLastModificationDate();
		}
		else if (o instanceof Container)
		{
			return ((Container) o).getProperties().getLastModificationDate();
		}
		return null;
	}

	private URI extractID(Object o)
	{
		if (o instanceof Image)
		{
			return ((Image) o).getId();
		}
		else if (o instanceof Container)
		{
			return ((Container) o).getId();
		}
		else if (o instanceof MetadataProfile)
		{
			return ((MetadataProfile) o).getId();
		}
		else if (o instanceof User)
		{
			return URI.create(((User) o).getEmail());
		}
		return null;
	}
}
