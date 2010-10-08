package thewebsemantic;

import static thewebsemantic.JenaHelper.toLiteral;
import static thewebsemantic.PrimitiveWrapper.isPrimitive;
import static thewebsemantic.TypeWrapper.instanceURI;
import static thewebsemantic.TypeWrapper.type;

import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.shared.Lock;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * <p>
 * Converts a simple java bean to RDF, provided it's annotated with
 * <code>Namespace</code>. To make a bean persitable by jenabean, you are merely
 * required to add the Namespace annotation. By default public bean properties
 * are converted to rdf properties by appending "has" and proper casing the
 * property name. For example, a bean with methods getName() and setName() would
 * result in the RDF property "hasName", with the namespace given in the classes
 * Namespace annotation.
 * </p>
 * <p>
 * The default behavior for rdf property naming is overridden by using the
 * RdfProperty annotation along with the getter method. The value supplied to
 * the RdfProperty annotation is taken as the full RDF property URI. <br/>
 * </p>
 * <p>
 * The bean itself is typed using the Namespace annotation along with the bean
 * name, for example, Book.class with namespace "http://example.org/" becomes
 * rdf type "http://example.org/Book". <br/>
 * </p>
 * <p>
 * Here's a simple example of a bean that's ready to be saved:
 * <code>
 * <pre>
 * package org.example;
 * import thewebsemantic.Id;
 * public Book {
 *    private String name;
 *    public void setName(String s) { name=s;}
 *    &#064;Id
 *    public String getName() {return name;}
 * }
 * </pre>
 * </code>
 * </p>
 * @see Namespace
 * @see Id
 * @see RdfProperty
 */
public class Bean2RDF extends Base {
	private static final String UNSUPPORTED_TYPE = "UNSUPPORTED_TYPE";
	private ArrayList<Object> cycle;
	private boolean forceDeep = false;
    public static Logger logger = Logger.getLogger("com.thewebsemantic");
    ResourceBundle bundle = ResourceBundle.getBundle("thewebsemantic.messages");
    private AnnotationHelper jpa;

	/**
	 * construct a new instance bound to OntModel <code>m</code>.
	 * 
	 * @param m
	 * Jena OntModel instance
	 */
	public Bean2RDF(Model m) {
		super(m);
		jpa = new NullJPAHelper();
	}

	public Bean2RDF(Model m, AnnotationHelper jpa) {
		super(m);
		this.jpa = jpa;
		
	}	
	/**
	 * Saves <code>bean</code> to jena model.
	 * 
	 * @param bean
	 * @return jena resource representing <tt>bean</tt> within the model
	 */
	public Resource save(Object bean) {
		return write(bean, false);
	}

	/**
	 * Deletes <code>bean</code> from the model.
	 * 
	 * @param bean
	 */
	public void delete(Object bean) {
		Resource i = m.getResource(instanceURI(bean));
		m.removeAll(null, null, i).removeAll(i, null, null);
	}

	/**
	 * Saves the entire object graph starting with <tt>bean</tt>.
	 * 
	 * @param bean
	 * @return
	 */
	public Resource saveDeep(Object bean) {
		return write(bean, true);
	}

	/**
	 * Writes a bean to the triple store.
	 * 
	 * @param bean
	 * @return
	 */
	private synchronized Resource write(Object bean, boolean forceDeep) {
		try {
			m.enterCriticalSection(Lock.WRITE);
			this.forceDeep = forceDeep;
			cycle = new ArrayList<Object>();
			return _write(bean, false);
		} finally {
			m.leaveCriticalSection();
		}
	}

	private Resource _write(Object bean, boolean shallow) {
		return (cycle.contains(bean)) ? existing(bean) : write(bean,
				toResource(bean), shallow);
	}

	private Resource toResource(Object bean) {
		String uri = instanceURI(bean);
		Resource type = getRDFSClass(bean);
		if (jpa.isEmbedded(bean) || uri==null)
			return m.createResource(type); 
		else
			return m.createResource(uri, type);
	}

	private Resource existing(Object bean) {
		return m.createResource(instanceURI(bean));
	}

	/**
	 * returns an existing OntClass or creates a new one adding an important
	 * annotation indicating the original java class.
	 * 
	 * @param bean
	 *            the bean we are saving or updating to the triple store
	 * @return
	 */
	private Resource getRDFSClass(Object bean) {
		return m.getResource(getURI(bean)).
			addProperty(RDF.type, RDFS.Class).
			addProperty(javaclass, bean.getClass().getName());
	}

	private String getURI(Object bean) {
		return (isBound(bean)) ? binder.getUri(bean) : type(bean).typeUri();
	}

	private Resource write(Object bean, Resource subject, boolean shallow) {
		cycle.add(bean);
		for (ValuesContext p : TypeWrapper.valueContexts(bean))
			if (!(shallow && p.type().isAssignableFrom(Collection.class)) || forceDeep)
				saveOrUpdate(subject, p);
		return subject;
	}

	private void saveOrUpdate(Resource subject, ValuesContext pc) {
		Object o = pc.invokeGetter();
		Property property = toRdfProperty(pc);
		if ( Saver.supports(pc.type()))
			Saver.of(pc.type()).save(this,subject, property, o);
		else if (o == null)
			subject.removeAll(property);
		else if (pc.isPrimitive())
			subject.removeAll(property).addProperty(property, toLiteral(m, o));
		else if (isNormalObject(o))
			setPropertyValue(subject, property, o);
		else 
			logger.log(Level.WARNING, MessageFormat.format(bundle
					.getString(UNSUPPORTED_TYPE), pc.type(),pc.subject.getClass()));
	}

	private boolean isNormalObject(Object o) {
		return !o.getClass().isArray() && !(o instanceof Collection) && !(o instanceof Map);
	}

	protected RDFNode toRDFNode(Object o) {
		if (isPrimitive(o)) 
			return toLiteral(m, o);
		else if (o instanceof URI || o instanceof thewebsemantic.Resource)
			return m.createResource(o.toString());
		else
			return _write(o, true);
	}
	
	/**
	 * Update or persist a domain object outside String, Date, and the usual
	 * primitive types. We set the write style to shallow=true, causing an end
	 * of recursive traversal of the object graph.
	 * 
	 * @param subject
	 * @param property
	 * @param o
	 */
	private void setPropertyValue(Resource subject, Property property, Object o) {
		Statement s = subject.getProperty(property);
		Resource existing=null;
		if (s!=null ) {
			existing = s.getResource();
			if (existing.isAnon())
				existing.removeProperties();
		}
		subject.removeAll(property).addProperty(property, _write(o, true));
	}
	
	public void n3() {
		m.write(System.out,"N3");
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