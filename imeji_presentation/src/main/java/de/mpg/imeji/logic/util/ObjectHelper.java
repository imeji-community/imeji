/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.presentation.util.PropertyReader;
import de.mpg.j2j.annotations.j2jModel;
import de.mpg.j2j.annotations.j2jResource;

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
        String baseURI;
        try
        {
            baseURI = PropertyReader.getProperty("imeji.jena.resource.base_uri");
        }
        catch (Exception e)
        {
            baseURI = "http://imeji.org/";
//            throw new RuntimeException(
//                    "Error reading property escidoc.imeji.instance.url. Check imeji.properties file.", e);
        }
        j2jModel modelName = c.getAnnotation(j2jModel.class);
        if (modelName != null)
        {
            baseURI += modelName.value();
        }
        else
        {
            baseURI = c.getAnnotation(j2jResource.class).value();
        }
        String encodedId = id;
        try
        {
            encodedId = URLEncoder.encode(id, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException(e);
        }
        return URI.create(baseURI + "/" + encodedId);
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
                if (Item.class.getPackage().equals(f.getType().getPackage()) && !f.getType().isEnum()
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
