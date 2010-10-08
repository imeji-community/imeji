package thewebsemantic;

import java.beans.PropertyDescriptor;
import java.lang.reflect.ParameterizedType;
import java.util.ResourceBundle;

import thewebsemantic.binder.Binder;
import thewebsemantic.binder.BinderImp;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.shared.Lock;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * Provides functionality common to both {@link RDF2Bean} and {@link Bean2RDF}
 * 
 *
 */
public class Base {

	public static final String JAVACLASS = "http://thewebsemantic.com/javaclass";
	public static final String SEQUENCE = "http://thewebsemantic.com/sequence";
    ResourceBundle bundle = ResourceBundle.getBundle("thewebsemantic.messages");
	protected OntModel om;
	protected Model m;
	protected Binder binder;
	protected Property javaclass;
	protected Property sequence;
	protected Base(Model m) {
		this.m = m;
		binder = BinderImp.instance();		
		if ( m instanceof OntModel) {om = (OntModel)m;}
		m.enterCriticalSection(Lock.WRITE);
		javaclass = m.createProperty(JAVACLASS);
		sequence = m.createProperty(SEQUENCE);
		javaclass.addProperty(RDF.type,OWL.AnnotationProperty);
		m.leaveCriticalSection();
	}

	protected Property toRdfProperty(ValuesContext ctx) {
		return ctx.existsInModel(m) ? ctx.property(m) : applyEntailments(ctx);
	}

	private Property applyEntailments(ValuesContext ctx) {

		if (om == null) return m.getProperty(ctx.uri());		
		OntProperty op = om.createOntProperty(ctx.uri());
		if (ctx.isSymmetric())
			op.convertToSymmetricProperty();
		else if (ctx.isTransitive())
			op.convertToTransitiveProperty();
		else if (ctx.isInverse()) {
			TypeWrapper type = TypeWrapper.wrap(ctx.t());
			ValuesContext inverse = type.getProperty(ctx.inverseOf());
			op.addInverseOf(inverse.property(om));
		}
		return op;
	}

	protected Class<?> t(PropertyDescriptor propDesc) { 

		ParameterizedType type = (ParameterizedType) propDesc.getReadMethod()
				.getGenericReturnType();
		return (type == null) ? NullType.class : (Class<?>) type
				.getActualTypeArguments()[0];
	}

	protected boolean isBound(Object o) {
		return binder.isBound(o.getClass());
	}

	class NullType {

	}
	
	public Model getModel() {
		return m;
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
 * 
 */
