package de.mpg.jena;

import java.net.URI;
import java.util.Date;

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

import de.mpg.jena.concurrency.locks.Lock;
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
	private boolean optimisticLocking = false;
	private boolean pessimisticLocking = true;
	
	private static Bean2RDF bean2rdf;
	private static RDF2Bean rdf2Bean;
	private Security security;
	private static Logger logger = Logger.getLogger(ImejiBean2RDF.class);
	
	public ImejiBean2RDF(Model model) 
	{
		bean2rdf = new Bean2RDF(model);
		rdf2Bean = new RDF2Bean(model);
		security = new Security();
	}
	
	public void create(Object bean, User user) throws Exception
	{
		beginTransaction(bean, user, OperationsType.CREATE);
		bean2rdf.saveDeep(bean);
		commitTransaction(bean, user);
	}

	public void delete(Object bean, User user) throws Exception 
	{
		beginTransaction(bean, user, OperationsType.DELETE);
		bean2rdf.delete(bean);
		commitTransaction(bean, user);
	}

	public Resource saveDeep(Object bean, User user) throws Exception
	{
		beginTransaction(bean, user, OperationsType.UPDATE);
		bean2rdf.saveDeep(bean);
		commitTransaction(bean, user);
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
			if (optimisticLocking) checkOptimisticLocks(bean);
			if (pessimisticLocking) checkPessimisticLock(bean, user);
			Locks.lock(new Lock(extractID(bean).toString()));
			bean2rdf.getModel().begin();
			setLastModificationDate(bean, user);
		}
		catch (Exception e)
		{
			commitTransaction(bean, user);
			throw e;
		}
	}
	
	private void commitTransaction(Object bean, User user)
	{
		Locks.unLock(new Lock(extractID(bean).toString()));
		bean2rdf.getModel().commit();
	}
	
	private void checkSecurity(Object bean, User user, OperationsType opType)
	{
		if (!security.check(opType, user, bean) )
		{
			throw new RuntimeException("Imeji Security exception: " +  user.getEmail() +" not allowed to update " + extractID(bean));
		}
	}
	
	private void checkOptimisticLocks(Object bean)
	{
		Object o1 = rdf2Bean.load(bean.getClass(), extractID(bean));
		if (!getLastModificationDate(o1).equals(getLastModificationDate(bean)))
		{
			throw new RuntimeException(extractID(bean)+ " has been modified previously!");
		}
	}
	
	private void checkPessimisticLock(Object bean, User user)
	{
		if (Locks.isLocked(extractID(bean).toString(), user.getEmail()))
		{
			throw new RuntimeException("Imeji Locking exception: resource " + extractID(bean) +   " locked!");
		}
	}
	
	private void cleanGraph()
    {
        try
        {
        	bean2rdf.getModel().enterCriticalSection(com.hp.hpl.jena.shared.Lock.WRITE);
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
        finally
        {
        	bean2rdf.getModel().leaveCriticalSection();
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
