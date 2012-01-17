/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.jena.util;

import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import de.mpg.jena.vo.Image;

public class ObjectHelper
{
    /**
     * Get the URI of a Jena object. Return
     * 
     * @param o
     * @return
     */
    public static URI getURI(Class<?> c, String id)
    {
        Annotation namespaceAnn = c.getAnnotation(thewebsemantic.Namespace.class);
        String namespace = namespaceAnn.toString().split("@thewebsemantic.Namespace\\(value=")[1].split("\\)")[0];
        Annotation rdfTypeAnn = c.getAnnotation(thewebsemantic.RdfType.class);
        String objectType = rdfTypeAnn.toString().split("@thewebsemantic.RdfType\\(value=")[1].split("\\)")[0];
        String encodedId = id;
        try
        {
            encodedId = URLEncoder.encode(id, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
        	throw new RuntimeException(e);
        }
        return URI.create(namespace + objectType + "/" + encodedId);
    }

    public static String getId(URI uri)
    {
        return uri.toString().substring(uri.toString().lastIndexOf("/"), uri.toString().length()).replace("/", "");
    }

    /**
     * Fields with {@link Collection} type are retrieved as {@link HashSet} from Jena: BUG This method cast all this
     * mistyped fields in their original type.
     * 
     * @param obj
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static Object castAllHashSetToList(Object obj) throws IllegalArgumentException, IllegalAccessException
    {
        if (obj != null)
        {
            for (Field f : getAllObjectFields(obj.getClass()))
            {
                f.setAccessible(true);
                if (f.getType().isInstance(new LinkedList<Object>()))
                {
                	List<Object> list = new ArrayList<Object>((Collection<Object>)f.get(obj));                	
                	
                    f.set(obj, list);
                    for (Object o : list)
                    {
                        castAllHashSetToList(o);
                    }
                }
                if (Image.class.getPackage().equals(f.getType().getPackage()) && !f.getType().isEnum()
                        && !f.getType().isPrimitive())
                {
                    castAllHashSetToList(f.get(obj));
                }
            }
        }
        return obj;
    }

    
    
    /**
     * Return Fields of this class (excluding superclass fields)
     * 
     * @param cl
     * @return
     */
    public static List<Field> getObjectFields(Class<?> cl)
    {
        List<Field> fields = new ArrayList<Field>();
        for (Field f : cl.getDeclaredFields())
        {
            fields.add(f);
        }
        return fields;
    }

    /**
     * Returns all Field of a class, including those of superclass.
     * 
     * @param cl
     * @return
     */
    public static List<Field> getAllObjectFields(Class<?> cl)
    {
        List<Field> fields = getObjectFields(cl);
        if (cl.getSuperclass() != null)
            fields.addAll(getAllObjectFields(cl.getSuperclass()));
        return fields;
    }

    public static boolean hasFields(Object obj)
    {
        if (getObjectFields(obj.getClass()).size() > 0)
            return true;
        return false;
    }
}
