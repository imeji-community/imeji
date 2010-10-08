package thewebsemantic;

import thewebsemantic.vocabulary.Foaf;
import thewebsemantic.vocabulary.Geo;
import thewebsemantic.vocabulary.Sioc;

import com.hp.hpl.jena.rdf.model.Resource;

/**
 * Provides standard polymorphic interface for {@link Thing} vocabulary
 * interfaces. The <code>as(Class)</code> method returns dynamic proxies related
 * to the same model for a different vocabulary, while <code>isa(Class)</code>
 * does the same thing in addition to asserting a classification
 * <code>(a rdf:type p)</code>. Typically the interface is used allong with the
 * <code>Namespace</code> annotation like this:
 * 
 * <code><pre>
 * @Namespace(&quot;http://xmlns.com/foaf/0.1/&quot;) 
 * public interface Foaf extends As {
 *  // ... } 
 * </pre></code>
 * 
 * @see Thing
 * @see Foaf
 * @see Geo
 * @see Sioc
 */
public interface As {
	
	/**
	 * Polymorph this proxied interface into a new vocabulary by
	 * providing it's class.
	 * 
	 * @return a dynamic proxy related to the connected Jena model
	 * of this proxy.
	 */
	public <T> T as(Class<T> c);

	/**
	 * Polymorph and reclassify this proxied interface into a new vocabulary by
	 * providing it's class.
	 * 
	 * @return a dynamic proxy related to the connected Jena model
	 * of this proxy.
	 */
	public <T> T isa(Class<T> c);

	/**
	 * Provides access to the raw Jena resource in focus.  All requests or sets on this
	 * proxy apply the the resource
	 * 
	 * @return the underlying Jena resource targeted by this proxy.
	 */
	public Resource asResource();
}
