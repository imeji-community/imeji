/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.vo.predefinedMetadata.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.imeji.logic.vo.Metadata.Types;
import de.mpg.imeji.logic.vo.predefinedMetadata.ConePerson;
import de.mpg.imeji.logic.vo.predefinedMetadata.Text;

public class MetadataTypesHelper
{
    public static Metadata.Types getTypesForNamespace(String namespace)
    {
        for (Types t : Types.values())
        {
            if (t.getClazzNamespace().equals(namespace))
            {
                return t;
            }
        }
        return null;
    }

    public static String getSimpleValueAsString(Metadata ct)
    {
        if (ct instanceof ConePerson)
        {
            return ((ConePerson)ct).getPerson().getFamilyName() + ((ConePerson)ct).getPerson().getGivenName();
        }
        else if (ct instanceof Text)
        {
            return ((Text)ct).getText();
        }
        return "";
    }

    /**
     * Set a value of a field of a {@link MetadataType}
     * 
     * @param md
     * @param field
     * @param value
     * @return
     * @throws Exception
     */
    public static Metadata setComplexTypeValue(Metadata md, String field, Object value) throws Exception
    {
        return (Metadata)setObjectValue(md, field, value);
    }

    /**
     * Set a value of a field of an object.
     * 
     * @param o
     * @param field
     * @param value
     * @return
     * @throws Exception
     */
    public static Object setObjectValue(Object o, String field, Object value) throws Exception
    {
        for (Field f : getComplexTypeFields(o.getClass(), true))
        {
            if (isField(f, o.getClass()) && f.getName().equals(field))
                f.set(o, value);
            else if (isField(f, o.getClass()) && hasFields(f))
            {
                if (f.get(o) == null)
                    f.set(o, f.getType().newInstance());
                setObjectValue(f.get(o), field, value);
            }
        }
        return o;
    }

    /**
     * Returns all fields of a {@link MetadataType}
     * 
     * @param c
     * @param includeComplexFields if true, include fields which are composed of other fields, i.e complex types
     * @return
     * @throws Exception
     */
    public static List<Field> getComplexTypeFields(Class<?> c, boolean includeComplexFields) throws Exception
    {
        List<Field> list = new ArrayList<Field>();
        for (Field f : c.getDeclaredFields())
        {
            f.setAccessible(true);
            if (isField(f, c) && (!hasFields(f) || includeComplexFields))
                list.add(f);
            else if (isField(f, c) && hasFields(f))
                list.addAll(getComplexTypeFields(f.getType(), includeComplexFields));
        }
        return list;
    }

    /**
     * Returns true if f has fields itself.
     * 
     * @param f
     * @return
     */
    public static boolean hasFields(Field f)
    {
        for (Field f1 : f.getType().getDeclaredFields())
        {
            if (isField(f1, f.getType()))
                return true;
        }
        return false;
    }

    /**
     * f is a field of c if it has getter and setter
     * 
     * @param f
     * @param c
     * @return
     */
    public static boolean isField(Field f, Class<?> c)
    {
        try
        {
            String getter = "get" + f.getName().toUpperCase().substring(0, 1) + f.getName().substring(1);
            String setter = "set" + f.getName().toUpperCase().substring(0, 1) + f.getName().substring(1);
            c.getMethod(getter, null);
            c.getMethod(setter, f.getType());
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }
}
