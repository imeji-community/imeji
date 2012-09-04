package thewebsemantic;

import static com.hp.hpl.jena.graph.Node.ANY;
import static com.hp.hpl.jena.graph.Node.createURI;
import static com.hp.hpl.jena.vocabulary.RDF.type;
import static thewebsemantic.JenaHelper.convertLiteral;
import static thewebsemantic.TypeWrapper.instanceURI;
import static thewebsemantic.TypeWrapper.typeUri;
import static thewebsemantic.TypeWrapper.wrap;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import thewebsemantic.binding.Persistable;
import thewebsemantic.lazy.LazyList;
import thewebsemantic.lazy.LazySet;
import thewebsemantic.lazy.Provider;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Seq;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.shared.Lock;
import com.hp.hpl.jena.shared.PropertyNotFoundException;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * RDF2Bean converts one or more RDF nodes into java beans. Normally these are
 * nodes created by the Bean2RDF class. 
 * 
 * @see Bean2RDF
 */
public class RDF2Bean extends Base implements Provider {
	private HashMap<String, Object> cycle;

	private boolean shallow = false;
	private Set<String> myIncludes = new HashSet<String>();
	private static final String[] none = new String[0];
	private AnnotationHelper jpa;

	/**
	 * Constructs and instance of RDF2Bean bound to a particular Jena model.
	 * Operations have potential to modify the model.
	 * 
	 * @param model
	 * a Jena Ontology Model instance
	 */
	public RDF2Bean(Model model) {
		super(model);
		jpa = new NullJPAHelper();
	}
	
	public RDF2Bean(Model model, AnnotationHelper h) {
		super(model);
		jpa = h;
	}

	/**
	 * Deeply loads all individuals having RDF type which matches Class
	 * <tt>c</tt>. Depending on the density of your model this method has the
	 * potential to load the entire graph into memory as Java beans. For
	 * example, if you been has a <tt>hasFriends</tt> property,
	 * <tt>loadDeep()</tt> will load each friend, each friend's friends, and so
	 * on. Every reachable property that maps to a corresponding bean property
	 * will be loaded recursively.
	 * 
	 * @param <T>
	 * @param c
	 *            a java class which maps to individuals in your ontology.
	 * @return collection of java beans, all instances of Class <tt>c</tt>.
	 */
	public <T> Collection<T> loadDeep(Class<T> c) {
		return load(c, false, none);
	}

	/**
	 * Loads all individuals having RDF type which maps to Class <tt>c</tt>.
	 * Mappings are either based on Class annotations or bindings given at VM
	 * startup. <tt>load()</tt> is safe for dense graphs as it has a
	 * conservative or shallow policy. It loads all functional properties (where
	 * there is only one) but ignores bean properties of type
	 * <tt>Collection</tt>. All returned bean's Collection properties are
	 * initialized to size 0.
	 * 
	 * Once loaded you may add to Collection properties and save. This will
	 * result in addition assertions being made in your jena ontology model,
	 * however, saving will not delete assertions unless the property is fully
	 * loaded. Consider the common customer/order scenario. We can load a
	 * customer, and fill the orders, leaving other properties alone:
	 * 
	 * <code>
	 * Collection<Customer> customers = myRDF2Bean.load(Customer.class)
	 * ...
	 * myRDF2Bean.fill(aCustomer).with("orders");
	 * foreach(Order o: aCustomer.getOrders())...
	 * </code>
	 * 
	 * You may also use this alternate method to fill collection properties:
	 * 
	 * <code>
	 * myRDF2Bean.fill(aCustomer, "orders");
	 * </code>
	 * 
	 * @see Namespace
	 * @param <T>
	 * @param c
	 * @return
	 */
	public <T> Collection<T> load(Class<T> c) {
		return load(c, true, none);
	}

	/**
	 * Similar to <tt>load(Class<T> c)</tt> except that you may include an array
	 * of property names to "include". The properties should be of type
	 * Collection. This allows you to be specific about wich children you want
	 * unmarshalled from the ontology model into java objects. For instance, you
	 * may just need a customer, their outstanding orders, and their recent
	 * addresses. Assuming your ontology relates customers to many more
	 * non-functional properties, it'd save time to just load exacly what you
	 * need.
	 * 
	 * @param <T>
	 * @param c
	 * @param includes
	 * @return
	 */
	public <T> Collection<T> load(Class<T> c, String... includes) {
		return load(c, true, includes);
	}

	/**
	 * load all rdf entries that map to the bean.
	 * 
	 * @param <T>
	 * @param c
	 * @return
	 */
	protected synchronized <T> Collection<T> load(Class<T> c, boolean shallow,
			String... includes) {
		init(shallow, includes);
		try {
			return loadAll(c);
		} finally {
			m.leaveCriticalSection();
		}
	}

	private void init(boolean shallow, String[] includes) {
		cycle = new HashMap<String, Object>();
		m.enterCriticalSection(Lock.READ);
		this.shallow = shallow;
		this.myIncludes.clear();
		for (String s : includes)
			myIncludes.add(s);
	}

	private <T> Collection<T> loadAll(Class<T> c) {
		return loadIndividuals(c, m.listSubjectsWithProperty(type, rdfType(c)));
	}

	private <T> Collection<T> loadIndividuals(Class<T> c, ResIterator it) {
		Collection<T> list = new LinkedList<T>();
		while (it.hasNext())
			list.add(toObject(c, it.nextResource()));
		it.close();
		return list;
	}

	/**
	 * <tt>loadDeep</tt> will load a particular individual and all it's
	 * properties, recursively. <em>Beware</em>, this could result in loading
	 * the entire model into memory as java objects depending on the density of
	 * your graph. Therefore use this method with care knowing that it's purpose
	 * is to load all information reachable via properties that bind to your
	 * objects.
	 * 
	 * @param c
	 *            java class of the bean. The class is converted to a URI based
	 *            on its annotations or bindings.
	 * @param id
	 *            unique id of the bean to find
	 * @return An instance of T, otherwise null
	 * @see Namespace
	 */
	public <T> T loadDeep(Class<T> c, Object id) throws NotFoundException {
		return load(c, id.toString(), false);
	}

	/**
	 * Similar to load(Class, String), with the ability to include
	 * non-functional Collection based properties. <tt>includes</tt> should be
	 * an array of property names, for example, if you want to load a customer
	 * with their orders and recent purchases...
	 * 
	 * <code>
	 * RDF2Bean reader = new RDF2Bean(model);
	 * String[] includes = {"orders","recentPurchases"};
	 * reader.load(Customer.class, "cust#2", includes);
	 * </code>
	 * 
	 * @param <T>
	 * @param c
	 * @param id
	 * @param includes
	 * @return
	 * @throws NotFoundException
	 */
	public <T> T load(Class<T> c, String id, String[] includes)
			throws NotFoundException {
		return load(c, id, true, includes);
	}
	
	/**
	 * Loads an ontology individual as a java bean, based on annotations or
	 * bindings applied to Class <tt>c</tt>.
	 * 
	 * @param <T>
	 * @param c
	 * @param id
	 * @return instance of Class<tt>c</tt> matching <tt>id</tt> from model, if
	 *         one exists.
	 * @throws NotFoundException
	 */
	public <T> T load(Class<T> c, Object id) throws NotFoundException {
		return load(c, id.toString(), true);
	}

	public <T> T load(Class<T> c, Resource r) {
		return load(c, r, true, new String[0]);
	}

	protected <T> T load(Class<T> c, String id, boolean shallow)
			throws NotFoundException {
		return load(c, id, shallow, new String[0]);
	}

	private synchronized <T> T load(Class<T> c, String id, boolean shallow,
			String[] includes) throws NotFoundException {
		init(shallow, includes);
		try {
			if (exists(c, id))
				return toObject(c, id);
			throw new NotFoundException();
		} finally {
			m.leaveCriticalSection();
		}
	}

	private synchronized <T> T load(Class<T> c, Resource r, boolean shallow,
			String[] includes) {
		init(shallow, includes);
		try {
			return (T) toObject(c, r);
		} finally {
			m.leaveCriticalSection();
		}
	}

	/**
	 * Loads an object from model with the same identifier as <tt>target</tt>.
	 * 
	 * @param target
	 * @return
	 * @throws NotFoundException
	 */
	public synchronized <A> A load(A target) {
		init(shallow, none);
		try {
			Resource source = m.getResource(instanceURI(target));
			return (A)applyProperties(source, target);
		} finally {
			m.leaveCriticalSection();
		}
	}

	/**
	 * returns true if target exists in the model
	 * 
	 * @param target
	 * @return
	 */
	public synchronized boolean exists(Object target) {
		init(shallow, none);
		try {
			return exists(instanceURI(target));
		} finally {
			m.leaveCriticalSection();
		}
	}

	/**
	 * Returns a <tt>Filler</tt> for this bean. When beans are loaded they are
	 * normally shallow, ie, their Collections are still empty. This allows the
	 * client to decide which lists (sometimes large) they'd like to work with.
	 * 
	 * This provides a certain type of calling style:
	 * 
	 * <code>
	 * RDF2Bean rdf2bean = new RDF2Bean(model);
	 * ...
	 * rdf2bean.fill(myBean).with("children");
	 * </code>
	 * 
	 * @param o
	 * @return
	 */
	@Deprecated
	public Filler fill(Object o) {
		return new Filler(this, o);
	}

	/**
	 * fill or reload a non-functional property with values from the model. This
	 * is useful when you've recently shallow loaded a bean from the triple
	 * store. non-functional properties can contain unlimited elements, so your
	 * app will need to be careful regarding when it loads them.
	 * 
	 * in Jenabean, non-functional properties are represented as properties of
	 * type java.util.Collection.
	 * 
	 * <code>
	 * RDF2Bean rdf2bean = new RDF2Bean(model);
	 * ...
	 * rdf2bean.fill(myBean,"children");
	 * </code>
	 * 
	 * @param o
	 * @param propertyName
	 * @deprecated collections a filled lazily.  Simple access of collection in your loaded
	 * bean will cause it to load from the model.
	 */
	@Deprecated
	public synchronized void fill(Object o, String propertyName) {
		init(shallow, none);
		try {
			fillWithChildren(o, propertyName);
		} finally {
			m.leaveCriticalSection();
		}
	}

	/**
	 * returns true if matching individual is found in the model.
	 * 
	 * @param c
	 * @param id
	 * @return
	 */
	public boolean exists(Class<?> c, String id) {
		return exists(wrap(c).uri(id));
	}

	public boolean exists(String uri) {
		return m.getGraph().contains(createURI(uri), ANY, ANY);
	}

	private <T> T toObject(Class<T> c, String id) {
		return toObject(c, m.getResource(wrap(c).uri(id)));
	}

	public Object load(String uri) throws NotFoundException {
		init(shallow, none);
		try {
			if (exists(uri))
				return toObject(Object.class, m.getResource(uri));
			else
				throw new NotFoundException();
		} finally {
			m.leaveCriticalSection();
		}
	}
	
	private <T> T toObject(Class<T> c, Resource i) { 
		if (c == thewebsemantic.Resource.class)
			return (T) new thewebsemantic.Resource(i.getURI());
		else if (c == URI.class)
			return (T) URI.create(i.getURI());
		else
			return (i != null) ? (T) testCycle(i, c) : null;
	}

	private Object testCycle(Resource i, Class<?> c) {
		return (isCycle(i)) ? cachedObject(i) : applyProperties(i, c);
	}

	private Object cachedObject(Resource i) {
		return cycle.get(key(i));
	}

	/**
	 * literals are not resources, and cannot "as" to Resource.
	 */
	private <T> T toObject(Class<T> c, RDFNode node) {
		if (node.isLiteral()) 
			return (T) convertLiteral(node, c);
		else
			return toObject(c, node.as(Resource.class));
	}
	
	private boolean isCycle(Resource i) {
		return cycle.containsKey(key(i));
	}

	private String key(Resource i) {
		return (i.isAnon()) ? i.getId().toString() : i.getURI();
	}

	private Object fillWithChildren(Object target, String propertyName) {
		Resource source = m.getResource(instanceURI(target));
		for (ValuesContext p : TypeWrapper.valueContexts(target))
			if (match(propertyName, p))
				fill(source, p);
		return target;
	}

	private boolean match(String propertyName, ValuesContext p) {
		return p.getName().equals(propertyName)
				&& (p.isAggregateType());
	}

	private Object applyProperties(Resource source, Class c) {
		return applyProperties(source, newInstance(source, c));
	}

	private Object applyProperties(Resource source, Object target) {
		cycle.put(source.getURI(), target);
		// first get non-aggregate singular values
		for (ValuesContext ctx : TypeWrapper.valueContexts(target))
			if (!ctx.isAggregateType())
				apply(source, ctx);
		// now get aggregate (array, collection, set, list)
		for (ValuesContext ctx : TypeWrapper.valueContexts(target))
			if (ctx.isAggregateType())
				apply(source, ctx);
		
		/*
		 * now activate the proxy so that it marks dirty state 
		 * for saving on commit or flush
		 */
		if ( target instanceof Persistable)
			((Persistable)target).activate();
		return target;
	}
	
	/**
	 * Initializes null collections and sets with a lazy loader.
	 * You may use this in lieu of creating new collections in your bean constuctor, 
	 * however, it's main purpose is to initialize beans for use in the JPA api, 
	 * which requires that managed beans have non-null collection properties
	 * after being made persistent.
	 * 
	 * @param target
	 */
	public void init(Object target) {
		Resource node = m.getResource(instanceURI(target)); 
		for (ValuesContext ctx : TypeWrapper.valueContexts(target)) {
			if ( ctx.isCollectionOrSet() && ctx.invokeGetter() == null)
			    ctx.setProperty(createCollection(node, ctx.uri(), ctx.t()));
			    //ctx.setProperty(new LazySet(node, ctx.uri(), ctx.t(), this));
			else if ( ctx.isList() && ctx.invokeGetter() == null)
			    ctx.setProperty(createList(node, ctx.uri(), ctx.t()));
			    //ctx.setProperty(new LazyList(node, ctx.uri(), ctx.t(), this));
			else if (ctx.isId() && jpa.isGenerated(ctx))
				generateid(target, ctx);
		}
	}

	private void generateid(Object target, ValuesContext ctx) {
		String uri = TypeWrapper.type(target).typeUri();
		Resource r = m.createResource(uri);
		int idx=0;
		try {
			Statement s = r.getRequiredProperty(sequence);
			idx = s.getInt();
		} catch (PropertyNotFoundException e) {

		}
		ctx.setProperty(idx);
		r.removeAll(sequence).addProperty(sequence, m.createTypedLiteral(idx+1));
	}

	private Object newInstance(Resource source, Class c) {
		try {
			TypeWrapper t = wrap(javaclass(source, c));
			return (jpa.proxyRequired()) ?
				t.toProxyBean(source, jpa):
				t.toBean(source);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		/*
		catch(NotBoundException nbe)
		{
		    return null;
		}
		*/
		return null;
	}

	/**
	 * Given an Individual, return the appropriate class. This could have been
	 * stored as an annotation if jenabean was involved in writing the
	 * individual. If not we'll use the binder.
	 * 
	 * @param source
	 * @return
	 * @throws ClassNotFoundException
	 */
	private Class<?> javaclass(Resource source, Class<?> c)
			throws ClassNotFoundException {		
		StmtIterator it = source.listProperties(RDF.type);
		Resource oc = null;
		while (it.hasNext()) {
			oc = it.nextStatement().getResource();
			Class<?> declared = declaredClass(oc);
			if (c.isAssignableFrom(declared)) {
				it.close();
				return declared;
			}
		}	
		it.close();
		throw new NotBoundException(source.getURI() + " exists but is not bound to or able to coerce as " + c);
	}

	private Class<?> declaredClass(Resource oc) throws ClassNotFoundException {
		Class<?> result = NoBinding.class;
		if (binder.getClass(oc.getURI()) != null)
			result = binder.getClass(oc.getURI());
		else if ( oc.getProperty(javaclass) != null) {
			Statement node = oc.getProperty(javaclass);
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			Class<?> klass = cl.loadClass(node.getLiteral().getString());
			binder.save(klass, oc.getURI());
			result = klass;
		}
		return result;			
	}

	private Resource rdfType(Class<?> c) {
		return m.getResource((binder.isBound(c)) ? binder.getUri(c) : typeUri(c));
	}

	/**
	 * Apply a particular property of an Individual (rdf) to a Java Object
	 * 
	 * @param i
	 *            Found individual we are using as a data source
	 * @param o
	 *            raw object ready to receive data from rdf
	 * @param property
	 *            descriptor for property we are applying
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private void apply(Resource i, ValuesContext ctx) {
		if ( (ctx.isCollection() || ctx.isSet()) && (shallow && !included(ctx.getName())) ) {
		    
		    ctx.setProperty(createCollection(i, ctx.uri(), ctx.t()));
		    //ctx.setProperty(new LazySet(i, ctx.uri(), ctx.t(), this));
			return;
		} else if (ctx.isList() && (shallow && !included(ctx.getName())) ) {
		    ctx.setProperty(createList(i, ctx.uri(), ctx.t()));
		    //ctx.setProperty(new LazyList(i, ctx.uri(), ctx.t(), this));
			return;
		}
		StmtIterator it = i.listProperties(m.getProperty(ctx.uri()));
		apply(ctx, it);
		it.close();
	}
	
	/**
	 * By Markus H
	 * @param <T>
	 * @param i
	 * @param propertyUri
	 * @param c
	 * @return
	 */
	private <T> Collection<T> createCollection(Resource i, String propertyUri, Class<T> c)
	{
	    Property p = m.createProperty(propertyUri);    
        StmtIterator values = i.listProperties(p);
	    Set<T> set = fillCollection(c, values);
	    return set;
	}
	
	/**
     * By Markus H
     * @param <T>
     * @param i
     * @param propertyUri
     * @param c
     * @return
     */
	private <T> List<T> createList(Resource i, String propertyUri, Class<T> c)
    {
        Property p = m.createProperty(propertyUri);   
        StmtIterator values = i.listProperties(p);
        List<T> l;
        if (values.hasNext())
        {
           l  = fillList(c, values.nextStatement().getSeq());
        }
        else
        {
            l = new ArrayList<T>();
          
        }
        
        return l;
    }

	private void fill(Resource i, ValuesContext ctx) {
		Property p = m.createProperty(ctx.uri());
		StmtIterator values = i.listProperties(p);
		if (ctx.isArray()) {
			Seq s = values.nextStatement().getSeq();
			Class<?> type = ctx.type().getComponentType();
			ctx.setProperty(fillArray(type, s));
		} else if (ctx.isList()){
			Seq s = values.nextStatement().getSeq();
			ctx.setProperty(fillList(ctx.t(), s));			
		} else if (ctx.isCollectionType()) {
			ctx.setProperty(fillCollection(ctx.t(), values));
		}
		values.close();
	}

	public Set lazySet(Resource i, String propertyUri, Class type) {
		Property p = m.createProperty(propertyUri);   
		StmtIterator values = i.listProperties(p);
		Set l = fillCollection(type, values);
		values.close();
		return l;
	}	
	
	public List lazyList(Resource i, String propertyUri, Class type) {
		Property p = m.createProperty(propertyUri);
		List l = null;
		StmtIterator values = i.listProperties(p);
		if ( values.hasNext()) {
			Seq s = values.nextStatement().getSeq();
			l = fillList(type, s);
		} else {
			l = new ArrayList();
		}
		values.close();
		return l;
	}	
	
	private void apply(ValuesContext ctx, StmtIterator nodes) {
		if (ctx.isCollection())
			collection(ctx, nodes);
		
		// important, if not hasNext, we need to bail
		else if (!nodes.hasNext())
			return;
		else if (ctx.isPrimitive())
			applyLiteral(ctx, nodes.nextStatement().getLiteral());
		else if (ctx.isArray())
			array(ctx, nodes.nextStatement().getSeq());
		else if (ctx.isList())
			list(ctx, nodes.nextStatement().getSeq());
		else if (ctx.isURI())
			applyURI(ctx, nodes.nextStatement().getResource());
		else
			applyIndividual(ctx, nodes.nextStatement().getResource());
	}

	private void list(ValuesContext ctx, Seq s) {
		ctx.setProperty(fillList(ctx.t(), s));
	}
	
	private <T> List<T> fillList(Class<T> type, Seq s) {
		ArrayList<T> list = new ArrayList<T>();
		for (int i = 0; i < s.size(); i++)
			list.add( toObject(type, s.getObject(i + 1)));
		return list;
	}

	private void applyURI(ValuesContext ctx, Resource resource) {
		ctx.setProperty(URI.create(resource.getURI()));
	}

	private void array(ValuesContext ctx, Seq s) {
		Class<?> type = ctx.type().getComponentType();
		ctx.setProperty(fillArray(type, s));
	}

	private Object fillArray(Class<?> type, Seq s) {
		Object array = Array.newInstance(type, s.size());
		for (int i = 0; i < Array.getLength(array); i++)
			Array.set(array, i, toObject(type, s.getObject(i + 1)));
		return array;
	}

	private void collection(ValuesContext ctx, StmtIterator nodes) {
		ctx.setProperty(fillCollection(ctx.t(), nodes));
	}

	private boolean included(String property) {
		return myIncludes.contains(property);
	}

	protected <T> Set<T> fillCollection(Class<T> c, StmtIterator nodes) {
		HashSet<T> results = new HashSet<T>();
		while (nodes.hasNext())
			results.add(toObject(c, nodes.nextStatement().getObject())); 
		return results;
	}

	private void applyIndividual(ValuesContext ctx, Resource i) {
		ctx.setProperty(toObject(ctx.type(), i));
	}

	private void applyLiteral(ValuesContext ctx, Literal l) {
		ctx.setProperty(convertLiteral(l, ctx.type()));
	}

	/**
	 * Prepares this reader to bind to all annotated classes in 
	 * provided list of packages.  If the RDF wasn't created using
	 * Jenabean, it lacks annotations specifying the source class for each
	 * individual.  Invoking <code>bindAll</code> prepares the reader by
	 * indicating the packages where your jenabeans can be found.
	 * 
	 * @param pkg
	 */
	public void bindAll(String... pkg) {
		ResolverUtil<Object> resolver = new ResolverUtil<Object>();
		resolver.findAnnotated(Namespace.class, pkg);
		Set<Class<? extends Object>> classes = resolver.getClasses();
		for (Class<? extends Object> class1 : classes)
			bind(class1);
	}

	/**
	 * Prepares this reader to bind to a particular
	 * annotated class.
	 *  
	 */
	public void bind(Class<? extends Object> class1) {
		Namespace ns = class1.getAnnotation(Namespace.class);
		   m.getResource(ns.value() + Util.getRdfType(class1)).addProperty(
				javaclass, class1.getName());
	}
	
	/**
	 * Prepares this reader to bind to all annotated classes in 
	 * provided list of package.
	 */	
	public void bind(Package... packages) {
		for (Package p : packages)
			bindAll(p.getName());
	}
	
}
/*
 * Copyright (c) 2007
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */