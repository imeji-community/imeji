package thewebsemantic;

import static thewebsemantic.Bean2RDF.logger;
import static thewebsemantic.PrimitiveWrapper.isPrimitive;
import static thewebsemantic.TypeWrapper.wrap;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;

import thewebsemantic.Base.NullType;

import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * 
 * 
 *
 */
public class Thing implements InvocationHandler, As {

    private Model model;
    private Resource r;
    private static Method as;
    private static Method isa;
    private static Method asResource;

    static {
        try {
            as = As.class.getMethod("as", Class.class);
            isa = As.class.getMethod("isa", Class.class);
            asResource = As.class.getMethod("asResource");
        } catch (Exception e) {
            logger.log(Level.WARNING, "Could not access methods on As interface.", e);
        }
    }

    public Thing(Resource resource, Model m) {
        model = m;
        r = resource;
    }

    public Thing(String resource, Model m) {
        this(m.getResource(resource), m);
    }

    public Thing(Model m) {
        this(m.createResource(), m);
    }

    public Thing at(String uri) {
        return new Thing(uri, model);
    }

    public Resource getResource() {
        return r;
    }

    public OntResource getOntResource() {
        return r.as(OntResource.class);
    }

    public Model getModel() {
        return model;
    }

    public <T> T as(Class<T> c) {
        return (T) Proxy.newProxyInstance(c.getClassLoader(),
                new Class[]{c}, this);
    }

    public <T> T isa(Class<T> c) {
        String ns = c.getEnclosingClass().getAnnotation(Namespace.class).value();
        RdfType rdfType = c.getEnclosingClass().getAnnotation(RdfType.class);
        r.addProperty(RDF.type, model.getResource(ns + Util.getRdfType(c)));
        return as(c);
    }

    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        Class<?> returnType = method.getReturnType();
        if (method.equals(as)) {
            return as((Class) args[0]);
        } else if (method.equals(isa)) {
            return isa((Class) args[0]);
        } else if (method.equals(asResource)) {
            return asResource();
        } else if (method.getParameterTypes().length == 0) {
            return get(method);
        } else if (method.isAnnotationPresent(Functional.class)) {
            set(method, args);
            return proxy;
        }
        add(method, args);
        return proxy;
    }

    public Resource asResource() {
        return this.r;
    }

    private Object get(Method m) {
        String methodName = trim(m.getName());
        Class<?> genericType = Object.class;
        if (m.getGenericReturnType() instanceof ParameterizedType) {
            genericType = getGenericType((ParameterizedType) m.getGenericReturnType());
        }
        Class<?> returnType = m.getReturnType();
        Class<?> c = m.getDeclaringClass();
        String ns = wrap(c).namespace();
        Property p = model.getProperty(ns + methodName);
        StmtIterator it = r.listProperties(p);
        return (it.hasNext()) ? convertToObject(genericType, returnType, it) : null;
    }

    private Object convertToObject(Class<?> genericType, Class<?> returnType,
            StmtIterator it) {
        if (isPrimitive(returnType)) {
            return primitive(returnType, it);
        } else if (returnType == Literal.class) {
            return literal(it);
        } else if (returnType == Thing.class) {
            return thing(it);
        } else if ( As.class.isAssignableFrom(returnType) ) {
            return thing(it, returnType);
        } else if (returnType == Collection.class && genericType == Literal.class) {
            return literalCollection(it);
        } else if (returnType == Collection.class && isPrimitive(genericType)) {
            return primitiveCollection(it, returnType);
        } else if (returnType == Collection.class
                && genericType.equals(Thing.class)) {
            return thingCollection(it);
        } else {
            return null;
        }
    }

    private Object thing(StmtIterator it, Class<?> as) {
        Statement s = it.nextStatement();
        if (s.getObject().isLiteral()) {
            return s.getLiteral();
        } else {
            return new Thing(s.getResource(), s.getModel()).as(as);
        }
    }
    
    private Object thing(StmtIterator it) {
        Statement s = it.nextStatement();
        if (s.getObject().isLiteral()) {
            return s.getLiteral();
        } else {
            return new Thing(s.getResource(), s.getModel());
        }
    }

    private Object literal(StmtIterator it) {
        return it.nextStatement().getLiteral();
    }

    private Object primitive(Class<?> returnType, StmtIterator it) {
        return JenaHelper.convertLiteral(it.nextStatement().getLiteral(),
                returnType);
    }

    private Object thingCollection(StmtIterator it) {
        ArrayList<Object> list = new ArrayList<Object>();
        while (it.hasNext()) {
            Statement s = it.nextStatement();
            if (!s.getObject().isLiteral()) {
                list.add(new Thing(s.getResource(), s.getModel()));
            }
        }
        return list;
    }

    public Class<?> getGenericType(ParameterizedType type) {
        return (type == null) ? NullType.class : (Class<?>) type.getActualTypeArguments()[0];
    }

    private Object literalCollection(StmtIterator it) {
        ArrayList<Object> list = new ArrayList<Object>();
        while (it.hasNext()) {
            Statement s = it.nextStatement();
            if (s.getObject().isLiteral()) {
                list.add(s.getLiteral());
            }
        }
        return list;
    }

    private Object primitiveCollection(StmtIterator it, Class t) {
        ArrayList<Object> list = new ArrayList<Object>();
        while (it.hasNext()) {
            Statement s = it.nextStatement();
            if (s.getObject().isLiteral()) {
                list.add(JenaHelper.convertLiteral(s.getLiteral(), t));
            }
        }
        return list;
    }

    private void set(Method m, Object[] arg) {
        Property p = getProperty(m);
        r.removeAll(p);
        apply(arg, p, m.getParameterTypes()[0] == Object.class);
    }

    private Property getProperty(Method m) {
        String methodName = trim(m.getName());
        Class<?> c = m.getDeclaringClass();
        String ns = wrap(c).namespace();
        Property p = model.getProperty(ns + methodName);
        return p;
    }

    private void add(Method m, Object[] arg) {
        Property p = getProperty(m);
        apply(arg, p, m.getParameterTypes()[0] == Object.class);
    }

    private void apply(Object[] arg, Property p, boolean rawLiteral) {
        if (PrimitiveWrapper.isPrimitive(arg[0])) {
            if (arg.length < 2) {
                if (rawLiteral) {
                    r.addProperty(p, arg[0].toString());
                } else {
                    r.addProperty(p, JenaHelper.toLiteral(model, arg[0]));
                }
            } else {
                r.addProperty(p, arg[0].toString(), arg[1].toString());
            }
        } else if (arg[0] instanceof Thing) {
            set(p, (Thing) arg[0]);
        } else if (arg[0] instanceof As) {
            set(p, ((As) arg[0]).asResource());
        } else if (arg[0] instanceof URI) {
            set(p, ((URI) arg[0]).toString());
        } else if (arg[0] instanceof Resource) {
            set(p, (Resource) arg[0]);
        }
    }

    private void set(Property p, Thing arg) {
        set(p, arg.getResource());
    }

    private void set(Property p, String arg) {
        set(p, model.getResource(arg));
    }

    private void set(Property p, Resource object) {
        r.addProperty(p, object);
    }

    private String trim(String s) {
        if (s.endsWith("_")) {
            return s.substring(0, s.length() - 1);
        } else {
            return s;
        }
    }

    public String toString() {
        return this.r.getURI();
    }
}
