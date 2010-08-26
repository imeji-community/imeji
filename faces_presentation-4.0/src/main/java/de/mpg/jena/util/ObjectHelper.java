package de.mpg.jena.util;

import java.lang.annotation.Annotation;
import java.net.URI;

public class ObjectHelper
{
    /**
     * Get the URI of a Jena object.
     * Return 
     * @param o
     * @return
     */
    public static URI getURI(Class c, String id)
    {
        Annotation namespaceAnn = c.getAnnotation(thewebsemantic.Namespace.class);
        String namespace = namespaceAnn.toString().split("@thewebsemantic.Namespace\\(value=")[1].split("\\)")[0];
        Annotation rdfTypeAnn =  c.getAnnotation(thewebsemantic.RdfType.class);
        String objectType = rdfTypeAnn.toString().split("@thewebsemantic.RdfType\\(value=")[1].split("\\)")[0];
        return URI.create(namespace + objectType + "/" + id);
    }
}
