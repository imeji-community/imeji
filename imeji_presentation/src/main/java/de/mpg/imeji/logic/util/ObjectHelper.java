/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.util;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            // throw new RuntimeException(
            // "Error reading property escidoc.imeji.instance.url. Check imeji.properties file.", e);
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

    /**
     * Extract the id number from the object uri
     * @param uri
     * @return
     */
    public static String getId(URI uri)
    {
        Pattern p = Pattern.compile("(.*)/(\\d+)");
        Matcher m = p.matcher(uri.getPath());
        if (m.matches())
        {
            return m.group(2);
        }
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

    
    /**
     * Copy {@link Field} from obj1 to obj2. Only Fields with same name and same type are copied. Fields from superclass are not copied.
     * @param obj1
     * @param obj2
     */
    public static void copyFields(Object obj1, Object obj2)
    {
        for (Field f2 : getObjectFields(obj2.getClass()))
        {
            try
            {
                f2.setAccessible(true);
                for (Field f1 : getObjectFields(obj1.getClass()))
                {
                    f1.setAccessible(true);
                    if (f1.getName().equals(f2.getName()) && f1.getType().equals(f2.getType()))
                    {
                        f2.set(obj2, f1.get(obj1)); 
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Copy {@link Field} from obj1 to obj2. Only Fields with same name and same type are copied. Fields from superclass are copied.
     * @param obj1
     * @param obj2
     */
    public static void copyAllFields(Object obj1, Object obj2)
    {
        for (Field f2 : getAllObjectFields(obj2.getClass()))
        {
            try
            {
                f2.setAccessible(true);
                for (Field f1 : getAllObjectFields(obj1.getClass()))
                {
                    f1.setAccessible(true);
                    if (f1.getName().equals(f2.getName()) && f1.getType().equals(f2.getType()))
                    {
                        f2.set(obj2, f1.get(obj1)); 
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
