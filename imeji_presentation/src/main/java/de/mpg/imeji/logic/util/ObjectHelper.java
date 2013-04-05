/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.util;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import de.mpg.imeji.presentation.util.PropertyReader;
import de.mpg.j2j.annotations.j2jModel;
import de.mpg.j2j.annotations.j2jResource;

/**
 * Helper for imeji {@link Object}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class ObjectHelper
{
    private static Logger logger = Logger.getLogger(ObjectHelper.class);

    /**
     * Get the {@link URI} of {@link Object} according to its {@link Class} and the id (not uri)
     * 
     * @param o
     * @return
     */
    public static URI getURI(Class<?> c, String id)
    {
        String baseURI = null;
        String applicationURL = null;
        try
        {
            applicationURL = StringHelper.normalizeURI(PropertyReader.getProperty("escidoc.imeji.instance.url"));
            baseURI = StringHelper.normalizeURI(PropertyReader.getProperty("imeji.jena.resource.base_uri"));
        }
        catch (Exception e)
        {
            logger.error("Error reading properties:", e);
        }
        if (baseURI == null || baseURI.trim().equals("/"))
        {
            baseURI = applicationURL;
        }
        if (baseURI == null)
        {
            throw new RuntimeException("Error in properties. Check property: escidoc.imeji.instance.url");
        }
        j2jModel modelName = c.getAnnotation(j2jModel.class);
        if (modelName != null)
        {
            baseURI = StringHelper.normalizeURI(baseURI + modelName.value());
        }
        else
        {
             baseURI = StringHelper.normalizeURI(c.getAnnotation(j2jResource.class).value());
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
        return URI.create(baseURI + encodedId);
    }

    /**
     * Extract the id number from the object uri
     * 
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
     * Copy {@link Field} from obj1 to obj2. Only Fields with same name and same type are copied. Fields from superclass
     * are not copied.
     * 
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
     * Copy {@link Field} from obj1 to obj2. Only Fields with same name and same type are copied. Fields from superclass
     * are copied.
     * 
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
